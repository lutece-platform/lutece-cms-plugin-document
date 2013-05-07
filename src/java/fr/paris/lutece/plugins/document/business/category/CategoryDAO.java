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
package fr.paris.lutece.plugins.document.business.category;

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for Category objects
 */
public final class CategoryDAO implements ICategoryDAO
{
    // Constants
    private static final String SQL_QUERY_MAX_PK = " SELECT MAX(id_category) FROM document_category ";
    private static final String SQL_QUERY_SELECT_BY_NAME = " SELECT id_category, description, icon_content, icon_mime_type, workgroup_key FROM document_category WHERE document_category_name = ? ";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_category, document_category_name, description, icon_content, icon_mime_type, workgroup_key FROM document_category ORDER BY document_category_name";
    private static final String SQL_QUERY_INSERT = " INSERT INTO document_category ( id_category, document_category_name, description, icon_content, icon_mime_type, workgroup_key ) VALUES ( ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_SELECT = " SELECT document_category_name, description, icon_content, icon_mime_type ,workgroup_key FROM document_category WHERE id_category = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM document_category WHERE id_category = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE document_category SET document_category_name = ?, description = ?, icon_content = ?, icon_mime_type = ?, workgroup_key= ? WHERE id_category = ?";
    private static final String SQL_QUERY_DELETE_LINK_CATEGORY_DOCUMENT = " DELETE FROM document_category WHERE id_category = ? ";
    private static final String SQL_QUERY_SELECTALL_ID_DOCUMENT = " SELECT a.id_document FROM document_category_link a WHERE a.id_category = ? ";
    private static final String SQL_QUERY_DELETE_LINKS_CATEGORY = " DELETE FROM document_category_link WHERE id_category = ? ";
    private static final String SQL_QUERY_SELECT_COUNT_OF_DOCUMENT_ID = " SELECT COUNT(*) FROM document_category_link WHERE id_category = ?";

    // ImageResource queries
    private static final String SQL_QUERY_SELECT_RESOURCE_IMAGE = " SELECT icon_content, icon_mime_type FROM document_category WHERE id_category = ? ";

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
    * Load the list of Category
    * @return The Collection of Category (empty collection is no result)
    */
    public Collection<Category> selectAll(  )
    {
        int nParam;
        Collection<Category> listCategory = new ArrayList<Category>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nParam = 0;

            Category category = new Category(  );
            category.setId( daoUtil.getInt( ++nParam ) );
            category.setName( daoUtil.getString( ++nParam ) );
            category.setDescription( daoUtil.getString( ++nParam ) );
            category.setIconContent( daoUtil.getBytes( ++nParam ) );
            category.setIconMimeType( daoUtil.getString( ++nParam ) );
            category.setWorkgroup( daoUtil.getString( ++nParam ) );

            listCategory.add( category );
        }

        daoUtil.free(  );

