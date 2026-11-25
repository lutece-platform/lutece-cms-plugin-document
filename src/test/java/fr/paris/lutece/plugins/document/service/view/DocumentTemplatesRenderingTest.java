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

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.service.spaces.SpaceNode;
import fr.paris.lutece.plugins.document.web.DocumentMetaPageInclude;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Version;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Renders the document templates shipped with the plugin (default templates and templates of the initial document types) with FreeMarker,
 * with the configurations used by Lutece: incompatible improvements 2.3.0 or 2.3.28, auto-escaping disabled or enabled.
 */
public class DocumentTemplatesRenderingTest
{
    private static final String PATH_TEMPLATES = "webapp/WEB-INF/templates";
    private static final String PATH_INIT_SQL = "src/sql/plugins/document/plugin/init_db_document.sql";
    private static final String MACRO_FILE = "util/document/macro_document_view.html";
    private static final String PATH_TEST_TEMPLATES = "src/test/resources/templates";
    private static final String FO_MACROS_STUB_FILE = "fo_macros_stub.ftl";
    private static final String SQL_STRING = "('(?:[^']|'')*'|NULL)";
    private static final Pattern PATTERN_TYPE = Pattern.compile( "INSERT INTO document_type \\([^)]*\\) VALUES \\('(\\w+)',(?:'(?:[^']|'')*',){2}\\d+,'(?:[^']|'')*',"
            + SQL_STRING + "," + SQL_STRING + ",", Pattern.DOTALL );
    private static final Pattern PATTERN_ATTRIBUTE = Pattern.compile(
            "INSERT INTO document_type_attr \\([^)]*\\) VALUES \\((\\d+),'(\\w+)','(\\w+)','(\\w+)','((?:[^']|'')*)','(?:[^']|'')*',(\\d+)," );

    private final DocumentTemplateService _templateService = new DocumentTemplateService( new DocumentViewService( new TestAttributeService( ) ) );

    /**
     * The FreeMarker configurations used by Lutece
     *
     * @return The incompatible improvements version and the auto-escaping flag
     */
    static Stream<Arguments> configurations( )
    {
        return Stream.of( Arguments.of( Configuration.VERSION_2_3_0, false ), Arguments.of( Configuration.VERSION_2_3_0, true ),
                Arguments.of( Configuration.VERSION_2_3_28, false ), Arguments.of( Configuration.VERSION_2_3_28, true ) );
    }

    /**
     * The default front office template escapes the texts, keeps the rich texts and displays the files
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @throws Exception
     *             if the rendering fails
     */
    @ParameterizedTest( name = "{0} auto-escape={1}" )
    @MethodSource( "configurations" )
    public void testDefaultContentTemplate( Version version, boolean bAutoEscape ) throws Exception
    {
        String strHtml = render( version, bAutoEscape, DocumentTemplateService.TEMPLATE_DEFAULT_CONTENT, null, buildType( ), buildDocument( true ) );

        assertTrue( strHtml.contains( "<strong>Title &lt;b&gt; &amp; co</strong>" ), strHtml );
        assertTrue( strHtml.contains( "Text &lt;script&gt;" ), strHtml );
        assertFalse( strHtml.contains( "<script>" ), strHtml );
        assertTrue( strHtml.contains( "<p>Rich <em>text</em></p>" ), strHtml );
        assertTrue( strHtml.contains( "<img src=\"servlet/plugins/document/resource?id=5&amp;id_attribute=3\"" )
                || strHtml.contains( "<img src=\"servlet/plugins/document/resource?id=5&id_attribute=3\"" ), strHtml );
        assertFalse( strHtml.contains( "document-attribute-missing" ), strHtml );
    }

    /**
     * The default back office template labels the fields and links the files of the working content
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @throws Exception
     *             if the rendering fails
     */
    @ParameterizedTest( name = "{0} auto-escape={1}" )
    @MethodSource( "configurations" )
    public void testDefaultAdminTemplate( Version version, boolean bAutoEscape ) throws Exception
    {
        String strHtml = render( version, bAutoEscape, DocumentTemplateService.TEMPLATE_DEFAULT_ADMIN, null, buildType( ), buildDocument( false ) );

        assertTrue( strHtml.contains( "#i18n{document.document_admin_default.labelTitle}" ), strHtml );
        assertTrue( strHtml.contains( "Text &lt;script&gt;" ), strHtml );
        assertTrue( strHtml.contains( "<p>Rich <em>text</em></p>" ), strHtml );
        assertTrue( strHtml.contains( "working_content=true" ), strHtml );
    }

