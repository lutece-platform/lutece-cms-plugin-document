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
package fr.paris.lutece.plugins.document.business.attributes;

import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class provides Data Access methods for DocumentAttribute objects
 */
@ApplicationScoped
public final class DocumentAttributeDAO implements IDocumentAttributeDAO
{
    // Constants
    private static final String SQL_QUERY_INSERT = " INSERT INTO document_type_attr ( code_document_type, code_attr_type, code, document_type_attr_name, description, attr_order, required, searchable ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM document_type_attr WHERE id_document_attr = ?  ";
    private static final String SQL_QUERY_UPDATE = " UPDATE document_type_attr SET code_document_type = ?, code_attr_type = ?, code = ?, document_type_attr_name = ?, description = ?, attr_order = ?, required = ?, searchable = ? WHERE id_document_attr = ?  ";
    private static final String SQL_QUERY_SELECTALL_ATTRIBUTES = " SELECT a.id_document_attr, a.code_document_type," + " a.code_attr_type, a.code, "
            + " a.document_type_attr_name, a.description, a.attr_order, a.required, a.searchable " + " FROM document_type_attr a, document_attr_type b"
            + " WHERE a.code_attr_type =  b.code_attr_type" + " AND a.code_document_type = ? ORDER BY  a.attr_order";
    private static final String SQL_QUERY_SELECT_ATTRIBUTE = " SELECT a.id_document_attr, a.code_document_type," + " a.code_attr_type, a.code, "
            + " a.document_type_attr_name, a.description, a.attr_order, a.required, a.searchable " + " FROM document_type_attr a, document_attr_type b"
            + " WHERE a.code_attr_type =  b.code_attr_type" + " AND a.id_document_attr = ? ";
    private static final String SQL_QUERY_SELECTALL_ATTRIBUTES_OF_DOCUMENT_TYPE = " SELECT DISTINCT a.id_document_attr, "
            + " a.code_document_type, a.code_attr_type, a.code, " + " a.document_type_attr_name, a.description, a.attr_order, a.required, a.searchable "
            + " FROM document_type_attr a" + " WHERE a.code_document_type = ?" + " ORDER BY  a.attr_order";
    private static final String SQL_QUERY_INSERT_PARAMETER_VALUES = "INSERT INTO document_type_attr_parameters ( id_document_attr, parameter_name, id_list_parameter, parameter_value )"
            + "VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_SELECT_PARAMETERS = "SELECT DISTINCT parameter_name FROM document_type_attr_parameters WHERE id_document_attr = ? ";
    private static final String SQL_QUERY_SELECT_PARAMETER_VALUES = "SELECT parameter_value FROM document_type_attr_parameters "
            + "WHERE id_document_attr = ? AND parameter_name = ? ";
    private static final String SQL_QUERY_DELETE_PARAMETER_VALUES = "DELETE FROM document_type_attr_parameters WHERE id_document_attr = ? AND parameter_name = ? ";
    private static final String SQL_QUERY_DELETE_PARAMETERS_VALUES = "DELETE FROM document_type_attr_parameters WHERE id_document_attr = ? ";
    private static final String SQL_QUERY_INSERT_REGULAR_EXPRESSION = "INSERT INTO document_type_attr_verify_by(id_document_attr,id_expression) VALUES(?,?)";
    private static final String SQL_QUERY_DELETE_REGULAR_EXPRESSION = "DELETE FROM document_type_attr_verify_by WHERE id_document_attr=? AND id_expression=?";
    private static final String SQL_QUERY_DELETE_REGULAR_EXPRESSIONS = "DELETE FROM document_type_attr_verify_by WHERE id_document_attr=?";
    private static final String SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_ATTRIBUTE = "SELECT id_expression FROM document_type_attr_verify_by WHERE id_document_attr=?";



