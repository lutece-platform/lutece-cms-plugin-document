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
package fr.paris.lutece.plugins.document.service.publishing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortlet;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublicationHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Renders the document selector of the portlet forms and applies selections to a document list portlet
 */
public class PortletDocumentSelectionServiceTest extends LuteceTestCase
{
    private static final String TYPE_ARTICLE = "article";
    private static final String TITLE_PREFIX = "PortletDocumentSelectionServiceTest ";
    private static final String TITLE_TO_ESCAPE = "<b>escaped</b>";
    private static final int SPACE_ID = 1;
    private static final int NB_DOCUMENTS = 3;

    @Inject
    private PortletDocumentSelectionService _selectionService;

    @Inject
    private PublishingService _publishingService;

    @Inject
    private DocumentListPortletHome _documentListPortletHome;

    private final List<Document> _listDocuments = new ArrayList<>( );
    private DocumentListPortlet _portlet;

    /**
     * Creates three validated articles and an empty document list portlet
     */
    @BeforeEach
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        for ( int i = 0; i < NB_DOCUMENTS; i++ )
        {
            Document document = new Document( );
            document.setCodeDocumentType( TYPE_ARTICLE );
            document.setTitle( TITLE_PREFIX + i );
            document.setValidatedTitle( ( i == 0 ) ? TITLE_TO_ESCAPE : ( TITLE_PREFIX + i ) );
            document.setSummary( TITLE_PREFIX );
            document.setSpaceId( SPACE_ID );
            document.setStateId( DocumentState.STATE_VALIDATE );
            document.setDateCreation( new Timestamp( System.currentTimeMillis( ) - ( NB_DOCUMENTS - i ) * 60000L ) );
            document.setDateModification( new Timestamp( System.currentTimeMillis( ) ) );
            document.setAttributes( new ArrayList<>( ) );
            document.setCategories( new ArrayList<>( ) );
            DocumentHome.create( document );
            _listDocuments.add( document );
        }

