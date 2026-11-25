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
package fr.paris.lutece.plugins.document.business;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface for DocumentDAO
 */
public interface IDocumentDAO
{

    /**
     * Delete a record from the table
     * 
     * @param nDocumentId
     *            the document identifier
     */
    void delete( int nDocumentId );

    /**
     * Insert a new record in the table.
     * 
     * @param document
     *            The document object
     */
    void insert( Document document );

    /**
     * Load the data of Document from the table
     * 
     * @param nDocumentId
     *            The identifier of Document
     * @return the instance of the Document
     */
    Document load( int nDocumentId );

    /**
     * Returns an instance of a document whose identifier is specified in parameter
     *
     * @param nDocumentId
     *            The Primary key of the document
     * @return An instance of document
     */
    Document loadWithoutBinaries( int nDocumentId );

    /**
     * Returns documents by space id
     * 
     * @param nSpaceId
     *            The space Id
     * @return A list of documents
     */
    List<Document> loadFromSpaceId( int nSpaceId );

    /**
     * Load a resource (image, file, ...) corresponding to an attribute of a Document
     * 
     * @param nDocumentId
     *            The Document Id
     * @return the instance of the DocumentResource
     */
    DocumentResource loadResource( int nDocumentId );

    /**
     * Load a resource (image, file, ...) corresponding to an attribute of a Document
     * 
     * @param nDocumentId
     *            The Document Id
     * @param nAttributeId
     *            The Attribute Id
     * @param bValidated
     *            true if we want the validated resource
     * @return the instance of the DocumentResource
     */
    DocumentResource loadSpecificResource( int nDocumentId, int nAttributeId, boolean bValidated );

    /**
     * Gets all documents id
     * 
     * @return A collection of Integer
     */
    Collection<Integer> selectAllPrimaryKeys( );

    /**
     * Gets the ids of the documents whose validated content has a text value containing a fragment
     *
     * @param strFragment
     *            The fragment searched in the text values
     * @param nExcludedDocumentId
     *            The id of a document to leave out
     * @return The ids of the documents
     */
    Collection<Integer> selectIdsByValidatedTextValue( String strFragment, int nExcludedDocumentId );

    /**
     * Gets all documents
     *
     * @return the document list

    /**
     * Load the list of documents
     *
     * @return The Collection of the Document ids
     * @param filter
     *            The DocumentFilter Object
     */
    Collection<Integer> selectPrimaryKeysByFilter( DocumentFilter filter );

    /**
     * Load the list of documents
     * 
     * @param filter
     *            The DocumentFilter Object
     * @return The Collection of the Documents
     */
    List<Document> selectByFilter( DocumentFilter filter );

    /**
     * Load the list of published documents in relation with categories of specified document
     * 
     * @param document
     *            The document with the categories
     * @return The Collection of the Documents
     */
    List<Document> selectByRelatedCategories( Document document );

    /**
     * Update the record in the table
     * 
     * @param document
     *            The reference of document
     * @param bUpdateContent
     *            the boolean
     */
    void store( Document document, boolean bUpdateContent );

    /**
     * Load document attributes
     * 
     * @param document
     *            the reference of the document
     */
    void loadAttributes( Document document );

    /**
     * Load document attributes
     * 
     * @param document
     *            the reference of the document
     * @param bValidated
     *            true if the content of the document must be validated, false otherwise
     */
    void loadAttributesWithoutBinaries( Document document, boolean bValidated );

    /**
     * Load document pageTemplatePath
     * 
     * @param idPageTemplateDocument
     *            the Id page template identifier
     * @return The page template document path
     */
    String getPageTemplateDocumentPath( int idPageTemplateDocument );

    /**
     * Load document type and date last modification for HTTP GET conditional request ("If-Modified-Since")
     * 
     * @param nDocumentId
     * @return the document
     */
    Document loadLastModifiedAttributes( int nDocumentId );

    /**
     * Validate the document attributes
     * 
     * @param nDocumentId
     *            the Id of the document
     */
    void validateAttributes( int nDocumentId );

    /**
     * Load the data of last Document the user worked in from the table
     *
     * @param strUserName
     *            the user name
     * @return the instance of the Document
     */
    Document loadLastModifiedDocumentFromUser( String strUserName );

    /**
     * Load the data of last Document the user worked in from the table
     *
     * @return the instance of the Document
     */
    Document loadLastPublishedDocument( );

    /**
     * Load a page of the documents of a type that have a validated version, the last created first. The documents are loaded without their attributes
     * nor their categories, with their space name.
     *
     * @param strCodeDocumentType
     *            The document type code
     * @param strTitleFragment
     *            A fragment the working or the validated title contains, ignoring case, or an empty string for any title
     * @param listSpaceIds
     *            The spaces the documents belong to
     * @param nOffset
     *            The number of documents skipped
     * @param nLimit
     *            The maximum number of documents loaded
     * @return The list of the documents
     */
    List<Document> selectWithValidatedVersion( String strCodeDocumentType, String strTitleFragment, Collection<Integer> listSpaceIds, int nOffset,
            int nLimit );

    /**
     * Count the documents of a type that have a validated version
     *
     * @param strCodeDocumentType
     *            The document type code
     * @param strTitleFragment
     *            A fragment the working or the validated title contains, ignoring case, or an empty string for any title
     * @param listSpaceIds
     *            The spaces the documents belong to
     * @return The number of documents
     * @see #selectWithValidatedVersion(String, String, Collection, int, int)
     */
    int countWithValidatedVersion( String strCodeDocumentType, String strTitleFragment, Collection<Integer> listSpaceIds );

    /**
     * Load the metadata of the documents still stored in the legacy XML column, before the migration to JSON
     *
     * @return The legacy XML metadata by document identifier
     */
    Map<Integer, String> selectLegacyMetadata( );

    /**
     * Store the metadata migrated from the legacy XML column, and clear the legacy column
     *
     * @param nDocumentId
     *            The document identifier
     * @param strMetadata
     *            The metadata, as a JSON object
     */
    void storeMigratedMetadata( int nDocumentId, String strMetadata );
}