    /**
     * The templates of the initial document types render a sample document of their type
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @throws Exception
     *             if the rendering fails
     */
    @ParameterizedTest( name = "{0} auto-escape={1}" )
    @MethodSource( "configurations" )
    public void testTemplatesOfTheInitialDocumentTypes( Version version, boolean bAutoEscape ) throws Exception
    {
        String strSql = Files.readString( Path.of( PATH_INIT_SQL ), StandardCharsets.UTF_8 );
        Map<String, DocumentType> mapTypes = readTypes( strSql );
        Matcher matcher = PATTERN_TYPE.matcher( strSql );
        int nTemplates = 0;

        while ( matcher.find( ) )
        {
            DocumentType type = mapTypes.get( matcher.group( 1 ) );

            for ( int nGroup = 2; nGroup <= 3; nGroup++ )
            {
                String strTemplate = sqlString( matcher.group( nGroup ) );

                if ( strTemplate != null )
                {
                    boolean bAdmin = nGroup == 2;
                    DocumentView view = _templateService.buildSampleView( type, !bAdmin );
                    String strHtml = render( version, bAutoEscape, null, strTemplate, type, view );

                    assertTrue( strHtml.contains( "Lorem ipsum" ), type.getCode( ) + " : " + strHtml );
                    nTemplates++;
                }
            }
        }

        assertEquals( 4, nTemplates );
    }

    /**
     * The sample view used to validate the templates has a value for every attribute
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @throws Exception
     *             if the rendering fails
     */
    @ParameterizedTest( name = "{0} auto-escape={1}" )
    @MethodSource( "configurations" )
    public void testSampleViewOfTheInitialDocumentTypes( Version version, boolean bAutoEscape ) throws Exception
    {
        DocumentType type = readTypes( Files.readString( Path.of( PATH_INIT_SQL ), StandardCharsets.UTF_8 ) ).get( "actor" );
        DocumentView view = _templateService.buildSampleView( type, true );

        assertTrue( view.getAttributes( ).get( "photo" ) instanceof FileAttributeValue );
        assertEquals( "Lorem ipsum", view.getAttributes( ).get( "firstname" ) );

        String strHtml = render( version, bAutoEscape, DocumentTemplateService.TEMPLATE_DEFAULT_CONTENT, null, type, view );
        assertTrue( strHtml.contains( "document-attribute-photo" ), strHtml );
    }

    /**
     * The meta tags template escapes the contents in the attributes, in the order of the meta tags
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @throws Exception
     *             if the rendering fails
     */
    @ParameterizedTest( name = "{0} auto-escape={1}" )
    @MethodSource( "configurations" )
    public void testMetaTagsTemplate( Version version, boolean bAutoEscape ) throws Exception
    {
        Map<String, String> mapMetaTags = new LinkedHashMap<>( );
        mapMetaTags.put( "DC.Title", "A \"title\" <b> & co" );
        mapMetaTags.put( "DC.Creator", "Mairie de Paris" );
        Map<String, Object> model = new HashMap<>( );
        model.put( DocumentMetaPageInclude.MARK_META_TAGS, mapMetaTags );

        String strHtml = render( version, bAutoEscape, DocumentMetaPageInclude.TEMPLATE_META, model );

        assertTrue( strHtml.contains( "<meta name=\"DC.Title\" content=\"A &quot;title&quot; &lt;b&gt; &amp; co\">" ), strHtml );
        assertTrue( strHtml.indexOf( "DC.Title" ) < strHtml.indexOf( "DC.Creator" ), strHtml );
    }

