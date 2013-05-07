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
package fr.paris.lutece.plugins.document.business.publication;

import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


/**
 * This class provides Data Access methods for DocumentPublication objects
 */
public class DocumentPublicationDAO implements IDocumentPublicationDAO
{
    private static final String SQL_QUERY_DELETE = " DELETE FROM document_published WHERE id_portlet = ? AND id_document = ? ";
    private static final String SQL_QUERY_DELETE_FROM_PORTLET_ID = " DELETE FROM document_published WHERE id_portlet = ? ";
    private static final String SQL_QUERY_DELETE_FROM_DOCUMENT_ID = " DELETE FROM document_published WHERE id_document = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO document_published ( id_portlet, id_document, document_order, status, date_publishing ) VALUES ( ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_UPDATE = " UPDATE document_published SET document_order = ?, status = ?, date_publishing = ? WHERE id_portlet = ? AND id_document = ? ";
    private static final String SQL_QUERY_SELECT = " SELECT document_order, status, date_publishing FROM document_published WHERE id_portlet = ? AND id_document = ? ORDER BY document_order ASC ";
    private static final String SQL_QUERY_SELECT_BY_PORTLET_ID = " SELECT id_document, document_order, status, date_publishing FROM document_published WHERE id_portlet = ? ORDER BY document_order ASC ";
    private static final String SQL_QUERY_SELECT_BY_DOCUMENT_ID = " SELECT id_portlet, document_order, status, date_publishing FROM document_published WHERE id_document = ? ORDER BY document_order ASC ";
    private static final String SQL_QUERY_SELECT_BY_PORTLET_ID_AND_STATUS = " SELECT id_document, document_order, date_publishing FROM document_published WHERE id_portlet = ? AND status = ? ORDER BY document_order ASC ";
    private static final String SQL_QUERY_SELECT_BY_DOCUMENT_ID_AND_STATUS = " SELECT id_portlet, document_order, date_publishing FROM document_published WHERE id_document = ? AND status = ? ORDER BY document_order ASC ";
    private static final String SQL_QUERY_SELECT_BY_DATE_PUBLISHING_AND_STATUS = " SELECT id_portlet, id_document, document_order, date_publishing FROM document_published WHERE date_publishing >= ? AND status = ? ORDER BY document_order ASC ";
    private static final String SQL_QUERY_MAX_ORDER = "SELECT max(document_order) FROM document_published WHERE id_portlet = ?  ";
    private static final String SQL_QUERY_MODIFY_ORDER_BY_ID = "SELECT id_document FROM document_published  WHERE document_order = ? AND id_portlet = ?";

