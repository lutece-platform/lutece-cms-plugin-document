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
package fr.paris.lutece.plugins.document.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.service.docsearch.DocSearchItem;
import fr.paris.lutece.plugins.document.service.view.DocumentTemplateService;
import fr.paris.lutece.plugins.document.service.view.DocumentViewService;
import fr.paris.lutece.plugins.document.web.docsearch.AdvancedSearch;
import fr.paris.lutece.portal.service.portal.ThemesService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

/**
 * Renders the front office templates of the plugin with the front office macros of the core
 */
public class FrontOfficeTemplatesTest extends LuteceTestCase
{
    private static final String TEMPLATE_ADVANCED_SEARCH = "skin/plugins/document/advanced_search.html";
    private static final String TEMPLATE_CATEGORIES = "skin/plugins/document/document_categories.html";
    private static final String TEMPLATE_CONTENT_SERVICE = "skin/plugins/document/document_content_service.html";
    private static final String TEMPLATE_PRINT = "skin/plugins/document/page_print_document.html";
    private static final String TEMPLATE_PAGE_FULL = "skin/plugins/document/page_templates/page_template_document1.html";
    private static final String TEMPLATE_PAGE_SIDEBAR = "skin/plugins/document/page_templates/page_template_document2.html";
    private static final String TYPE_WITHOUT_TEMPLATE = "video";

    @Inject
    private DocumentTemplateService _documentTemplateService;

    @Inject
    private DocumentViewService _documentViewService;

    /**
     * The advanced search page keeps the criteria and links the results to their portlet
     */
    @Test
    public void testAdvancedSearchWithResults( )
    {
        DocSearchItem item = new DocSearchItem( new org.apache.lucene.document.Document( ) );
        item.setId( "1234" );
        item.setTitle( "Result title" );
        item.setSummary( "Result summary" );
        item.setType( "article" );
        Map<String, Object> result = new HashMap<>( );
        result.put( "docSearchItem", item );
        result.put( "idPortlet", 5678 );

        Map<String, Object> model = buildAdvancedSearchModel( );
        model.put( "query", "lorem" );
        model.put( "title", true );
        model.put( "selected_type", "article" );
        model.put( "date_query", "31/02/26" );
        model.put( "date_error", "Invalid date" );
        model.put( "content", List.of( result ) );

        String strHtml = render( TEMPLATE_ADVANCED_SEARCH, model );

        assertTrue( strHtml.contains( "http://localhost/jsp/site/Portal.jsp?page=advanced_search&amp;search_query=true" ), strHtml );
        assertTrue( strHtml.contains( "value=\"lorem\"" ), strHtml );
        assertTrue( strHtml.contains( "value=\"article\" selected" ) || strHtml.contains( "value=\"article\"  selected" ), strHtml );
        assertTrue( strHtml.contains( "Invalid date" ), strHtml );
        assertTrue( strHtml.contains( "document_id=1234&amp;portlet_id=5678" ), strHtml );
        assertTrue( strHtml.contains( "Result title" ), strHtml );
        assertFalse( strHtml.contains( "jquery" ), strHtml );
    }

    /**
     * The advanced search page without a query shows the form only
     */
    @Test
    public void testAdvancedSearchWithoutQuery( )
    {
        Map<String, Object> model = buildAdvancedSearchModel( );
        model.put( "selected_type", "-1" );

        String strHtml = render( TEMPLATE_ADVANCED_SEARCH, model );

        assertTrue( strHtml.contains( "id=\"advanced_search_form\"" ), strHtml );
        assertFalse( strHtml.contains( "document_id=" ), strHtml );
        assertTrue( strHtml.contains( ">Tous les types<" ), strHtml );
    }

    /**
     * The related documents link to the portlet they are published in
     */
    @Test
    public void testDocumentCategories( )
    {
        Document document = new Document( );
        document.setId( 1234 );
        document.setTitle( "Related document" );
        document.setSummary( "Related summary" );
        ReferenceList listPortlets = new ReferenceList( );
        listPortlets.addItem( "1234", "5678" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "document_categories_list", List.of( document ) );
        model.put( "portlet_id_list", listPortlets );

        String strHtml = render( TEMPLATE_CATEGORIES, model );

        assertTrue( strHtml.contains( "jsp/site/Portal.jsp?document_id=1234&amp;portlet_id=5678" ), strHtml );
        assertTrue( strHtml.contains( "target=\"_top\"" ), strHtml );
        assertTrue( strHtml.contains( "Related summary" ), strHtml );
    }

