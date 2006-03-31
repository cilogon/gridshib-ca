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
import org.globus.util.Util;

public class GridShibCATester {
    GUI gui = new GUI("GridShib CA Test Application");

	public static void main(String[] args) {
		GridShibCATester app = new GridShibCATester();
		app.doit(args);
	}

	public void doit(String[] args) {
        gui.display();
        gui.displayMessage("If you can see this message, that means Java Web Start is working fine on your system.");

		try {
            // Check umask
            UMask umask = new UMask();
            gui.displayMessage("Checking your umask setting.");
            try
            {
                umask.checkForSecureUMask();
            }
            catch (Exception e)
            {
                throw new Exception(e.getMessage() + "\nYour UMASK is set to a insecure value. This can cause some web browsers to write files that are world-readable which can cause the GridShib CA to be insecure on multi-users systems. Please set your UMASK to 077.");
            }
            gui.displayMessage("Success.");

			String proxyLocation = ConfigUtil.discoverProxyLocation();
			gui.displayMessage("Your default proxy location is " + proxyLocation);

            // Try writing a file
            String testFile = proxyLocation + ".test";
            gui.displayMessage("Trying to write " + testFile);
			File outFile = new File(testFile);

			outFile.delete();
			outFile.createNewFile();
			Util.setFilePermissions(testFile, 0600);

			FileWriter out = new FileWriter(outFile);
            out.write("Test\n");
            out.close();
            outFile.delete();
            gui.displayMessage("Success.");
            gui.displayMessage("All tests passed. Your setup looks good.");

		} catch (java.io.IOException e) {
			gui.error("IO Error: " + e.getMessage());
		} catch (Exception e) {
			gui.error(e.getMessage());
		}

        gui.displayMessage("Press OK to close application.");
        gui.waitForOK();
	}
}