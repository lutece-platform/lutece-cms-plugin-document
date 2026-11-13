/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.workflow.DocumentAction;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;

/**
 * Renders, with the HTML auto-escaping of the core (LUT-33153), the document templates that write HTML built in Java:
 * the attribute, category, page template and metadata forms, the spaces tree and browser, the XSL output of a document,
 * the publishing lists and the rule descriptions are marked with HtmlMarkup where they are put in the model and keep
 * their tags, while the text typed by a user is escaped once. The templates whose captured block is compared, whose
 * links called JavaScript or whose attributes were a string render too.
 */
public class DocumentHtmlFragmentsRenderTest extends LuteceTestCase
{
    private static final String FRAGMENT = "<div class=\"fragment\"><input name=\"x\" value=\"a &lt;b&gt;\"></div>";
    private static final String FRAGMENT_ESCAPED = "&lt;div class=&quot;fragment&quot;&gt;";
    private static final String TEXT = "Dupont <b>\"&'";
    private static final String TEXT_ESCAPED = "Dupont &lt;b&gt;&quot;&amp;&#39;";
    private static final String TEXT_UNESCAPED = "Dupont <b>";
    private static final int ID_DOCUMENT = 43;

    /**
     * Renders a template with the core configuration.
     *
     * @param strTemplate
     *            the template path
     * @param model
     *            the model
     * @return the HTML
     */
    private static String render( String strTemplate, Map<String, Object> model )
    {
        return AppTemplateService.getTemplate( strTemplate, Locale.FRENCH, model ).getHtml( );
    }

    /**
     * A document whose title, summary and comment hold markup characters
     *
     * @return the document
     */
    private static Document document( )
    {
        Document document = new Document( );
        document.setId( ID_DOCUMENT );
        document.setLocale( Locale.FRENCH );
        document.setTitle( TEXT );
        document.setSummary( TEXT );
        document.setComment( "" );
        document.setType( "article" );
        document.setStateKey( "document.manage_documents.labelState" );
        document.setDateCreation( new Timestamp( 0 ) );
        document.setDateModification( new Timestamp( 0 ) );
        DocumentAction action = new DocumentAction( );
        action.setIdAction( 2 );
        action.setLocale( Locale.FRENCH );
        action.setNameKey( "document.manage_documents.buttonCreate" );
        action.setDescriptionKey( "document.manage_documents.buttonCreate" );
        action.setUrl( "jsp/admin/plugins/document/ModifyDocument.jsp?" );
        action.setIconUrl( "edit" );
        List<DocumentAction> listActions = new ArrayList<>( );
        listActions.add( action );
        document.setActions( listActions );
        return document;
    }

    /**
     * The fields, the categories and the page templates of the creation form are written as markup, a String would print
     * as text
     */
    @Test
    public void testCreateDocumentFormKeepsItsTags( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "locale", "fr" );
        model.put( "document_type", "article" );
        model.put( "fields", HtmlMarkup.of( FRAGMENT ) );
        model.put( "category", HtmlMarkup.of( FRAGMENT ) );
        model.put( "page_template_list", HtmlMarkup.of( FRAGMENT ) );
        String strHtml = render( "admin/plugins/document/create_document.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertFalse( strHtml, strHtml.contains( FRAGMENT_ESCAPED ) );
        assertTrue( strHtml, strHtml.contains( "data-lutece-action=\"back\"" ) );

        model.put( "fields", FRAGMENT );
        strHtml = render( "admin/plugins/document/create_document.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT_ESCAPED ) );
    }

    /**
     * The modification form writes the fields and the metadata form as markup and the title and summary of the document
     * escaped once
     */
    @Test
    public void testModifyDocumentFormEscapesTheDocument( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "document", document( ) );
        model.put( "id_state", "" );
        model.put( "date_validity_begin", "" );
        model.put( "date_validity_end", "" );
        model.put( "fields", HtmlMarkup.of( FRAGMENT ) );
        model.put( "category", HtmlMarkup.of( "" ) );
        model.put( "page_template_list", HtmlMarkup.of( "" ) );
        model.put( "metadata_form", HtmlMarkup.of( FRAGMENT ) );
        String strHtml = render( "admin/plugins/document/modify_document.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( TEXT_ESCAPED ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
        assertFalse( strHtml, strHtml.contains( "&amp;lt;" ) );
    }

    /**
     * The preview of a document is the output of the admin stylesheet, written as markup
     */
    @Test
    public void testPreviewKeepsTheStylesheetOutput( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "document", document( ) );
        model.put( "preview", HtmlMarkup.of( FRAGMENT ) );
        String strHtml = render( "admin/plugins/document/preview_document.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( TEXT_ESCAPED ) );
        assertFalse( strHtml, strHtml.contains( "&lt;/span&gt;" ) );
    }

    /**
     * The page of a document writes the output of the content stylesheet and the list portlet as markup
     */
    @Test
    public void testDocumentPageKeepsTheStylesheetOutput( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "document", HtmlMarkup.of( FRAGMENT ) );
        model.put( "portlet", HtmlMarkup.of( "<ul class=\"portlet\"></ul>" ) );
        model.put( "document_id", Integer.toString( ID_DOCUMENT ) );
        model.put( "portlet_id", "1" );
        model.put( "isExtendInstalled", false );
        for ( String strTemplate : List.of( "skin/plugins/document/document_content_service.html",
                "skin/plugins/document/page_templates/page_template_document1.html",
                "skin/plugins/document/page_templates/page_template_document2.html" ) )
        {
            String strHtml = render( strTemplate, model );
            assertTrue( strTemplate + strHtml, strHtml.contains( FRAGMENT ) );
            assertTrue( strTemplate + strHtml, strHtml.contains( "<ul class=\"portlet\"></ul>" ) );
        }
    }

