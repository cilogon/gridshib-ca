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

/*
 * Utilities functions for PEM encoding.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.String;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;

public class PEM {

    // Number of characters on a line
    static int PEMLineLength = 64;

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

    static String encodePKCS10CertificationRequest(
        PKCS10CertificationRequest request)
        throws java.io.IOException
    {
        return toPEM(request.getEncoded(), "CERTIFICATE REQUEST");
    }

    static String toPEM(byte[] bytes, String what)
        throws java.io.IOException
    {
        return beginString(what) +
            base64Encode(bytes) +
            endString(what);
    }

    private static String beginString(String what)
    {
        String s = "-----BEGIN " + what + "-----\n";
        return s;
    }

    private static String endString(String what)
    {
        String s = "-----END " + what + "-----\n";
        return s;
    }   

    public static String base64Encode(byte[] bytes)
        throws java.io.IOException
    {
        // Kudos to Java COG here
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b64data = Base64.encode(bytes);
        int offset = 0;
        int length = b64data.length;
        while (offset < length)
        {
            int bytesToWrite = PEMLineLength;
            if ((length - offset) < PEMLineLength)
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
