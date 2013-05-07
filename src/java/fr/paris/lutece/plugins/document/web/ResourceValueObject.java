/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.document.web;

import fr.paris.lutece.plugins.document.business.DocumentResource;

import java.io.Serializable;


/**
 * Resource Value Object
 */
public class ResourceValueObject implements Serializable
{
    private static final long serialVersionUID = -1354905008213695289L;
    private static final String DEFAULT_FILENAME = "document";

    // Variables declarations
    private int _nIdObject;
    private byte[] _content;
    private String _strContentType;
    private String _strFilename;
    private long _lLastModified;

    /**
     * Basic constructor
     */
    public ResourceValueObject(  )
    {
    }

    /**
     * Constructor using a document resource object
     * @param resource The document resource
     */
    public ResourceValueObject( DocumentResource resource )
    {
        _strFilename = resource.getName(  );

        if ( ( _strFilename == null ) || _strFilename.equals( "" ) )
        {
            _strFilename = DEFAULT_FILENAME;
        }

        _strContentType = resource.getContentType(  );
        _content = resource.getContent(  );
    }

    /**
     * Returns the IdObject
     * @return The IdObject
     */
    public int getIdObject(  )
    {
        return _nIdObject;
    }

    /**
     * Sets the IdObject
     * @param nIdObject The IdObject
     */
    public void setIdObject( int nIdObject )
    {
        _nIdObject = nIdObject;
    }

    /**
     * Returns the Content
     * @return The Content
     */
    public byte[] getContent(  )
    {
        return _content;
    }

    /**
     * Sets the Content
     * @param content The content
     */
    public void setContent( byte[] content )
    {
        _content = content;
    }

    /**
     * Returns the ContentType
     * @return The ContentType
     */
    public String getContentType(  )
    {
        return _strContentType;
    }

    /**
     * Sets the ContentType
     * @param strContentType The ContentType
     */
    public void setContentType( String strContentType )
    {
        _strContentType = strContentType;
    }

    /**
     * Returns the Filename
     * @return The Filename
     */
    public String getFilename(  )
    {
        return _strFilename;
    }

    /**
     * Sets the Filename
     * @param strFilename The Filename
     */
    public void setFilename( String strFilename )
    {
        _strFilename = strFilename;
    }

    /**
     * Sets the Last modified time
     * @param lLastModified Last modified time
     */
    public void setLastModified( long lLastModified )
    {
        _lLastModified = lLastModified;
    }

    /**
     * Returns the Last modified time
     * @return The Last modified time
     */
    public long getLastModified(  )
    {
        return _lLastModified;
    }
}
