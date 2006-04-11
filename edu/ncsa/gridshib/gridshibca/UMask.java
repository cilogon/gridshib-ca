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

import java.io.*;
import org.globus.util.ConfigUtil;

public class UMask
{
    String os = System.getProperty("os.name");

    public void checkForSecureUMask() throws Exception
    {
        String umask = getUMask();

        if ((otherMask(umask) != 7) ||
            (groupMask(umask) != 7))
        {
            throw new Exception("UMask (" + umask + ") is not secure.");
        }
    }

    public int otherMask(String umask)
    {
        if (umask.length() < 1)
        {
            return 0;
        }
        String mask = umask.substring(umask.length() - 1);
        return Integer.valueOf(mask).intValue();
    }
	
    public int groupMask(String umask)
    {
        if (umask.length() < 2)
        {
            return 0;
        }
        String mask = umask.substring(umask.length() - 2, umask.length() - 1);
        return Integer.valueOf(mask).intValue();
    }

    public int selfMask(String umask)
    {
        if (umask.length() < 3)
        {
            return 0;
        }
        String mask = umask.substring(umask.length() - 3, umask.length() - 2);
        return Integer.valueOf(mask).intValue();
    }

    public String getUMask() throws IOException
    {
        Runtime runTime = Runtime.getRuntime();
        Process process = null;
        BufferedReader fromUMask = null;
        StringBuffer output = new StringBuffer();

        if (ConfigUtil.getOS() == ConfigUtil.WINDOWS_OS)
        {
            return "777";
        }

        try {
            String input;

            process = runTime.exec("umask");
            fromUMask = new BufferedReader
                ( new InputStreamReader(process.getInputStream()) ); 
            while ((input = fromUMask.readLine()) != null) {
                output.append(input);
            }

            int exitStatus = process.waitFor();
            if (exitStatus != 0)
            {
                throw new IOException("Unable to execute 'umask' (status = " +
                                      Integer.toString(exitStatus) + ")");
            }
        } catch (Exception e) {
            throw new IOException("Unable to execute 'umask'");
        } finally {
            if (fromUMask != null)
            {
                try { 
                    fromUMask.close();
                } catch (IOException e) {}
            }
            if (process != null)
            {
                try { 
                    process.getErrorStream().close(); 
                } catch (IOException e) {}
                try { 
                    process.getOutputStream().close(); 
                } catch (IOException e) {}
            }
        }
        return output.toString().trim();
    }
}