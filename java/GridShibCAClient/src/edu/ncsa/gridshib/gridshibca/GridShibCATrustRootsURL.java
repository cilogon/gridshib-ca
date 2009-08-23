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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.globus.util.ConfigUtil;

/**
 * URL for GridShibCA trusted CA server.
 */
public class GridShibCATrustRootsURL extends GridShibCAURL
{
    // String used to separate files in stream
    private String prefix = "-----File:";

    public GridShibCATrustRootsURL(URL url)
    {
        super(url);
    }

    public void getTrustRoots()
        throws IOException
    {
        File trustedCAPath = getUserCADir();
        GridShibCAClientLogger.debugMessage("Writing trusted CAs to " + trustedCAPath);
        this.openConnection();

        BufferedReader trustedCAStream =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        FileOutputStream out = null;
        while ((line = trustedCAStream.readLine()) != null)
        {
            if (line.startsWith(prefix))
            {
                // Start of new file
                String filename = line.substring(prefix.length()).trim();
                if (out != null)
                {
                    out.close();
                    out = null;
                }
                File file = new File(trustedCAPath + File.separator + filename);
                if (file.exists())
                {
                    GridShibCAClientLogger.debugMessage("File " + file + " already exists. Skipping.");
                } else
                {
                    GridShibCAClientLogger.debugMessage("Writing " + file);
                    out = new FileOutputStream(file);
                }
            } else
            {
                // Line of data, output if we have a valid output file
                if (out != null)
                {
                    String lineCR = line + "\n";
                    out.write(lineCR.getBytes());
                }
            }
        }
        if (out != null)
        {
            out.close();
        }
    }

    private File getUserCADir()
        throws IOException
    {
        File trustedCAPath = new File(ConfigUtil.globus_dir + "certificates" + File.separator);
        if (!trustedCAPath.exists())
        {
            trustedCAPath.mkdirs();
        }
        if (!trustedCAPath.isDirectory())
        {
            throw new IOException("Trusted CA directory is not a directory: " + trustedCAPath);
        }
        return trustedCAPath;
    }
}
