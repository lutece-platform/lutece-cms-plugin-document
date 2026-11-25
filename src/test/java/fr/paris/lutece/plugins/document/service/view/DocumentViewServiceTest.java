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
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.category.Category;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the view model that replaces the XML content of the documents
 */
public class DocumentViewServiceTest
{
    private static final int DOCUMENT_ID = 12;
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_RICHTEXT = "richtext";
    private static final String TYPE_FILE = "file";

    private final DocumentViewService _service = new DocumentViewService( new TestAttributeService( ) );

    /**
     * The attribute values are keyed by attribute code, in the attribute order, and the rich text is exposed as is
     */
    @Test
    public void testAttributesAreKeyedByCodeInAttributeOrder( )
    {
        Document document = buildDocument( );

        DocumentView view = _service.buildView( document, true );

        assertEquals( List.of( "body", "title2", "attachment" ), new ArrayList<>( view.getAttributes( ).keySet( ) ) );
        assertEquals( "Sub title", view.getAttributes( ).get( "title2" ) );
        assertEquals( "<p>Body</p>", view.getAttributes( ).get( "body" ) );
    }

    /**
     * A file attribute is exposed as a file, with the URL of the validated or working content
     */
    @Test
    public void testFileAttributeValue( )
    {
        DocumentView validatedView = _service.buildView( buildDocument( ), true );
        FileAttributeValue file = assertInstanceOf( FileAttributeValue.class, validatedView.getAttributes( ).get( "attachment" ) );

        assertTrue( file.isImage( ) );
        assertEquals( "photo.png", file.getFileName( ) );
        assertEquals( "servlet/plugins/document/resource?id=12&id_attribute=3", file.getUrl( ) );

        DocumentView workingView = _service.buildView( buildDocument( ), false );
        FileAttributeValue workingFile = (FileAttributeValue) workingView.getAttributes( ).get( "attachment" );
        assertEquals( "servlet/plugins/document/resource?id=12&id_attribute=3&working_content=true&nocache", workingFile.getUrl( ) );
    }

    /**
     * A file attribute without file is absent from the view
     */
    @Test
    public void testEmptyFileAttributeIsAbsent( )
    {
        Document document = buildDocument( );
        DocumentAttribute emptyFile = buildAttribute( 4, "empty", TYPE_FILE, 4, null );
        emptyFile.setBinary( false );
        emptyFile.setValueContentType( "" );
        document.getAttributes( ).add( emptyFile );

        DocumentView view = _service.buildView( document, true );

        assertFalse( view.getAttributes( ).containsKey( "empty" ) );
    }

    /**
     * The validated view uses the title and summary saved at validation time, the working view the current ones
     */
    @Test
    public void testValidatedViewUsesTheValidationSnapshot( )
    {
        Document document = buildDocument( );
        document.setValidatedTitle( "Published title" );
        document.setValidatedSummary( "Published summary" );

        DocumentView validatedView = _service.buildView( document, true );
        assertEquals( "Published title", validatedView.getTitle( ) );
        assertEquals( "Published summary", validatedView.getSummary( ) );
        assertTrue( validatedView.isValidated( ) );

        DocumentView workingView = _service.buildView( document, false );
        assertEquals( "Working title", workingView.getTitle( ) );
        assertEquals( "Working summary", workingView.getSummary( ) );
    }

    /**
     * The validated view of a document validated before the snapshot existed uses the current title and summary
     */
    @Test
    public void testValidatedViewFallsBackWithoutSnapshot( )
    {
        DocumentView view = _service.buildView( buildDocument( ), true );

        assertEquals( "Working title", view.getTitle( ) );
        assertEquals( "Working summary", view.getSummary( ) );
    }

    /**
     * The document fields and the category names are copied to the view
     */
    @Test
    public void testDocumentFields( )
    {
        Document document = buildDocument( );
        Category category = new Category( );
        category.setName( "News" );
        document.setCategories( List.of( category ) );
        document.setSkipPortlet( true );

        DocumentView view = _service.buildView( document, true );

        assertEquals( DOCUMENT_ID, view.getId( ) );
        assertEquals( "article", view.getCodeDocumentType( ) );
        assertEquals( List.of( "News" ), view.getCategories( ) );
        assertTrue( view.isSkipPortlet( ) );
        assertFalse( view.isSkipCategories( ) );
    }

    /**
     * Builds an article with a text, a rich text and an image attribute, in an order different from the attribute order
     *
     * @return The document
     */
    private Document buildDocument( )
    {
        Document document = new Document( );
        document.setId( DOCUMENT_ID );
        document.setCodeDocumentType( "article" );
        document.setTitle( "Working title" );
        document.setSummary( "Working summary" );

        List<DocumentAttribute> listAttributes = new ArrayList<>( );
        listAttributes.add( buildAttribute( 2, "title2", TYPE_TEXT, 2, "Sub title" ) );

        DocumentAttribute file = buildAttribute( 3, "attachment", TYPE_FILE, 3, "photo.png" );
        file.setBinary( true );
        file.setValueContentType( "image/png" );
        listAttributes.add( file );

        listAttributes.add( buildAttribute( 1, "body", TYPE_RICHTEXT, 1, "<p>Body</p>" ) );
        document.setAttributes( listAttributes );

        return document;
    }

    /**
     * Builds a document attribute
     *
     * @param nId
     *            The attribute identifier
     * @param strCode
     *            The attribute code
     * @param strType
     *            The attribute type code
     * @param nOrder
     *            The attribute order
     * @param strValue
     *            The text value
     * @return The attribute
     */
    private DocumentAttribute buildAttribute( int nId, String strCode, String strType, int nOrder, String strValue )
    {
        DocumentAttribute attribute = new DocumentAttribute( );
        attribute.setId( nId );
        attribute.setCode( strCode );
        attribute.setCodeAttributeType( strType );
        attribute.setAttributeOrder( nOrder );
        attribute.setTextValue( strValue );

        return attribute;
    }
}
