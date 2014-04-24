/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


/**
 * This class represents the business object DocumentType
 */
public class DocumentType implements RBACResource
{
    public static final String RESOURCE_TYPE = "DOCUMENT_TYPE";
    private static final String TEMPLATE_ADMIN_DEFAULT_XSL = "/admin/plugins/document/document_admin_default_xsl.html";
    private static final String TEMPLATE_CONTENT_SERVICE_DEFAULT_XSL = "/skin/plugins/document/document_content_service_default_xsl.html";
    private static final String MARK_DOCUMENT_TYPE = "document_type";
    private static final int MODE_ADMIN = 1;

    //Style prefix Id
    private static final String STYLE_PREFIX_ID = UniqueIDGenerator.getNewId( ) + "document-";
    private static final String STYLE_PREFIX_DEFAULT = UniqueIDGenerator.getNewId( ) + "default-";
    private static final String STYLE_PREFIX_CONTENT_SERVICE = STYLE_PREFIX_ID + "content-";
    private static final String STYLE_PREFIX_ADMIN_SERVICE = STYLE_PREFIX_ID + "admin-";
    private static final String STYLE_PREFIX_DEFAULT_CONTENT_SERVICE = STYLE_PREFIX_CONTENT_SERVICE
            + STYLE_PREFIX_DEFAULT;
    private static final String STYLE_PREFIX_DEFAULT_ADMIN_SERVICE = STYLE_PREFIX_ADMIN_SERVICE + STYLE_PREFIX_DEFAULT;

    // Variables declarations
    private String _strCode;
    private String _strOldCode;
    private String _strName;
    private String _strDescription;
    private byte[] _baAdminXsl;
    private byte[] _baContentServiceXsl;
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
     * @param strCode The Code
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
     * @param strCode The old code
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
     * @param strName The Name
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
     * @param strDescription The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the Xsl for the Administration module
     * 
     * @return The Xsl
     */
    public byte[] getAdminXsl( )
    {
        return _baAdminXsl;
    }

    /**
     * Sets the Xsl for the Administration module
     * 
     * @param baXsl The Xsl
     */
    public void setAdminXsl( byte[] baXsl )
    {
        _baAdminXsl = baXsl;
    }

    /**
     * Returns the Xsl for the Document ContentService
     * 
     * @return The Xsl
     */
    public byte[] getContentServiceXsl( )
    {
        return _baContentServiceXsl;
    }

    /**
     * Sets the Xsl for the Document ContentService
     * 
     * @param baXsl The Xsl
     */
    public void setContentServiceXsl( byte[] baXsl )
    {
        _baContentServiceXsl = baXsl;
    }

    /**
     * Add a document attribute to this type of document
     * @param documentAttribute The documentAttribute to add
     */
    public void addAttribute( DocumentAttribute documentAttribute )
    {
        _listAttributes.add( documentAttribute );
    }

    /**
     * Gets attributes list for the document type
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
     * @param nThumbnailAttributeId The ThumbnailAttributeId
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
     * @param strDefaultThumbnailUrl The DefaultThumbnailUrl
     */
    public void setDefaultThumbnailUrl( String strDefaultThumbnailUrl )
    {
        _strDefaultThumbnailUrl = strDefaultThumbnailUrl;
    }

    /**
     * RBAC resource implementation
     * @return The resource type code
     */
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * RBAC resource implementation
     * @return The resourceId
     */
    public String getResourceId( )
    {
        return getCode( );
    }

