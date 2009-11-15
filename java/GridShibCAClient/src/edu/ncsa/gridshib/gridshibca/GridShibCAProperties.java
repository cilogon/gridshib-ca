package edu.ncsa.gridshib.gridshibca;
/*
GridShibCAProperties.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

/**
 * Interface to our properties, both from properties file in our jar and from
 * any command line arguments.
 */
public class GridShibCAProperties
{

    /**
     * Name of resource containing our properties.
     */
    private final static String resourceName = "resources/properties";
    private static Properties properties = null;
    /**
     * Command line arguments we recognize (without shibsession, which is
     * special).
     */
    private final static String[] knownArguments =
    {
        "AuthenticationToken", // Required
        "WebAppURL", // URL to of GridShibCA (required)
        "DownloadCAs", // Download trusted CAs?
        "UseBundledCAs", // Use CAs in JWS jar to validate HTTPs connections.
        "RedirectURL", // URL to redirect browser to on success
        "TestLaunch", // Just test launch and exit
    };

    /**
     * Initialize our properties. Must be called before properties are
     * accessed.
     * @throws java.io.IOException
     */
    public static void init()
            throws java.io.IOException
    {
        Resource propertiesResource = new Resource(resourceName);
        properties = new Properties();

        // Defaults
        properties.setProperty("UseBundledCAs", "true");
        properties.setProperty("TestLaunch", "false");
        properties.setProperty("DownloadCAs", "true");
        
        // Load from our JWS Jar
        properties.load(propertiesResource.asStream());
    }

    /**
     * @param name Name of option to test for.
     * @return true if set and non-null, false otherwise
     */
    public static Boolean isSet(String name)
    {
        checkInit();
        String value = properties.getProperty(name);
        if (value == null)
        {
            // Not set
            return false;
        }
        return true;
    }

    /**
     * Get property with the given name.
     * @param name Name of property to retrieve.
     * @return Value of property as String
     * @throws IllegalArgumentException if property not found.
     */
    public static String getProperty(String name)
    {
        checkInit();
        if (name == null)
        {
            throw new IllegalArgumentException("null name");
        }
        if (properties == null)
        {
            throw new RuntimeException("getProperty() called before init()");
        }
        String value = properties.getProperty(name);
        if (value == null)
        {
            throw new IllegalArgumentException("Property \"" + name + "\" unknown.");
        }
        return value;
    }

    /**
     * Get property as Boolean.
     * @param name Name of property to retrieve.
     * @return Value of property as Boolean
     * @throws IllegalArgumentException if property not found.
     */
    public static Boolean getPropertyAsBoolean(String name)
    {
        return Boolean.valueOf(getProperty(name));
    }

    /**
     * Get property as Int.
     * @param name Name of property to retrieve.
     * @return Value of property as int
     * @throws IllegalArgumentException if property not found.
     */
    public static int getPropertyAsInt(String name)
    {
        Integer i = Integer.parseInt(getProperty(name));
        return i.intValue();
    }

    /**
     * Get property as URL.
     * @param name Name of property to retrieve.
     * @return Value of property as URL.
     * @throws IllegalArgumentException if property not found.
     * @throws java.net.MalformedURLException if URL parsing fails.
     */
    public static URL getPropertyAsURL(String name)
            throws java.net.MalformedURLException
    {
        return new URL(getProperty(name));
    }

    /**
     * Parse command line arguments and set properties based on those
     * arguments. These should all be of the form "variable=value".
     * 
     * Known variables are defined in knownArguments.
     *
     * @param args Arguments from the commandline.
     * @throws java.lang.IllegalArgumentException
     */
    public static void parseArguments(String[] args)
            throws IllegalArgumentException
    {
        int argIndex = 0;
        boolean argError = false;

        checkInit();

        // Convert array to Set for contains() method
        HashSet argumentSet = new HashSet(Arrays.asList(knownArguments));

        for (argIndex = 0; argIndex < args.length; argIndex++)
        {
            String arg = args[argIndex].trim();

            /*
             * If argument appears in argumentSet, add to
             * properties. Otherwise, it is an error.
             */

            // Split 'var=value' into var and value
            int equalIndex = arg.indexOf('=');
            if (equalIndex == -1)
            {
                throw new IllegalArgumentException(
                        "Failed to parse argument (no '='): " + arg);
            }
            String var = arg.substring(0, equalIndex).trim();
            String value = arg.substring(equalIndex + 1).trim();

            if (argumentSet.contains(var))
            {
                properties.setProperty(var, value);
            } else
            {
                throw new IllegalArgumentException(
                        "Unrecognized variable: " + var);
            }
        }
    }

    /**
     * Convert to string.
     * @return String containing all properties as comma-separated list.
     */
    public static String dumpToString()
    {
        checkInit();
        return properties.toString();
    }

    /**
     * Make sure we have been initialized and throw runtime exception otherwise.
     */
    private static void checkInit()
    {
        if (properties == null)
        {
            throw new RuntimeException("GridShibCAProperties has not been initialized.");
        }
    }
}
