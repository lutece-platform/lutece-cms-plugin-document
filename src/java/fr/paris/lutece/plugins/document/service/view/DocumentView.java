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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Read-only view of a document content (working or validated), used as the model of the document templates.
 * It replaces the XML content of the document.
 */
public class DocumentView implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int _nId;
    private String _strCodeDocumentType;
    private String _strTitle;
    private String _strSummary;
    private Timestamp _dateValidityBegin;
    private Timestamp _dateValidityEnd;
    private boolean _bSkipPortlet;
    private boolean _bSkipCategories;
    private boolean _bValidated;
    private List<String> _listCategories = new ArrayList<>( );
    private Map<String, Object> _mapAttributes = new LinkedHashMap<>( );

    /**
     * Returns the document identifier
     *
     * @return The document identifier
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the document identifier
     *
     * @param nId
     *            The document identifier
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the document type code
     *
     * @return The document type code
     */
    public String getCodeDocumentType( )
    {
        return _strCodeDocumentType;
    }

    /**
     * Sets the document type code
     *
     * @param strCodeDocumentType
     *            The document type code
     */
    public void setCodeDocumentType( String strCodeDocumentType )
    {
        _strCodeDocumentType = strCodeDocumentType;
    }

    /**
     * Returns the title
     *
     * @return The title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Sets the title
     *
     * @param strTitle
     *            The title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Returns the summary
     *
     * @return The summary
     */
    public String getSummary( )
    {
        return _strSummary;
    }

    /**
     * Sets the summary
     *
     * @param strSummary
     *            The summary
     */
    public void setSummary( String strSummary )
    {
        _strSummary = strSummary;
    }

    /**
     * Returns the beginning of the validity period
     *
     * @return The beginning of the validity period, or null
     */
    public Timestamp getDateValidityBegin( )
    {
        return _dateValidityBegin;
    }

    /**
     * Sets the beginning of the validity period
     *
     * @param dateValidityBegin
     *            The beginning of the validity period
     */
    public void setDateValidityBegin( Timestamp dateValidityBegin )
    {
        _dateValidityBegin = dateValidityBegin;
    }

    /**
     * Returns the end of the validity period
     *
     * @return The end of the validity period, or null
     */
    public Timestamp getDateValidityEnd( )
    {
        return _dateValidityEnd;
    }

    /**
     * Sets the end of the validity period
     *
     * @param dateValidityEnd
     *            The end of the validity period
     */
    public void setDateValidityEnd( Timestamp dateValidityEnd )
    {
        _dateValidityEnd = dateValidityEnd;
    }

    /**
     * Tells if the portlet must be skipped when the document is displayed
     *
     * @return true if the portlet must be skipped
     */
    public boolean isSkipPortlet( )
    {
        return _bSkipPortlet;
    }

    /**
     * Sets if the portlet must be skipped when the document is displayed
     *
     * @param bSkipPortlet
     *            true if the portlet must be skipped
     */
    public void setSkipPortlet( boolean bSkipPortlet )
    {
        _bSkipPortlet = bSkipPortlet;
    }

    /**
     * Tells if the categories must be skipped when the document is displayed
     *
     * @return true if the categories must be skipped
     */
    public boolean isSkipCategories( )
    {
        return _bSkipCategories;
    }

    /**
     * Sets if the categories must be skipped when the document is displayed
     *
     * @param bSkipCategories
     *            true if the categories must be skipped
     */
    public void setSkipCategories( boolean bSkipCategories )
    {
        _bSkipCategories = bSkipCategories;
    }

    /**
     * Tells if this view shows the validated content or the working content
     *
     * @return true for the validated content, false for the working content
     */
    public boolean isValidated( )
    {
        return _bValidated;
    }

    /**
     * Sets if this view shows the validated content or the working content
     *
     * @param bValidated
     *            true for the validated content, false for the working content
     */
    public void setValidated( boolean bValidated )
    {
        _bValidated = bValidated;
    }

    /**
     * Returns the names of the document categories
     *
     * @return The category names
     */
    public List<String> getCategories( )
    {
        return _listCategories;
    }

    /**
     * Sets the names of the document categories
     *
     * @param listCategories
     *            The category names
     */
    public void setCategories( List<String> listCategories )
    {
        _listCategories = listCategories;
    }

    /**
     * Returns the attribute values, keyed by attribute code, in the attribute order of the document type. A value is a String, or a
     * {@link FileAttributeValue} for file attributes.
     *
     * @return The attribute values
     */
    public Map<String, Object> getAttributes( )
    {
        return _mapAttributes;
    }

    /**
     * Returns the files of the document (values of the file and image attributes), in the attribute order
     *
     * @return The files, may be empty
     */
    public List<FileAttributeValue> getFiles( )
    {
        return _mapAttributes.values( ).stream( ).filter( FileAttributeValue.class::isInstance ).map( FileAttributeValue.class::cast )
                .collect( Collectors.toList( ) );
    }

    /**
     * Sets the attribute values
     *
     * @param mapAttributes
     *            The attribute values, keyed by attribute code
     */
    public void setAttributes( Map<String, Object> mapAttributes )
    {
        _mapAttributes = mapAttributes;
    }
}
