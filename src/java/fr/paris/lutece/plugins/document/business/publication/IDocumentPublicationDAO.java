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
package fr.paris.lutece.plugins.document.business.publication;

import java.util.Collection;
import java.util.Date;

/**
 *
 * This class porvides Data Access methods for DocumentPublicationDAO interface
 *
 */
public interface IDocumentPublicationDAO
{
    /**
     * Insert the documentsPublication object
     *
     * @param documentPublication
     *            The document Publication object
     */
    void insert( DocumentPublication documentPublication );

    /**
     * Update the {@link DocumentPublication} object
     *
     * @param documentPublication
     *            The {@link DocumentPublication} object
     */
    void store( DocumentPublication documentPublication );

    /**
     * Delete records for table document_published specified by portlet id and document id
     *
     * @param nPortletId
     *            the portlet identifier
     * @param nDocumentId
     *            the document identifier
     */
    void delete( int nPortletId, int nDocumentId );

    /**
     * Delete records for table document_published specified by portlet id
     *
     * @param nPortletId
     *            the portlet identifier
     */
    void deleteFromPortletId( int nPortletId );

    /**
     * Delete records for table document_published specified by portlet id
     *
     * @param nDocumentId
     *            the document identifier
     */
    void deleteFromDocumentId( int nDocumentId );

    /**
     * Select the {@link DocumentPublication} object specified by the portlet id and document id
     * 
     * @param nPortletId
     *            The portlet identifier
     * @param nDocumentId
     *            The document identifier
     * @return The {@link DocumentPublication} object or null if the object does not exists
     */
    DocumentPublication select( int nPortletId, int nDocumentId );

    /**
     * Select the list of {@link DocumentPublication} objects specified by the portlet id
     * 
     * @param nPortletId
     *            The portlet identifier
     * @return The {@link DocumentPublication} objects list (empty list if no objects found)
     */
    Collection<DocumentPublication> selectByPortletId( int nPortletId );

    /**
     * Select the list of {@link DocumentPublication} objects specified by the document id
     * 
     * @param nDocumentId
     *            The document identifier
     * @return The {@link DocumentPublication} objects list (empty list if no objects found)
     */
    Collection<DocumentPublication> selectByDocumentId( int nDocumentId );

    /**
     * Select the list of {@link DocumentPublication} objects specified by the portlet id and the status
     * 
     * @param nPortletId
     *            The portlet identifier
     * @param nStatus
     *            The status
     * @return The {@link DocumentPublication} objects list (empty list if no objects found)
     */
    Collection<DocumentPublication> selectByPortletIdAndStatus( int nPortletId, int nStatus );

    /**
     * Select the list of {@link DocumentPublication} objects specified by the document id and the status
     * 
     * @param nDocumentId
     *            The document identifier
     * @param nStatus
     *            The status
     * @return The {@link DocumentPublication} objects list (empty list if no objects found)
     */
    Collection<DocumentPublication> selectByDocumentIdAndStatus( int nDocumentId, int nStatus );

    /**
     * Find the list of {@link DocumentPublication} objects specified the status and published at or after the specified date
     * 
     * @param datePublishing
     *            The publication date
     * @param nStatus
     *            The status
     * @return The {@link DocumentPublication} objects {@link Collection} ordered by documentOrder ascending. The list is empty if no objects found.
     */
    Collection<DocumentPublication> selectSinceDatePublishingAndStatus( Date datePublishing, int nStatus );

    /**
     * Select the max order from a list of {@link DocumentPublication} specified by portlet id
     * 
     * @param nPortletId
     *            the portlet identifer
     * @return The max order of document
     */
    int selectMaxDocumentOrder( int nPortletId );

    /**
     * Return a document identifier in a distinct order
     *
     * @param nDocumentOrder
     *            The order number
     * @param nPortletId
     *            the portlet identifier
     * @return The order of the Document
     */
    int selectDocumentIdByOrder( int nDocumentOrder, int nPortletId );
}
