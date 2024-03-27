/*
 * Copyright (c) 2002-2023, City of Paris
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
package fr.paris.lutece.plugins.document.business.history;

import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class provides Data Access methods for HistoryEvent objects
 */
public final class HistoryEventDAO implements IHistoryEventDAO
{
    // Constants
    private static final String SQL_QUERY_INSERT = " INSERT INTO document_history ( id_document, event_date, event_user, event_message_key, document_state_key, document_space ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM document_history WHERE id_document = ?  ";
    private static final String SQL_QUERY_SELECT_BY_DOCUMENT = " SELECT id_document, event_date, event_user, event_message_key, document_state_key, document_space FROM document_history WHERE id_document = ? ";
    private static final String SQL_QUERY_SELECT_BY_USER = " SELECT id_document, event_date, event_user, event_message_key, document_state_key, document_space FROM document_history WHERE event_user = ? ";

    /**
     * Insert a new record in the table.
     *
     * @param historyEvent
     *            The historyEvent object
     */
    public void insert( HistoryEvent historyEvent )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
        {
            daoUtil.setInt( 1, historyEvent.getIdDocument( ) );
            daoUtil.setTimestamp( 2, historyEvent.getDate( ) );
            daoUtil.setString( 3, historyEvent.getEventUser( ) );
            daoUtil.setString( 4, historyEvent.getEventMessageKey( ) );
            daoUtil.setString( 5, historyEvent.getDocumentStateKey( ) );
            daoUtil.setString( 6, historyEvent.getSpace( ) );

            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    /**
     * Delete a record from the table
     *
     * @param nDocumentId
     *            The id of the document
     */
    public void delete( int nDocumentId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nDocumentId );

            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    /**
     * Load the list of historyEvents
     *
     * @return The Collection of the HistoryEvents
     * @param nDocumentId
     *            The document Id
     */
    public List<HistoryEvent> selectEventListByDocument( int nDocumentId )
    {
        List<HistoryEvent> listHistoryEvents = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DOCUMENT ) )
        {
            daoUtil.setInt( 1, nDocumentId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                HistoryEvent event = new HistoryEvent( );
                event.setIdDocument( daoUtil.getInt( 1 ) );
                event.setDate( daoUtil.getTimestamp( 2 ) );
                event.setEventUser( daoUtil.getString( 3 ) );
                event.setEventMessageKey( daoUtil.getString( 4 ) );
                event.setDocumentStateKey( daoUtil.getString( 5 ) );
                event.setSpace( daoUtil.getString( 6 ) );

                listHistoryEvents.add( event );
            }

            daoUtil.free( );
        }
        return listHistoryEvents;
    }

    /**
     * Load the list of historyEvents
     *
     * @return The Collection of the HistoryEvents
     * @param strUserId
     *            The UserId
     */
    public Collection<HistoryEvent> selectEventListByUser( String strUserId )
    {
        Collection<HistoryEvent> listHistoryEvents = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_USER ) )
        {
            daoUtil.setString( 1, strUserId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                HistoryEvent event = new HistoryEvent( );
                event.setIdDocument( daoUtil.getInt( 1 ) );
                event.setDate( daoUtil.getTimestamp( 2 ) );
                event.setEventUser( daoUtil.getString( 3 ) );
                event.setEventMessageKey( daoUtil.getString( 4 ) );
                event.setDocumentStateKey( daoUtil.getString( 5 ) );
                event.setSpace( daoUtil.getString( 6 ) );

                listHistoryEvents.add( event );
            }

            daoUtil.free( );
        }
        return listHistoryEvents;
    }
}
