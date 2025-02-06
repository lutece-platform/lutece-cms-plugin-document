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

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;
import java.util.Date;

/**
 * This class provides instances management methods for DocumentPublication objects
 */
public class DocumentPublicationHome
{
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    // Static variable pointed at the DAO instance
    private static IDocumentPublicationDAO _dao = SpringContextService.getBean( "document.documentPublicationDAO" );

    /**
     * Create the documentsPublication object
     *
     * @param documentPublication
     *            The document Publication object
     */
    public static void create( DocumentPublication documentPublication )
    {
        _dao.insert( documentPublication );

        /*
         * IndexationService.getInstance().addIndexerAction( documentPublication.getDocumentId() , DocumentIndexer.INDEXER_NAME , IndexerAction.TASK_CREATE ,
         * documentPublication.getPortletId() );
         */
    }

    /**
     * Update the {@link DocumentPublication} object
     *
     * @param documentPublication
     *            The {@link DocumentPublication} object
     */
    public static void update( DocumentPublication documentPublication )
    {
        _dao.store( documentPublication );
    }

    /**
     * Remove the {@link DocumentPublication} object specified by portlet id and document id
     *
     * @param nPortletId
     *            the portlet identifier
     * @param nDocumentId
     *            the document identifier
     */
    public static void remove( int nPortletId, int nDocumentId )
    {
        _dao.delete( nPortletId, nDocumentId );
    }

    /**
     * Remove all {@link DocumentPublication} objects specified by portlet id
     *
     * @param nPortletId
     *            the portlet identifier
     */
    public static void removeFromPortletId( int nPortletId )
    {
        _dao.deleteFromPortletId( nPortletId );
    }

    /**
     * Remove all {@link DocumentPublication} objects specified by document id
     *
     * @param nDocumentId
     *            the document identifier
     */
    public static void removeFromDocumentId( int nDocumentId )
    {
        _dao.deleteFromDocumentId( nDocumentId );
    }

    /**
     * Find the {@link DocumentPublication} object specified by the portlet id and document id
     * 
     * @param nPortletId
     *            The portlet identifier
     * @param nDocumentId
     *            The document identifier
     * @return The {@link DocumentPublication} object or null if the object does not exists
     */
    public static DocumentPublication findByPrimaryKey( int nPortletId, int nDocumentId )
    {
        return _dao.select( nPortletId, nDocumentId );
    }

    /**
     * Find the list of {@link DocumentPublication} objects specified by the portlet id
     * 
     * @param nPortletId
     *            The portlet identifier
     * @return The {@link DocumentPublication} objects {@link Collection} ordered by documentOrder ascending. The list is empty if no objects found.
     */
    public static Collection<DocumentPublication> findByPortletId( int nPortletId )
    {
        return _dao.selectByPortletId( nPortletId );
    }

    /**
     * Find the list of {@link DocumentPublication} objects specified by the document id
     * 
     * @param nDocumentId
     *            The document identifier
     * @return The {@link DocumentPublication} objects {@link Collection} ordered by documentOrder ascending. The list is empty if no objects found.
     */
    public static Collection<DocumentPublication> findByDocumentId( int nDocumentId )
    {
        return _dao.selectByDocumentId( nDocumentId );
    }

    /**
     * Find the list of {@link DocumentPublication} objects specified by the portlet id and the status
     * 
     * @param nPortletId
     *            The portlet identifier
     * @param nStatus
     *            The status
     * @return The {@link DocumentPublication} objects {@link Collection} ordered by documentOrder ascending. The list is empty if no objects found.
     */
    public static Collection<DocumentPublication> findByPortletIdAndStatus( int nPortletId, int nStatus )
    {
        return _dao.selectByPortletIdAndStatus( nPortletId, nStatus );
    }

    /**
     * Find the list of {@link DocumentPublication} objects specified by the document id and the status
     * 
     * @param nDocumentId
     *            The document identifier
     * @param nStatus
     *            The status
     * @return The {@link DocumentPublication} objects {@link Collection} ordered by documentOrder ascending. The list is empty if no objects found.
     */
    public static Collection<DocumentPublication> findByDocumentIdAndStatus( int nDocumentId, int nStatus )
    {
        return _dao.selectByDocumentIdAndStatus( nDocumentId, nStatus );
    }

    /**
     * Find the list of {@link DocumentPublication} objects specified the status and published at or after the specified date
     * 
     * @param datePublishing
     *            The publication date
     * @param nStatus
     *            The status
     * @return The {@link DocumentPublication} objects {@link Collection} ordered by documentOrder ascending. The list is empty if no objects found.
     */
    public static Collection<DocumentPublication> findSinceDatePublishingAndStatus( Date datePublishing, int nStatus )
    {
        return _dao.selectSinceDatePublishingAndStatus( datePublishing, nStatus );
    }

    /**
     * Find the max document order from a {@link Portlet} id
     * 
     * @param nPortletId
     *            the {@link Portlet} identifer
     * @return The max document order
     */
    public static int findMaxDocumentOrderByPortletId( int nPortletId )
    {
        return _dao.selectMaxDocumentOrder( nPortletId );
    }
}
