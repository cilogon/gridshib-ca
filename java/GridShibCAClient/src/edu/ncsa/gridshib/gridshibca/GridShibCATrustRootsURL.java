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
import java.util.HashMap;
import org.globus.util.ConfigUtil;

/**
 * URL for GridShibCA trusted CA server.
 */
public class GridShibCATrustRootsURL
        extends GridShibCAURL
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


        GridShibCAClientLogger.debugMessage("Writing request to server for trust roots...");
        this.openConnection();
        this.writeRequest();

        GridShibCAClientLogger.debugMessage("Reading trust roots...");
        BufferedReader trustedCAStream =
                new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
        String line;
        FileOutputStream out = null;
        while ((line = trustedCAStream.readLine()) != null)
        {
            if (line.startsWith(prefix))
            {
                // Start of new file
                File origFile = new File(line.substring(prefix.length()).trim());
                if (out != null)
                {
                    out.close();
                    out = null;
                }
                File file = new File(trustedCAPath + File.separator + origFile.getName());
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

    /**
     * Send a request to the GridShibCA server for Trust Roots.
     * @throws java.io.IOException
     */
    private void writeRequest()
            throws IOException
    {
        HashMap values = new HashMap();
        values.put("command", "TrustRoots");
        this.post(values);
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
