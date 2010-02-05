package edu.ncsa.gridshib.gridshibca;
/*
GridShibCAURL.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

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
        } else
        {
            GridShibCAClientLogger.debugMessage("Using default trust store since useBundledCAs is false.");
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

        this.conn.setRequestProperty("accept", "text/plain");
        this.conn.setRequestProperty("User-Agent",
                "GridShibCA-JWS/" + GridShibCAProperties.getProperty("Version"));

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
     * Post a request to the connection
     * @param values Paramets to post.
     * @throws IOException
     */
    protected void post(HashMap values)
            throws IOException
    {
        GridShibCAClientLogger.debugMessage("POSTing...");
        OutputStreamWriter postWriter =
                new OutputStreamWriter(this.conn.getOutputStream());
        String postData = "";
        Iterator keysIterator = values.keySet().iterator();
        while (keysIterator.hasNext())
        {
            String key = (String) keysIterator.next();
            String value = URLEncoder.encode((String) values.get(key), "UTF-8");
            postData += key + "=" + value;
            GridShibCAClientLogger.debugMessage("\t" + key + "=" + value);
            if (keysIterator.hasNext())
            {
                postData += "&";
            }
        }
        GridShibCAClientLogger.debugMessage("POST: " + postData);
        postWriter.write(postData);
        postWriter.flush();
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
