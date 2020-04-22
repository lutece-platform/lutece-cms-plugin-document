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
package fr.paris.lutece.plugins.document.business.autopublication;

import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for DocumentAutoPublication objects
 *
 */
public class DocumentAutoPublicationDAO implements IDocumentAutoPublicationDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO document_auto_publication ( id_portlet , id_space ) VALUES ( ? , ? )";
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY = "SELECT id_portlet, id_space FROM document_auto_publication WHERE id_portlet = ? AND id_space = ?";
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_portlet, id_space FROM document_auto_publication ";
    private static final String SQL_QUERY_SELECT_BY_PORTLET_ID = "SELECT id_space FROM document_auto_publication WHERE id_portlet = ? ";
    private static final String SQL_QUERY_SELECT_BY_SPACE_ID = "SELECT id_portlet FROM document_auto_publication WHERE id_space = ? ";

    //    private static final String SQL_QUERY_UPDATE = "UPDATE document_auto_publication SET WHERE id_portlet = ? AND id_space = ? ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM document_auto_publication WHERE id_portlet= ? AND id_space= ? ";
    private static final String SQL_QUERY_DELETE_ALL_SPACES = "DELETE FROM document_auto_publication WHERE id_portlet= ? ";

    /**
     * Deletes records from a Document Auto Publication object identifier in the table document_auto_publication
     *
     * @param nPortletId the portlet identifier
     * @param nSpaceId the {@link DocumentSpace} identifier
     */
    public void delete( int nPortletId, int nSpaceId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.setInt( 2, nSpaceId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete records from a portlet
     *
     * @param nPortletId the portlet identifier
     */
    public void deleteAllSpaces( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL_SPACES );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Insert a new record in the table document_auto_publication
     *
     * @param documentAutoPublication the instance of the
     *            DocumentAutoPublication object to insert
     */
    public void insert( DocumentAutoPublication documentAutoPublication )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );
        daoUtil.setInt( 1, documentAutoPublication.getIdPortlet(  ) );
        daoUtil.setInt( 2, documentAutoPublication.getIdSpace(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Loads the data of Document Auto Publication whose identifier is specified in parameter
     *
     * @param nPortletId The {@link Portlet} identifier
     * @param nSpaceId The {@link DocumentSpace} identifier
     * @return The {@link DocumentAutoPublication} object
     */
    public DocumentAutoPublication load( int nPortletId, int nSpaceId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PRIMARY_KEY );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.setInt( 2, nSpaceId );
        daoUtil.executeQuery(  );

        DocumentAutoPublication documentAutoPublication = null;

        if ( daoUtil.next(  ) )
        {
            documentAutoPublication = new DocumentAutoPublication(  );
            documentAutoPublication.setIdPortlet( daoUtil.getInt( 1 ) );
            documentAutoPublication.setIdSpace( daoUtil.getInt( 2 ) );
        }

        daoUtil.free(  );

        return documentAutoPublication;
    }

    /**
     * Loads all data Document Auto Publication
     *
     * @return The {@link Collection} of {@link DocumentAutoPublication} object
     */
    public Collection<DocumentAutoPublication> load(  )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL );
        daoUtil.executeQuery(  );

        Collection<DocumentAutoPublication> listDocumentAutoPublication = new ArrayList<DocumentAutoPublication>(  );

        while ( daoUtil.next(  ) )
        {
            DocumentAutoPublication documentAutoPublication = new DocumentAutoPublication(  );
            documentAutoPublication.setIdPortlet( daoUtil.getInt( 1 ) );
            documentAutoPublication.setIdSpace( daoUtil.getInt( 2 ) );
            listDocumentAutoPublication.add( documentAutoPublication );
        }

        daoUtil.free(  );

        return listDocumentAutoPublication;
    }

    /**
     * Load the list of Document Auto Publication whose portlet identifier is specified in parameter
     *
     * @param nPortletId The {@link Portlet} identifier
     * @return The {@link DocumentAutoPublication} object
     */
    public Collection<DocumentAutoPublication> selectByPortletId( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PORTLET_ID );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeQuery(  );

        Collection<DocumentAutoPublication> listDocumentAutoPublication = new ArrayList<DocumentAutoPublication>(  );

        while ( daoUtil.next(  ) )
        {
            DocumentAutoPublication documentAutoPublication = new DocumentAutoPublication(  );
            documentAutoPublication.setIdPortlet( nPortletId );
            documentAutoPublication.setIdSpace( daoUtil.getInt( 1 ) );
            listDocumentAutoPublication.add( documentAutoPublication );
        }

        daoUtil.free(  );

        return listDocumentAutoPublication;
    }

    /**
     * Load the list of Document Auto Publication whose {@link DocumentSpace} identifier is specified in parameter
     *
     * @param nSpaceId The {@link DocumentSpace} identifier
     * @return The {@link DocumentAutoPublication} object
     */
    public Collection<DocumentAutoPublication> selectBySpaceId( int nSpaceId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_SPACE_ID );
        daoUtil.setInt( 1, nSpaceId );
        daoUtil.executeQuery(  );

        Collection<DocumentAutoPublication> listDocumentAutoPublication = new ArrayList<DocumentAutoPublication>(  );

        while ( daoUtil.next(  ) )
        {
            DocumentAutoPublication documentAutoPublication = new DocumentAutoPublication(  );
            documentAutoPublication.setIdPortlet( daoUtil.getInt( 1 ) );
            documentAutoPublication.setIdSpace( nSpaceId );
            listDocumentAutoPublication.add( documentAutoPublication );
        }

        daoUtil.free(  );

        return listDocumentAutoPublication;
    }

    /**
     * Update the record in the table
     *
     * @param documentAutoPublication The DocumentAutoPublication to update
     */
    public void store( DocumentAutoPublication documentAutoPublication )
    {
        //        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        //
        //        daoUtil.setInt( 1, documentAutoPublication.getIdPortlet(  ) );
        //        daoUtil.setInt( 2, documentAutoPublication.getIdSpace(  ) );
        //
        //        daoUtil.executeUpdate(  );
        //
        //        daoUtil.free(  );
    }
}
