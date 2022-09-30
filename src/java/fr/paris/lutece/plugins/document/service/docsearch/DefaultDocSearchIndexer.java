/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.document.service.docsearch;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.parser.service.IParserFactory;
import fr.paris.lutece.plugins.parser.service.IStreamParser;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * DefaultDocSearchIndexer
 */
public class DefaultDocSearchIndexer implements IDocSearchIndexer
{

     /**
     * Build Lucene docs to index
     * @param listDocumentIds Documents to index
     * @return A list of Lucene documents
     * @throws IOException i/o exception
     */
    public List<org.apache.lucene.document.Document> getDocuments( Collection<Integer> listDocumentIds )
        throws IOException
    {
        List<org.apache.lucene.document.Document> listLuceneDocs = new ArrayList<org.apache.lucene.document.Document>(  );

        for ( Integer documentId : listDocumentIds )
        {
            Document document = DocumentHome.findByPrimaryKey( documentId );

            if ( document != null )
            {
                listLuceneDocs.add( getDocument( document ) );
            }
        }

        return listLuceneDocs;
    }

    /**
     * Return the document
     * @param document Documents object
     * @return document
     * @throws IOException i/o exception
     */
    private org.apache.lucene.document.Document getDocument( Document document )
        throws IOException
    {
        // make a new, empty Lucene document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document(  );

        // Add the last modified date of the file a field named "modified".
        // Use a field that is indexed (i.e. searchable), but don't tokenize
        // the field into words.
        FieldType ft = new FieldType( StringField.TYPE_STORED );
        ft.setOmitNorms( false );

        DateFormat formater = DateFormat.getDateInstance( DateFormat.SHORT );
        String strDate = formater.format( document.getDateModification(  ) );
        doc.add( new Field( SearchItem.FIELD_DATE, strDate, ft ) );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        String strIdDocument = String.valueOf( document.getId(  ) );
        doc.add( new Field( SearchItem.FIELD_UID, strIdDocument, ft ) );

        String strContentToIndex = getContentToIndex( document );
       
        IParserFactory factoryParser =  SpringContextService.getBean( IParserFactory.BEAN_FILE_PARSER_FACTORY );
        Optional<IStreamParser> parser = factoryParser.getParser( "text/html" );

        //the content of the article is recovered in the parser because this one
        //had replaced the encoded caracters (as &eacute;) by the corresponding special caracter (as ?)
        StringBuilder sb = new StringBuilder(  );
        if ( parser.isPresent( ) )
        {
        	sb.append( parser.get().parse(new ByteArrayInputStream(strContentToIndex.getBytes())));
        }else {
        	
        	AppLogService.debug("Error HtmlParser not found");
        }  
        
        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        doc.add( new Field( SearchItem.FIELD_CONTENTS, sb.toString(  ), TextField.TYPE_NOT_STORED ) );

        // Add the title as a separate Text field, so that it can be searched
        // separately.
        FieldType ft2 = new FieldType( TextField.TYPE_STORED );
        ft2.setOmitNorms( true );
        doc.add( new Field( SearchItem.FIELD_TITLE, document.getTitle(  ), ft2 ) );
        doc.add( new Field( DocSearchItem.FIELD_SUMMARY, document.getSummary(  ), ft2 ) );

        doc.add( new Field( SearchItem.FIELD_TYPE, document.getType(  ), ft ) );
        doc.add( new Field( DocSearchItem.FIELD_SPACE, "s" + document.getSpaceId(  ), ft2 ) );

        // return the document
        return doc;
    }

    /**
     * Return the content
     * @param document Document object
     * @return content
     */
    private static String getContentToIndex( Document document )
    {
        StringBuilder sbContentToIndex = new StringBuilder(  );
        sbContentToIndex.append( document.getTitle(  ) );
        sbContentToIndex.append( " " );
        sbContentToIndex.append( document.getSummary(  ) );
        sbContentToIndex.append( " " );

        for ( DocumentAttribute attribute : document.getAttributes(  ) )
        {
            if ( attribute.isSearchable(  ) )
            {
                if ( !attribute.isBinary(  ) )
                {
                    sbContentToIndex.append( attribute.getTextValue(  ) );
                    sbContentToIndex.append( " " );
                }
                else
                {
                    IParserFactory factoryParser =  SpringContextService.getBean( IParserFactory.BEAN_FILE_PARSER_FACTORY );
                    Optional<IStreamParser> parser = factoryParser.getParser( attribute.getValueContentType(  ) );

                    if ( parser.isPresent( ) )
                    {
                        try
                        {
                            ByteArrayInputStream bais = new ByteArrayInputStream( attribute.getBinaryValue(  ) );
                            sbContentToIndex.append( parser.get().parse( bais ) );
                            sbContentToIndex.append( " " );
                            bais.close(  );
                        }
                        catch ( IOException e )
                        {
                            AppLogService.error( e.getMessage(  ), e );
                        }
                    }
                }
            }
        }

        // Add metadata in XML (xml tags will be ignored by the HTML parsing)
        sbContentToIndex.append( document.getXmlMetadata(  ) );

        return sbContentToIndex.toString(  );
    }
}
