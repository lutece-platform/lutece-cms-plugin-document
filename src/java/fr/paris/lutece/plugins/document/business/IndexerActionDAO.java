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
package fr.paris.lutece.plugins.document.business;

import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Indexer Action objects
 */
public final class IndexerActionDAO implements IIndexerActionDAO
{
    // Constants
    public static final String CONSTANT_WHERE = " WHERE ";
    public static final String CONSTANT_AND = " AND ";
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_action ) FROM document_indexer_action";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_action,id_record,id_task" + " FROM document_indexer_action WHERE id_action = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO document_indexer_action( id_action,id_record,id_task)" + " VALUES(?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM document_indexer_action WHERE id_action = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE document_indexer_action SET id_action=?,id_record=?,id_task=? WHERE id_action = ? ";
    private static final String SQL_QUERY_SELECT = "SELECT id_action,id_record,id_task" + " FROM document_indexer_action  ";
    private static final String SQL_QUERY_DELETE_ALL = "DELETE from document_indexer_action";
    private static final String SQL_FILTER_ID_TASK = " id_task = ? ";

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.document.business.IIndexerActionDAO#newPrimaryKey()
     */
    public int newPrimaryKey( )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK );
        daoUtil.executeQuery( );

        int nKey;

        if ( !daoUtil.next( ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free( );

        return nKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.document.business.IIndexerActionDAO#insert(fr.paris.lutece.plugins.document.business.IndexerAction)
     */
    public synchronized void insert( IndexerAction indexerAction )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );
        daoUtil.setInt( 2, indexerAction.getIdDocument( ) );
        daoUtil.setInt( 3, indexerAction.getIdTask( ) );

        indexerAction.setIdAction( newPrimaryKey( ) );
        daoUtil.setInt( 1, indexerAction.getIdAction( ) );

        daoUtil.executeUpdate( );

        daoUtil.free( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.document.business.IIndexerActionDAO#load(int)
     */
    public IndexerAction load( int nId )
    {
        IndexerAction indexerAction = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            indexerAction = new IndexerAction( );
            indexerAction.setIdAction( daoUtil.getInt( 1 ) );
            indexerAction.setIdDocument( daoUtil.getInt( 2 ) );
            indexerAction.setIdTask( daoUtil.getInt( 3 ) );
        }

        daoUtil.free( );

        return indexerAction;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.document.business.IIndexerActionDAO#delete(int)
     */
    public void delete( int nId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( 1, nId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.document.business.IIndexerActionDAO#delete(int)
     */
    public void deleteAll( )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.document.business.IIndexerActionDAO#store(fr.paris.lutece.plugins.document.business.IndexerAction)
     */
    public void store( IndexerAction indexerAction )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        daoUtil.setInt( 1, indexerAction.getIdAction( ) );
        daoUtil.setInt( 2, indexerAction.getIdDocument( ) );
        daoUtil.setInt( 3, indexerAction.getIdTask( ) );

        daoUtil.setInt( 4, indexerAction.getIdAction( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.document.business.IIndexerActionDAO#selectList(fr.paris.lutece.plugins.document.business.IndexerActionlFilter)
     */
    public List<IndexerAction> selectList( IndexerActionFilter filter )
    {
        List<IndexerAction> indexerActionList = new ArrayList<IndexerAction>( );
        IndexerAction indexerAction = null;
        List<String> listStrFilter = new ArrayList<String>( );

        if ( filter.containsIdTask( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_TASK );
        }

        String strSQL = buildRequestWithFilter( SQL_QUERY_SELECT, listStrFilter, null );

        DAOUtil daoUtil = new DAOUtil( strSQL );

        int nIndex = 1;

        if ( filter.containsIdTask( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdTask( ) );
            nIndex++;
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            indexerAction = new IndexerAction( );
            indexerAction.setIdAction( daoUtil.getInt( 1 ) );
            indexerAction.setIdDocument( daoUtil.getInt( 2 ) );
            indexerAction.setIdTask( daoUtil.getInt( 3 ) );

            indexerActionList.add( indexerAction );
        }

        daoUtil.free( );

        return indexerActionList;
    }

    /**
     * Builds a query with filters placed in parameters
     * 
     * @param strSelect
     *            the select of the query
     * @param listStrFilter
     *            the list of filter to add in the query
     * @param strOrder
     *            the order by of the query
     * @return a query
     */
    public static String buildRequestWithFilter( String strSelect, List<String> listStrFilter, String strOrder )
    {
        StringBuffer strBuffer = new StringBuffer( );
        strBuffer.append( strSelect );

        int nCount = 0;

        for ( String strFilter : listStrFilter )
        {
            if ( ++nCount == 1 )
            {
                strBuffer.append( CONSTANT_WHERE );
            }

            strBuffer.append( strFilter );

            if ( nCount != listStrFilter.size( ) )
            {
                strBuffer.append( CONSTANT_AND );
            }
        }

        if ( strOrder != null )
        {
            strBuffer.append( strOrder );
        }

        return strBuffer.toString( );
    }
}
