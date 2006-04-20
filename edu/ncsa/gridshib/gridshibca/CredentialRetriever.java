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
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLDecoder;
import org.globus.util.ConfigUtil;
import org.globus.util.Util;

public class CredentialRetriever {
    GUI gui = new GUI("GridShib CA Credential Retriever");
    Boolean debug = false;

	public static void main(String[] args) {
		CredentialRetriever app = new CredentialRetriever();
		app.doit(args);
	}

	public void doit(String[] args) {
        gui.display();
        gui.displayMessage("Running...");

		try {
			URL credURL = new URL(args[0]);
			String token = args[1];

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

			setupTrustedCAs();
			
			// URL must be https
			String protocol = credURL.getProtocol();
			if (!protocol.equals("https"))
			{
				throw new Exception("Credential URL is not secure (is '" + protocol + "' rather than 'https'). Service is misconfigured.");
			}

			gui.displayMessage("Connecting to " + credURL.toString());

            // In 1.5 Java web start sets up a CookieHandler
            // that screws us up, so if this class exists, then
            // disable the default CookieHander
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
        
			URLConnection conn = credURL.openConnection();
	        conn.setDoOutput(true);
            conn.setRequestProperty("Cookie", token);

			BufferedReader credStream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	
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
			// Check first line for error or success indicator
			line = credStream.readLine();
			if (line != null)
			{
				if (line.startsWith("ERROR"))
				{
					String error = line;
					while ((line = credStream.readLine()) != null)
					{
						error += "\n" + line;
					}	
					throw new Exception("Got error from GridShib CA server.\n"
										+ error);
				}
                if (!line.startsWith("GRIDSHIB-CA-SUCCESS"))
                {
                    throw new Exception("Cannot parse response from GridShib CA server: " + line);
                }
			}
			else
			{
				throw new Exception("Got no data from server trying to read Credential.");
			}
            while ((line = credStream.readLine()) != null) {
                out.write(line + "\n");
            }
			out.close();
			credStream.close();
			gui.displayMessage("Success.");
		} catch (java.net.MalformedURLException e) {
			gui.error("Malformed URL: " + args[0]);
		} catch (java.io.IOException e) {
			gui.error("IO Error: " + e.getMessage());
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			gui.error("Missing argument");
		} catch (Exception e) {
			gui.error(e.getMessage());
		}

        gui.displayMessage("Press OK to close application.");
        gui.waitForOK();
	}

	public void setupTrustedCAs()
	{
		try
		{
			ClassLoader cl = this.getClass().getClassLoader();
			URL url = cl.getResource("resources/trustStore");
			if (url == null)
			{
				gui.warning("Could not find trusted CAs");
				return;
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
