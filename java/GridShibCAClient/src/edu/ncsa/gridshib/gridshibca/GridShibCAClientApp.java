package edu.ncsa.gridshib.gridshibca;
/*
GridShibCAClientApp.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/
import java.io.IOException;
import java.net.URL;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class GridShibCAClientApp
        extends SingleFrameApplication
{
    // Our command-line arguments
    // We save these so we can parse them after GUI has started
    // up and we can present a better error to the user.

    private String[] arguments;

    // Our view
    private GridShibCAClientView view;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup()
    {
        view = new GridShibCAClientView(this);
        GridShibCAClientLogger.setGUI(view);
        show(view);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root)
    {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of GridShibCAClientApp
     */
    public static GridShibCAClientApp getApplication()
    {
        return Application.getInstance(GridShibCAClientApp.class);
    }

    /**
     * Main method launching the application.
     * @param args Command-line arguments (argv).
     */
    public static void main(String[] args)
    {
        launch(GridShibCAClientApp.class, args);
    }

    /**
     * Called before startup().
     * @param args Command-line arguments (argv).
     */
    @Override
    protected void initialize(String[] args)
    {
        arguments = args;
    }

    /**
     *  Called when application is ready to run.
     */
    @Override
    protected void ready()
    {
        /* Unthreaded version
        GridShibCACredentialRetriever retriever = new GridShibCACredentialRetriever(arguments, view);
        retriever.retrieveCredential();
        */
        GridShibCACredentialRetriever retrieverThread;
        view.debugMessage("Creating thread for credential retriever");
        retrieverThread = new GridShibCACredentialRetriever(arguments, view);
        retrieverThread.start();
        view.debugMessage("Credential retriever thread off and running.");
    }
}
