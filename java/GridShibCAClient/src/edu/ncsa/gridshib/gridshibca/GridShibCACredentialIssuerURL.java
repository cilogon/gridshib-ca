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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

/**
 * URL representing GridShibCA Server.
 */
public class GridShibCACredentialIssuerURL extends GridShibCAURL
{
    private String authenticationToken;

    /**
     *
     * @param GridShibCACredentialIssuerURL should be the URL to the GridShibCA service.
     * @param authToken should be the authentication token usually passed in JNLP file.
     * @throws java.io.IOException
     */
    public GridShibCACredentialIssuerURL(URL GridShibCACredentialIssuerURL,
                                         String authToken)
        throws IOException
    {
        super(GridShibCACredentialIssuerURL);

        // URL must be https
        String protocol = GridShibCACredentialIssuerURL.getProtocol();
        if (!protocol.equals("https"))
        {
            throw new IOException("GridShibCA URL is not secure (protocol is '" + protocol + "' rather than 'https').");
        }

        this.authenticationToken = authToken;
    }

    /**
     * Request a credential from the GridShibCA.
     * @param lifetime Requested lifetime in seconds.
     * @return Credential object.
     * @throws java.io.IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    public Credential requestCredential(int lifetime)
        throws IOException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, InvalidKeyException
    {
        this.openConnection();

        Credential cred = new Credential();
        GridShibCAClientLogger.debugMessage("Generating certificate request");
        String requestPEM = cred.generatePEMCertificateRequest();

        GridShibCAClientLogger.debugMessage("Writing certificate request");
        this.writeRequest(requestPEM, lifetime);

        GridShibCAClientLogger.debugMessage("Reading certificate");
        this.readCertificate(cred);

        this.closeConnection();
        return cred;
    }


    /**
     * Send a POST request to the GridShibCA server.
     * @param requestPEM PEM-encoded certificate request.
     * @param lifetime Requested certificate lifetime in seconds.
     * @throws java.io.IOException
     */
    private void writeRequest(String requestPEM,
                              int lifetime)
        throws IOException
    {
        OutputStreamWriter postWriter =
                new OutputStreamWriter(this.conn.getOutputStream());
        String postData =
                "certificateRequest=" +
                URLEncoder.encode(requestPEM, "UTF-8") + "&" +
                "token=" +
                URLEncoder.encode(this.authenticationToken, "UTF-8") + "&" +
                "lifetime=" + lifetime;
        GridShibCAClientLogger.debugMessage("POST data: " + postData);
        postWriter.write(postData);
        postWriter.flush();
    }

    /**
     * Read a certifcate in response to a request from GridShibCA server.
     * @param cred Credential object which generated request that certificate will be read into.
     * @throws java.io.IOException
     */
    private void readCertificate(Credential cred)
        throws IOException, CertificateException
    {
        try
        {
            InputStream credStream = this.conn.getInputStream();
            cred.readX509CertFromPEM(credStream);
            credStream.close();

        } catch (java.io.IOException e)
        {
            int statusCode;
            String responseMessage;
            try
            {
                statusCode = this.conn.getResponseCode();
                responseMessage = this.conn.getResponseMessage();
            } catch (IOException ex)
            {
                throw ex;
            }
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                throw new IOException("Authentication failed: " +
                        responseMessage);
            } else if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST)
            {
                throw new IOException("Request malformed: " +
                        responseMessage);
            } else if (statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
            {
                throw new IOException("Internal server error: " +
                        responseMessage);
            } else
            {
                throw new IOException("Server returned error status " +
                        statusCode + ":" +
                        responseMessage);
            }
        }
    }
}