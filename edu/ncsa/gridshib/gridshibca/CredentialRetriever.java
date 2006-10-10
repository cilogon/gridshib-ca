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
import java.net.URLEncoder;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import org.globus.util.ConfigUtil;
import org.globus.util.Util;

public class CredentialRetriever {
    GUI gui = new GUI("GridShib CA Credential Retriever");
    Boolean debug = false;

    // Size of private key to generate
    int keySize = 1024;

    // Key algorithm to use
    String keyAlg = "RSA";

	public static void main(String[] args) {
		CredentialRetriever app = new CredentialRetriever();
		app.doit(args);
	}

	public void doit(String[] args) {
        gui.display();
        gui.displayMessage("Running...");

		try {
            // Create my SSLSocketFactory beforce JWS has a change to
            // initialize things and install its own.
            SSLSocketFactory mySSLSocketFactory = 
                getMySSLSocketFactory();

            int argIndex = 0;
			URL credURL = new URL(args[argIndex++]);
			String token = args[argIndex++];
            String DN = args[argIndex++];

            
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
            gui.displayMessage("Generating keys...");
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(keyAlg);
            keyGenerator.initialize(keySize);

            KeyPair keypair = keyGenerator.genKeyPair();

			gui.displayMessage("Generating certificate request...");
            gui.displayMessage("Grid identity is: " + DN);

            PKCS10CertificateRequest pkcs10 =
                new PKCS10CertificateRequest(keypair, DN);

            String requestPEM = pkcs10.toPEM();

			gui.displayMessage("Connecting to " + credURL.toString());

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

            HttpsURLConnection conn =
                (HttpsURLConnection) credURL.openConnection();
            conn.setSSLSocketFactory(mySSLSocketFactory);
	        conn.setDoOutput(true);
            conn.setRequestProperty("Cookie", token);
            
            // Write our POST data
            OutputStreamWriter postWriter =
                new OutputStreamWriter(conn.getOutputStream());
            String postData =
                "certificateRequest=" +
                URLEncoder.encode(requestPEM, "UTF-8");
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
			Util.setFilePermissions(targetFile, 0600);

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
                PEM.encodeRSAPrivateKey(privateKey);
            out.write(privateKeyPEM);
			out.close();
            
			gui.displayMessage("Success.");
		} catch (java.net.MalformedURLException e) {
			gui.error("Malformed URL: " + args[0]);
		} catch (java.io.IOException e) {
			gui.error("IO Error: " + e.getMessage());
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			gui.error("Missing argument");
        } catch (java.security.NoSuchAlgorithmException e) {
            gui.error("Could not load " + keyAlg + " key generator algorithm:"
                      + e.getMessage());
		} catch (Exception e) {
			gui.error(e.getMessage());
		}

        gui.displayMessage("Press OK to close application.");
        gui.waitForOK();
	}

    private SSLSocketFactory getMySSLSocketFactory()
    {
        /*
         * Get a socket factory for our use that trusts all the CAs we want it
         * to trust.This function has to be called before Java Web Start has
         * a change to initialize its own SSLSocketFactory.
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
             gui.displayMessage(msg);  
        }
    }
}
