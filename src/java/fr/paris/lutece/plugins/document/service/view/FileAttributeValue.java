/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.document.service.view;

import org.apache.commons.lang3.Strings;

import java.io.Serializable;

/**
 * Value of a file attribute, as exposed to the document templates
 */
public class FileAttributeValue implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String URL_RESOURCE_SERVLET = "servlet/plugins/document/resource?id=";
    private static final String URL_PARAMETER_ATTRIBUTE_ID = "&id_attribute=";
    private static final String URL_PARAMETER_WORKING_CONTENT = "&working_content=true";
    // The working content changes while the document is edited: the browser must not keep it (the servlet sends no-cache headers)
    private static final String URL_PARAMETER_NOCACHE = "&nocache";
    private static final String PREFIX_IMAGE_CONTENT_TYPE = "image/";

    private final int _nDocumentId;
    private final int _nAttributeId;
    private final String _strFileName;
    private final String _strContentType;
    private final boolean _bValidated;

    /**
     * Constructor
     *
     * @param nDocumentId
     *            The document identifier
     * @param nAttributeId
     *            The attribute identifier
     * @param strFileName
     *            The file name
     * @param strContentType
     *            The content type of the file
     * @param bValidated
     *            true if the file belongs to the validated content, false for the working content
     */
    public FileAttributeValue( int nDocumentId, int nAttributeId, String strFileName, String strContentType, boolean bValidated )
    {
        _nDocumentId = nDocumentId;
        _nAttributeId = nAttributeId;
        _strFileName = strFileName;
        _strContentType = strContentType;
        _bValidated = bValidated;
    }

    /**
     * Returns the document identifier
     *
     * @return The document identifier
     */
    public int getDocumentId( )
    {
        return _nDocumentId;
    }

    /**
     * Returns the attribute identifier
     *
     * @return The attribute identifier
     */
    public int getAttributeId( )
    {
        return _nAttributeId;
    }

    /**
     * Returns the file name
     *
     * @return The file name
     */
    public String getFileName( )
    {
        return _strFileName;
    }

    /**
     * Returns the content type of the file
     *
     * @return The content type
     */
    public String getContentType( )
    {
        return _strContentType;
    }

    /**
     * Tells if the file is an image
     *
     * @return true if the content type is an image type
     */
    public boolean isImage( )
    {
        return Strings.CI.startsWith( _strContentType, PREFIX_IMAGE_CONTENT_TYPE );
    }

    /**
     * Returns the relative URL of the file, served by the document resource servlet
     *
     * @return The URL of the file
     */
    public String getUrl( )
    {
        StringBuilder sbUrl = new StringBuilder( URL_RESOURCE_SERVLET ).append( _nDocumentId ).append( URL_PARAMETER_ATTRIBUTE_ID )
                .append( _nAttributeId );

        if ( !_bValidated )
        {
            sbUrl.append( URL_PARAMETER_WORKING_CONTENT ).append( URL_PARAMETER_NOCACHE );
        }

        return sbUrl.toString( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString( )
    {
        return getUrl( );
    }
}
