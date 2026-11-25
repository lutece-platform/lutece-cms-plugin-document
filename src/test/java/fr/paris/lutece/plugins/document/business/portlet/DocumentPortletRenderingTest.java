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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublication;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublicationHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.business.portlet.PortletTemplate;
import fr.paris.lutece.portal.business.portlet.PortletTemplateHome;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;

/**
 * Renders the document portlets with every FreeMarker template shipped with the plugin and registered in the core
 */
public class DocumentPortletRenderingTest extends LuteceTestCase
{
    private static final String PORTLET_NAME = "DocumentPortletRenderingTest portlet name";
    private static final String DOCUMENT_TITLE = "DocumentPortletRenderingTest title";
    private static final String DOCUMENT_SUMMARY = "DocumentPortletRenderingTest summary";
    private static final String DOCUMENT_BODY = "<p>DocumentPortletRenderingTest <em>body</em></p>";
    private static final String MARKER_LIST_PORTLET = "portlet-document-list";
    private static final String MARKER_DOCUMENT_PORTLET = "portlet portlet-document ";
    private static final String DEVICE_CLASS = "d-none d-md-block";
    private static final String TYPE_ARTICLE = "article";
    private static final int ATTRIBUTE_ARTICLE_BODY = 12;
    private static final int SPACE_ID = 1;
    private static final int UNKNOWN_TEMPLATE_ID = 99999;
    private static final int NB_LIST_TEMPLATES = 7;
    private static final int NB_DOCUMENT_TEMPLATES = 5;
    private static final Set<String> LIST_TEMPLATES_WITHOUT_SUMMARY = Set.of( "skin/plugins/document/portlet/document_list_portlet_actor.html" );
    private static final Set<String> DOCUMENT_TEMPLATES_WITH_BODY = Set.of( "skin/plugins/document/portlet/document_portlet.html",
            "skin/plugins/document/portlet/document_portlet_article.html" );
    private static final Set<String> DOCUMENT_TEMPLATES_WITHOUT_TITLE = Set.of( "skin/plugins/document/portlet/document_portlet_actor.html" );

    @Inject
    private DocumentListPortletHome _documentListPortletHome;

    @Inject
    private DocumentPortletHome _documentPortletHome;

    private Document _document;
    private DocumentListPortlet _listPortlet;
    private DocumentPortlet _documentPortlet;

    /**
     * Creates a validated article published in a document list portlet and in a document portlet
     */
    @BeforeEach
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        _document = new Document( );
        _document.setCodeDocumentType( TYPE_ARTICLE );
        _document.setTitle( DOCUMENT_TITLE );
        _document.setSummary( DOCUMENT_SUMMARY );
        _document.setValidatedTitle( DOCUMENT_TITLE );
        _document.setValidatedSummary( DOCUMENT_SUMMARY );
        _document.setSpaceId( SPACE_ID );
        _document.setStateId( DocumentState.STATE_VALIDATE );
        _document.setDateCreation( new Timestamp( System.currentTimeMillis( ) ) );
        _document.setDateModification( new Timestamp( System.currentTimeMillis( ) ) );

        DocumentAttribute body = new DocumentAttribute( );
        body.setId( ATTRIBUTE_ARTICLE_BODY );
        body.setTextValue( DOCUMENT_BODY );

        List<DocumentAttribute> listAttributes = new ArrayList<>( );
        listAttributes.add( body );
        _document.setAttributes( listAttributes );
        _document.setCategories( new ArrayList<>( ) );
        DocumentHome.create( _document );
        DocumentHome.validateAttributes( _document.getId( ) );

        _listPortlet = new DocumentListPortlet( );
        initPortlet( _listPortlet );
        _listPortlet.setDocumentTypeCode( TYPE_ARTICLE );
        _documentListPortletHome.create( _listPortlet );
        publish( _listPortlet );

