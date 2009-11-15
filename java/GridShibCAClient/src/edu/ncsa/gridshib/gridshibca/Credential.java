package edu.ncsa.gridshib.gridshibca;
/*
Credential.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;

import org.globus.util.ConfigUtil;
import org.globus.util.Util;

/**
 * Class representing the user's X.509 credential (private key and
 * certificate).
 */
public class Credential
{

    // Default private key size to generate
    private static int defaultKeySize = 1024;

    // Default key algorithm to use for generation
    private static String defaultKeyAlg = "RSA";

    // Algorithm to use when signing request
    // XXX Should be SHA?
    private static String pkcs10SigAlgName = "MD5withRSA";

    // A bogus DN to put in the certificate request. It will
    // be overwritten by the GridShib-CA with the real user DN
    private static String requestDN = "CN=Credential Retriever, O=GridShib-CA, C=US";

    // This just seems to work, not sure why
    private static String pkcs10Provider = "SunRsaSign";

    // Our key pair
    private KeyPair keyPair = null;
    // And associated certificate
    private X509Certificate cert = null;

    /**
     * Generate new key pair and return a PEM-encoded certificate request using
     * default key size and algorithm.
     * @return The certificate request in PEM format.
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.IOException
     * @throws java.security.NoSuchProviderException
     * @throws java.security.InvalidKeyException
     * @throws java.security.SignatureException
     */
    public String generatePEMCertificateRequest()
            throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeyException, SignatureException
    {
        return generatePEMCertificateRequest(Credential.defaultKeySize,
                Credential.defaultKeyAlg);
    }

    /**
     * Generate new key pair and return a PEM-encoded certificate request.
     * @param keySize The key size for the generated keys.
     * @param keyAlg The key algorithm to use.
     * @return The certificate request in PEM format.
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.IOException
     * @throws java.security.NoSuchProviderException
     * @throws java.security.InvalidKeyException
     * @throws java.security.SignatureException
     */
    public String generatePEMCertificateRequest(int keySize,
            String keyAlg)
            throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeyException, SignatureException
    {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(keyAlg);
        keyGenerator.initialize(keySize);

        this.keyPair = keyGenerator.genKeyPair();

        PKCS10CertificationRequest pkcs10 =
                new PKCS10CertificationRequest(Credential.pkcs10SigAlgName,
                new X509Name(Credential.requestDN),
                keyPair.getPublic(),
                new DERSet(),
                keyPair.getPrivate(),
                pkcs10Provider);

        String requestPEM = PEMEncoder.encodePKCS10CertificationRequest(pkcs10);

        return requestPEM;
    }

    /**
     * @return subject in RFC 2533 format.
     */
    public String getSubject()
    {
        return this.cert.getSubjectDN().toString();
    }

    /**
     * @return subject in Globus (aka OpenSSL one-line) format.
     */
    public String getSubjectGlobusFormat()
    {
        String subject = this.getSubject();

        // Split DN commas (plus a space)
        final String regex = ", ";
        String[] components = subject.split(regex);

        // Reverse components and join with forward slashes
        int index = components.length - 1;
        String convertedSubject = "";
        while (index >= 0)
        {
            convertedSubject += "/" + components[index];
            index--;
        }
        return convertedSubject;
    }

    /**
     * @return date Gets the notBefore date from the validity period of the certificate.
     */
    public Date getNotDefore()
    {
        return cert.getNotBefore();
    }

    /**
     * @return date Gets the notAfter date from the validity period of the certificate.
     */
    public Date getNotAfter()
    {
        return cert.getNotAfter();
    }

    /**
     * Read a X.509 certificate from given stream and add to credential object.
     * @param inStream Stream to read the certificate from.
     * @throws java.io.IOException
     * @throws java.security.cert.CertificateException
     */
    public void readX509CertFromPEM(InputStream inStream)
            throws IOException, CertificateException
    {
        this.cert = PEMEncoder.x509CertFromPEM(inStream);
    }

    /**
     * Write this credential, in a format suitable for Globus, to the current
     * user's default proxy file.
     * @return Path of default proxy file.
     * @throws java.io.IOException
     * @throws java.security.cert.CertificateException
     */
    public String writeToDefaultProxyFile()
            throws IOException, CertificateException
    {
        String proxyPath = ConfigUtil.discoverProxyLocation();
        this.writeToFile(proxyPath);
        return proxyPath;
    }

    /**
     * Write this credential, in a format suitable for Globus, to the given
     * path.
     * @param path Path to which to write the file.
     * @throws java.io.IOException
     * @throws java.security.cert.CertificateException
     */
    public void writeToFile(String path)
            throws IOException, CertificateException
    {
        File outFile = new File(path);

        outFile.delete();
        // Argh. small time window here where file could be opened()
        // unless umask is set correctly.
        //
        // XXX Java 6 allows for attributes in the Create File call which
        // should be used here to prevent race condition.
        // http://java.sun.com/docs/books/tutorial/essential/io/fileAttr.html#posix
        outFile.createNewFile();

        // Note that umask value here is converted to a string as a decimal
        // value. So don't pass a octal value, but the octal value as if it
        // was a decimal.
        Util.setFilePermissions(path, 600);

        FileWriter out = new FileWriter(outFile);

        out.write(PEMEncoder.x509CertToPEM(cert));

        // Now output private key
        RSAPrivateKey privateKey = (RSAPrivateKey) this.keyPair.getPrivate();
        String privateKeyPEM =
                PEMEncoder.encodeRSAPrivateKeyPKCS1(privateKey);
        out.write(privateKeyPEM);
        out.close();
    }
}