    /**
     * Insert a new record in the table.
     *
     * @param documentAttribute
     *            The documentAttribute object
     */
    @Override
    public synchronized void insert( DocumentAttribute documentAttribute )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS ) )
        {
            daoUtil.setString( 1, documentAttribute.getCodeDocumentType( ) );
            daoUtil.setString( 2, documentAttribute.getCodeAttributeType( ) );
            daoUtil.setString( 3, documentAttribute.getCode( ) );
            daoUtil.setString( 4, documentAttribute.getName( ) );
            daoUtil.setString( 5, documentAttribute.getDescription( ) );
            daoUtil.setInt( 6, documentAttribute.getAttributeOrder( ) );
            daoUtil.setInt( 7, documentAttribute.isRequired( ) ? 1 : 0 );
            daoUtil.setInt( 8, documentAttribute.isSearchable( ) ? 1 : 0 );

            daoUtil.executeUpdate( );
            
            if ( daoUtil.nextGeneratedKey( ) )
            {
            	documentAttribute.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
        // Insert parameters
        insertAttributeParameters( documentAttribute );
    }

    /**
     * Load the data of DocumentAttribute from the table
     * 
     * @param nAttributeId
     *            The attribute Id
     * @return the instance of the DocumentAttribute
     */
    @Override
    public DocumentAttribute load( int nAttributeId )
    {
        DocumentAttribute documentAttribute = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ATTRIBUTE ) )
        {
            daoUtil.setInt( 1, nAttributeId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                documentAttribute = new DocumentAttribute( );
                documentAttribute.setId( daoUtil.getInt( 1 ) );
                documentAttribute.setCodeDocumentType( daoUtil.getString( 2 ) );
                documentAttribute.setCodeAttributeType( daoUtil.getString( 3 ) );
                documentAttribute.setCode( daoUtil.getString( 4 ) );
                documentAttribute.setName( daoUtil.getString( 5 ) );
                documentAttribute.setDescription( daoUtil.getString( 6 ) );
                documentAttribute.setAttributeOrder( daoUtil.getInt( 7 ) );
                documentAttribute.setRequired( daoUtil.getInt( 8 ) != 0 );
                documentAttribute.setSearchable( daoUtil.getInt( 9 ) != 0 );
            }
        }
        return documentAttribute;
    }

    /**
     * Delete a record from the table
     * 
     * @param nAttributeId
     *            The DocumentAttribute Id
     */
    @Override
    public void delete( int nAttributeId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nAttributeId );

            daoUtil.executeUpdate( );
        }
        deleteParameters( nAttributeId );
        deleteRegularExpressions( nAttributeId );
    }

    /**
     * Delete a record from the table
     * 
     * @param nAttributeId
     *            The DocumentAttribute Id
     */
    private void deleteParameters( int nAttributeId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_PARAMETERS_VALUES ) )
        {
            daoUtil.setInt( 1, nAttributeId );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Delete a record from the table
     * 
     * @param nAttributeId
     *            The DocumentAttribute Id
     * @param strParameterName
     *            The parameter name
     */
    private void deleteParameter( int nAttributeId, String strParameterName )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_PARAMETER_VALUES ) )
        {
            daoUtil.setInt( 1, nAttributeId );
            daoUtil.setString( 2, strParameterName );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the record in the table
     * 
     * @param documentAttribute
     *            The document attribute
     */
    @Override
    public void store( DocumentAttribute documentAttribute )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            daoUtil.setString( 1, documentAttribute.getCodeDocumentType( ) );
            daoUtil.setString( 2, documentAttribute.getCodeAttributeType( ) );
            daoUtil.setString( 3, documentAttribute.getCode( ) );
            daoUtil.setString( 4, documentAttribute.getName( ) );
            daoUtil.setString( 5, documentAttribute.getDescription( ) );
            daoUtil.setInt( 6, documentAttribute.getAttributeOrder( ) );
            daoUtil.setInt( 7, documentAttribute.isRequired( ) ? 1 : 0 );
            daoUtil.setInt( 8, documentAttribute.isSearchable( ) ? 1 : 0 );
            daoUtil.setInt( 9, documentAttribute.getId( ) );

            daoUtil.executeUpdate( );
        }
        // Update parameters
        deleteParameters( documentAttribute.getId( ) );
        insertAttributeParameters( documentAttribute );
    }

    /**
     * Add attributes to a document
     * 
     * @param documentType
     *            The document Type
     */
    @Override
    public void selectAttributesByDocumentType( DocumentType documentType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ATTRIBUTES ) )
        {
            daoUtil.setString( 1, documentType.getCode( ) );
            daoUtil.executeQuery( );

            int nOrder = 1;

            while ( daoUtil.next( ) )
            {
                DocumentAttribute documentAttribute = new DocumentAttribute( );
                documentAttribute.setId( daoUtil.getInt( 1 ) );
                documentAttribute.setCodeDocumentType( daoUtil.getString( 2 ) );
                documentAttribute.setCodeAttributeType( daoUtil.getString( 3 ) );
                documentAttribute.setCode( daoUtil.getString( 4 ) );
                documentAttribute.setName( daoUtil.getString( 5 ) );
                documentAttribute.setDescription( daoUtil.getString( 6 ) );
                documentAttribute.setAttributeOrder( nOrder );
                documentAttribute.setRequired( daoUtil.getInt( 8 ) != 0 );
                documentAttribute.setSearchable( daoUtil.getInt( 9 ) != 0 );

                documentType.addAttribute( documentAttribute );
                nOrder++;
            }
        }
    }

    /**
     * Get all attributes of document type
     * 
     * @param codeDocumentType
     *            The code document Type
     * @return listDocumentAttributes The list of all attributes of selected code document type
     */
    @Override
    public List<DocumentAttribute> selectAllAttributesOfDocumentType( String codeDocumentType )
    {
        List<DocumentAttribute> listDocumentAttributes = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ATTRIBUTES_OF_DOCUMENT_TYPE ) )
        {
            daoUtil.setString( 1, codeDocumentType );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentAttribute documentAttribute = new DocumentAttribute( );
                documentAttribute.setId( daoUtil.getInt( 1 ) );
                documentAttribute.setCodeDocumentType( daoUtil.getString( 2 ) );
                documentAttribute.setCodeAttributeType( daoUtil.getString( 3 ) );
                documentAttribute.setCode( daoUtil.getString( 4 ) );
                documentAttribute.setName( daoUtil.getString( 5 ) );
                documentAttribute.setDescription( daoUtil.getString( 6 ) );
                documentAttribute.setAttributeOrder( daoUtil.getInt( 7 ) );
                documentAttribute.setRequired( daoUtil.getInt( 8 ) != 0 );
                documentAttribute.setSearchable( daoUtil.getInt( 9 ) != 0 );
                listDocumentAttributes.add( documentAttribute );
            }
        }
        return listDocumentAttributes;
    }

    /**
     * Insert Attribute Parameters
     * 
     * @param documentAttribute
     *            Document Attribute
     */
    private void insertAttributeParameters( DocumentAttribute documentAttribute )
    {
        for ( AttributeTypeParameter parameter : documentAttribute.getParameters( ) )
        {
            deleteParameter( documentAttribute.getId( ), parameter.getName( ) );

            int i = 0;

            for ( String value : parameter.getValueList( ) )
            {
                try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_PARAMETER_VALUES ) )
                {
                    daoUtil.setInt( 1, documentAttribute.getId( ) );
                    daoUtil.setString( 2, parameter.getName( ) );
                    daoUtil.setInt( 3, i++ );
                    daoUtil.setString( 4, value );

                    daoUtil.executeUpdate( );
                }
            }
        }
    }

    /**
     * Gets Attribute parameters values
     * 
     * @param nAttributeId
     *            The attribute Id
     * @return List of attribute parameters values
     */
    @Override
    public List<AttributeTypeParameter> selectAttributeParametersValues( int nAttributeId )
    {
        ArrayList<AttributeTypeParameter> listParameters = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PARAMETERS ) )
        {
            daoUtil.setInt( 1, nAttributeId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                AttributeTypeParameter parameter = new AttributeTypeParameter( );
                parameter.setName( daoUtil.getString( 1 ) );
                parameter.setValueList( getAttributeParameterValues( nAttributeId, parameter.getName( ) ) );
                listParameters.add( parameter );
            }
        }
        return listParameters;
    }

    /**
     * Returns the parameter value of an attribute
     * 
     * @param nAttributeId
     *            The attribute Id
     * @param strParameterName
     *            The parameter name
     * @return The parameter values of an attribute
     */
    @Override
    public List<String> getAttributeParameterValues( int nAttributeId, String strParameterName )
    {
        List<String> listValues = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PARAMETER_VALUES ) )
        {
            daoUtil.setInt( 1, nAttributeId );
            daoUtil.setString( 2, strParameterName );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listValues.add( daoUtil.getString( 1 ) );
            }
        }
        return listValues;
    }

    /**
     * Inserts an association between an attribute and a regular expression
     *
     * @param nIdAttribute
     *            The identifier of the document attribute
     * @param nIdExpression
     *            The identifier of the regular expression
     */
    @Override
    public void insertRegularExpression( int nIdAttribute, int nIdExpression )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_REGULAR_EXPRESSION ) )
        {
            daoUtil.setInt( 1, nIdAttribute );
            daoUtil.setInt( 2, nIdExpression );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Deletes an association between an attribute and a regular expression
     *
     * @param nIdAttribute
     *            The identifier of the document attribute
     * @param nIdExpression
     *            The identifier of the regular expression
     */
    @Override
    public void deleteRegularExpression( int nIdAttribute, int nIdExpression )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_REGULAR_EXPRESSION ) )
        {
            daoUtil.setInt( 1, nIdAttribute );
            daoUtil.setInt( 2, nIdExpression );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Deletes all association between an attribute and the regular expression
     *
     * @param nIdAttribute
     *            The identifier of the document attribute
     */
    private void deleteRegularExpressions( int nIdAttribute )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_REGULAR_EXPRESSIONS ) )
        {
            daoUtil.setInt( 1, nIdAttribute );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Loads all regular expression key associated to the attribute and returns them into a collection
     *
     * @param nIdAttribute
     *            The identifier of the document attribute
     * @return A collection of regular expression key
     */
    @Override
    public Collection<Integer> selectListRegularExpressionKeyByIdAttribute( int nIdAttribute )
    {
        Collection<Integer> colRegularExpression = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_ATTRIBUTE ) )
        {
            daoUtil.setInt( 1, nIdAttribute );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                colRegularExpression.add( daoUtil.getInt( 1 ) );
            }
        }
        return colRegularExpression;
    }
}