        _documentPortlet = new DocumentPortlet( );
        initPortlet( _documentPortlet );
        _documentPortlet.setDocumentTypeCode( TYPE_ARTICLE );
        _documentPortletHome.create( _documentPortlet );
        publish( _documentPortlet );
    }

    /**
     * Removes the portlets, the publications and the document
     */
    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        if ( _listPortlet != null )
        {
            DocumentPublicationHome.removeFromPortletId( _listPortlet.getId( ) );
            _documentListPortletHome.remove( _listPortlet );
        }

        if ( _documentPortlet != null )
        {
            DocumentPublicationHome.removeFromPortletId( _documentPortlet.getId( ) );
            _documentPortletHome.remove( _documentPortlet );
        }

        if ( _document != null )
        {
            DocumentHome.remove( _document.getId( ) );
        }

        LocalVariables.remove( );
        super.tearDown( );
    }

    /**
     * The shipped templates are registered in the core for both portlet types
     */
    @Test
    public void testShippedTemplatesRegistered( )
    {
        assertEquals( NB_LIST_TEMPLATES, PortletTemplateHome.findByPortletType( DocumentListPortlet.RESOURCE_ID ).size( ) );
        assertEquals( NB_DOCUMENT_TEMPLATES, PortletTemplateHome.findByPortletType( DocumentPortlet.RESOURCE_ID ).size( ) );
    }

    /**
     * The template chosen for a portlet is stored by the core
     */
    @Test
    public void testTemplateStoredWithThePortlet( )
    {
        PortletTemplate template = PortletTemplateHome.findByPortletType( DocumentListPortlet.RESOURCE_ID ).get( 1 );
        _listPortlet.setIdTemplate( template.getId( ) );
        _listPortlet.update( );

        assertEquals( template.getId( ), PortletHome.findByPrimaryKey( _listPortlet.getId( ) ).getIdTemplate( ) );
        assertTrue( PortletTemplateHome.isTemplateUsed( template.getId( ) ) );
    }

    /**
     * Every document list template renders the portlet and the published document
     */
    @Test
    public void testRenderEveryListTemplate( )
    {
        MockHttpServletRequest request = initRequest( );

        for ( PortletTemplate template : PortletTemplateHome.findByPortletType( DocumentListPortlet.RESOURCE_ID ) )
        {
            String strContent = render( _listPortlet, template, request );

            assertPortlet( strContent, MARKER_LIST_PORTLET, template );

            if ( !LIST_TEMPLATES_WITHOUT_SUMMARY.contains( template.getTemplatePath( ) ) )
            {
                assertTrue( strContent.contains( DOCUMENT_SUMMARY ), template.getTemplatePath( ) + " should render the document summary" );
            }
        }
    }

    /**
     * Every document template renders the portlet and the published document
     */
    @Test
    public void testRenderEveryDocumentTemplate( )
    {
        MockHttpServletRequest request = initRequest( );

        for ( PortletTemplate template : PortletTemplateHome.findByPortletType( DocumentPortlet.RESOURCE_ID ) )
        {
            String strContent = render( _documentPortlet, template, request );

            assertPortlet( strContent, MARKER_DOCUMENT_PORTLET, template );

            if ( !DOCUMENT_TEMPLATES_WITHOUT_TITLE.contains( template.getTemplatePath( ) ) )
            {
                assertTrue( strContent.contains( DOCUMENT_TITLE ), template.getTemplatePath( ) + " should render the document title" );
            }

            if ( DOCUMENT_TEMPLATES_WITH_BODY.contains( template.getTemplatePath( ) ) )
            {
                assertTrue( strContent.contains( DOCUMENT_BODY ), template.getTemplatePath( ) + " should render the rich text body as is" );
            }
        }
    }

    /**
     * An unknown template falls back to the default template, and a hidden title is not rendered
     */
    @Test
    public void testFallbackToDefaultTemplate( )
    {
        MockHttpServletRequest request = initRequest( );
        _listPortlet.setIdTemplate( UNKNOWN_TEMPLATE_ID );
        _listPortlet.setDisplayPortletTitle( 1 );

        String strContent = _listPortlet.getHtmlContent( request );

        assertTrue( strContent.contains( MARKER_LIST_PORTLET ), "the default template should render the portlet wrapper" );
        assertTrue( strContent.contains( "document_id=" + _document.getId( ) ), "the default template should link the document" );
        assertFalse( strContent.contains( PORTLET_NAME ), "a hidden portlet title should not be rendered" );
    }

    /**
     * A portlet without published document renders its wrapper
     */
    @Test
    public void testRenderWithoutDocument( )
    {
        MockHttpServletRequest request = initRequest( );
        DocumentPublicationHome.removeFromPortletId( _documentPortlet.getId( ) );

        for ( PortletTemplate template : PortletTemplateHome.findByPortletType( DocumentPortlet.RESOURCE_ID ) )
        {
            String strContent = render( _documentPortlet, template, request );

            assertTrue( strContent.contains( MARKER_DOCUMENT_PORTLET ), template.getTemplatePath( ) + " should render the portlet wrapper" );
            assertFalse( strContent.contains( DOCUMENT_TITLE ), template.getTemplatePath( ) + " should not render a document" );
        }
    }

    /**
     * Initializes the common data of a test portlet
     *
     * @param portlet
     *            The portlet
     */
    private void initPortlet( Portlet portlet )
    {
        portlet.setPageId( PortalService.getRootPageId( ) );
        portlet.setStyleId( 0 );
        portlet.setColumn( 1 );
        portlet.setOrder( 1 );
        portlet.setName( PORTLET_NAME );
        portlet.setStatus( Portlet.STATUS_PUBLISHED );
        portlet.setDisplayPortletTitle( 0 );
        portlet.setDeviceDisplayFlags( Portlet.FLAG_DISPLAY_ON_NORMAL_DEVICE | Portlet.FLAG_DISPLAY_ON_LARGE_DEVICE | Portlet.FLAG_DISPLAY_ON_XLARGE_DEVICE );
    }

    /**
     * Publishes the test document in a portlet
     *
     * @param portlet
     *            The portlet
     */
    private void publish( Portlet portlet )
    {
        DocumentPublication publication = new DocumentPublication( );
        publication.setPortletId( portlet.getId( ) );
        publication.setDocumentId( _document.getId( ) );
        publication.setStatus( DocumentPublication.STATUS_PUBLISHED );
        publication.setDatePublishing( new Date( ) );
        publication.setDocumentOrder( 1 );
        DocumentPublicationHome.create( publication );
    }

    /**
     * Creates a request registered as the current request
     *
     * @return The request
     */
    private MockHttpServletRequest initRequest( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );

        return request;
    }

    /**
     * Renders a portlet with a template
     *
     * @param portlet
     *            The portlet
     * @param template
     *            The template
     * @param request
     *            The request
     * @return The HTML
     */
    private String render( PortletHtmlContent portlet, PortletTemplate template, MockHttpServletRequest request )
    {
        portlet.setIdTemplate( template.getId( ) );

        return portlet.getHtmlContent( request );
    }

    /**
     * Checks the common parts of a rendered portlet
     *
     * @param strContent
     *            The HTML
     * @param strMarker
     *            The CSS class of the portlet wrapper
     * @param template
     *            The template used
     */
    private void assertPortlet( String strContent, String strMarker, PortletTemplate template )
    {
        assertTrue( strContent.contains( strMarker ), template.getTemplatePath( ) + " should render the portlet wrapper" );
        assertTrue( strContent.contains( PORTLET_NAME ), template.getTemplatePath( ) + " should render the portlet title" );
        assertTrue( strContent.contains( DEVICE_CLASS ), template.getTemplatePath( ) + " should render the device classes" );
    }
}
