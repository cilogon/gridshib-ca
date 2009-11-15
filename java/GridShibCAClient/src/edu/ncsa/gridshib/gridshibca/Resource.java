package edu.ncsa.gridshib.gridshibca;
/*
Resource.java

This file is part of the GridShib-CA distribution.

Copyright 2006-2009 The Board of Trustees of the University of Illinois.
Please see LICENSE at the root of the distribution.
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Interface to our resources embedded in our JWS Jar.
 */
public class Resource
{
    private String resourceName;

    /**
     * Create a new resource representing resource described by resourceName.
     * @param resourceName is the name of the resource.
     */
    public Resource(String resourceName)
    {
        if (resourceName == null)
        {
            throw new IllegalArgumentException("Null resource name");
        }
        // Verify resource exists
        URL url = this.getClassLoader().getResource(resourceName);
        if (url == null)
        {
            throw new IllegalArgumentException("Cannot find resource \"" + resourceName + "\"");
        }   
        this.resourceName = resourceName;
    }

    /**
     * Return URL associated with resource.
     * @return URL
     */
    public URL getURL()
    {
        return this.getClassLoader().getResource(this.resourceName);
    }

    /**
     * Return steam for reading resource
     * @return An input stream for reading the resource, or null if the resource could not be found.
     */
    public InputStream asStream()
    {
        return this.getClassLoader().getResourceAsStream(this.resourceName);
    }

    /**
     * Return our classloader. Note this has to be handled special for JWS
     * applications.
     * See:
     * http://java.sun.com/j2se/1.5.0/docs/guide/javaws/developersguide/faq.html#211
     * @return ClassLoader
     */
    private ClassLoader getClassLoader()
    {
        return this.getClass().getClassLoader();
    }

    /**
     * Read string from resource and return.
     * @return String from file.
     */
    private String read()
        throws IOException
    {
        InputStream inStream = getClassLoader().getResourceAsStream(this.resourceName);
        if (inStream == null)
        {
            throw new IOException("Resource not found: " + this.resourceName);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
        String string = in.readLine();
        inStream.close();
        return string;
    }
}