    /**
     * The documents screen writes the spaces tree as markup; the link of a document title, a captured block, is
     * compared as a string and written once, the title escaped
     */
    @Test
    public void testManageDocumentsWritesTheTreeAndTheTitleLink( )
    {
        DocumentSpace space = new DocumentSpace( );
        space.setId( 1 );
        space.setName( TEXT );
        space.setDocumentCreationAllowed( false );
        List<Document> listDocuments = new ArrayList<>( );
        listDocuments.add( document( ) );
        Map<String, Object> model = new HashMap<>( );
        model.put( "spaces_tree", HtmlMarkup.of( FRAGMENT ) );
        model.put( "space", space );
        model.put( "space_actions_list", new ArrayList<>( ) );
        model.put( "documents_list", listDocuments );
        model.put( "document_types_filter_list", new ReferenceList( ) );
        model.put( "states_filter_list", new ReferenceList( ) );
        model.put( "view_types_list", new ReferenceList( ) );
        model.put( "document_types_list", new ReferenceList( ) );
        model.put( "child_spaces_list", new ArrayList<>( ) );
        model.put( "default_document_type", "" );
        model.put( "default_state", "" );
        model.put( "view_type", "detail" );
        model.put( "mass_action", false );
        model.put( "current_space_id", "1" );
        model.put( "nb_items_per_page", "10" );
        model.put( "paginator", new Paginator<>( listDocuments, 10, "jsp/admin/plugins/document/ManageDocuments.jsp", Paginator.PARAMETER_PAGE_INDEX, "1" ) );
        String strHtml = render( "admin/plugins/document/manage_documents.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( "id_document=" + ID_DOCUMENT + "&amp;id_action=2" ) );
        assertTrue( strHtml, strHtml.contains( TEXT_ESCAPED ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
        assertTrue( strHtml, strHtml.contains( "data-document=\"" + ID_DOCUMENT + " " + TEXT_ESCAPED + "\"" ) );
    }

    /**
     * The spaces browser writes no javascript: link: its links carry the action in a data attribute, and the URL of
     * the browser is a JavaScript string
     */
    @Test
    public void testSpacesBrowserLinksAreDataAttributes( )
    {
        DocumentSpace space = new DocumentSpace( );
        space.setId( 3 );
        space.setIdParent( 1 );
        DocumentSpace child = new DocumentSpace( );
        child.setId( 7 );
        child.setName( TEXT );
        child.setIconUrl( "images/space.png" );
        List<DocumentSpace> listSpaces = new ArrayList<>( );
        listSpaces.add( child );
        Map<String, Object> model = new HashMap<>( );
        model.put( "go_up", true );
        model.put( "space", space );
        model.put( "spaces_list", listSpaces );
        model.put( "has_childs", new int [ ] { 7 } );
        model.put( "action", "jsp/admin/plugins/document/MoveDocument.jsp?x='y" );
        for ( String strTemplate : List.of( "admin/plugins/document/spaces/browse_spaces.html",
                "admin/plugins/document/spaces/browse_spaces_for_file_selection.html" ) )
        {
            String strHtml = render( strTemplate, model );
            assertFalse( strTemplate + strHtml, strHtml.contains( "javascript:" ) );
            assertTrue( strTemplate + strHtml, strHtml.contains( "data-document-space-browser=\"open\"" ) );
            assertTrue( strTemplate + strHtml, strHtml.contains( "data-space-id=\"7\"" ) );
            assertTrue( strTemplate + strHtml, strHtml.contains( "MoveDocument.jsp?x=\\'y'" ) );
            assertTrue( strTemplate + strHtml, strHtml.contains( TEXT_ESCAPED ) );
            assertFalse( strTemplate + strHtml, strHtml.contains( TEXT_UNESCAPED ) );
        }
    }

    /**
     * The buttons of an internal link attribute declare their action in data attributes, read by
     * documentInternalLink.js
     */
    @Test
    public void testInternalLinkButtonsHaveNoHandler( )
    {
        DocumentAttribute attribute = new DocumentAttribute( );
        attribute.setCode( "link" );
        attribute.setName( TEXT );
        attribute.setTextValue( "<a href=\"jsp/site/Portal.jsp?page_id=1\">x</a>" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "attribute", attribute );
        for ( String strTemplate : List.of( "admin/plugins/document/attributes/create_internal_link.html",
                "admin/plugins/document/attributes/modify_internal_link.html" ) )
        {
            String strHtml = render( strTemplate, model );
            assertFalse( strTemplate + strHtml, strHtml.contains( "onclick" ) );
            assertTrue( strTemplate + strHtml, strHtml.contains( "data-document-internal-link=\"service\"" ) );
            assertTrue( strTemplate + strHtml, strHtml.contains( "data-code=\"link\"" ) );
            assertTrue( strTemplate + strHtml, strHtml.contains( "documentInternalLink.js" ) );
        }
    }

    /**
     * The link of the image of an image attribute is a lightbox for an image only, its title written escaped
     */
    @Test
    public void testModifyImageLinkAttributes( )
    {
        DocumentAttribute attribute = new DocumentAttribute( );
        attribute.setId( 5 );
        attribute.setCode( "photo" );
        attribute.setName( "Photo" );
        attribute.setTextValue( TEXT );
        attribute.setValueContentType( "image/png" );
        Document document = document( );
        Map<String, Object> model = new HashMap<>( );
        model.put( "attribute", attribute );
        model.put( "document", document );
        String strHtml = render( "admin/plugins/document/attributes/modify_image.html", model );
        assertTrue( strHtml, strHtml.contains( "data-title=\"" + TEXT_ESCAPED + "\"" ) );
        assertTrue( strHtml, strHtml.contains( "data-lightbox=\"image-document\"" ) );
        assertTrue( strHtml, strHtml.contains( "const attributeCode = 'photo';" ) );

        attribute.setValueContentType( "application/pdf" );
        strHtml = render( "admin/plugins/document/attributes/modify_image.html", model );
        assertFalse( strHtml, strHtml.contains( "data-lightbox" ) );
    }

    /**
     * The description of a rule is HTML the rule builds from escaped data: it is written as markup
     */
    @Test
    public void testRulesDescriptionsAreMarkup( )
    {
        Map<String, Object> rule = new HashMap<>( );
        rule.put( "id", 12 );
        List<Map<String, Object>> listRules = new ArrayList<>( );
        listRules.add( rule );
        Map<String, Object> mapDescriptions = new HashMap<>( );
        mapDescriptions.put( "12", HtmlMarkup.of( FRAGMENT ) );
        Map<String, Object> model = new HashMap<>( );
        model.put( "rules_list", listRules );
        model.put( "rules_descriptions", mapDescriptions );
        model.put( "rule_types_list", new ReferenceList( ) );
        String strHtml = render( "admin/plugins/document/rules/manage_rules.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( "DeleteRule.jsp?id_rule=12" ) );
    }

    /**
     * The lists of the documents assigned to and published in a portlet are written as markup
     */
    @Test
    public void testPublishingListsKeepTheirTags( )
    {
        Map<String, Object> portlet = new HashMap<>( );
        portlet.put( "id", 1 );
        portlet.put( "name", TEXT );
        portlet.put( "pageId", 1 );
        portlet.put( "status", 0 );
        Map<String, Object> model = new HashMap<>( );
        model.put( "portlet", portlet );
        model.put( "mode_publication", 0 );
        model.put( "assigned_document_list", HtmlMarkup.of( FRAGMENT ) );
        model.put( "published_document_list", HtmlMarkup.of( "<ul class=\"published\"></ul>" ) );
        String strHtml = render( "admin/plugins/document/publishing/manage_portlet_publishing.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( "<ul class=\"published\"></ul>" ) );
        assertTrue( strHtml, strHtml.contains( TEXT_ESCAPED ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
    }
}
