package edu.ncsa.gridshib.gridshibca;
/*
GridShibCAClientLogger.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
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