        return listCategory;
    }

    /**
    * Insert a new Category
    * @param category The object category to insert
    */
    public void insert( Category category )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );

        daoUtil.setInt( ++nParam, getNewPrimaryKey(  ) );
        daoUtil.setString( ++nParam, category.getName(  ) );
        daoUtil.setString( ++nParam, category.getDescription(  ) );
        daoUtil.setBytes( ++nParam, category.getIconContent(  ) );
        daoUtil.setString( ++nParam, category.getIconMimeType(  ) );
        daoUtil.setString( ++nParam, category.getWorkgroup(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Auto increment the primary key for the new category
     * @return the new primary key for category
     */
    private int getNewPrimaryKey(  )
    {
        int nNewPrimaryKey = -1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_MAX_PK );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNewPrimaryKey = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return ++nNewPrimaryKey;
    }

    /**
    * Load the data of Category from the table
    * @param nIdCategory The identifier of the category
    * @return The Instance of the object Category
    */
    public Category load( int nIdCategory )
    {
        int nParam;
        Category category = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );
        daoUtil.setInt( 1, nIdCategory );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nParam = 0;
            category = new Category(  );
            category.setId( nIdCategory );
            category.setName( daoUtil.getString( ++nParam ) );
            category.setDescription( daoUtil.getString( ++nParam ) );
            category.setIconContent( daoUtil.getBytes( ++nParam ) );
            category.setIconMimeType( daoUtil.getString( ++nParam ) );
            category.setWorkgroup( daoUtil.getString( ++nParam ) );
        }

        daoUtil.free(  );

        return category;
    }

    /**
     * Delete a record from the table
     * @param nIdCategory The identifier of the object Category
     */
    public void delete( int nIdCategory )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( ++nParam, nIdCategory );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     * @param category The instance of the Category to update
     */
    public void store( Category category )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        daoUtil.setString( ++nParam, category.getName(  ) );
        daoUtil.setString( ++nParam, category.getDescription(  ) );
        daoUtil.setBytes( ++nParam, category.getIconContent(  ) );
        daoUtil.setString( ++nParam, category.getIconMimeType(  ) );
        daoUtil.setString( ++nParam, category.getWorkgroup(  ) );
        daoUtil.setInt( ++nParam, category.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of Category from the table
     * @param strCategoryName The name of the category
     * @return The Collection of Category (empty collection is no result)
     */
    public Collection<Category> selectByName( String strCategoryName )
    {
        int nParam;
        Collection<Category> listCategory = new ArrayList<Category>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_NAME );
        daoUtil.setString( 1, strCategoryName );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nParam = 0;

            Category category = new Category(  );
            category.setId( daoUtil.getInt( ++nParam ) );
            category.setName( strCategoryName );
            category.setDescription( daoUtil.getString( ++nParam ) );
            category.setIconContent( daoUtil.getBytes( ++nParam ) );
            category.setIconMimeType( daoUtil.getString( ++nParam ) );
            category.setWorkgroup( daoUtil.getString( ++nParam ) );

            listCategory.add( category );
        }

        daoUtil.free(  );

        return listCategory;
    }

    /**
     * Delete a link between category and document
     * @param nIdCategory The identifier of the object Category
     * @param nIdDocument The id of document
     */
    public void deleteLinkCategoryDocument( int nIdCategory, int nIdDocument )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LINK_CATEGORY_DOCUMENT );
        daoUtil.setInt( ++nParam, nIdDocument );
        daoUtil.setInt( ++nParam, nIdCategory );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Select a list of Id Documents for a specified category
     * @param nIdCategory The category name
     * @return The array of Id Document
     */
    public int[] selectAllIdDocument( int nIdCategory )
    {
        Collection<Integer> listIdDocument = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID_DOCUMENT );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listIdDocument.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        // Convert ArrayList to Int[]
        int[] arrayIdDocument = new int[listIdDocument.size(  )];
        int i = 0;

        for ( Integer nIdDocument : listIdDocument )
        {
            arrayIdDocument[i++] = nIdDocument.intValue(  );
        }

        return arrayIdDocument;
    }

    /**
     * Delete all links for a category
     * @param nIdCategory The identifier of the object Category
     */
    public void deleteLinksCategory( int nIdCategory )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LINKS_CATEGORY );
        daoUtil.setInt( ++nParam, nIdCategory );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Return the number of documents linked to a category
     * @param nIdCategory The category name
     * @return count of id document
     */
    public int selectCountIdDocuments( int nIdCategory )
    {
        int nCountDocumentsId = -1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COUNT_OF_DOCUMENT_ID );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nCountDocumentsId = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nCountDocumentsId;
    }

    /**
     * Return the image resource corresponding to the category id
     * @param nCategoryId The identifier of Category object
     * @return The image resource
     */
    public ImageResource loadImageResource( int nCategoryId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_RESOURCE_IMAGE );
        daoUtil.setInt( 1, nCategoryId );
        daoUtil.executeQuery(  );

        ImageResource image = null;

        if ( daoUtil.next(  ) )
        {
            image = new ImageResource(  );
            image.setImage( daoUtil.getBytes( 1 ) );
            image.setMimeType( daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return image;
    }
}
