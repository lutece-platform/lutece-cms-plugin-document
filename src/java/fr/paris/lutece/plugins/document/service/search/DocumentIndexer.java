/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.document.service.search;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.plugins.lucene.service.indexer.IFileIndexer;
import fr.paris.lutece.plugins.lucene.service.indexer.IFileIndexerFactory;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.url.UrlItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.demo.html.HTMLParser;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;


/**
 * Document Indexer
 */
public class DocumentIndexer implements SearchIndexer
{
    public static final String INDEXER_NAME = "DocumentIndexer";
    public static final String SHORT_NAME = "dcm";
    private static final String INDEXER_DESCRIPTION = "Indexer service for documents";
    private static final String INDEXER_VERSION = "1.0.0";
    private static final String PROPERTY_PAGE_BASE_URL = "document.documentIndexer.baseUrl";
    private static final String PROPERTY_INDEXER_ENABLE = "document.documentIndexer.enable";
    private static final String PARAMETER_DOCUMENT_ID = "document_id";
    private static final String PARAMETER_PORTLET_ID = "portlet_id";
    private static final String JSP_PAGE_ADVANCED_SEARCH = "jsp/site/Portal.jsp?page=advanced_search";

    /**
     * index all lucene documents
     * @throws java.io.IOException i/o exception
     * @throws java.lang.InterruptedException interrupted exception
     */
    public void indexDocuments( ) throws IOException, InterruptedException
    {
        String strBaseUrl = AppPropertiesService.getProperty( PROPERTY_PAGE_BASE_URL );
        Page page;

        for ( Portlet portlet : PortletHome.findByType( DocumentListPortletHome.getInstance( ).getPortletTypeId( ) ) )
        {
            page = PageHome.getPage( portlet.getPageId( ) );

            for ( Document d : PublishingService.getInstance( ).getPublishedDocumentsByPortletId( portlet.getId( ) ) )
            {
                Document document = DocumentHome.findByPrimaryKey( d.getId( ) );

                // Reload the full object to get all its searchable attributes
                UrlItem url = new UrlItem( strBaseUrl );
                url.addParameter( PARAMETER_DOCUMENT_ID, document.getId( ) );
                url.addParameter( PARAMETER_PORTLET_ID, portlet.getId( ) );

                String strPortletDocumentId = document.getId( ) + "_" + SHORT_NAME + "&" + portlet.getId( );
                org.apache.lucene.document.Document doc = null;

                try
                {
                    doc = getDocument( document, url.getUrl( ), page.getRole( ), strPortletDocumentId );
                }
                catch ( Exception e )
                {
                    String strMessage = "Document ID : " + document.getId( ) + " - Portlet ID : " + portlet.getId( );
                    IndexationService.error( this, e, strMessage );
                }

                if ( doc != null )
                {
                    IndexationService.write( doc );
                }
            }
        }
    }

    /**
     * Returns a collection of lucene documents with the same id
     * @param strIdDocument the document id
     * @return lucene documents
     * @throws IOexception i/o exception
     * @throws InterruptedException interrupted exception
     */
    public List<org.apache.lucene.document.Document> getDocuments( String strIdDocument ) throws IOException,
            InterruptedException
    {
        List<org.apache.lucene.document.Document> listDocs = new ArrayList<org.apache.lucene.document.Document>( );
        int nIdDocument = IntegerUtils.convert( strIdDocument );
        Document document = DocumentHome.findByPrimaryKey( nIdDocument );
        Iterator<Portlet> it = PublishingService.getInstance( ).getPortletsByDocumentId( strIdDocument ).iterator( );
        String strBaseUrl = AppPropertiesService.getProperty( PROPERTY_PAGE_BASE_URL );
        Page page;

        while ( it.hasNext( ) )
        {
            Portlet portlet = it.next( );
            UrlItem url = new UrlItem( strBaseUrl );
            url.addParameter( PARAMETER_DOCUMENT_ID, nIdDocument );
            url.addParameter( PARAMETER_PORTLET_ID, portlet.getId( ) );

            String strPortletDocumentId = nIdDocument + "_" + SHORT_NAME + "&" + portlet.getId( );

            page = PageHome.getPage( portlet.getPageId( ) );

            org.apache.lucene.document.Document doc = getDocument( document, url.getUrl( ), page.getRole( ),
                    strPortletDocumentId );
            listDocs.add( doc );
        }

        return listDocs;
    }

    /**
     * Returns the indexer service name
     * @return the indexer service name
     */
    public String getName( )
    {
        return INDEXER_NAME;
    }

    /**
     * Returns the indexer service version
     * @return The indexer service version
     */
    public String getVersion( )
    {
        return INDEXER_VERSION;
    }

    /**
     * Returns the indexer service description
     * @return The indexer service description
     */
    public String getDescription( )
    {
        return INDEXER_DESCRIPTION;
    }

    /**
     * Tells whether the service is enable or not
     * @return true if enable, otherwise false
     */
    public boolean isEnable( )
    {
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE, "true" );

