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
        "token", // Authentication token (required)
        "url", // URL to of GridShibCA (required)
        "debug", // Turn on debugging if true
        "lifetime", // Credential lifetime in seconds (required)
        "trustURL", // URL from which to download trusted CAs
        "useBundledCAs", // Use CAs in JWS jar to validate HTTPs connections.
        "redirectURL",      // URL to redirect browser to on success
        "testLaunch", // Just test launch and exit
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
        properties.setProperty("debug", "false");
        properties.setProperty("useBundledCAs", "true");
        properties.setProperty("testLaunch", "false");
        
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
             * shibsession is a oddball since the variable name is
             * "_shibsession_" prefix with unpredictable token following,
             * so we match start and save whole argument as value.
             */
            if (arg.startsWith("_shibsession_"))
            {
                properties.setProperty("shibsession", arg);
                continue;
            }

            /*
             * Standard argument. If it appears in argumentSet, add to
             * properties. Otherwise, it is an error.
             */

            // Split 'var=value' into var and value
            int equalIndex = arg.indexOf('=');
            if (equalIndex == -1)
            {
                throw new IllegalArgumentException(
                        "Failed to parse argument (no '='): " + arg);
            }
            String var = arg.substring(0, equalIndex);
            String value = arg.substring(equalIndex + 1);

            if (argumentSet.contains(var))
            {
                properties.setProperty(var, value);
            } else
            {
                throw new IllegalArgumentException(
                        "Unrecognized variable in argument: " + arg);
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
