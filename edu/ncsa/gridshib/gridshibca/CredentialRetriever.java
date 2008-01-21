package edu.ncsa.gridshib.gridshibca;
// $Id$

/*
Copyright 2006 The Board of Trustees of the University of Illinois.
All rights reserved.

Developed by:

  The GridShib Project
  National Center for Supercomputing Applications
  University of Illinois
  http://gridshib.globus.org/

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal with the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

  Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimers.

  Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimers in the
  documentation and/or other materials provided with the distribution.

  Neither the names of the National Center for Supercomputing
  Applications, the University of Illinois, nor the names of its
  contributors may be used to endorse or promote products derived from
  this Software without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/

import java.io.*;
import java.lang.String;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.jnlp.*;
import java.net.URLEncoder;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import org.globus.util.ConfigUtil;
import org.globus.util.Util;

import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;

public class CredentialRetriever {
    GUI gui = new GUI("GridShib CA Credential Retriever");

    // Print debugging messages
    Boolean debug = false;

    // Size of private key to generate
    int keySize = 1024;

    // Key algorithm to use
    String keyAlg = "RSA";

    // This just seems to work, not sure why
    static String pkcs10Provider = "SunRsaSign";

    // Algorithm to use when signing request
    static String pkcs10SigAlgName = "MD5withRSA";

    // URL to use to retrieve credentials
    URL credURL = null;

    // URL from which to retrieve trusted CAs
    URL trustURL = null;

    // URL to send browser to when we complete successfully
    URL redirectURL = null;

    // Our shibboleth session cookie
    String shibSession = null;

    // Token string to pass
    String token = null;

    // Credential lifetime to request (0 == default)
    int lifetime = 0;

    // Use list of trusted CAs bundled with Jar when validating HTTPS
    // connections?
    boolean useBundledCAs = true;

    // SSLSocketFactory to use for my HTTPS connections
    // null means to use default JWS installs
    SSLSocketFactory mySSLSocketFactory = null;

    // A bogus DN to put in the certificate request. It will
    // be overwritten by the GridShib-CA with the real user DN
    String DN = "CN=Credential Retriever, O=GridShib-CA, C=US";
            
	public static void main(String[] args) {
		CredentialRetriever app = new CredentialRetriever();
		app.doit(args);
	}

	public void doit(String[] args) {
        gui.display();
        gui.displayMessage("Running...");

		try {
            parseArguments(args);

            if (useBundledCAs)
            {
                // Create my SSLSocketFactory beforce JWS has a change to
                // initialize things and install its own.
                mySSLSocketFactory = getMySSLSocketFactory();
            }

            UMask umask = new UMask();

            try
            {
                umask.checkForSecureUMask();
            }
            catch (UMaskException e)
            {
                throw new UMaskException(e.getMessage() + "\nYour UMASK is set to a insecure value. This can cause some web browsers to write files that are world-readable which can cause the GridShib CA to be insecure on multi-users systems. Please set your UMASK to 077.");
            }
            catch (Exception e)
            {
                throw new Exception("Error checking umask: " + e.getMessage());
            }
            
			// URL must be https
			String protocol = credURL.getProtocol();
			if (!protocol.equals("https"))
			{
				throw new Exception("Credential URL is not secure (is '" + protocol + "' rather than 'https'). Service is misconfigured.");
			}

            // Generate our keys
            gui.displayMessage("Generating cryptographic keys...");
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(keyAlg);
            keyGenerator.initialize(keySize);

            KeyPair keypair = keyGenerator.genKeyPair();

			gui.displayMessage("Generating certificate request...");

            PKCS10CertificationRequest pkcs10 =
                new PKCS10CertificationRequest(pkcs10SigAlgName,
                                               new X509Name(DN),
                                               keypair.getPublic(),
                                               new DERSet(),
                                               keypair.getPrivate(),
                                               pkcs10Provider);

            String requestPEM = PEM.encodePKCS10CertificationRequest(pkcs10);

			gui.displayMessage("Connecting to GridShib-CA server...");

            // It appears that Java Web Start for Java 1.5 under MacOS 10.4 (at
            // least) sets up a CookieHandler that screws us up, so if this
            // class exists, then disable the default CookieHandler
            ClassLoader cl = this.getClass().getClassLoader().getSystemClassLoader();
            try
            { 
                Class cookieHandler = cl.loadClass("java.net.CookieHandler");
                Class[] params = { cookieHandler };
                Method setDefaultCookieHandler
                    = cookieHandler.getMethod("setDefault", params);
                Object[] argArray = { null };
                setDefaultCookieHandler.invoke(cookieHandler, argArray); 
            } catch (ClassNotFoundException e) {
                // Class doesn't exist, don't need to do anything
            }

            HttpsURLConnection conn = openHttpsURL(credURL);
            
            // Write our POST data
            OutputStreamWriter postWriter =
                new OutputStreamWriter(conn.getOutputStream());
            String postData =
                "certificateRequest=" +
                URLEncoder.encode(requestPEM, "UTF-8")
                + "&" +
                "token=" +
                URLEncoder.encode(token, "UTF-8")
                + "&" +
                "lifetime=" + lifetime;
            debug("PostData: " + postData);
            postWriter.write(postData);
            postWriter.flush();
            
            BufferedReader credStream;

            try {
                credStream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } catch (java.io.IOException e) {
                int statusCode = conn.getResponseCode();
                String responseMessage = conn.getResponseMessage();
                if (statusCode == conn.HTTP_UNAUTHORIZED)
                {
                    throw new Exception("Authentication failed: " +
                                        responseMessage);
                }
                else if (statusCode == conn.HTTP_BAD_REQUEST)
                {
                    throw new Exception("Request malformed: " +
                                        responseMessage);
                }
                else if (statusCode == conn.HTTP_INTERNAL_ERROR)
                {
                    throw new Exception("Internal server error: " +
                                        responseMessage);
                }
                else
                {
                    throw new Exception("Server returned error status " +
                                        statusCode + ":" +
                                        responseMessage);
                }
            }

			String targetFile = ConfigUtil.discoverProxyLocation();
			gui.displayMessage("Writing credential to " + targetFile);

			File outFile = new File(targetFile);

			outFile.delete();
			outFile.createNewFile();
			// Argh. small time window here where file could be opened()
            // unless umask is set correctly.
            //
            // Note that umask value here is converted to a string as a decimal
            // value. So don't pass a octal value, but the octal value as if it
            // was a decimal.
			Util.setFilePermissions(targetFile, 600);

			FileWriter out = new FileWriter(outFile);

			String line;
            int lineCount = 0;
            while ((line = credStream.readLine()) != null) {
                out.write(line + "\n");
                lineCount++;
            }
			credStream.close();
            // Make sure we actually read some data
            if (lineCount == 0)
			{
				throw new Exception("Got no data from server trying to read Credential.");
			}

            // Now output private key
            RSAPrivateKey privateKey= (RSAPrivateKey) keypair.getPrivate();
            String privateKeyPEM =
                PEM.encodeRSAPrivateKeyPKCS1(privateKey);
            out.write(privateKeyPEM);
			out.close();

            // Get trusted CAs if we got a trustURL
            if (trustURL != null)
            {
                getTrustedCAs(trustURL);
            }

			gui.displayMessage("Success.");


            if (redirectURL != null)
            {
                gui.displayMessage("Opening " + redirectURL.toString());
                gotoURL(redirectURL);
            }

		} catch (java.io.IOException e) {
			gui.error("IO Error: " + e.getMessage());
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			gui.error("Missing argument");
        } catch (java.security.NoSuchAlgorithmException e) {
            gui.error("Could not load " + keyAlg + " key generator algorithm:"
                      + e.getMessage());
        } catch (java.lang.IllegalArgumentException e) {
            gui.error("Error parsing JWS arguments. Client/server version mismatch? Error: " + e.getMessage());
		} catch (Exception e) {
			gui.error(e.getMessage());
		}

        gui.displayMessage("Press OK to close application.");
        gui.waitForOK();
        // Timeout and exit in 10 seconds
        //new Timeout(10000).start();
	}
       
    // Parse commandline arguments. They should be of the form
    // "var=value"
    private void parseArguments(String[] args)
        throws java.lang.IllegalArgumentException
    {
        int argIndex = 0;
        boolean argError = false;
        for (argIndex = 0; argIndex < args.length; argIndex++)
        {
            String arg = args[argIndex].trim();
            int equalIndex = arg.indexOf('=');
            if (equalIndex == -1)
            {
                throw new java.lang.IllegalArgumentException(
                    "Failed to parse argument: " + arg);
            }
            String var = arg.substring(0,equalIndex);
            String value = arg.substring(equalIndex+1);
            if (arg.startsWith("_shibsession_"))
            {   
                shibSession = arg;
                debug("Shibboleth session: " + shibSession);
            }
            else if (var.equals("token"))
            {
                token = value;
                debug("Token: " + token);
            }
            else if (var.equals("url"))
            {
                try {
                    credURL = new URL(value);
                } catch (java.net.MalformedURLException e) {
                    throw new java.lang.IllegalArgumentException(
                        "Error parsing credential creator URL: " + value);
                }
                debug("URL: " + credURL.toString());
            }
            else if (var.equals("debug"))
            {
                debug = Boolean.valueOf(value);
                debug("Debug: " + debug.toString());
            }
            else if (var.equals("lifetime"))
            {
                try {
                    lifetime = Integer.parseInt(value);
                    debug("Requesting credential lifetime of " + lifetime);
                } catch (java.lang.NumberFormatException e) {
                    throw new java.lang.IllegalArgumentException(
                        "Bad lifetime argument: " + value);
                }
            }
            else if (var.equals("trustURL"))
            {
                try {
                    trustURL = new URL(value);
                } catch (java.net.MalformedURLException e) {
                    throw new java.lang.IllegalArgumentException(
                        "Error parsing trusted CA URL: " + value);
                }
                debug("Trusted CA URL: " + trustURL.toString());
            }
            else if (var.equals("useBundledCAs"))
            {
                useBundledCAs = Boolean.valueOf(value);
                debug("useBundledCAs: " + useBundledCAs);
            }
            else if (var.equals("redirectURL"))
            {
                try {
                    redirectURL = new URL(value);
                } catch (java.net.MalformedURLException e) {
                    throw new java.lang.IllegalArgumentException(
                        "Error parsing redirect URL: " + value);
                }
                debug("Redirect URL: " + redirectURL.toString());
            }
            else
            {
                throw new IllegalArgumentException(
                    "Unrecognized variable in argument: " + arg);
            }   
        }

        // Make sure everything got set that needed to be
        if (credURL == null)
        {
            throw new IllegalArgumentException("Missing URL argument for credential generator.");
        }
        if (shibSession == null)
        {
            throw new IllegalArgumentException("Missing Shibboleth Session argument.");
        }
        if (token == null)
        {
            throw new IllegalArgumentException("Missing token argument.");
        }
    }

    // Open and configure a HTTPS connection given a URL
    private HttpsURLConnection openHttpsURL(URL url)
        throws IOException
    {
        debug("Setting up connection to URL: " + url);

        HttpsURLConnection conn =
            (HttpsURLConnection) url.openConnection();
        if (mySSLSocketFactory != null)
        {
            debug("Using my list of bundled CAs.");
            conn.setSSLSocketFactory(mySSLSocketFactory);
        }
        conn.setDoOutput(true);
        conn.setRequestProperty("Cookie", shibSession);
        return conn;
    }

    private SSLSocketFactory getMySSLSocketFactory()
    {
        /*
         * Get a socket factory for our use that trusts all the CAs that come
         * bundled with this Jar. This function has to be called before Java
         * Web Start has a change to initialize its own SSLSocketFactory.
         */

		try
		{
            /*
             * First we create our list of trusted CAs and put them into java
             * properties so that they are read when the default
             * SSLSocketFactory is created.
             */
			ClassLoader cl = this.getClass().getClassLoader();
			URL url = cl.getResource("resources/trustStore");
			if (url == null)
			{
                throw new Exception("Could not find trusted CAs in JAR file.");
 			}
			String path = url.toString();
			// If file is in a jar, we need to write it into a temporary
			// file, as we need a real file for the trustStore
			if (path.startsWith("jar:"))
			{
                debug("Copying trusted CA certificates from jar.");

				File tempFile = File.createTempFile("CredentialRetriever",
													".trustStore");
				InputStream in = cl.getResourceAsStream("resources/trustStore");
				FileOutputStream out = new FileOutputStream(tempFile);
				copyStream(in, out);
				in.close();
				out.close();
				path = tempFile.getAbsolutePath();
				tempFile.deleteOnExit();
			}
			// Remove "file:" prefix from path if it exists as the security
			// runtime can't deal with it.
			if (path.startsWith("file:"))
			{
				path = path.substring(5);
			}
            debug("Setting trusted CAs to " + path);
			System.setProperty("javax.net.ssl.trustStore", path);
		} catch (Exception e ) {
			gui.warning("Failed to initiate trusted CAs: " + e.getMessage());
		}
 
        /*
         * Now we get the current default SSLSocketFactory. Doing this will
         * initialize the factory, causing the trusted CAs created above to
         * be read.
         */
        SSLSocketFactory defaultSSLSocketFactory = 
            (SSLSocketFactory) SSLSocketFactory.getDefault();

        return defaultSSLSocketFactory;
    }

    /*
     * Send web browser to give URL.
     */
    private void gotoURL(URL url)
    {
        String basicService = "javax.jnlp.BasicService";
        BasicService bs = null;

        try
        {
            // Lookup the javax.jnlp.BasicService object
            bs = (BasicService) ServiceManager.lookup(basicService);
        }
        catch (UnavailableServiceException e)
        {
            gui.warning("Failed to redirect to " + url +
                        ": could not load BasicService: " +
                        e.getMessage());
           return;
        } 

        if (bs.showDocument(url) == false)
        {
            // Redirect failed
            gui.warning("Failed to redirect to " + url +
                        ": showDocument() failed." +
                        " (isOffline = " + bs.isOffline() +
                        " isWebBrowserSupported = " +
                        bs.isWebBrowserSupported() + ")");
        }
    }

    /*
     * Read trusted CAs from given URL.
     */
    private void getTrustedCAs(URL trustedCAsURL)
        throws IOException
    {
        String prefix = "-----File:";

        gui.displayMessage("Reading trusted CAs");
        debug("Trusted CAs URL: " + trustedCAsURL);
        File trustedCAPath = getUserCADir();
        debug("Writing trusted CAs to " + trustedCAPath);
        HttpsURLConnection conn = openHttpsURL(trustedCAsURL);

        BufferedReader trustedCAStream =
            new BufferedReader(
                new InputStreamReader(
                    conn.getInputStream()));
        String line;
        FileOutputStream out = null;
        while ((line = trustedCAStream.readLine()) != null)
        {
            if (line.startsWith(prefix))
            {
                // Start of new file
                String filename = line.substring(prefix.length()).trim();
                if (out != null)
                {
                    out.close();
                    out = null;
                }
                File file = new File(trustedCAPath + File.separator + filename);
                if (file.exists())
                {
                    debug("File " + file + " already exists. Skipping.");
                }
                else
                {
                    debug("Writing " + file);
                    out = new FileOutputStream(file);
                }
            }
            else
            {
                // Line of data, output if we have a valid output file
                if (out != null)
                {
                    String lineCR = line + "\n";
                    out.write(lineCR.getBytes());
                }
            }
        }
        if (out != null)
        {
            out.close();
        }
    }

    /*
     * Return the path of the user's trusted certificates directory.
     * Normally ~/.globus/certificates.
     * Create this directory if it doesn't exist (counting on umask to be
     * set appropriately). 
     */
    private File getUserCADir()
        throws IOException
    {
        File trustedCAPath = new File(ConfigUtil.globus_dir
                                      + "certificates"
                                      + File.separator);
        if (!trustedCAPath.exists())
        {
            trustedCAPath.mkdirs();
        }
        if (!trustedCAPath.isDirectory())
        {
            throw new IOException("Trusted CA directory is not a directory: "
                                  + trustedCAPath);
        }
        return trustedCAPath;
    }

	public void copyStream(InputStream in, OutputStream out)
		throws IOException
	{
		byte[] buffer = new byte[1024];	// Arbitrary size
		int read;

		while((read = in.read(buffer)) != -1)
		{
			out.write(buffer, 0, read);
		}
	}

    private void debug(String msg)
    {
        if (debug)
        {
             gui.displayMessage("DEBUG: " + msg);  
        }
    }
}

// Thread that kills us after timeout
// Time is in milliseconds
class Timeout extends Thread
{
    int timeout;

    public Timeout(int time)
    {   
        timeout = time;
    }

    public synchronized void run()
    {
        try
        {   
            sleep(timeout);
        }
        catch (InterruptedException e)
        {}
        System.exit(0);
    }
}
