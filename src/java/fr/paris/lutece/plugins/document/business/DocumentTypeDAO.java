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
package fr.paris.lutece.plugins.document.business;

import fr.paris.lutece.plugins.document.business.attributes.DocumentAttributeHome;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for DocumentType objects
 */
public final class DocumentTypeDAO implements IDocumentTypeDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT code_document_type, document_type_name, description, thumbnail_attr_id, default_thumbnail_url, admin_xsl, content_service_xsl, metadata_handler FROM document_type WHERE code_document_type = ?  ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO document_type ( code_document_type, document_type_name, description, thumbnail_attr_id, default_thumbnail_url, metadata_handler  ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM document_type WHERE code_document_type = ?  ";
    private static final String SQL_QUERY_UPDATE = "UPDATE document_type SET code_document_type = ?, document_type_name = ?, description = ?, thumbnail_attr_id = ?, default_thumbnail_url = ?, metadata_handler = ? WHERE code_document_type = ?  ";
    private static final String SQL_QUERY_SELECTALL = "SELECT code_document_type, document_type_name, description, thumbnail_attr_id, default_thumbnail_url, metadata_handler  FROM document_type ";
    private static final String SQL_QUERY_CHECK_DOCUMENTS = "SELECT id_document FROM document WHERE code_document_type = ? ";
    private static final String SQL_QUERY_REORDER_ATTRIBUTES = "UPDATE document_type_attr SET attr_order = ? WHERE id_document_attr = ? ";
    private static final String SQL_QUERY_UPDATE_ADMIN_STYLESHEET = "UPDATE document_type SET admin_xsl = ? WHERE code_document_type = ? ";
    private static final String SQL_QUERY_UPDATE_CONTENT_STYLESHEET = "UPDATE document_type SET content_service_xsl = ? WHERE code_document_type = ? ";

    /**
     * Insert a new record in the table.
     *
     * @param documentType The documentType object
     */
    public void insert( DocumentType documentType )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );
        daoUtil.setString( 1, documentType.getCode(  ) );
        daoUtil.setString( 2, documentType.getName(  ) );
        daoUtil.setString( 3, documentType.getDescription(  ) );
        daoUtil.setInt( 4, documentType.getThumbnailAttributeId(  ) );
        daoUtil.setString( 5, documentType.getDefaultThumbnailUrl(  ) );
        daoUtil.setString( 6, documentType.getMetadataHandler(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of DocumentType from the table
     * @param strDocumentTypeCode the code
     * @return the instance of the DocumentType
     */
    public DocumentType load( String strDocumentTypeCode )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );
        daoUtil.setString( 1, strDocumentTypeCode );
        daoUtil.executeQuery(  );

        DocumentType documentType = null;

        if ( daoUtil.next(  ) )
        {
            documentType = new DocumentType(  );
            documentType.setCode( daoUtil.getString( 1 ) );
            documentType.setName( daoUtil.getString( 2 ) );
            documentType.setDescription( daoUtil.getString( 3 ) );
            documentType.setThumbnailAttributeId( daoUtil.getInt( 4 ) );
            documentType.setDefaultThumbnailUrl( daoUtil.getString( 5 ) );
            documentType.setAdminXsl( daoUtil.getBytes( 6 ) );
            documentType.setContentServiceXsl( daoUtil.getBytes( 7 ) );
            documentType.setMetadataHandler( daoUtil.getString( 8 ) );
            DocumentAttributeHome.setDocumentTypeAttributes( documentType );
        }

        daoUtil.free(  );

        return documentType;
    }

    /**
     * Delete a record from the table
     * @param strCode the code type
     */
    public void delete( String strCode )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setString( 1, strCode );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     * @param documentType The reference of documentType
     */
    public void store( DocumentType documentType )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        daoUtil.setString( 1, documentType.getCode(  ) );
        daoUtil.setString( 2, documentType.getName(  ) );
        daoUtil.setString( 3, documentType.getDescription(  ) );
        daoUtil.setInt( 4, documentType.getThumbnailAttributeId(  ) );
        daoUtil.setString( 5, documentType.getDefaultThumbnailUrl(  ) );
        daoUtil.setString( 6, documentType.getMetadataHandler(  ) );
        daoUtil.setString( 7, documentType.getCode(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the list of documentTypes
     * @return The Collection of the DocumentTypes
     */
    public Collection<DocumentType> selectDocumentTypeList(  )
    {
        Collection<DocumentType> listDocumentTypes = new ArrayList<DocumentType>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DocumentType documentType = new DocumentType(  );
            documentType.setCode( daoUtil.getString( 1 ) );
            documentType.setName( daoUtil.getString( 2 ) );
            documentType.setDescription( daoUtil.getString( 3 ) );
            documentType.setThumbnailAttributeId( daoUtil.getInt( 4 ) );
            documentType.setDefaultThumbnailUrl( daoUtil.getString( 5 ) );
            documentType.setMetadataHandler( daoUtil.getString( 6 ) );

            listDocumentTypes.add( documentType );
        }

        daoUtil.free(  );

        return listDocumentTypes;
    }

    /**
     * Load the Referencelist of documentTypes
     * @return listDocumentTypes
     */
    public ReferenceList getDocumentTypeList(  )
    {
        ReferenceList listDocumentTypes = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DocumentType documentType = new DocumentType(  );
            documentType.setCode( daoUtil.getString( 1 ) );
            documentType.setName( daoUtil.getString( 2 ) );
            listDocumentTypes.addItem( documentType.getCode(  ), documentType.getName(  ) );
        }

        daoUtil.free(  );

        return listDocumentTypes;
    }

    /**
     *
     * @param strCode The code type
     * @return bCheck the boolean
     */
    public boolean checkDocuments( String strCode )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_DOCUMENTS );
        daoUtil.setString( 1, strCode );
        daoUtil.executeQuery(  );

        boolean bCheck = daoUtil.next(  );
        daoUtil.free(  );

        return bCheck;
    }

    /**
     *
     * @param nIdAttribute1 the attribute order
     * @param nOrderAttribute1 the attribute order
     * @param nIdAttribute2 the attribute order
     * @param nOrderAttribute2 the attribute order
     */
    public void reorderAttributes( int nIdAttribute1, int nOrderAttribute1, int nIdAttribute2, int nOrderAttribute2 )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REORDER_ATTRIBUTES );
        daoUtil.setInt( 1, nOrderAttribute1 );
        daoUtil.setInt( 2, nIdAttribute1 );
        daoUtil.executeUpdate(  );
        daoUtil.setInt( 1, nOrderAttribute2 );
        daoUtil.setInt( 2, nIdAttribute2 );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Sets the admin stylesheet
     * @param baXslAdmin The stylesheet
     * @param strCodeType The code type
     */
    public void setAdminStyleSheet( byte[] baXslAdmin, String strCodeType )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ADMIN_STYLESHEET );
        daoUtil.setBytes( 1, baXslAdmin );
        daoUtil.setString( 2, strCodeType );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Sets the content service stylesheet
     * @param baXslContent The stylesheet
     * @param strCodeType The code type
     */
    public void setContentStyleSheet( byte[] baXslContent, String strCodeType )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_CONTENT_STYLESHEET );
        daoUtil.setBytes( 1, baXslContent );
        daoUtil.setString( 2, strCodeType );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
