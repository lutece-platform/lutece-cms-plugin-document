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
package fr.paris.lutece.plugins.document.business.spaces;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class provides Data Access methods for DocumentSpace objects
 */
@ApplicationScoped
public final class DocumentSpaceDAO implements IDocumentSpaceDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = " SELECT a.id_space, a.id_parent, a.document_space_name, a.description, a.document_space_view, a.id_space_icon, b.icon_url, a.document_creation_allowed, a.workgroup_key "
            + " FROM document_space a, document_space_icon b " + " WHERE a.id_space_icon = b.id_space_icon AND id_space = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO document_space ( id_parent, document_space_name, description, document_space_view, id_space_icon, document_creation_allowed ,workgroup_key ) VALUES ( ?, ?, ?, ?, ?, ? ,?) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM document_space WHERE id_space = ?  ";
    private static final String SQL_QUERY_UPDATE = " UPDATE document_space SET id_parent = ?, document_space_name = ?, description = ?, document_space_view = ?, id_space_icon = ?, document_creation_allowed = ? ,workgroup_key= ? WHERE id_space = ?  ";
    private static final String SQL_QUERY_SELECT_CHILDS = " SELECT a.id_space, a.id_parent, a.document_space_name, a.description, a.document_space_view, a.id_space_icon, b.icon_url, a.document_creation_allowed ,a.workgroup_key"
            + " FROM document_space a, document_space_icon b WHERE a.id_space_icon = b.id_space_icon AND id_parent = ? ORDER BY a.document_space_name ";
    private static final String SQL_QUERY_SELECT_CHILDS_BY_CODE_TYPE = " SELECT a.id_space, a.id_parent, a.document_space_name, a.description, a.document_space_view, a.id_space_icon, b.icon_url, a.document_creation_allowed ,a.workgroup_key"
            + " FROM document_space a, document_space_icon b, document_space_document_type c "
            + " WHERE a.id_space_icon = b.id_space_icon AND id_parent = ? AND a.id_space = c.id_space AND c.code_document_type = ?"
            + " ORDER BY a.document_space_name ";
    private static final String SQL_QUERY_SELECT_SPACES_WITH_DOCUMENT_CREATION_IS_ALLOWED_BY_CODE_TYPE = "SELECT a.id_space, a.id_parent, a.document_space_name, a.description, a.document_space_view, a.id_space_icon, b.icon_url, a.document_creation_allowed ,a.workgroup_key"
            + " FROM document_space a, document_space_icon b, document_space_document_type c "
            + " WHERE a.id_space_icon = b.id_space_icon AND a.id_space = c.id_space AND c.code_document_type = ? AND a.document_creation_allowed = ?"
            + " ORDER BY a.document_space_name ";
    private static final String SQL_QUERY_SELECTALL = " SELECT a.id_space, a.id_parent, a.document_space_name, a.description, a.document_space_view, a.id_space_icon, b.icon_url, a.document_creation_allowed, a.workgroup_key "
            + " FROM document_space a, document_space_icon b WHERE a.id_space_icon = b.id_space_icon ORDER BY a.document_space_name ";
    private static final String SQL_QUERY_SELECTALL_VIEWTYPE = " SELECT code_view , name_key FROM document_view";
    private static final String SQL_QUERY_SELECTALL_ICONS = " SELECT id_space_icon , icon_url FROM document_space_icon";
    private static final String SQL_QUERY_INSERT_DOCUMENT_TYPE = "INSERT INTO document_space_document_type ( id_space , code_document_type ) VALUES ( ? , ? )";
    private static final String SQL_QUERY_DELETE_DOCUMENT_TYPE = " DELETE FROM document_space_document_type WHERE id_space = ?  ";
    private static final String SQL_QUERY_SELECT_DOCUMENT_TYPE = " SELECT code_document_type FROM document_space_document_type WHERE id_space = ?  ";
    private static final String SQL_QUERY_SELECT_SPACE_DOCUMENT_TYPE = " SELECT a.code_document_type, a.document_type_name "
            + " FROM document_type a, document_space_document_type b " + " WHERE a.code_document_type = b.code_document_type AND b.id_space = ?"
            + " ORDER BY a.document_type_name";

    /**
     * Insert a new record in the table.
     *
     * @param space
     *            The space object
     */
    @Override
    public void insert( DocumentSpace space )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS ) )
        {
            daoUtil.setInt( 1, space.getIdParent( ) );
            daoUtil.setString( 2, space.getName( ) );
            daoUtil.setString( 3, space.getDescription( ) );
            daoUtil.setString( 4, space.getViewType( ) );
            daoUtil.setInt( 5, space.getIdIcon( ) );
            daoUtil.setInt( 6, space.isDocumentCreationAllowed( ) ? 1 : 0 );
            daoUtil.setString( 7, space.getWorkgroup( ) );
            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                space.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        // insert allowed document types
        insertAllowedDocumenTypes( space );
    }

    /**
     * Insert allowed document types to a space
     * 
     * @param space
     *            The space
     */
    private void insertAllowedDocumenTypes( DocumentSpace space )
    {
        String [ ] doctypes = space.getAllowedDocumentTypes( );

        for ( int i = 0; i < doctypes.length; i++ )
        {
            try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_DOCUMENT_TYPE ) )
            {
                daoUtil.setInt( 1, space.getId( ) );
                daoUtil.setString( 2, doctypes[i] );

                daoUtil.executeUpdate( );
            }
        }
    }

    /**
     * Load the data of DocumentSpace from the table
     *
     * @param nDocumentSpaceId
     *            The identifier of DocumentSpace
     * @return the instance of the DocumentSpace
     */    
    @Override
    public DocumentSpace load( int nDocumentSpaceId )
    {
        DocumentSpace space = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nDocumentSpaceId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                space = new DocumentSpace( );
                space.setId( daoUtil.getInt( 1 ) );
                space.setIdParent( daoUtil.getInt( 2 ) );
                space.setName( daoUtil.getString( 3 ) );
                space.setDescription( daoUtil.getString( 4 ) );
                space.setViewType( daoUtil.getString( 5 ) );
                space.setIdIcon( daoUtil.getInt( 6 ) );
                space.setIconUrl( daoUtil.getString( 7 ) );
                space.setDocumentCreationAllowed( daoUtil.getInt( 8 ) != 0 );
                space.setWorkgroup( daoUtil.getString( 9 ) );
            }
        }
        if ( space != null )
        {
            loadAllowedDocumentTypes( space );
        }

        return space;
    }

    /**
     * Load allowed document types for a space
     * 
     * @param space
     *            The space
     */
    private void loadAllowedDocumentTypes( DocumentSpace space )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DOCUMENT_TYPE ) )
        {
            daoUtil.setInt( 1, space.getId( ) );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                space.addAllowedDocumentType( daoUtil.getString( 1 ) );
            }
        }
    }

    /**
     * Delete a record from the table
     *
     * @param nSpaceId
     *            The Id to delete
     */
    @Override
    public void delete( int nSpaceId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nSpaceId );
            daoUtil.executeUpdate( );
        }
        deleteAllowedDocumentTypes( nSpaceId );
    }

    /**
     * Delete allowed document types
     * 
     * @param nSpaceId
     *            The space identifier
     */
    private void deleteAllowedDocumentTypes( int nSpaceId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_DOCUMENT_TYPE ) )
        {
            daoUtil.setInt( 1, nSpaceId );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the record in the table
     * 
     * @param space
     *            The reference of space
     */
    @Override
    public void store( DocumentSpace space )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            daoUtil.setInt( 1, space.getIdParent( ) );
            daoUtil.setString( 2, space.getName( ) );
            daoUtil.setString( 3, space.getDescription( ) );
            daoUtil.setString( 4, space.getViewType( ) );
            daoUtil.setInt( 5, space.getIdIcon( ) );
            daoUtil.setInt( 6, space.isDocumentCreationAllowed( ) ? 1 : 0 );
            daoUtil.setString( 7, space.getWorkgroup( ) );
            daoUtil.setInt( 8, space.getId( ) );

            daoUtil.executeUpdate( );
        }
        deleteAllowedDocumentTypes( space.getId( ) );
        insertAllowedDocumenTypes( space );
    }

    /**
     * Load the list of documentSpaces childs
     *
     * @param strCodeType
     *            the document type filter if needed (null if not)
     * @param nSpaceId
     *            The space identifier
     * @return The Collection of the DocumentSpaces
     */
    @Override
    public List<DocumentSpace> selectChilds( int nSpaceId, String strCodeType )
    {
        List<DocumentSpace> listDocumentSpaces = new ArrayList<>( );

        try (DAOUtil daoUtil = initSelectChildDAOUtil(nSpaceId, strCodeType))
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentSpace space = new DocumentSpace( );
                space.setId( daoUtil.getInt( 1 ) );
                space.setIdParent( daoUtil.getInt( 2 ) );
                space.setName( daoUtil.getString( 3 ) );
                space.setDescription( daoUtil.getString( 4 ) );
                space.setViewType( daoUtil.getString( 5 ) );
                space.setIdIcon( daoUtil.getInt( 6 ) );
                space.setIconUrl( daoUtil.getString( 7 ) );
                space.setDocumentCreationAllowed( daoUtil.getInt( 8 ) != 0 );
                space.setWorkgroup( daoUtil.getString( 9 ) );
                listDocumentSpaces.add( space );
            }
        }
        return listDocumentSpaces;
    }

    /**
     * Select the right daoUtil.
     *
     * @param strCodeType
     *            the document type filter if needed (null if not)
     * @param nSpaceId
     *            The space identifier
     * @return the right daoUtil.
     */
    private DAOUtil initSelectChildDAOUtil(int nSpaceId, String strCodeType) {
        DAOUtil daoUtil =null;
        if ( strCodeType != null )
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_CHILDS_BY_CODE_TYPE );
            daoUtil.setInt( 1, nSpaceId );
            daoUtil.setString( 2, strCodeType );
        }
        else
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_CHILDS );
            daoUtil.setInt( 1, nSpaceId );
        }
        return daoUtil;
    }

    /**
     * Load the list of documentSpaces authorizing the selected document type
     *
     * @param strCodeType
     *            the document type filter
     * @return The Collection of the DocumentSpaces
     */
    @Override
    public List<DocumentSpace> selectSpacesAllowingDocumentCreationByDocumentType( String strCodeType, int createDocumentIsAllowed )
    {
        List<DocumentSpace> listDocumentSpaces = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SPACES_WITH_DOCUMENT_CREATION_IS_ALLOWED_BY_CODE_TYPE ) )
        {
            daoUtil.setString( 1, strCodeType );
            daoUtil.setInt( 2, createDocumentIsAllowed );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentSpace space = new DocumentSpace( );
                space.setId( daoUtil.getInt( 1 ) );
                space.setIdParent( daoUtil.getInt( 2 ) );
                space.setName( daoUtil.getString( 3 ) );
                space.setDescription( daoUtil.getString( 4 ) );
                space.setViewType( daoUtil.getString( 5 ) );
                space.setIdIcon( daoUtil.getInt( 6 ) );
                space.setIconUrl( daoUtil.getString( 7 ) );
                space.setDocumentCreationAllowed( daoUtil.getInt( 8 ) != 0 );
                space.setWorkgroup( daoUtil.getString( 9 ) );
                listDocumentSpaces.add( space );
            }
        }
        return listDocumentSpaces;
    }

    /**
     * Load the list of documentSpaces
     * 
     * @return The Collection of the DocumentSpaces
     */
    @Override
    public ReferenceList getDocumentSpaceList( )
    {
        ReferenceList list = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentSpace space = new DocumentSpace( );
                space.setId( daoUtil.getInt( 1 ) );
                space.setName( daoUtil.getString( 3 ) );

                list.addItem( space.getId( ), space.getName( ) );
            }
        }
        return list;
    }

    /**
     * Load the list of documentSpaces
     * 
     * @param locale
     *            The locale
     * @return The Collection of the DocumentSpaces
     */
    @Override
    public ReferenceList getViewTypeList( Locale locale )
    {
        ReferenceList list = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_VIEWTYPE ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                String strCodeView = daoUtil.getString( 1 );
                String strViewNameKey = daoUtil.getString( 2 );
                list.addItem( strCodeView, I18nService.getLocalizedString( strViewNameKey, locale ) );
            }
        }
        return list;
    }

    /**
     * Gets a list of icons available or space customization
     * 
     * @return A list of icons
     */
    @Override
    public ReferenceList getIconsList( )
    {
        ReferenceList list = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ICONS ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nIconId = daoUtil.getInt( 1 );
                String strIconUrl = daoUtil.getString( 2 );
                list.addItem( nIconId, strIconUrl );
            }
        }
        return list;
    }

    /**
     * Select all spaces
     * 
     * @return A collection of all spaces.
     */
    @Override
    public List<DocumentSpace> selectAll( )
    {
        List<DocumentSpace> listDocumentSpaces = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentSpace space = new DocumentSpace( );
                space.setId( daoUtil.getInt( 1 ) );
                space.setIdParent( daoUtil.getInt( 2 ) );
                space.setName( daoUtil.getString( 3 ) );
                space.setDescription( daoUtil.getString( 4 ) );
                space.setViewType( daoUtil.getString( 5 ) );
                space.setIdIcon( daoUtil.getInt( 6 ) );
                space.setIconUrl( daoUtil.getString( 7 ) );
                space.setDocumentCreationAllowed( daoUtil.getInt( 8 ) != 0 );
                space.setWorkgroup( daoUtil.getString( 9 ) );
                listDocumentSpaces.add( space );
            }
        }
        return listDocumentSpaces;
    }

    /**
     * Returns all allowed document types for a given space
     * 
     * @param nSpaceId
     *            The space Id
     * @return Allowed documents types as a ReferenceList
     */
    @Override
    public ReferenceList getAllowedDocumentTypes( int nSpaceId )
    {
        ReferenceList list = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SPACE_DOCUMENT_TYPE ) )
        {
            daoUtil.setInt( 1, nSpaceId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                list.addItem( daoUtil.getString( 1 ), daoUtil.getString( 2 ) );
            }
        }
        return list;
    }
}
