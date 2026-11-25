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
package fr.paris.lutece.plugins.document.business.portlet;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.plugins.document.service.view.DocumentTemplateService;
import fr.paris.lutece.plugins.document.service.view.DocumentView;
import fr.paris.lutece.plugins.document.service.view.DocumentViewService;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.util.AppPathService;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 *
 * DocumentPortlet
 *
 */
public class DocumentPortlet extends PortletHtmlContent
{
    public static final String RESOURCE_ID = "DOCUMENT_PORTLET";

    /** Template used when no template is chosen for the portlet */
    public static final String TEMPLATE_PORTLET_DEFAULT = "skin/plugins/document/portlet/document_portlet.html";

    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_DOCUMENT_TYPE = "document_type";
    private static final String MARK_DOCUMENT_CONTENT = "document_content";
    private static final String MARK_SITE_PATH = "site_path";
    private static final String MARK_IS_EXTEND_INSTALLED = "is_extend_installed";

    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    private String _strDocumentTypeCode;
    private int _nPortletId;
    private int [ ] _nArrayIdCategory;

    private static DocumentPortletHome _portletHome = CDI.current( ).select( DocumentPortletHome.class ).get( );
    
    /**
     * Sets the identifier of the portlet type to the value specified in the DocumentPortletHome class
     */
    public DocumentPortlet( )
    {
        setPortletTypeId( _portletHome.getPortletTypeId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        Map<String, Object> model = createPortletModel( );
        model.put( MARK_SITE_PATH, AppPathService.getPortalUrl( ) );
        model.put( MARK_IS_EXTEND_INSTALLED, PortalService.isExtendActivated( ) );

        PublishingService publishingService = CDI.current( ).select( PublishingService.class ).get( );
        Document document = publishingService.getFirstValidPublishedDocument( getId( ) );

        if ( document != null )
        {
            DocumentView view = CDI.current( ).select( DocumentViewService.class ).get( ).buildValidatedView( document );
            DocumentType type = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType( ) );
            DocumentTemplateService documentTemplateService = CDI.current( ).select( DocumentTemplateService.class ).get( );

            model.put( MARK_DOCUMENT, view );
            model.put( MARK_DOCUMENT_TYPE, type );
            model.put( MARK_DOCUMENT_CONTENT, documentTemplateService.renderContent( type, view, null, getLocale( request ) ) );
        }

        return renderTemplate( request, TEMPLATE_PORTLET_DEFAULT, model );
    }

    /**
     * Updates the current instance of the DocumentPortlet object
     */
    public void update( )
    {
    	
    	_portletHome.update( this );
    }

    /**
     * Removes the current instance of the DocumentPortlet object
     */
    @Override
    public void remove( )
    {
    	_portletHome.remove( this );
    }

    /**
     * Returns the nPortletId
     *
     * @return The nPortletId
     */
    public int getPortletId( )
    {
        return _nPortletId;
    }

    /**
     * Sets the IdPortlet
     *
     * @param nPortletId
     *            The nPortletId
     */
    public void setPortletId( int nPortletId )
    {
        _nPortletId = nPortletId;
    }

    /**
     * Sets the parent page identifier of the portlet to the value specified in parameter
     *
     * @param strDocumentTypeCode
     *            the code
     */
    public void setDocumentTypeCode( String strDocumentTypeCode )
    {
        _strDocumentTypeCode = strDocumentTypeCode;
    }

    /**
     * Returns the identifier of the parent page of the portlet
     *
     * @return the parent page identifier
     */
    public String getDocumentTypeCode( )
    {
        return _strDocumentTypeCode;
    }

    /**
     * @return the _nIdCategory
     */
    public int [ ] getIdCategory( )
    {
        return _nArrayIdCategory;
    }

    /**
     * @param arrayIdCategory
     *            the _nIdCategory to set
     */
    public void setIdCategory( int [ ] arrayIdCategory )
    {
        _nArrayIdCategory = arrayIdCategory;
    }
}
