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
package fr.paris.lutece.plugins.document.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.enterprise.inject.Vetoed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortlet;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublication;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublicationHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;

/**
 * Tests the access rules to the files of the documents
 */
public class DocumentResourceAccessServiceTest extends LuteceTestCase
{
    private static final String TYPE_ARTICLE = "article";
    private static final int ATTRIBUTE_ARTICLE_ATTACHMENT = 11;
    private static final int ATTRIBUTE_ARTICLE_BODY = 12;
    private static final int SPACE_ID = 1;
    private static final String ATTRIBUTE_ADMIN_USER = "lutece_admin_user";

    @Inject
    private DocumentResourceAccessService _service;

    @Inject
    private DocumentListPortletHome _portletHome;

    private Document _document;
    private Document _article;
    private DocumentListPortlet _portlet;

    /**
     * Creates a validated article with an attached file, and a document list portlet on the root page
     */
    @BeforeEach
    public void createData( )
    {
        _document = new Document( );
        _document.setCodeDocumentType( TYPE_ARTICLE );
        _document.setTitle( "DocumentResourceAccessServiceTest" );
        _document.setSpaceId( SPACE_ID );
        _document.setStateId( DocumentState.STATE_VALIDATE );
        _document.setDateCreation( new Timestamp( System.currentTimeMillis( ) ) );
        _document.setDateModification( new Timestamp( System.currentTimeMillis( ) ) );
        _document.setCategories( new ArrayList<>( ) );

        DocumentAttribute attachment = new DocumentAttribute( );
        attachment.setId( ATTRIBUTE_ARTICLE_ATTACHMENT );
        attachment.setBinary( true );
        attachment.setTextValue( "file.txt" );
        attachment.setBinaryValue( "content".getBytes( StandardCharsets.UTF_8 ) );
        attachment.setValueContentType( "text/plain" );

        List<DocumentAttribute> listAttributes = new ArrayList<>( );
        listAttributes.add( attachment );
        _document.setAttributes( listAttributes );
        DocumentHome.create( _document );
        DocumentHome.validateAttributes( _document.getId( ) );

        _portlet = new DocumentListPortlet( );
        _portlet.setPageId( PortalService.getRootPageId( ) );
        _portlet.setName( "DocumentResourceAccessServiceTest" );
        _portlet.setColumn( 1 );
        _portlet.setOrder( 1 );
        _portlet.setStatus( Portlet.STATUS_PUBLISHED );
        _portlet.setDocumentTypeCode( TYPE_ARTICLE );
        _portletHome.create( _portlet );
    }

    /**
     * Removes the data created by a test
     */
    @AfterEach
    public void removeData( )
    {
        DocumentPublicationHome.removeFromPortletId( _portlet.getId( ) );
        _portletHome.remove( _portlet );
        DocumentHome.remove( _document.getId( ) );

        if ( _article != null )
        {
            DocumentHome.remove( _article.getId( ) );
        }
    }

