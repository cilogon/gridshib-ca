package edu.ncsa.gridshib.gridshibca;
/*
GridShibCACredentialIssuerURL.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.HashMap;

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
     * @return Credential object.
     * @throws java.io.IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    public Credential requestCredential()
        throws IOException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, InvalidKeyException
    {
        this.openConnection();

        Credential cred = new Credential();
        GridShibCAClientLogger.debugMessage("Generating certificate request");
        String requestPEM = cred.generatePEMCertificateRequest();

        GridShibCAClientLogger.debugMessage("Writing certificate request");
        this.writeRequest(requestPEM);

        GridShibCAClientLogger.debugMessage("Reading certificate");
        this.readCertificate(cred);

        this.closeConnection();
        return cred;
    }


    /**
     * Send a POST request to the GridShibCA server.
     * @param requestPEM PEM-encoded certificate request.
     * @throws java.io.IOException
     */
    private void writeRequest(String requestPEM)
        throws IOException
    {
        OutputStreamWriter postWriter =
                new OutputStreamWriter(this.conn.getOutputStream());
        HashMap values = new HashMap();
        values.put("command", "IssueCert");
        values.put("GRIDSHIBCA_SESSION_ID", this.authenticationToken);
        values.put("certificateRequest", requestPEM);
        this.post(values);
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