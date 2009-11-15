package edu.ncsa.gridshib.gridshibca;
/*
BrowserInterface.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/

import java.net.URL;
import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

/**
 * Class for controlling the user's browser.
 */
public class BrowserInterface
{

    /**
     * Direct the browser to open the given URL.
     * @param url URL to open.
     * @throws javax.jnlp.UnavailableServiceException
     */
    public static void openURL(URL url)
        throws UnavailableServiceException
    {
        String basicService = "javax.jnlp.BasicService";
        BasicService bs = null;

        // Lookup the javax.jnlp.BasicService object
        bs = (BasicService) ServiceManager.lookup(basicService);

        if (bs.showDocument(url) == false)
        {
            throw new UnavailableServiceException("Failed to redirect to " + url +
                    ": showDocument() failed." +
                    " (isOffline = " + bs.isOffline() +
                    " isWebBrowserSupported = " +
                    bs.isWebBrowserSupported() + ")");
        }
    }
}
