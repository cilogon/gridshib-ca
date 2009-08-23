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