        _portlet = new DocumentListPortlet( );
        _portlet.setPageId( PortalService.getRootPageId( ) );
        _portlet.setColumn( 1 );
        _portlet.setOrder( 1 );
        _portlet.setName( TITLE_PREFIX );
        _portlet.setStatus( Portlet.STATUS_PUBLISHED );
        _portlet.setDocumentTypeCode( TYPE_ARTICLE );
        _portlet.setIdCategory( new int [ 0 ] );
        _documentListPortletHome.create( _portlet );
    }

    /**
     * Removes the portlet, its publications and the documents
     */
    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        if ( _portlet != null )
        {
            DocumentPublicationHome.removeFromPortletId( _portlet.getId( ) );
            _documentListPortletHome.remove( _portlet );
        }

        for ( Document document : _listDocuments )
        {
            DocumentHome.remove( document.getId( ) );
        }

        _listDocuments.clear( );
        _portlet = null;
        super.tearDown( );
    }

    /**
     * The ordered selector lists the selected documents in the order given, with escaped titles, and searches through the autocomplete
     */
    @Test
    public void testMultipleSelectorKeepsTheOrder( )
    {
        String strHtml = _selectionService.getDocumentSelect( List.of( _listDocuments.get( 2 ), _listDocuments.get( 0 ) ), true, Locale.FRENCH );

        int nFirst = strHtml.indexOf( "name=\"id_document\" value=\"" + id( 2 ) + "\"" );
        int nSecond = strHtml.indexOf( "name=\"id_document\" value=\"" + id( 0 ) + "\"" );
        assertTrue( nFirst > 0 );
        assertTrue( nSecond > nFirst );
        assertFalse( strHtml.contains( "name=\"id_document\" value=\"" + id( 1 ) + "\"" ) );
        assertTrue( strHtml.contains( "data-url=\"jsp/admin/plugins/document/SearchPortletDocuments.jsp\" data-multiple=\"true\"" ) );
        assertTrue( strHtml.contains( "id=\"document-browser-rows\"" ) );
        String strPick = strHtml.substring( strHtml.indexOf( "<template id=\"document-browser-pick\">" ), strHtml.indexOf( "<template id=\"document-browser-picked\">" ) );
        assertTrue( strPick.contains( "<button class=\"btn btn-sm btn-primary" ) );
        assertTrue( strHtml.contains( "ti-plus" ) );
        assertTrue( strHtml.contains( "Ajouter" ) );
        assertTrue( strHtml.contains( "<template id=\"document-item-template\">" ) );
        assertTrue( strHtml.contains( "data-drag-handle title=" ) );
        assertTrue( strHtml.contains( "new Sortable( selection" ) );
        assertTrue( strHtml.contains( "&lt;b&gt;escaped&lt;/b&gt;" ) );
        assertFalse( strHtml.contains( TITLE_TO_ESCAPE ) );
    }

    /**
     * The single selector holds the shown document, without the reordering controls
     */
    @Test
    public void testSingleSelector( )
    {
        String strHtml = _selectionService.getDocumentSelect( List.of( _listDocuments.get( 1 ) ), false, Locale.FRENCH );

        assertTrue( strHtml.contains( "name=\"id_document\" value=\"" + id( 1 ) + "\"" ) );
        assertTrue( strHtml.contains( "data-multiple=\"false\"" ) );
        assertTrue( strHtml.contains( "ti-check" ) );
        assertTrue( strHtml.contains( "Choisir" ) );
        assertTrue( strHtml.contains( ">" + TITLE_PREFIX + 1 + "<" ) );
        assertFalse( strHtml.contains( "data-drag-handle title=" ) );

        String strEmptyHtml = _selectionService.getDocumentSelect( List.of( ), false, Locale.FRENCH );
        assertFalse( strEmptyHtml.contains( "id=\"document-selection-empty\" class=\"text-muted\" hidden" ) );
    }

    /**
     * The documents are listed the last created first, by page, and filtered on the working and the validated titles ignoring case
     */
    @Test
    public void testSearchPages( )
    {
        List<Integer> listSpaceIds = List.of( SPACE_ID );
        List<Document> listAll = DocumentHome.findWithValidatedVersion( TYPE_ARTICLE, TITLE_PREFIX.toUpperCase( ), listSpaceIds, 0, 10 );
        assertEquals( List.of( id( 2 ), id( 1 ), id( 0 ) ), listAll.stream( ).map( Document::getId ).toList( ) );
        assertEquals( NB_DOCUMENTS, DocumentHome.countWithValidatedVersion( TYPE_ARTICLE, TITLE_PREFIX, listSpaceIds ) );

        List<Document> listSecondPage = DocumentHome.findWithValidatedVersion( TYPE_ARTICLE, TITLE_PREFIX, listSpaceIds, 1, 1 );
        assertEquals( List.of( id( 1 ) ), listSecondPage.stream( ).map( Document::getId ).toList( ) );

        List<Document> listFound = DocumentHome.findWithValidatedVersion( TYPE_ARTICLE, "ESCAPED", listSpaceIds, 0, 10 );
        assertEquals( List.of( id( 0 ) ), listFound.stream( ).map( Document::getId ).toList( ) );
        assertTrue( DocumentHome.findWithValidatedVersion( "unknown_type", "", listSpaceIds, 0, 10 ).isEmpty( ) );
        assertTrue( DocumentHome.findWithValidatedVersion( TYPE_ARTICLE, "", List.of( ), 0, 10 ).isEmpty( ) );
        assertTrue( DocumentHome.countWithValidatedVersion( TYPE_ARTICLE, "", listSpaceIds ) >= NB_DOCUMENTS );

        String strJson = _selectionService.toSearchJson( new PortletDocumentSelectionService.DocumentSearchPage( listFound, 2, 3, 41 ), Locale.FRENCH );
        assertTrue( strJson.startsWith( "{\"page\":2,\"pageCount\":3,\"total\":41,\"items\":[{\"id\":" + id( 0 ) + "," ) );
        assertTrue( strJson.contains( "\"title\":\"" + TITLE_TO_ESCAPE + "\"" ) );
        assertTrue( strJson.contains( "\"type\":\"" + TYPE_ARTICLE + "\"" ) );
        assertTrue( strJson.contains( "\"dateCreation\":\"" ) );
    }

    /**
     * Applying a selection publishes the documents in its order, and a later selection reorders and removes
     */
    @Test
    public void testApplySelectionOrder( )
    {
        int nPortletId = _portlet.getId( );

        _selectionService.applySelection( List.of( id( 2 ), id( 0 ), id( 1 ) ), Collections.emptySet( ), nPortletId );
        assertEquals( List.of( id( 2 ), id( 0 ), id( 1 ) ), publishedIds( ) );

        _selectionService.applySelection( List.of( id( 1 ), id( 2 ) ), Set.of( id( 0 ) ), nPortletId );
        assertEquals( List.of( id( 1 ), id( 2 ) ), publishedIds( ) );
        assertFalse( _publishingService.isAssigned( id( 0 ), nPortletId ) );
    }

    /**
     * Returns the identifier of a test document
     *
     * @param nIndex
     *            The index of the document
     * @return The identifier
     */
    private int id( int nIndex )
    {
        return _listDocuments.get( nIndex ).getId( );
    }

    /**
     * Returns the identifiers of the documents published in the test portlet, in their order
     *
     * @return The identifiers
     */
    private List<Integer> publishedIds( )
    {
        List<Integer> listIds = new ArrayList<>( );

        for ( Document document : _publishingService.getPublishedDocumentsByPortletId( _portlet.getId( ) ) )
        {
            listIds.add( document.getId( ) );
        }

        return listIds;
    }
}