    /**
     * A visitor may download the validated files of a validated document inserted in the body of a published article, as an image of the
     * library
     */
    @Test
    public void testDocumentReferencedByPublishedDocument( )
    {
        _article = createArticle( "<p><img src=\"document?id=" + _document.getId( ) + "&amp;id_attribute=" + ATTRIBUTE_ARTICLE_ATTACHMENT + "\"></p>" );
        publish( _article, DocumentPublication.STATUS_PUBLISHED );

        assertTrue( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), false ) );
        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), true ), "the working content is not public" );
    }

    /**
     * A document referenced by a published article is refused when it is not validated
     */
    @Test
    public void testNotValidatedDocumentReferencedByPublishedDocument( )
    {
        _article = createArticle( "<p><img src=\"document?id=" + _document.getId( ) + "&amp;id_attribute=" + ATTRIBUTE_ARTICLE_ATTACHMENT + "\"></p>" );
        publish( _article, DocumentPublication.STATUS_PUBLISHED );
        _document.setStateId( DocumentState.STATE_WRITING );
        DocumentHome.update( _document, false );

        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), false ) );
    }

    /**
     * A document referenced by an article is refused when the article is not published, or only looks like a reference
     */
    @Test
    public void testDocumentReferencedByUnpublishedDocument( )
    {
        _article = createArticle( "<p><img src=\"document?id=" + _document.getId( ) + "&amp;id_attribute=" + ATTRIBUTE_ARTICLE_ATTACHMENT + "\"></p>" );
        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), false ), "the article is not published" );

        DocumentHome.remove( _article.getId( ) );
        _article = createArticle( "<p><img src=\"document?id=" + _document.getId( ) + "0&amp;id_attribute=" + ATTRIBUTE_ARTICLE_ATTACHMENT + "\"></p>" );
        publish( _article, DocumentPublication.STATUS_PUBLISHED );
        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), false ), "another id starting with the same digits" );
    }

    /**
     * A visitor may download the validated files of a published document, not its working files
     */
    @Test
    public void testPublishedDocumentForVisitor( )
    {
        publish( DocumentPublication.STATUS_PUBLISHED );

        assertTrue( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), false ) );
        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), true ), "the working content is not public" );
    }

    /**
     * A visitor may not download the files of a document that is not published
     */
    @Test
    public void testUnpublishedDocumentForVisitor( )
    {
        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), false ) );

        publish( DocumentPublication.STATUS_UNPUBLISHED );
        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), false ), "an unpublished publication is not public" );
    }

    /**
     * A visitor may not download the files of a document published in an unpublished portlet
     */
    @Test
    public void testUnpublishedPortlet( )
    {
        publish( DocumentPublication.STATUS_PUBLISHED );
        PortletHome.updateStatus( _portlet, Portlet.STATUS_UNPUBLISHED );

        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), false ) );
    }

    /**
     * A visitor may not download the files of a document out of its validity period
     */
    @Test
    public void testExpiredDocument( )
    {
        publish( DocumentPublication.STATUS_PUBLISHED );
        _document.setDateValidityEnd( new Timestamp( System.currentTimeMillis( ) - 172800000L ) );
        DocumentHome.update( _document, false );

        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), _document.getId( ), false ) );
    }

    /**
     * An unknown document is refused
     */
    @Test
    public void testUnknownDocument( )
    {
        assertFalse( _service.isAuthorized( new MockHttpServletRequest( ), -1, false ) );
    }

    /**
     * An administrator allowed to view the document may download its working files, even if it is not published
     */
    @Test
    public void testAdministrator( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId( ) ) );
        request.getSession( true ).setAttribute( ATTRIBUTE_ADMIN_USER, user );

        assertTrue( _service.isAuthorized( request, _document.getId( ), true ) );
        assertTrue( _service.isAuthorized( request, _document.getId( ), false ) );
    }

    /**
     * The servlet refuses the working file to a visitor, and sends the validated file of a published document
     *
     * @throws Exception
     *             if the servlet fails
     */
    @Test
    public void testServlet( ) throws Exception
    {
        publish( DocumentPublication.STATUS_PUBLISHED );

        RecordingResponse response = new RecordingResponse( );
        new TestServlet( ).get( buildRequest( true ), response );
        assertEquals( HttpServletResponse.SC_FORBIDDEN, response.getStatus( ) );

        response = new RecordingResponse( );
        new TestServlet( ).get( buildRequest( false ), response );
        assertEquals( HttpServletResponse.SC_OK, response.getStatus( ) );
        assertEquals( "content", response.getContentAsString( ) );
    }

    /**
     * Publishes the document in the portlet
     *
     * @param nStatus
     *            The publication status
     */
    private void publish( int nStatus )
    {
        publish( _document, nStatus );
    }

    /**
     * Publishes a document in the portlet
     *
     * @param document
     *            The document
     * @param nStatus
     *            The publication status
     */
    private void publish( Document document, int nStatus )
    {
        DocumentPublication publication = new DocumentPublication( );
        publication.setPortletId( _portlet.getId( ) );
        publication.setDocumentId( document.getId( ) );
        publication.setStatus( nStatus );
        publication.setDatePublishing( new Date( ) );
        publication.setDocumentOrder( 1 );
        DocumentPublicationHome.create( publication );
    }

    /**
     * Creates a validated article whose body holds a text
     *
     * @param strBody
     *            The HTML of the body
     * @return The article
     */
    private Document createArticle( String strBody )
    {
        Document article = new Document( );
        article.setCodeDocumentType( TYPE_ARTICLE );
        article.setTitle( "DocumentResourceAccessServiceTest article" );
        article.setSpaceId( SPACE_ID );
        article.setStateId( DocumentState.STATE_VALIDATE );
        article.setDateCreation( new Timestamp( System.currentTimeMillis( ) ) );
        article.setDateModification( new Timestamp( System.currentTimeMillis( ) ) );
        article.setCategories( new ArrayList<>( ) );

        DocumentAttribute body = new DocumentAttribute( );
        body.setId( ATTRIBUTE_ARTICLE_BODY );
        body.setTextValue( strBody );

        List<DocumentAttribute> listAttributes = new ArrayList<>( );
        listAttributes.add( body );
        article.setAttributes( listAttributes );
        DocumentHome.create( article );
        DocumentHome.validateAttributes( article.getId( ) );

        return article;
    }

    /**
     * Builds the request of the attached file
     *
     * @param bWorkingContent
     *            true to ask the working file
     * @return The request
     */
    private MockHttpServletRequest buildRequest( boolean bWorkingContent )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id", String.valueOf( _document.getId( ) ) );
        request.addParameter( "id_attribute", String.valueOf( ATTRIBUTE_ARTICLE_ATTACHMENT ) );

        if ( bWorkingContent )
        {
            request.addParameter( "working_content", "true" );
        }

        return request;
    }

    /**
     * Servlet exposing its GET handler to the test. Vetoed: it must not be a second CDI bean of the servlet type.
     */
    @Vetoed
    private static class TestServlet extends fr.paris.lutece.plugins.document.web.DocumentResourceServlet
    {
        private static final long serialVersionUID = 1L;

        /**
         * Handles a GET request
         *
         * @param request
         *            The request
         * @param response
         *            The response
         * @throws Exception
         *             if the servlet fails
         */
        void get( MockHttpServletRequest request, HttpServletResponse response ) throws Exception
        {
            doGet( request, response );
        }
    }

    /**
     * Mock response recording the status and ignoring the headers, which the library mock does not implement
     */
    private static class RecordingResponse extends MockHttpServletResponse
    {
        private int _nStatus = HttpServletResponse.SC_OK;

        /**
         * {@inheritDoc}
         */
        @Override
        public void sendError( int nStatus )
        {
            _nStatus = nStatus;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getStatus( )
        {
            return _nStatus;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setHeader( String strName, String strValue )
        {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setDateHeader( String strName, long lDate )
        {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setContentLength( int nLength )
        {
        }
    }
}