    /**
     * Insert the documentsPublication object
     *
     * @param documentPublication The document Publication object
     */
    public void insert( DocumentPublication documentPublication )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );

        daoUtil.setInt( 1, documentPublication.getPortletId(  ) );
        daoUtil.setInt( 2, documentPublication.getDocumentId(  ) );
        daoUtil.setInt( 3, documentPublication.getDocumentOrder(  ) );
        daoUtil.setInt( 4, documentPublication.getStatus(  ) );
        daoUtil.setTimestamp( 5, new Timestamp( documentPublication.getDatePublishing(  ).getTime(  ) ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the {@link DocumentPublication} object
     *
     * @param documentPublication The {@link DocumentPublication} object
     */
    public void store( DocumentPublication documentPublication )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        daoUtil.setInt( 1, documentPublication.getDocumentOrder(  ) );
        daoUtil.setInt( 2, documentPublication.getStatus(  ) ); //FIXME old : daoUtil.setInt( 2, maxOrderDocumentList( nPortletId ) + 1 );
        daoUtil.setTimestamp( 3, new Timestamp( documentPublication.getDatePublishing(  ).getTime(  ) ) );
        daoUtil.setInt( 4, documentPublication.getPortletId(  ) );
        daoUtil.setInt( 5, documentPublication.getDocumentId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * Delete records for table document_published specified by portlet id and document id
    *
    * @param nPortletId the portlet identifier
    * @param nDocumentId the document identifier
    */
    public void delete( int nPortletId, int nDocumentId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.setInt( 2, nDocumentId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * Delete records for table document_published specified by portlet id
    *
    * @param nPortletId the portlet identifier
    */
    public void deleteFromPortletId( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FROM_PORTLET_ID );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * Delete records for table document_published specified by portlet id
    *
    * @param nDocumentId the document identifier
    */
    public void deleteFromDocumentId( int nDocumentId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FROM_DOCUMENT_ID );
        daoUtil.setInt( 1, nDocumentId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Select

    /**
     * Select the {@link DocumentPublication} object specified by the portlet id and document id
     * @param nPortletId The portlet identifier
     * @param nDocumentId The document identifier
     * @return The {@link DocumentPublication} object or null if the object does not exists
     */
    public DocumentPublication select( int nPortletId, int nDocumentId )
    {
        DocumentPublication documentPublication = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.setInt( 2, nDocumentId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            documentPublication = new DocumentPublication(  );
            documentPublication.setPortletId( nPortletId );
            documentPublication.setDocumentId( nDocumentId );
            documentPublication.setDocumentOrder( daoUtil.getInt( 1 ) );
            documentPublication.setStatus( daoUtil.getInt( 2 ) );
            documentPublication.setDatePublishing( daoUtil.getTimestamp( 3 ) );
        }

        daoUtil.free(  );

        return documentPublication;
    }

    /**
     * Select the list of {@link DocumentPublication} objects specified by the portlet id
     * @param nPortletId The portlet identifier
     * @return The {@link DocumentPublication} objects list (empty list if no objects found)
     */
    public Collection<DocumentPublication> selectByPortletId( int nPortletId )
    {
        Collection<DocumentPublication> listDocumentPublication = new ArrayList<DocumentPublication>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PORTLET_ID );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DocumentPublication documentPublication = new DocumentPublication(  );
            documentPublication.setPortletId( nPortletId );
            documentPublication.setDocumentId( daoUtil.getInt( 1 ) );
            documentPublication.setDocumentOrder( daoUtil.getInt( 2 ) );
            documentPublication.setStatus( daoUtil.getInt( 3 ) );
            documentPublication.setDatePublishing( daoUtil.getTimestamp( 4 ) );
            listDocumentPublication.add( documentPublication );
        }

        daoUtil.free(  );

        return listDocumentPublication;
    }

    /**
     * Select the list of {@link DocumentPublication} objects specified by the document id
     * @param nDocumentId The document identifier
     * @return The {@link DocumentPublication} objects list (empty list if no objects found)
     */
    public Collection<DocumentPublication> selectByDocumentId( int nDocumentId )
    {
        Collection<DocumentPublication> listDocumentPublication = new ArrayList<DocumentPublication>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DOCUMENT_ID );
        daoUtil.setInt( 1, nDocumentId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DocumentPublication documentPublication = new DocumentPublication(  );
            documentPublication.setPortletId( daoUtil.getInt( 1 ) );
            documentPublication.setDocumentId( nDocumentId );
            documentPublication.setDocumentOrder( daoUtil.getInt( 2 ) );
            documentPublication.setStatus( daoUtil.getInt( 3 ) );
            documentPublication.setDatePublishing( daoUtil.getTimestamp( 4 ) );
            listDocumentPublication.add( documentPublication );
        }

        daoUtil.free(  );

        return listDocumentPublication;
    }

    /**
     * Select the list of {@link DocumentPublication} objects specified by the portlet id and the status
     * @param nPortletId The portlet identifier
     * @param nStatus The status
     * @return The {@link DocumentPublication} objects list (empty list if no objects found)
     */
    public Collection<DocumentPublication> selectByPortletIdAndStatus( int nPortletId, int nStatus )
    {
        Collection<DocumentPublication> listDocumentPublication = new ArrayList<DocumentPublication>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PORTLET_ID_AND_STATUS );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.setInt( 2, nStatus );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DocumentPublication documentPublication = new DocumentPublication(  );
            documentPublication.setPortletId( nPortletId );
            documentPublication.setDocumentId( daoUtil.getInt( 1 ) );
            documentPublication.setDocumentOrder( daoUtil.getInt( 2 ) );
            documentPublication.setStatus( nStatus );
            documentPublication.setDatePublishing( daoUtil.getTimestamp( 3 ) );
            listDocumentPublication.add( documentPublication );
        }

        daoUtil.free(  );

        return listDocumentPublication;
    }

    /**
     * Select the list of {@link DocumentPublication} objects specified by the document id and the status
     * @param nDocumentId The document identifier
     * @param nStatus The status
     * @return The {@link DocumentPublication} objects list (empty list if no objects found)
     */
    public Collection<DocumentPublication> selectByDocumentIdAndStatus( int nDocumentId, int nStatus )
    {
        Collection<DocumentPublication> listDocumentPublication = new ArrayList<DocumentPublication>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DOCUMENT_ID_AND_STATUS );
        daoUtil.setInt( 1, nDocumentId );
        daoUtil.setInt( 2, nStatus );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DocumentPublication documentPublication = new DocumentPublication(  );
            documentPublication.setPortletId( daoUtil.getInt( 1 ) );
            documentPublication.setDocumentId( nDocumentId );
            documentPublication.setDocumentOrder( daoUtil.getInt( 2 ) );
            documentPublication.setStatus( nStatus );
            documentPublication.setDatePublishing( daoUtil.getTimestamp( 3 ) );
            listDocumentPublication.add( documentPublication );
        }

        daoUtil.free(  );

        return listDocumentPublication;
    }

    /**
     * Find the list of {@link DocumentPublication} objects specified the status and published at or after the specified date
     * @param datePublishing The publication date
     * @param nStatus The status
     * @return The {@link DocumentPublication} objects {@link Collection} ordered by documentOrder ascending. The list is empty if no objects found.
     */
    public Collection<DocumentPublication> selectSinceDatePublishingAndStatus( Date datePublishing, int nStatus )
    {
        Collection<DocumentPublication> listDocumentPublication = new ArrayList<DocumentPublication>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DATE_PUBLISHING_AND_STATUS );
        daoUtil.setTimestamp( 1, new Timestamp( datePublishing.getTime(  ) ) );
        daoUtil.setInt( 2, nStatus );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DocumentPublication documentPublication = new DocumentPublication(  );
            documentPublication.setPortletId( daoUtil.getInt( 1 ) );
            documentPublication.setDocumentId( daoUtil.getInt( 2 ) );
            documentPublication.setDocumentOrder( daoUtil.getInt( 3 ) );
            documentPublication.setStatus( nStatus );
            documentPublication.setDatePublishing( daoUtil.getTimestamp( 4 ) );
            listDocumentPublication.add( documentPublication );
        }

        daoUtil.free(  );

        return listDocumentPublication;
    }

    /**
     * Select the max order from a list of {@link DocumentPublication} specified by portlet id
     * @param nPortletId the portlet identifer
     * @return The max order of document
     */
    public int selectMaxDocumentOrder( int nPortletId )
    {
        int nOrder = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_MAX_ORDER );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nOrder = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nOrder;
    }

    /**
     * Return a document identifier in a distinct order
     *
     * @param nDocumentOrder The order number
     * @param nPortletId the portlet identifier
     * @return The order of the Document
     */
    public int selectDocumentIdByOrder( int nDocumentOrder, int nPortletId )
    {
        //    	FIXME The document_order column is not a primary key, so this method have to return a collection
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_MODIFY_ORDER_BY_ID );
        int nResult = nDocumentOrder;
        daoUtil.setInt( 1, nResult );
        daoUtil.setInt( 2, nPortletId );
        daoUtil.executeQuery(  );

        if ( !daoUtil.next(  ) )
        {
            // If number order doesn't exist
            nResult = 1;
        }
        else
        {
            nResult = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nResult;
    }
}
