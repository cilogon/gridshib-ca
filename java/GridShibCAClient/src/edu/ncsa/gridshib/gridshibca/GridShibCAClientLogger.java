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

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Logging interface for GridShibCA Client. This implementation uses GUI
 * methods to display messages.
 */
public class GridShibCAClientLogger {
    // Log debug messages?
    private static boolean debug = false;

    // GUI to use to display messages
    private static GridShibCAClientView gui = null;

    /**
     * Handle an error message.
     * @param msg Message to display.
     */
    public static void errorMessage(String msg)
    {
        handleException(msg, null);
    }

    /**
     * Handle an exception.
     * @param msg Error message (can be null)
     * @param ex Exception (can be null).
     */
    public static void handleException(String msg, Exception ex)
    {
        if (gui == null)
        {   
            if (ex == null)
            {   
                throw new IllegalStateException("gui == null. Original error: " + msg);
            }
            else
            {   
                throw new RuntimeException("Could not handle exception properly. See causal exception.", ex);
            }
        }
        gui.error("Exception!");
        if (msg != null)
        {
            gui.error(msg);
        }
        if (ex != null)
        {   
            gui.error(ex.toString());

            // getMessage() sometimes returns null, so just 
            // go after the stack trace.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            ex.printStackTrace(pw);
            pw.flush();
            sw.flush();
            gui.error(sw.toString());
        }
    }

    /**
     * Set debug mode.
     * @param value New value for debug mode.
     * @return Previous debug mode.
     */
    static public boolean setDebug(boolean value)
    {
        boolean oldValue = debug;
        debug = value;
        return oldValue;
    }

    /**
     * Log a message.
     * @param msg Message to log.
     */
    static public void message (String msg)
    {
        if (msg == null)
        {
            throw new IllegalArgumentException("msg == null");
        }
        if (gui == null)
        {
            throw new IllegalStateException("gui == null. Original msg: " + msg);
        }
        gui.setStatus(msg);
    }

    /**
     * Log a warning.
     * @param msg Warning message.
     */
    static public void warning (String msg)
    {
        if (msg == null)
        {
            throw new IllegalArgumentException("msg == null");
        }
        message(msg);
    }

    /**
     * Log a debugging message.
     * @param msg Message to log.
     */
    static public void debugMessage(String msg)
    {

        if (msg == null)
        {
            throw new IllegalArgumentException("msg == null");
        }
        if (gui == null)
        {
            throw new IllegalStateException("gui == null. Original msg: " + msg);
        }
        gui.debugMessage(msg);
    }

    /**
     * Set the GUI object to use for displaying messages.
     *
     * @param newGUI GUI object to use.
     */
    static public void setGUI(GridShibCAClientView newGUI)
    {
        gui = newGUI;
    }
}