    /**
     * The spaces tree renders the markup of the luteceTree.js module: a space with children is a node with its icon, a space without children
     * is an item whose icon is added by the module, the current space is in bold and the space names are escaped
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @throws Exception
     *             if the rendering fails
     */
    @ParameterizedTest( name = "{0} auto-escape={1}" )
    @MethodSource( "configurations" )
    public void testSpacesTreeTemplate( Version version, boolean bAutoEscape ) throws Exception
    {
        String strHtml = renderSpacesTree( version, bAutoEscape, "1234" );

        assertTrue( strHtml.contains( "<div id=\"lutece-tree\" class=\"lutece-tree\">" ), strHtml );
        assertTrue( strHtml.contains( "<li class=\"lutece-tree-node\" id=\"node-0\" data-tree-icon=\"\">" ), strHtml );
        assertTrue( strHtml.contains( "<img src=\"icon-root.svg\" class=\"me-2\" alt=\"\">" ), strHtml );
        assertTrue( strHtml.contains( "<li class=\"lutece-tree-item\" id=\"node-1234\" data-tree-icon=\"icon-child.svg\">" ), strHtml );
        assertTrue( strHtml.contains( "ManageDocuments.jsp?id_space_filter=1234\"><strong>Child &amp; co</strong>" ), strHtml );
        assertTrue( strHtml.contains( "id_space_filter=0\">Root &lt;b&gt;</a>" ), strHtml );
        assertTrue( strHtml.indexOf( "node-0\"" ) < strHtml.indexOf( "node-1234" ), strHtml );
    }

    /**
     * The root space is opened by the template when it is the current space, since the luteceTree.js module never selects it
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @throws Exception
     *             if the rendering fails
     */
    @ParameterizedTest( name = "{0} auto-escape={1}" )
    @MethodSource( "configurations" )
    public void testSpacesTreeRootOpened( Version version, boolean bAutoEscape ) throws Exception
    {
        String strHtml = renderSpacesTree( version, bAutoEscape, "0" );

        assertTrue( strHtml.contains( "<li class=\"lutece-tree-node lutece-tree-node-open\" id=\"node-0\"" ), strHtml );
    }

    /**
     * The tree to select a space carries, on each radio button, the document types allowed in the space and whether documents can be created
     * in it, so that a page can disable the spaces that do not accept its document
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @throws Exception
     *             if the rendering fails
     */
    @ParameterizedTest( name = "{0} auto-escape={1}" )
    @MethodSource( "configurations" )
    public void testSpacesTreeSelectTemplate( Version version, boolean bAutoEscape ) throws Exception
    {
        SpaceNode root = new SpaceNode( 0, "Root", "", "" );
        SpaceNode child = new SpaceNode( 1234, "Child", "", "" );
        child.getAllowedDocumentTypes( ).addAll( List.of( "article", "image" ) );
        child.setDocumentCreationAllowed( true );
        root.getChildren( ).add( child );
        Map<String, Object> model = new HashMap<>( );
        model.put( "nodes", List.of( root ) );
        StringTemplateLoader stringLoader = new StringTemplateLoader( );
        stringLoader.putTemplate( "source", "<#macro icon style=''></#macro><#include \"/admin/plugins/document/spaces/spaces_tree_select.html\" />"
                + "<@spacesTreeSelect id='tree' nodes=nodes selectedId='1234' />" );

        String strHtml = render( version, bAutoEscape, "source", model, stringLoader );

        assertTrue( strHtml.contains( "id=\"tree_space_0\" value=\"0\" data-document-types=\"\" data-document-creation-allowed=\"false\"" ), strHtml );
        assertTrue( strHtml.contains( "id=\"tree_space_1234\" value=\"1234\" data-document-types=\"article,image\" data-document-creation-allowed=\"true\" checked" ),
                strHtml );
    }

    /**
     * Renders the spaces tree of a root space with one child space
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @param strCurrentSpaceId
     *            The identifier of the current space
     * @return The HTML
     * @throws Exception
     *             if the rendering fails
     */
    private String renderSpacesTree( Version version, boolean bAutoEscape, String strCurrentSpaceId ) throws Exception
    {
        SpaceNode root = new SpaceNode( 0, "Root <b>", "", "icon-root.svg" );
        root.getChildren( ).add( new SpaceNode( 1234, "Child & co", "", "icon-child.svg" ) );
        Map<String, Object> model = new HashMap<>( );
        model.put( "spaces_tree_nodes", List.of( root ) );
        model.put( "current_space_id", strCurrentSpaceId );

        return render( version, bAutoEscape, "admin/plugins/document/spaces/spaces_tree.html", model );
    }