    /**
     * Returns a default XSL stylesheet to display the document into the
     * backoffice
     * @return An XSL stylesheet as a String
     */
    private String getAdminDefaultXsl( )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_DOCUMENT_TYPE, this );

        HtmlTemplate template = AppTemplateService
                .getTemplate( TEMPLATE_ADMIN_DEFAULT_XSL, Locale.getDefault( ), model );

        return template.getHtml( );
    }

    /**
     * Returns a default XSL stylesheet to display the document into the
     * frontoffice
     * @return An XSL stylesheet as a String
     */
    private String getContentServiceDefaultXsl( )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_DOCUMENT_TYPE, this );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CONTENT_SERVICE_DEFAULT_XSL,
                Locale.getDefault( ), model );

        return template.getHtml( );
    }

    /**
     * Return the admin xsl source : if the admin xsl is null, a default one is
     * generated and returned
     * @return the xsl stylesheet as a source
     */
    public Source getAdminXslSource( )
    {
        Source xslSource;

        if ( getAdminXsl( ) != null )
        {
            xslSource = new StreamSource( new ByteArrayInputStream( getAdminXsl( ) ) );
        }
        else
        {
            StringReader xslStringReader = new StringReader( getAdminDefaultXsl( ) );
            xslSource = new StreamSource( xslStringReader );
        }

        return xslSource;
    }

    /**
     * Return the xsl source to display the document into the frontoffice :
     * if the admin xsl is null, a default one is generated and returned.
     * @return the xsl stylesheet as a source
     */
    public Source getContentServiceXslSource( )
    {
        Source xslSource;

        if ( ( getContentServiceXsl( ) != null ) && ( getContentServiceXsl( ).length > 0 ) )
        {
            xslSource = new StreamSource( new ByteArrayInputStream( getContentServiceXsl( ) ) );
        }
        else
        {
            StringReader xslStringReader = new StringReader( getContentServiceDefaultXsl( ) );
            xslSource = new StreamSource( xslStringReader );
        }

        return xslSource;
    }

    /**
     * Return the StyleSheet unique Id
     * @param nMode The current mode.
     * @return the StyleSheet unique Id
     */
    public String getStyleSheetId( int nMode )
    {
        String strResult = null;

        if ( nMode == MODE_ADMIN )
        {
            strResult = this.getAdminStyleSheetId( );
        }
        else
        {
            strResult = this.getContentServiceStyleSheetId( );
        }

        return strResult;
    }

    /**
     * Return the content service StyleSheet unique Id
     * @return The id
     */
    public String getContentServiceStyleSheetId( )
    {
        String strStyleSheetId;

        if ( ( getContentServiceXsl( ) != null ) && ( getContentServiceXsl( ).length > 0 ) )
        {
            strStyleSheetId = STYLE_PREFIX_CONTENT_SERVICE + this.getResourceId( );
        }
        else
        {
            strStyleSheetId = STYLE_PREFIX_DEFAULT_CONTENT_SERVICE;
        }

        return strStyleSheetId;
    }

    /**
     * Return the admin StyleSheet unique Id
     * @return The id
     */
    public String getAdminStyleSheetId( )
    {
        String strStyleSheetId;

        if ( ( getAdminXsl( ) != null ) && ( getAdminXsl( ).length > 0 ) )
        {
            strStyleSheetId = STYLE_PREFIX_ADMIN_SERVICE + this.getResourceId( );
        }
        else
        {
            strStyleSheetId = STYLE_PREFIX_DEFAULT_ADMIN_SERVICE;
        }

        return strStyleSheetId;
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
     * @param strMetadataHandler The MetadataHandler
     */
    public void setMetadataHandler( String strMetadataHandler )
    {
        _strMetadataHandler = strMetadataHandler;
    }

    /**
     * Returns the metahandler
     * @return the metahandler
     */
    public MetadataHandler metadataHandler( )
    {
        MetadataHandler handler = null;

        if ( ( _strMetadataHandler != null ) && ( !_strMetadataHandler.equals( "" ) )
                && ( !_strMetadataHandler.equals( MetadataService.NO_HANDLER ) ) )
        {
            String strBeanName = MetadataService.getBeanName( _strMetadataHandler );
            handler = SpringContextService.getBean( strBeanName );
        }

        return handler;
    }
}
