/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.document.business;

import fr.paris.lutece.plugins.document.service.docsearch.DocSearchService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...) for Document objects
 */
public final class DocumentHome
{
    // Static variable pointed at the DAO instance
    private static IDocumentDAO _dao = (IDocumentDAO) SpringContextService.getPluginBean( "document",
            "document.documentDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DocumentHome(  )
    {
    }

    /**
     * Creation of an instance of document
     *
     * @param document The instance of the document which contains the informations to store
     * @return The  instance of document which has been created with its primary key.
     */
    public static Document create( Document document )
    {
        _dao.insert( document );
        DocSearchService.getInstance(  ).addIndexerAction( document.getId(  ), IndexerAction.TASK_CREATE );

        /*IndexationService.addIndexerAction( document.getId(), DocumentIndexer.INDEXER_NAME, IndexerAction.TASK_CREATE );*/
        return document;
    }

    /**
     * Update of the document which is specified in parameter
     *
     * @return The instance of the  document which has been updated
     * @param bUpdateContent
     * @param document The instance of the document which contains the data to store
     */
    public static Document update( Document document, boolean bUpdateContent )
    {
        _dao.store( document, bUpdateContent );
        DocSearchService.getInstance(  ).addIndexerAction( document.getId(  ), IndexerAction.TASK_MODIFY );

        /* if(PublishingService.getInstance().isPublished(document.getId()))
         {
                 IndexationService.getInstance().addIndexerAction( document.getId()
                                 , DocumentIndexer.INDEXER_NAME
                                 , IndexerAction.TASK_MODIFY
                                 , IndexationService.ALL_DOCUMENT );
         }    */
        return document;
    }

    /**
     * Validate of the document attributes
     * @param nIdDocument The id of the document
     */
    public static void validateAttributes( int nIdDocument )
    {
        _dao.validateAttributes( nIdDocument );
    }

    /**
     * Remove the Document whose identifier is specified in parameter
     *
     * @param nDocumentId
     */
    public static void remove( int nDocumentId )
    {
        _dao.delete( nDocumentId );
        DocSearchService.getInstance(  ).addIndexerAction( nDocumentId, IndexerAction.TASK_DELETE );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a document whose identifier is specified in parameter
     *
     * @param nKey The Primary key of the document
     * @return An instance of document
     */
    public static Document findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Returns an instance of a document whose identifier is specified in parameter
     *
     * @param nKey The Primary key of the document
     * @return An instance of document
     */
    public static Document findByPrimaryKeyWithoutBinaries( int nKey )
    {
        return _dao.loadWithoutBinaries( nKey );
    }

    /**
     * Returns documents by space id
     * @param nSpaceId The space Id
     * @return A list of documents
     */
    public static List<Document> findBySpaceKey( int nSpaceId )
    {
        return _dao.loadFromSpaceId( nSpaceId );
    }

    /**
     * Returns a collection of documents ids
     * @return A collection of documents ids
     * @param filter
     * @param locale
     */
    public static Collection<Integer> findPrimaryKeysByFilter( DocumentFilter filter, Locale locale )
    {
        return _dao.selectPrimaryKeysByFilter( filter );
    }

    /**
     * Returns a collection of documents objects
     * @return A collection of documents
     * @param filter
     * @param locale
     */
    public static List<Document> findByFilter( DocumentFilter filter, Locale locale )
    {
        List<Document> listDocuments = _dao.selectByFilter( filter );

        return (List) I18nService.localizeCollection( listDocuments, locale );
    }

    /**
     * Returns a collection of documents objects
     * If more than one category is specified on filter,
     * the result will corresponding to the document wich matched with one category at least.
     * @param document The {@link Document}
     * @param locale The {@link Locale}
     * @return A collection of documents
     */
    public static List<Document> findByRelatedCategories( Document document, Locale locale )
    {
        List<Document> listDocuments = _dao.selectByRelatedCategories( document );

        return (List) I18nService.localizeCollection( listDocuments, locale );
    }

    /**
     *
     * @param nDocumentId
     * @param nAttributeId
     * @return
     */
    public static DocumentResource getValidatedResource( int nDocumentId, int nAttributeId )
    {
        return _dao.loadSpecificResource( nDocumentId, nAttributeId, true );
    }

    /**
    *
    * @param nDocumentId
    * @param nAttributeId
    * @return
    */
    public static DocumentResource getWorkingResource( int nDocumentId, int nAttributeId )
    {
        return _dao.loadSpecificResource( nDocumentId, nAttributeId, false );
    }

    /**
     *
     * @param nDocumentId
     * @return
     */
    public static DocumentResource getResource( int nDocumentId )
    {
        return _dao.loadResource( nDocumentId );
    }

    /**
     *
     * @return
     */
    public static int newPrimaryKey(  )
    {
        return _dao.newPrimaryKey(  );
    }

    /**
     * Gets all documents id
     * @return A collection of Integer
     */
    public static Collection<Integer> findAllPrimaryKeys(  )
    {
        return _dao.selectAllPrimaryKeys(  );
    }

    /**
     *
     * @return
     */
    public static List<Document> findAll(  )
    {
        return _dao.selectAll(  );
    }

    /**
     * Load document attributes
     * @param document the document reference
     */
    public static void loadAttributes( Document document )
    {
        _dao.loadAttributes( document );
    }

    /**
     * Load document pageTemplatePath
     * @param IdPageTemplateDocument the Id page template identifier
     */
    public static String getPageTemplateDocumentPath( int IdPageTemplateDocument )
    {
        return _dao.getPageTemplateDocumentPath( IdPageTemplateDocument );
    }

    /**
     * Load document type and date last modification for HTTP GET conditional request ("If-Modified-Since")
     * @param nDocumentId
     * @return the document
     */
    public static Document loadLastModifiedAttributes( int nDocumentId )
    {
        return _dao.loadLastModifiedAttributes( nDocumentId );
    }
    
    /**
     * Load the data of last Document the user worked in from the table
     *
     * @param strUserName the user name
     * @return the instance of the Document
     */
    public static Document loadLastModifiedDocumentFromUser( String strUserName )
    {
    	return _dao.loadLastModifiedDocumentFromUser( strUserName );
    }
    
    /**
     * Load the data of last Document the user worked in from the table
     *
     * @return the instance of the Document
     */
    public static Document loadLastPublishedDocument(  )
    {
    	return _dao.loadLastPublishedDocument(  );
    }
}