        return ( strEnable.equalsIgnoreCase( "true" ) );
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the
     * pages of the site with the following
     * fields : summary, uid, url, contents, title and description.
     * 
     * @param document the document to index
     * @param strUrl the url of the documents
     * @param strRole the lutece role of the page associate to the document
     * @param strPortletDocumentId the document id concatened to the id portlet
     *            with a & in the middle
     * @return the built Document
     * @throws IOException The IO Exception
     * @throws InterruptedException The InterruptedException
     */
    public static org.apache.lucene.document.Document getDocument( Document document, String strUrl, String strRole,
            String strPortletDocumentId ) throws IOException, InterruptedException
    {
        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document( );

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the document, but is not searchable.
        doc.add( new Field( SearchItem.FIELD_URL, strUrl, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        // Add the PortletDocumentId as a field named "document_portlet_id".  
        doc.add( new Field( SearchItem.FIELD_DOCUMENT_PORTLET_ID, strPortletDocumentId, Field.Store.YES,
                Field.Index.NOT_ANALYZED ) );

        // Add the last modified date of the file a field named "modified".
        // Use a field that is indexed (i.e. searchable), but don't tokenize
        // the field into words.
        String strDate = DateTools.dateToString( document.getDateModification( ), DateTools.Resolution.DAY );
        doc.add( new Field( SearchItem.FIELD_DATE, strDate, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        String strIdDocument = String.valueOf( document.getId( ) );
        doc.add( new Field( SearchItem.FIELD_UID, strIdDocument + "_" + DocumentIndexer.SHORT_NAME, Field.Store.YES,
                Field.Index.NOT_ANALYZED ) );

        String strContentToIndex = getContentToIndex( document );
        StringReader readerPage = new StringReader( strContentToIndex );
        HTMLParser parser = new HTMLParser( readerPage );

        //the content of the article is recovered in the parser because this one
        //had replaced the encoded caracters (as &eacute;) by the corresponding special caracter (as ?)
        Reader reader = parser.getReader( );
        int c;
        StringBuilder sb = new StringBuilder( );

        while ( ( c = reader.read( ) ) != -1 )
        {
            sb.append( String.valueOf( (char) c ) );
        }

        reader.close( );

        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        doc.add( new Field( SearchItem.FIELD_CONTENTS, sb.toString( ), Field.Store.NO, Field.Index.ANALYZED ) );

        // Add the title as a separate Text field, so that it can be searched
        // separately.
        doc.add( new Field( SearchItem.FIELD_TITLE, document.getTitle( ), Field.Store.YES, Field.Index.NO ) );

        doc.add( new Field( SearchItem.FIELD_TYPE, document.getType( ), Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        doc.add( new Field( SearchItem.FIELD_ROLE, strRole, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        // add metadata (mapped to summary)
        doc.add( new Field( SearchItem.FIELD_METADATA, document.getSummary( ), Field.Store.NO, Field.Index.ANALYZED ) );

        // return the document
        return doc;
    }

    /**
     * Get the content from the document
     * @param document the document to index
     * @return the content
     */
    private static String getContentToIndex( Document document )
    {
        StringBuilder sbContentToIndex = new StringBuilder( );
        sbContentToIndex.append( document.getTitle( ) );

        for ( DocumentAttribute attribute : document.getAttributes( ) )
        {
            if ( attribute.isSearchable( ) )
            {
                if ( !attribute.isBinary( ) )
                {
                    // Text attributes
                    sbContentToIndex.append( " " );
                    sbContentToIndex.append( attribute.getTextValue( ) );
                }
                else
                {
                    // Binary file attribute
                    // Gets indexer depending on the ContentType (ie: "application/pdf" should use a PDF indexer)
                    IFileIndexerFactory factoryIndexer = (IFileIndexerFactory) SpringContextService
                            .getBean( IFileIndexerFactory.BEAN_FILE_INDEXER_FACTORY );
                    IFileIndexer indexer = factoryIndexer.getIndexer( attribute.getValueContentType( ) );

                    if ( indexer != null )
                    {
                        try
                        {
                            ByteArrayInputStream bais = new ByteArrayInputStream( attribute.getBinaryValue( ) );
                            sbContentToIndex.append( " " );
                            sbContentToIndex.append( indexer.getContentToIndex( bais ) );
                            bais.close( );
                        }
                        catch ( IOException e )
                        {
                            AppLogService.error( e.getMessage( ), e );
                        }
                    }
                }
            }
        }

        // Index Metadata
        sbContentToIndex.append( " " );
        sbContentToIndex.append( document.getXmlMetadata( ) );

        return sbContentToIndex.toString( );
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getListType( )
    {
        List<String> typeList = new ArrayList<String>( );

        for ( ReferenceItem item : DocumentTypeHome.getDocumentTypesList( ) )
        {
            typeList.add( item.getName( ) );
        }

        return typeList;
    }

    /**
     * {@inheritDoc}
     */
    public String getSpecificSearchAppUrl( )
    {
        return JSP_PAGE_ADVANCED_SEARCH;
    }
}
