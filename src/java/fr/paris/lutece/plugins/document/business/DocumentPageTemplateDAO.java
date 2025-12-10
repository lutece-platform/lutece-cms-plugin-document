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

import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for DocumentPageTemplate objects
 */
@ApplicationScoped
public final class DocumentPageTemplateDAO implements IDocumentPageTemplateDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = " SELECT id_page_template_document, description, page_template_path, picture_path FROM document_page_template WHERE id_page_template_document = ?";
    private static final String SQL_QUERY_INSERT = " INSERT INTO document_page_template ( description, page_template_path, picture_path ) VALUES ( ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM document_page_template WHERE id_page_template_document = ?";
    private static final String SQL_QUERY_UPDATE = " UPDATE document_page_template SET description = ?, page_template_path = ?, picture_path = ? WHERE id_page_template_document = ?";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_page_template_document , description, page_template_path, picture_path FROM document_page_template ORDER BY id_page_template_document ";

    ///////////////////////////////////////////////////////////////////////////////////////
    // Access methods to data

    /**
     * Insert a new record in the table.
     * 
     * @param documentPageTemplate
     *            The Instance of the object DocumentPageTemplate
     */
    @Override
    public void insert( DocumentPageTemplate documentPageTemplate )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS ) )
        {
            daoUtil.setString( 1, documentPageTemplate.getDescription( ) );
            daoUtil.setString( 2, documentPageTemplate.getFile( ) );
            daoUtil.setString( 3, documentPageTemplate.getPicture( ) );

            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                documentPageTemplate.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * load the data of DocumentPageTemplate from the table
     *
     * @param nPageTemplateId
     *            The indentifier of the object DocumentPageTemplate
     * @return The Instance of the object PageTemplate
     */
    @Override
    public DocumentPageTemplate load( int nPageTemplateId )
    {
        DocumentPageTemplate documentPageTemplate = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nPageTemplateId );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                documentPageTemplate = new DocumentPageTemplate( );
                documentPageTemplate.setId( daoUtil.getInt( 1 ) );
                documentPageTemplate.setDescription( daoUtil.getString( 2 ) );
                documentPageTemplate.setFile( daoUtil.getString( 3 ) );
                documentPageTemplate.setPicture( daoUtil.getString( 4 ) );
            }


        }
        return documentPageTemplate;
    }

    /**
     * Delete a record from the table
     * 
     * @param nPageTemplateId
     *            The indentifier of the object PageTemplate
     */
    @Override
    public void delete( int nPageTemplateId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nPageTemplateId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the record in the table
     * 
     * @param documentPageTemplate
     *            The instance of the PageTemplate to update
     */
    @Override
    public void store( DocumentPageTemplate documentPageTemplate )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            daoUtil.setString( 1, documentPageTemplate.getDescription( ) );
            daoUtil.setString( 2, documentPageTemplate.getFile( ) );
            daoUtil.setString( 3, documentPageTemplate.getPicture( ) );
            daoUtil.setInt( 4, documentPageTemplate.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Returns a list of all the page templates
     * 
     * @return A list of PageTemplates objects
     */
    @Override
    public List<DocumentPageTemplate> selectPageTemplatesList( )
    {
        List<DocumentPageTemplate> listDocumentPageTemplates = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentPageTemplate documentPageTemplate = new DocumentPageTemplate( );

                documentPageTemplate.setId( daoUtil.getInt( 1 ) );
                documentPageTemplate.setDescription( daoUtil.getString( 2 ) );
                documentPageTemplate.setFile( daoUtil.getString( 3 ) );
                documentPageTemplate.setPicture( daoUtil.getString( 4 ) );
                listDocumentPageTemplates.add( documentPageTemplate );
            }
        }
        return listDocumentPageTemplates;
    }
}