    /**
     * The default front office template renders a document of a type that has no specific template
     */
    @Test
    public void testDefaultContentTemplate( )
    {
        DocumentType type = DocumentTypeHome.findByPrimaryKey( TYPE_WITHOUT_TEMPLATE );
        Document document = new Document( );
        document.setId( 1234 );
        document.setCodeDocumentType( TYPE_WITHOUT_TEMPLATE );
        document.setTitle( "Video title" );
        document.setSummary( "Video summary" );
        document.setAttributes( new java.util.ArrayList<>( ) );

        String strHtml = _documentTemplateService.renderContent( type, _documentViewService.buildView( document, true ), null, Locale.FRENCH );

        assertTrue( strHtml.contains( "class=\"document document-video\"" ), strHtml );
        assertTrue( strHtml.contains( "Video title" ), strHtml );
        assertTrue( strHtml.contains( "Video summary" ), strHtml );
    }

    /**
     * The document page shows the portlet, the document and the extender markers
     */
    @Test
    public void testDocumentContentService( )
    {
        String strHtml = render( TEMPLATE_CONTENT_SERVICE, buildDocumentPageModel( ) );

        assertTrue( strHtml.contains( "id=\"content\"" ), strHtml );
        assertTrue( strHtml.contains( "<div>PORTLET</div>" ), strHtml );
        assertTrue( strHtml.contains( "<p>DOCUMENT</p>" ), strHtml );
        assertTrue( strHtml.contains( "@Extender[1234,document,comment]@" ), strHtml );
    }

    /**
     * The page templates that a document can use show the document, the portlet and the extender markers
     */
    @Test
    public void testPageTemplates( )
    {
        for ( String strTemplate : List.of( TEMPLATE_PAGE_FULL, TEMPLATE_PAGE_SIDEBAR ) )
        {
            String strHtml = render( strTemplate, buildDocumentPageModel( ) );

            assertTrue( strHtml.contains( "id=\"content\"" ), strTemplate + " : " + strHtml );
            assertTrue( strHtml.contains( "id=\"sidebar\"" ), strTemplate + " : " + strHtml );
            assertTrue( strHtml.contains( "<p>DOCUMENT</p>" ), strTemplate + " : " + strHtml );
            assertTrue( strHtml.contains( "<div>PORTLET</div>" ), strTemplate + " : " + strHtml );
            assertTrue( strHtml.contains( "@Extender[1234,document,actionbar]@" ), strTemplate + " : " + strHtml );
            assertTrue( strHtml.contains( "@Extender[1234,document,comment]@" ), strTemplate + " : " + strHtml );
        }

        assertTrue( render( TEMPLATE_PAGE_SIDEBAR, buildDocumentPageModel( ) ).contains( "col-sm-3" ) );
    }

    /**
     * The print page loads the theme styles and shows the print button and the document
     */
    @Test
    public void testPrintPage( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "base_url", "http://localhost/" );
        model.put( "preview", HtmlMarkup.of( "<p>PREVIEW</p>" ) );
        model.put( "portal_domain", "localhost" );
        model.put( "theme", ThemesService.getGlobalThemeObject( ) );

        String strHtml = render( TEMPLATE_PRINT, model );

        assertTrue( strHtml.contains( "<p>PREVIEW</p>" ), strHtml );
        assertTrue( strHtml.contains( "window.print()" ), strHtml );
        assertTrue( strHtml.contains( "/theme.min.css" ), strHtml );
        assertFalse( strHtml.contains( "jquery" ), strHtml );
    }

    /**
     * Builds the model common to the advanced search pages
     *
     * @return The model
     */
    private Map<String, Object> buildAdvancedSearchModel( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "url", "http://localhost/jsp/site/Portal.jsp" );
        model.put( "document_type_list", AdvancedSearch.getRefListDocumentType( Locale.FRENCH ) );
        model.put( "locale", Locale.FRENCH );

        return model;
    }

    /**
     * Builds the model of a document page, with the extend plugin installed
     *
     * @return The model
     */
    private Map<String, Object> buildDocumentPageModel( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "document", HtmlMarkup.of( "<p>DOCUMENT</p>" ) );
        model.put( "portlet", HtmlMarkup.of( "<div>PORTLET</div>" ) );
        model.put( "document_id", "1234" );
        model.put( "isExtendInstalled", true );

        return model;
    }

    /**
     * Renders a template file
     *
     * @param strTemplate
     *            The template path
     * @param model
     *            The model
     * @return The HTML
     */
    private String render( String strTemplate, Map<String, Object> model )
    {
        return AppTemplateService.getTemplate( strTemplate, Locale.FRENCH, model ).getHtml( );
    }
}
