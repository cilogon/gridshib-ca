package edu.ncsa.gridshib.gridshibca;
/*
PEMEncoder.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;


import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.PKCS10CertificationRequest;
// XXX Use COG Base64 class here to save including another class?
import org.bouncycastle.util.encoders.Base64;

/**
 * Utility class for PEM encoding and decoding.
 */
public class PEMEncoder {

    // Number of characters on a line
    static int defaultPEMMaxLineLength = 64;

    /**
     * Given a RSAPrivateKey, return a PEM-encoded representation.
     * @param key The RSAPrivateKey object
     * @return PEM-encoded key.
     * @throws java.io.IOException
     */
    static String encodeRSAPrivateKeyPKCS1(RSAPrivateKey key)
        throws java.io.IOException
    {
        ByteArrayInputStream inStream =
            new ByteArrayInputStream(key.getEncoded());
        DERInputStream derInputStream = new DERInputStream(inStream);
        DERObject keyInfo = derInputStream.readObject();
        PrivateKeyInfo pkey = new PrivateKeyInfo((ASN1Sequence)keyInfo);
        DERObject derKey = pkey.getPrivateKey();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DEROutputStream der = new DEROutputStream(bout);
        der.writeObject(derKey);
        return toPEM(bout.toByteArray(), "RSA PRIVATE KEY");
    }

    /**
     * Given a PKCS10CertificationReques, return a PEM-encoded representation.
     * @param request The PKCS10Certification request.
     * @return PEM-encoded key.
     * @throws java.io.IOException
     */
    static String encodePKCS10CertificationRequest(
        PKCS10CertificationRequest request)
        throws java.io.IOException
    {
        return toPEM(request.getEncoded(), "CERTIFICATE REQUEST");
    }

    /**
     * Given a PEM-encoded X.509 certificate, return a X509Certiicate object.
     * @param inStream Stream from which to read the X.509 certificate.
     * @return X509Certificate object.
     * @throws java.io.IOException
     * @throws java.security.cert.CertificateException
     */
    static X509Certificate x509CertFromPEM(InputStream inStream)
        throws IOException, CertificateException
    {
        CertificateFactory certFactory =
            CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(inStream);
    }

    /**
     * Given a X509Certificate, return a PEM-encoded version.
     * @param cert Certificate to encode.
     * @return PEM-encoded certificate.
     * @throws java.io.IOException
     * @throws java.security.cert.CertificateException
     */
    static String x509CertToPEM(X509Certificate cert)
        throws IOException, CertificateException
    {
        return toPEM(cert.getEncoded(), "CERTIFICATE");
    }

    /**
     * Given an array of bytes, return PEM coding.
     * @param bytes Array of bytes to encode.
     * @param title Title to use in PEM encoding.
     * @return PEM-coding as String.
     * @throws java.io.IOException
     */
    static String toPEM(byte[] bytes, String title)
        throws java.io.IOException
    {
        return beginString(title) +
            base64Encode(bytes) +
            endString(title);
    }

    /**
     * Return preamble String to PEM encoding.
     * @param title Title to use in PEM encoding.
     * @return String with preamble.
     */
    private static String beginString(String title)
    {
        String s = "-----BEGIN " + title + "-----\n";
        return s;
    }

    /**
     * Return closing String for PEM encoding
     * @param title Title to use in PEM encoding.
     * @return String with closing.
     */
    private static String endString(String title)
    {
        String s = "-----END " + title + "-----\n";
        return s;
    }

    /**
     * Given an array of bytes, return a Base64 encoding of those bytes.
     * @param bytes Array of bytes to encode.
     * @return String with base64 encoding.
     * @throws java.io.IOException
     */
    public static String base64Encode(byte[] bytes)
        throws java.io.IOException
    {
        return base64Encode(bytes, PEMEncoder.defaultPEMMaxLineLength);
    }

    /**
     * Given an array of bytes, return a Base64 encoding of those bytes.
     * @param bytes Array of bytes to encode.
     * @param maxLineLength Maximum length of line before carriage return insertion.
     * @return String with base64 encoding.
     * @throws java.io.IOException
     */
    public static String base64Encode(byte[] bytes, int maxLineLength)
        throws java.io.IOException
    {
        // Kudos to Java COG here
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b64data = Base64.encode(bytes);
        int offset = 0;
        int length = b64data.length;
        while (offset < length)
        {
            int bytesToWrite = maxLineLength;
            if ((length - offset) < maxLineLength)
            {
                bytesToWrite = length - offset;
            }
            out.write(b64data, offset, bytesToWrite);
            offset += bytesToWrite;
            out.write("\n".getBytes());
        }
        return new String(out.toByteArray());
    }
}
