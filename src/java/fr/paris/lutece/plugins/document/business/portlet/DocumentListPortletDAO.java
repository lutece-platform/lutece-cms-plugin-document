/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.document.business.portlet;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for ArticlesListPortlet objects
 */
public final class DocumentListPortletDAO implements IDocumentListPortletDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO document_list_portlet ( id_portlet , code_document_type ) VALUES ( ? , ? )";
    private static final String SQL_QUERY_SELECT = "SELECT id_portlet , code_document_type FROM document_list_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE document_list_portlet SET id_portlet = ?, code_document_type = ? WHERE id_portlet = ? ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM document_list_portlet WHERE id_portlet= ? ";
    private static final String SQL_QUERY_DELETE_PUBLISHED_DOCUMENT_PORTLET = " DELETE FROM document_published WHERE id_portlet = ? ";
    private static final String SQL_QUERY_SELECT_DOCUMENTS_BY_TYPE_AND_CATEGORY = "SELECT DISTINCT b.id_portlet , a.name " +
        "FROM document_list_portlet b " +
        "LEFT JOIN document_published c ON b.id_portlet = c.id_portlet AND c.id_document= ? " +
        "INNER JOIN core_portlet a ON b.id_portlet = a.id_portlet " +
        "LEFT OUTER JOIN document_category_list_portlet d ON b.id_portlet = d.id_portlet " +
        "WHERE c.id_portlet IS NULL AND b.code_document_type = ? AND (d.id_category IN (SELECT e.id_category " +
        "FROM document_category_link e WHERE e.id_document = ?) OR d.id_category IS NULL) ";
    private static final String SQL_QUERY_CHECK_IS_ALIAS = "SELECT id_alias FROM core_portlet_alias WHERE id_alias = ?";

    //Category
    private static final String SQL_QUERY_INSERT_CATEGORY_PORTLET = "INSERT INTO document_category_list_portlet ( id_portlet , id_category ) VALUES ( ? , ? )";
    private static final String SQL_QUERY_DELETE_CATEGORY_PORTLET = " DELETE FROM document_category_list_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_DELETE_AUTO_PUBLICATION_PORTLET = " DELETE FROM document_auto_publication WHERE id_portlet = ? ";
    private static final String SQL_QUERY_SELECT_CATEGORY_PORTLET = "SELECT id_category FROM document_category_list_portlet WHERE id_portlet = ? ";

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Insert a new record in the table portlet_articles_list
     *
     * @param portlet the instance of the Portlet object to insert
     */
    public void insert( Portlet portlet )
    {
        DocumentListPortlet p = (DocumentListPortlet) portlet;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );
        daoUtil.setInt( 1, p.getId(  ) );
        daoUtil.setString( 2, p.getDocumentTypeCode(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        insertCategory( portlet );
    }

    /**
     * Insert a list of category for a specified portlet
     * @param portlet the DocumentListPortlet to insert
     */
    private void insertCategory( Portlet portlet )
    {
        DocumentListPortlet p = (DocumentListPortlet) portlet;

        if ( p.getIdCategory(  ) != null )
        {
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_CATEGORY_PORTLET );

            for ( int nIdCategory : p.getIdCategory(  ) )
            {
                daoUtil.setInt( 1, p.getId(  ) );
                daoUtil.setInt( 2, nIdCategory );

                daoUtil.executeUpdate(  );
            }

            daoUtil.free(  );
        }
    }

    /**
    * Deletes records for a portlet identifier in the tables document_list_portlet, document_published,
    * document_category_list_portlet
    *
    * @param nPortletId the portlet identifier
    */
    public void delete( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        daoUtil = new DAOUtil( SQL_QUERY_DELETE_PUBLISHED_DOCUMENT_PORTLET );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        deleteCategories( nPortletId );
        deleteAutoPublication( nPortletId );
    }

    /**
     * Delete categories for the specified portlet
     * @param nPortletId The portlet identifier
     */
    private void deleteCategories( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_CATEGORY_PORTLET );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete auto publication records for the specified portlet
     * @param nPortletId The portlet identifier
     */
    private void deleteAutoPublication( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_AUTO_PUBLICATION_PORTLET );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Loads the data of Document List Portlet whose identifier is specified in parameter
     *
     * @param nPortletId The Portlet identifier
     * @return theDocumentListPortlet object
     */
    public Portlet load( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeQuery(  );

        DocumentListPortlet portlet = new DocumentListPortlet(  );

        if ( daoUtil.next(  ) )
        {
            portlet.setId( daoUtil.getInt( 1 ) );
            portlet.setDocumentTypeCode( daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        portlet.setIdCategory( loadCategories( nPortletId ) );

        return portlet;
    }

    /**
     * Load a list of Id categories
     * @param nPortletId
     * @return Array of categories
     */
    private int[] loadCategories( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_CATEGORY_PORTLET );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeQuery(  );

        Collection<Integer> nListIdCategory = new ArrayList<Integer>(  );

        while ( daoUtil.next(  ) )
        {
            nListIdCategory.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        int[] nArrayIdCategory = new int[nListIdCategory.size(  )];
        int i = 0;

        for ( Integer nIdCategory : nListIdCategory )
        {
            nArrayIdCategory[i++] = nIdCategory.intValue(  );
        }

        return nArrayIdCategory;
    }

    /**
     * Update the record in the table
     *
     * @param portlet A portlet
     */
    public void store( Portlet portlet )
    {
        DocumentListPortlet p = (DocumentListPortlet) portlet;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        daoUtil.setInt( 1, p.getId(  ) );
        daoUtil.setString( 2, p.getDocumentTypeCode(  ) );
        daoUtil.setInt( 3, p.getId(  ) );

        daoUtil.executeUpdate(  );

        daoUtil.free(  );

        deleteCategories( p.getId(  ) );
        insertCategory( p );
    }

    /**
     * Load the list of documentTypes
     * @param strCodeDocumentType The code
     * @return The Collection of the DocumentTypes
     */
    public Collection selectDocumentTypeListByCodeAndCategory( int nDocumentId, String strCodeDocumentType )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DOCUMENTS_BY_TYPE_AND_CATEGORY );
        daoUtil.setInt( 1, nDocumentId );
        daoUtil.setString( 2, strCodeDocumentType );
        daoUtil.setInt( 3, nDocumentId );
        daoUtil.executeQuery(  );

        ReferenceList list = new ReferenceList(  );

        while ( daoUtil.next(  ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Tests if is a portlet is portlet type alias
     *
     * @param nPortletId The identifier of the document
     * @return true if the portlet is alias, false otherwise
     */
    public boolean checkIsAliasPortlet( int nPortletId )
    {
        boolean bIsAlias = false;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_IS_ALIAS );

        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            bIsAlias = true;
        }

        daoUtil.free(  );

        return bIsAlias;
    }
}
