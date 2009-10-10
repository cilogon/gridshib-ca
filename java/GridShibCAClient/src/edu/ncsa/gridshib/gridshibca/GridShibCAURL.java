package edu.ncsa.gridshib.gridshibca;
// $Id$

/*
Copyright 2009 The Board of Trustees of the University of Illinois.
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
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.jdesktop.application.ResourceMap;

/**
 * Base class for URLs representing GridShibCA services.
 */
public class GridShibCAURL
{
    // Target URL

    private URL url = null;

    // Connection to target
    protected HttpURLConnection conn;

    // SSLSocketFactory to use for HTTPS connections
    private static SSLSocketFactory mySSLSocketFactory = null;

    /**
     * Create new URL object presenting server at given url.
     * @param url URL to service.
     */
    public GridShibCAURL(URL url)
    {
        this.url = url;
        this.conn = null;
    }

    /**
     * One-time initialization of networking. Should be done as earlier as
     * possible.
     */
    public static void init()
    {
        Boolean useBundledCAs = GridShibCAProperties.getPropertyAsBoolean("UseBundledCAs");

        if (useBundledCAs)
        {
            try
            {
                /*
                 * Get a socket factory for our use that trusts all the CAs in
                 * the given trustStore. This has to be done before Java has a
                 * chance to initialize its own SSLSocketFactory.
                 */
                String trustStoreResourceName = GridShibCAProperties.getProperty("TrustStore");
                String trustStorePath = new GridShibCATrustStore(trustStoreResourceName).getPath();
                GridShibCAClientLogger.debugMessage("Setting SSL trust store to " + trustStorePath);
                System.setProperty("javax.net.ssl.trustStore", trustStorePath);

                // Now we get the current default SSLSocketFactory. Doing
                // this will initialize the factory, causing the trusted
                // CAs created above to be read.
                mySSLSocketFactory =
                        (SSLSocketFactory) SSLSocketFactory.getDefault();

            } catch (IOException e)
            {
                // Could not find trust store
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Open the connection to the GridShibCA server in this.conn.
     * @throws java.io.IOException
     */
    protected void openConnection()
            throws IOException
    {
        GridShibCAClientLogger.debugMessage("Establishing connection to " + this.url);
        this.conn = (HttpURLConnection) this.url.openConnection();

        // If this is a https connection and we have SSLSocketFactory
        // to use (i.e. we have a trustStore besides the default) now
        // is the time to take care of that.
        if (this.url.getProtocol().equals("https") &&
                (this.mySSLSocketFactory != null))
        {
            HttpsURLConnection sconn = (HttpsURLConnection) this.conn;
            GridShibCAClientLogger.debugMessage("Using my trustStore with my CAs");
            sconn.setSSLSocketFactory(this.mySSLSocketFactory);
        }

        this.conn.setDoOutput(true);
    }

    /**
     * Close the connection to the GridShibCA server in this.conn.
     */
    protected void closeConnection()
    {
        GridShibCAClientLogger.debugMessage("Disconnecting from " + this.url);
        if (this.conn != null)
        {
            this.conn.disconnect();
        }
        this.conn = null;
    }
}
