package edu.ncsa.gridshib.gridshibca;
/*
GridShibCATrustRootsURL.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
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
