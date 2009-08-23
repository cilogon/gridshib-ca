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
