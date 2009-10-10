/*
 * GridShibCAClientApp.java
 */
package edu.ncsa.gridshib.gridshibca;

import java.io.IOException;
import java.net.URL;

/**
 * The main class of the application.
 */
public class GridShibCACredentialRetriever
        extends Thread
{
    // Our command-line arguments
    // We save these so we can parse them after GUI has started
    // up and we can present a better error to the user.

    private String[] arguments;

    // Our view
    private GridShibCAClientView view;

    public GridShibCACredentialRetriever(String[] args,
            GridShibCAClientView clientView)
    {
        arguments = args;
        view = clientView;
    }

    /**
     *  Called when application is ready to run.
     */
    @Override
    public void run()
    {
        debug("GridShibCA CredentialRetrieve thread started.");

        try
        {
            retrieveCredential();
        } catch (Exception e)
        {
            fatalError("Uncaught exception", e);
        }
    }

    public void retrieveCredential()
    {
        GridShibCACredentialIssuerURL credIssuerURL = null;
        Credential credential = null;
        String request = null;

        debug("Parsing arguments.");
        try
        {
            GridShibCAProperties.init();
            GridShibCAProperties.parseArguments(arguments);
        } catch (IOException ex)
        {
            fatalError("Error initializing properties", ex);
        }
        debug("Properties:\n" + GridShibCAProperties.dumpToString());

        if (GridShibCAProperties.getPropertyAsBoolean("TestLaunch"))
        {
            debug("TestLaunch is True");
            this.message("Test successful.");
            view.doneLabel.setEnabled(true);
            view.displaySuccess();
            view.enableExitButton();
            return;
        }
        try
        {
            validateProperties();
        } catch (IllegalArgumentException ex)
        {
            fatalError("Invalid augument state", ex);
        }

        debug("Retrieving Credential.");

        try
        {
            // Do this as early as possible
            debug("Initializing networking.");
            GridShibCAURL.init();
        } catch (Exception e)
        {
            fatalError("Error initializing networking", e);
            return;
        }

        debug("Establishing connection to GridShib-CA");

        try
        {
            URL gridshibCAURL = GridShibCAProperties.getPropertyAsURL("WebAppURL");
            String token = GridShibCAProperties.getProperty("AuthenticationToken");

            credIssuerURL = new GridShibCACredentialIssuerURL(gridshibCAURL,
                    token);
        } catch (java.net.MalformedURLException e)
        {
            fatalError("Error parseing GridShib-CA URL", e);
            return;
        } catch (java.io.IOException iOException)
        {
            fatalError("Error preparing to connect to server:",
                    iOException);
            return;
        }

        this.message("Requesting credential from GridShib-CA server.");
        try
        {
            credential = credIssuerURL.requestCredential();
        } catch (javax.net.ssl.SSLHandshakeException e)
        {
            fatalError("SSL error communicating with server. Unrecognized HTTPS certificate?", e);
            return;
        } catch (java.io.IOException iOException)
        {
            fatalError("Error communicating with server:",
                    iOException);
            return;
        } catch (Exception e) // A bunch of various errors with the crypto
        {
            fatalError("Error generating credentials", e);
            return;
        }

        this.message("Successfully received credential.");

        try
        {
            String proxyFile = null;

            proxyFile = credential.writeToDefaultProxyFile();
            this.debug("Credential written to: " + proxyFile);

        } catch (Exception e)
        {
            fatalError("Error writing credentials to local file", e);
            return;
        }
        this.message("Successfully wrote credential to disk.");
        view.displayCredentalInfo(credential);

        if (GridShibCAProperties.isSet("trustURL"))
        {
            GridShibCATrustRootsURL trustRootsURL;

            try
            {
                message("Retrieving trusted CAs.");
                URL trustURL = GridShibCAProperties.getPropertyAsURL("trustURL");
                String shibSession = GridShibCAProperties.getProperty("shibsession");
                trustRootsURL = new GridShibCATrustRootsURL(trustURL);
            } catch (java.net.MalformedURLException e)
            {
                error("Could not parse redirect URL", e);
                return;
            } catch (Exception e)
            {
                error("Error retrieving trusted CAs: " + e.toString(), e);
                return;
            }
        }

        if (GridShibCAProperties.isSet("redirectURL"))
        {
            URL redirectURL = null;
            try
            {
                redirectURL = GridShibCAProperties.getPropertyAsURL("redirectURL");
            } catch (java.net.MalformedURLException e)
            {
                error("Could not parse redirect URL", e);
                return;
            }

            this.message("Opening " + redirectURL.toString());
            try
            {
                debug("Redirecting browser to " + redirectURL.toString());
                BrowserInterface.openURL(redirectURL);
            } catch (Exception e)
            {
                error("Error redirecting browser: " + e.toString(), e);
                return;
            }
        }

        this.message("Credential successfully saved.");
        view.doneLabel.setEnabled(true);
        view.displaySuccess();
        view.enableExitButton();
    }

    /**
     * Display a message to the user.
     * @param msg Message to display.
     */
    private void message(String msg)
    {
        if (msg == null)
        {
            throw new IllegalArgumentException("null message");
        }
        view.setStatus(msg);
    }

    /**
     * Handle a debug message.
     * @param msg Message.
     */
    private void debug(String msg)
    {
        if (msg == null)
        {
            throw new IllegalArgumentException("null message");
        }
        view.debugMessage(msg);
    }

    /**
     * Handle an error message.
     * @param msg Message.
     */
    private void error(String msg)
    {
        error(msg, (Exception) null);
    }

    /**
     * Handle an error message along with causal exception.
     * @param msg Message.
     * @param ex Exception.
     */
    private void error(String msg, Exception ex)
    {
        view.error(msg, ex);
    }

    /**
     * Handle a fatal error. Does not return.
     * @param msg Message.
     */
    private void fatalError(String msg)
    {
        fatalError(msg, (Exception) null);
    }

    /**
     * Handle a fatal error with casual exeption. Does not return.
     * @param msg Message.
     * @param ex Exception.
     */
    private void fatalError(String msg, Exception ex)
    {
        error(msg, ex);
    }

    /**
     * Verify propertie are valid.
     * @throws IllegalArgumentException
     */
    private void validateProperties()
    {
        if (!GridShibCAProperties.isSet("WebAppURL"))
        {
            throw new IllegalArgumentException("Missing URL argument for GridShibCA Web Application.");
        }
        if (!GridShibCAProperties.isSet("AuthenticationToken"))
        {
            throw new IllegalArgumentException("Missing token argument.");
        }
    }
}