    /**
     * Every template of the plugin is syntactically valid FreeMarker (the macros are resolved at rendering time, not checked here)
     *
     * @throws Exception
     *             if a template can not be read
     */
    @Test
    public void testTemplatesSyntax( ) throws Exception
    {
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_28 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setTemplateLoader( new FileTemplateLoader( new File( PATH_TEMPLATES ) ) );
        List<Path> listTemplates;

        try ( Stream<Path> stream = Files.walk( Path.of( PATH_TEMPLATES ) ) )
        {
            listTemplates = stream.filter( path -> path.toString( ).endsWith( ".html" ) ).collect( Collectors.toList( ) );
        }

        assertFalse( listTemplates.isEmpty( ) );

        for ( Path path : listTemplates )
        {
            String strName = Path.of( PATH_TEMPLATES ).relativize( path ).toString( ).replace( '\\', '/' );
            cfg.getTemplate( strName );
        }
    }

    /**
     * Renders a template file or a template source with the configuration of Lutece
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @param strTemplateFile
     *            The path of the template file, or null
     * @param strTemplateSource
     *            The template source, used when there is no file
     * @param type
     *            The document type
     * @param view
     *            The document view
     * @return The HTML
     * @throws Exception
     *             if the rendering fails
     */
    private String render( Version version, boolean bAutoEscape, String strTemplateFile, String strTemplateSource, DocumentType type,
            DocumentView view ) throws Exception
    {
        StringTemplateLoader stringLoader = new StringTemplateLoader( );
        stringLoader.putTemplate( "source", strTemplateSource == null ? "" : strTemplateSource );

        Map<String, Object> model = new HashMap<>( );
        model.put( DocumentTemplateService.MARK_DOCUMENT, view );
        model.put( DocumentTemplateService.MARK_DOCUMENT_TYPE, type );
        model.put( DocumentTemplateService.MARK_SITE_LOCALE, "fr" );
        model.put( DocumentTemplateService.MARK_REQUEST_PARAMETERS, new HashMap<String, String>( ) );

        return render( version, bAutoEscape, strTemplateFile != null ? strTemplateFile : "source", model, stringLoader );
    }

    /**
     * Renders a template file with the configuration of Lutece
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @param strTemplateFile
     *            The path of the template file
     * @param model
     *            The model
     * @return The HTML
     * @throws Exception
     *             if the rendering fails
     */
    private String render( Version version, boolean bAutoEscape, String strTemplateFile, Map<String, Object> model ) throws Exception
    {
        return render( version, bAutoEscape, strTemplateFile, model, new StringTemplateLoader( ) );
    }

