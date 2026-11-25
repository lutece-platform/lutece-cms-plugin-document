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
package fr.paris.lutece.plugins.document.business;

import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.service.metadata.MetadataHandler;
import fr.paris.lutece.plugins.document.service.metadata.MetadataService;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the business object DocumentType
 */
public class DocumentType implements RBACResource, Serializable
{
	
    private static final long serialVersionUID = 1L;
    
    public static final String RESOURCE_TYPE = "DOCUMENT_TYPE";

    // Variables declarations
    private String _strCode;
    private String _strOldCode;
    private String _strName;
    private String _strDescription;
    private String _strAdminTemplate;
    private String _strContentTemplate;
    private ArrayList<DocumentAttribute> _listAttributes = new ArrayList<DocumentAttribute>( );
    private int _nThumbnailAttributeId;
    private String _strDefaultThumbnailUrl;
    private String _strMetadataHandler;

    /**
     * Returns the Code
     *
     * @return The Code
     */
    public String getCode( )
    {
        return _strCode;
    }

    /**
     * Sets the Code
     *
     * @param strCode
     *            The Code
     */
    public void setCode( String strCode )
    {
        _strCode = strCode;
    }

    /**
     * Returns the old code
     *
     * @return The old code
     */
    public String getOldCode( )
    {
        return _strOldCode;
    }

    /**
     * Sets the old code
     *
     * @param strCode
     *            The old code
     */
    public void setOldCode( String strOldCode )
    {
        _strOldCode = strOldCode;
    }

    /**
     * Returns the Name
     *
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     *
     * @param strName
     *            The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the Description
     *
     * @return The Description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     *
     * @param strDescription
     *            The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the FreeMarker template used to preview the document in the back office
     *
     * @return The template, or null to use the default template
     */
    public String getAdminTemplate( )
    {
        return _strAdminTemplate;
    }

    /**
     * Sets the FreeMarker template used to preview the document in the back office
     *
     * @param strAdminTemplate
     *            The template, or null to use the default template
     */
    public void setAdminTemplate( String strAdminTemplate )
    {
        _strAdminTemplate = strAdminTemplate;
    }

    /**
     * Returns the FreeMarker template used to display the document in the front office
     *
     * @return The template, or null to use the default template
     */
    public String getContentTemplate( )
    {
        return _strContentTemplate;
    }

    /**
     * Sets the FreeMarker template used to display the document in the front office
     *
     * @param strContentTemplate
     *            The template, or null to use the default template
     */
    public void setContentTemplate( String strContentTemplate )
    {
        _strContentTemplate = strContentTemplate;
    }

    /**
     * Add a document attribute to this type of document
     * 
     * @param documentAttribute
     *            The documentAttribute to add
     */
    public void addAttribute( DocumentAttribute documentAttribute )
    {
        _listAttributes.add( documentAttribute );
    }

    /**
     * Gets attributes list for the document type
     * 
     * @return The attrubutes list
     */
    public List<DocumentAttribute> getAttributes( )
    {
        return _listAttributes;
    }

    /**
     * Returns the ThumbnailAttributeId
     *
     * @return The ThumbnailAttributeId
     */
    public int getThumbnailAttributeId( )
    {
        return _nThumbnailAttributeId;
    }

    /**
     * Sets the ThumbnailAttributeId
     *
     * @param nThumbnailAttributeId
     *            The ThumbnailAttributeId
     */
    public void setThumbnailAttributeId( int nThumbnailAttributeId )
    {
        _nThumbnailAttributeId = nThumbnailAttributeId;
    }

    /**
     * Returns the DefaultThumbnailUrl
     *
     * @return The DefaultThumbnailUrl
     */
    public String getDefaultThumbnailUrl( )
    {
        return ( _strDefaultThumbnailUrl != null ) ? _strDefaultThumbnailUrl : "";
    }

    /**
     * Sets the DefaultThumbnailUrl
     *
     * @param strDefaultThumbnailUrl
     *            The DefaultThumbnailUrl
     */
    public void setDefaultThumbnailUrl( String strDefaultThumbnailUrl )
    {
        _strDefaultThumbnailUrl = strDefaultThumbnailUrl;
    }

    /**
     * RBAC resource implementation
     * 
     * @return The resource type code
     */
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * RBAC resource implementation
     * 
     * @return The resourceId
     */
    public String getResourceId( )
    {
        return getCode( );
    }

    /**
     * Returns the MetadataHandler name
     *
     * @return The MetadataHandler
     */
    public String getMetadataHandler( )
    {
        return _strMetadataHandler;
    }

    /**
     * Sets the MetadataHandler name
     *
     * @param strMetadataHandler
     *            The MetadataHandler
     */
    public void setMetadataHandler( String strMetadataHandler )
    {
        _strMetadataHandler = strMetadataHandler;
    }

    /**
     * Returns the metahandler
     * 
     * @return the metahandler
     */
    public MetadataHandler metadataHandler( )
    {
        MetadataHandler handler = null;

        if ( ( _strMetadataHandler != null ) && ( !_strMetadataHandler.equals( "" ) ) && ( !_strMetadataHandler.equals( MetadataService.NO_HANDLER ) ) )
        {
            String strBeanName = MetadataService.getBeanName( _strMetadataHandler );
            handler = CDI.current().select( MetadataHandler.class, NamedLiteral.of( strBeanName ) ).get( );
        }

        return handler;
    }
}
