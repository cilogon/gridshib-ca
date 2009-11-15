package edu.ncsa.gridshib.gridshibca;
/*
GridShibCATrustStore.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Class representing the GridShibCA client's trust store of trusted CA
 * certificates.
 */
public class GridShibCATrustStore {

    private Resource resource;

    public GridShibCATrustStore(String resourceName) {
        this.resource = new Resource(resourceName);
    }

    /**
     * Return a path to a trust store file. This may be a temporary file.
     * @return Path of trust store file as string.
     */
    String getPath()
        throws IOException
    {
        URL url = this.resource.getURL();
        if (url == null)
        {
            throw new IOException("Could not find trust store.");
        }
        String path = url.toString();
        // If file is in a jar, we need to write it into a temporary
        // file, as we need a real file for the trustStore
        if (path.startsWith("jar:")) {
            File tempFile = File.createTempFile("GridShibCATrustStore",
                                                ".trustStore");
            tempFile.deleteOnExit();    
            InputStream in = this.resource.asStream();
            FileOutputStream out = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];	// Arbitrary size
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
            out.close();
            path = tempFile.getAbsolutePath();
        }
        // Remove "file:" prefix from path if it exists as the security
        // runtime can't deal with it.
        if (path.startsWith("file:")) {
            path = path.substring(5);
        }
        return path;
    }
}