    /**
     * Renders a template with the configuration of Lutece
     *
     * @param version
     *            The incompatible improvements version
     * @param bAutoEscape
     *            true if auto-escaping is enabled
     * @param strTemplate
     *            The name of the template
     * @param model
     *            The model
     * @param stringLoader
     *            The loader of the template sources
     * @return The HTML
     * @throws Exception
     *             if the rendering fails
     */
    private String render( Version version, boolean bAutoEscape, String strTemplate, Map<String, Object> model, StringTemplateLoader stringLoader )
            throws Exception
    {
        Configuration cfg = new Configuration( version );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                new FileTemplateLoader( new File( PATH_TEMPLATES ) ), new FileTemplateLoader( new File( PATH_TEST_TEMPLATES ) ), stringLoader
        } ) );
        cfg.addAutoInclude( FO_MACROS_STUB_FILE );
        cfg.addAutoInclude( MACRO_FILE );

        if ( bAutoEscape )
        {
            cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
            cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        }

        StringWriter writer = new StringWriter( );
        cfg.getTemplate( strTemplate ).process( model, writer );

        return writer.toString( );
    }

    /**
     * Builds a document type with a text, a rich text, an image and a missing attribute
     *
     * @return The document type
     */
    private DocumentType buildType( )
    {
        DocumentType type = new DocumentType( );
        type.setCode( "test" );
        type.addAttribute( buildTypeAttribute( 1, "text", "plain", 1 ) );
        type.addAttribute( buildTypeAttribute( 2, "richtext", "body", 2 ) );
        type.addAttribute( buildTypeAttribute( 3, "image", "photo", 3 ) );
        type.addAttribute( buildTypeAttribute( 4, "text", "missing", 4 ) );

        return type;
    }

    /**
     * Builds the view of a document of the test type
     *
     * @param bValidated
     *            true for the validated view
     * @return The view
     */
    private DocumentView buildDocument( boolean bValidated )
    {
        Document document = new Document( );
        document.setId( 5 );
        document.setCodeDocumentType( "test" );
        document.setTitle( "Title <b> & co" );
        document.setSummary( "Summary" );

        List<DocumentAttribute> listAttributes = new ArrayList<>( );
        listAttributes.add( buildValue( 1, "text", "plain", "Text <script>" ) );
        listAttributes.add( buildValue( 2, "richtext", "body", "<p>Rich <em>text</em></p>" ) );

        DocumentAttribute photo = buildValue( 3, "image", "photo", "photo.png" );
        photo.setBinary( true );
        photo.setValueContentType( "image/png" );
        listAttributes.add( photo );
        document.setAttributes( listAttributes );

        return new DocumentViewService( new TestAttributeService( ) ).buildView( document, bValidated );
    }

    /**
     * Reads the attributes of the initial document types from the SQL script
     *
     * @param strSql
     *            The SQL script
     * @return The document types by code
     */
    private Map<String, DocumentType> readTypes( String strSql )
    {
        Map<String, DocumentType> mapTypes = new HashMap<>( );
        Matcher matcher = PATTERN_ATTRIBUTE.matcher( strSql );

        while ( matcher.find( ) )
        {
            DocumentType type = mapTypes.computeIfAbsent( matcher.group( 2 ), strCode -> {
                DocumentType newType = new DocumentType( );
                newType.setCode( strCode );
                return newType;
            } );
            DocumentAttribute attribute = buildTypeAttribute( Integer.parseInt( matcher.group( 1 ) ), matcher.group( 3 ), matcher.group( 4 ),
                    Integer.parseInt( matcher.group( 6 ) ) );
            attribute.setName( matcher.group( 5 ).replace( "''", "'" ) );
            type.addAttribute( attribute );
        }

        return mapTypes;
    }

    /**
     * Returns the value of a SQL string literal
     *
     * @param strValue
     *            The literal, or NULL
     * @return The value, or null
     */
    private static String sqlString( String strValue )
    {
        return "NULL".equals( strValue ) ? null : strValue.substring( 1, strValue.length( ) - 1 ).replace( "''", "'" );
    }

    /**
     * Builds an attribute of a document type
     *
     * @param nId
     *            The attribute identifier
     * @param strType
     *            The attribute type code
     * @param strCode
     *            The attribute code
     * @param nOrder
     *            The attribute order
     * @return The attribute
     */
    private DocumentAttribute buildTypeAttribute( int nId, String strType, String strCode, int nOrder )
    {
        DocumentAttribute attribute = new DocumentAttribute( );
        attribute.setId( nId );
        attribute.setCodeAttributeType( strType );
        attribute.setCode( strCode );
        attribute.setName( strCode );
        attribute.setAttributeOrder( nOrder );

        return attribute;
    }

    /**
     * Builds an attribute of a document with its value
     *
     * @param nId
     *            The attribute identifier
     * @param strType
     *            The attribute type code
     * @param strCode
     *            The attribute code
     * @param strValue
     *            The text value
     * @return The attribute
     */
    private DocumentAttribute buildValue( int nId, String strType, String strCode, String strValue )
    {
        DocumentAttribute attribute = buildTypeAttribute( nId, strType, strCode, nId );
        attribute.setTextValue( strValue );

        return attribute;
    }
}
