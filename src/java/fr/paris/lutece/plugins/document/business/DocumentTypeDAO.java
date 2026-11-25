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

import fr.paris.lutece.plugins.document.business.attributes.DocumentAttributeHome;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class provides Data Access methods for DocumentType objects
 */
@ApplicationScoped
public final class DocumentTypeDAO implements IDocumentTypeDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT code_document_type, document_type_name, description, thumbnail_attr_id, default_thumbnail_url, admin_template, content_template, metadata_handler FROM document_type WHERE code_document_type = ?  ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO document_type ( code_document_type, document_type_name, description, thumbnail_attr_id, default_thumbnail_url, metadata_handler  ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM document_type WHERE code_document_type = ?  ";
    private static final String SQL_QUERY_UPDATE = "UPDATE document_type SET code_document_type = ?, document_type_name = ?, description = ?, thumbnail_attr_id = ?, default_thumbnail_url = ?, metadata_handler = ? WHERE code_document_type = ?  ";
    private static final String SQL_QUERY_SELECTALL = "SELECT code_document_type, document_type_name, description, thumbnail_attr_id, default_thumbnail_url, metadata_handler  FROM document_type ";
    private static final String SQL_QUERY_CHECK_DOCUMENTS = "SELECT id_document FROM document WHERE code_document_type = ? ";
    private static final String SQL_QUERY_REORDER_ATTRIBUTES = "UPDATE document_type_attr SET attr_order = ? WHERE id_document_attr = ? ";
    private static final String SQL_QUERY_UPDATE_TEMPLATES = "UPDATE document_type SET admin_template = ?, content_template = ? WHERE code_document_type = ? ";
    private static final String SQL_QUERY_SELECT_CODES_WITH_LEGACY_STYLESHEETS = "SELECT code_document_type FROM document_type WHERE admin_xsl IS NOT NULL OR content_service_xsl IS NOT NULL ORDER BY code_document_type ";
    private static final String SQL_QUERY_SELECT_LEGACY_STYLESHEETS = "SELECT admin_xsl, content_service_xsl FROM document_type WHERE code_document_type = ? ";
    private static final String SQL_QUERY_DELETE_LEGACY_STYLESHEETS = "UPDATE document_type SET admin_xsl = NULL, content_service_xsl = NULL WHERE code_document_type = ? ";

    /**
     * Insert a new record in the table.
     *
     * @param documentType
     *            The documentType object
     */
    @Override
    public void insert( DocumentType documentType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
        {
            daoUtil.setString( 1, documentType.getCode( ) );
            daoUtil.setString( 2, documentType.getName( ) );
            daoUtil.setString( 3, documentType.getDescription( ) );
            daoUtil.setInt( 4, documentType.getThumbnailAttributeId( ) );
            daoUtil.setString( 5, documentType.getDefaultThumbnailUrl( ) );
            daoUtil.setString( 6, documentType.getMetadataHandler( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the data of DocumentType from the table
     * 
     * @param strDocumentTypeCode
     *            the code
     * @return the instance of the DocumentType
     */
    @Override
    public DocumentType load( String strDocumentTypeCode )
    {
        DocumentType documentType = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setString( 1, strDocumentTypeCode );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                documentType = new DocumentType( );
                documentType.setCode( daoUtil.getString( 1 ) );
                documentType.setName( daoUtil.getString( 2 ) );
                documentType.setDescription( daoUtil.getString( 3 ) );
                documentType.setThumbnailAttributeId( daoUtil.getInt( 4 ) );
                documentType.setDefaultThumbnailUrl( daoUtil.getString( 5 ) );
                documentType.setAdminTemplate( daoUtil.getString( 6 ) );
                documentType.setContentTemplate( daoUtil.getString( 7 ) );
                documentType.setMetadataHandler( daoUtil.getString( 8 ) );
                DocumentAttributeHome.setDocumentTypeAttributes( documentType );
            }

        }
        return documentType;
    }

    /**
     * Delete a record from the table
     * 
     * @param strCode
     *            the code type
     */
    @Override
    public void delete( String strCode )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setString( 1, strCode );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the record in the table
     * 
     * @param documentType
     *            The reference of documentType
     */
    @Override
    public void store( DocumentType documentType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            daoUtil.setString( 1, documentType.getCode( ) );
            daoUtil.setString( 2, documentType.getName( ) );
            daoUtil.setString( 3, documentType.getDescription( ) );
            daoUtil.setInt( 4, documentType.getThumbnailAttributeId( ) );
            daoUtil.setString( 5, documentType.getDefaultThumbnailUrl( ) );
            daoUtil.setString( 6, documentType.getMetadataHandler( ) );
            daoUtil.setString( 7, documentType.getOldCode( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the list of documentTypes
     * 
     * @return The Collection of the DocumentTypes
     */
    @Override
    public Collection<DocumentType> selectDocumentTypeList( )
    {
        Collection<DocumentType> listDocumentTypes = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentType documentType = new DocumentType( );
                documentType.setCode( daoUtil.getString( 1 ) );
                documentType.setName( daoUtil.getString( 2 ) );
                documentType.setDescription( daoUtil.getString( 3 ) );
                documentType.setThumbnailAttributeId( daoUtil.getInt( 4 ) );
                documentType.setDefaultThumbnailUrl( daoUtil.getString( 5 ) );
                documentType.setMetadataHandler( daoUtil.getString( 6 ) );

                listDocumentTypes.add( documentType );
            }
        }
        return listDocumentTypes;
    }

    /**
     * Load the Referencelist of documentTypes
     * 
     * @return listDocumentTypes
     */
    @Override
    public ReferenceList getDocumentTypeList( )
    {
        ReferenceList listDocumentTypes = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentType documentType = new DocumentType( );
                documentType.setCode( daoUtil.getString( 1 ) );
                documentType.setName( daoUtil.getString( 2 ) );
                listDocumentTypes.addItem( documentType.getCode( ), documentType.getName( ) );
            }
        }
        return listDocumentTypes;
    }

    /**
     *
     * @param strCode
     *            The code type
     * @return bCheck the boolean
     */
    @Override
    public boolean checkDocuments( String strCode )
    {
        boolean bCheck;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_DOCUMENTS ) )
        {
            daoUtil.setString( 1, strCode );
            daoUtil.executeQuery( );

            bCheck = daoUtil.next( );
        }
        return bCheck;
    }

    /**
     *
     * @param nIdAttribute1
     *            the attribute order
     * @param nOrderAttribute1
     *            the attribute order
     * @param nIdAttribute2
     *            the attribute order
     * @param nOrderAttribute2
     *            the attribute order
     */
    @Override
    public void reorderAttributes( int nIdAttribute1, int nOrderAttribute1, int nIdAttribute2, int nOrderAttribute2 )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REORDER_ATTRIBUTES ) )
        {
            daoUtil.setInt( 1, nOrderAttribute1 );
            daoUtil.setInt( 2, nIdAttribute1 );
            daoUtil.executeUpdate( );
            daoUtil.setInt( 1, nOrderAttribute2 );
            daoUtil.setInt( 2, nIdAttribute2 );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeTemplates( String strCodeType, String strAdminTemplate, String strContentTemplate )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_TEMPLATES ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, strAdminTemplate );
            daoUtil.setString( nIndex++, strContentTemplate );
            daoUtil.setString( nIndex, strCodeType );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> selectCodesWithLegacyStylesheets( )
    {
        List<String> listCodes = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_CODES_WITH_LEGACY_STYLESHEETS ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listCodes.add( daoUtil.getString( 1 ) );
            }
        }

        return listCodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LegacyStylesheets loadLegacyStylesheets( String strCodeType )
    {
        LegacyStylesheets stylesheets = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LEGACY_STYLESHEETS ) )
        {
            daoUtil.setString( 1, strCodeType );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                stylesheets = new LegacyStylesheets( toText( daoUtil.getBytes( 1 ) ), toText( daoUtil.getBytes( 2 ) ) );
            }
        }

        return stylesheets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteLegacyStylesheets( String strCodeType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LEGACY_STYLESHEETS ) )
        {
            daoUtil.setString( 1, strCodeType );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Decodes a stylesheet stored as bytes
     *
     * @param baStylesheet
     *            The stylesheet, or null
     * @return The stylesheet as a string, or null
     */
    private static String toText( byte [ ] baStylesheet )
    {
        return ( baStylesheet != null ) ? new String( baStylesheet, StandardCharsets.UTF_8 ) : null;
    }
}
