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

import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * This class provides Data Access methods for DocumentAttributeType objects
 */
@ApplicationScoped
public final class AttributeTypeDAO implements IAttributeTypeDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = " SELECT code_attr_type, name_key, description_key, manager_class FROM document_attr_type WHERE code_attr_type = ?  ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO document_attr_type ( code_attr_type, name_key, description_key, manager_class ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM document_attr_type WHERE code_attr_type = ?  ";
    private static final String SQL_QUERY_UPDATE = " UPDATE document_attr_type SET code_attr_type = ?, name_key = ?, description_key = ?, manager_class = ? WHERE code_attr_type = ?  ";
    private static final String SQL_QUERY_SELECTALL = " SELECT code_attr_type, name_key, description_key , manager_class FROM document_attr_type ";
    private static final String SQL_QUERY_SELECT_MANAGERS = "SELECT code_attr_type , manager_class  FROM document_attr_type";
    private static final String SQL_QUERY_SELECT_PARAMETERS = "SELECT parameter_name, parameter_label_key, parameter_description_key, parameter_default_value FROM document_attr_type_parameter WHERE code_attr_type = ? ORDER BY parameter_index";

    /**
     * Insert a new record in the table.
     *
     * @param documentAttributeType
     *            The documentAttributeType object
     */
    @Override
    public void insert( AttributeType documentAttributeType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
        {
            daoUtil.setString( 1, documentAttributeType.getCode( ) );
            daoUtil.setString( 2, documentAttributeType.getNameKey( ) );
            daoUtil.setString( 3, documentAttributeType.getDescriptionKey( ) );
            daoUtil.setString( 4, documentAttributeType.getClassName( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the data of DocumentAttributeType from the table
     *
     * @param nDocumentAttributeTypeId
     *            The identifier of DocumentAttributeType
     * @return the instance of the DocumentAttributeType
     */
    @Override
    public AttributeType load( int nDocumentAttributeTypeId )
    {
        AttributeType documentAttributeType = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nDocumentAttributeTypeId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                documentAttributeType = new AttributeType( );
                documentAttributeType.setCode( daoUtil.getString( 1 ) );
                documentAttributeType.setNameKey( daoUtil.getString( 2 ) );
                documentAttributeType.setDescriptionKey( daoUtil.getString( 3 ) );
                documentAttributeType.setClassName( daoUtil.getString( 4 ) );
            }
        }
        return documentAttributeType;
    }

    /**
     * Delete a record from the table
     * 
     * @param documentAttributeType
     *            The DocumentAttributeType object
     */
    @Override
    public void delete( AttributeType documentAttributeType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setString( 1, documentAttributeType.getCode( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the record in the table
     * 
     * @param documentAttributeType
     *            The reference of documentAttributeType
     */
    @Override
    public void store( AttributeType documentAttributeType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            daoUtil.setString( 1, documentAttributeType.getCode( ) );
            daoUtil.setString( 2, documentAttributeType.getNameKey( ) );
            daoUtil.setString( 3, documentAttributeType.getDescriptionKey( ) );
            daoUtil.setString( 4, documentAttributeType.getClassName( ) );
            daoUtil.setString( 5, documentAttributeType.getCode( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the list of documentAttributeTypes
     * 
     * @return The Collection of the DocumentAttributeTypes
     */
    @Override
    public Collection<AttributeType> selectDocumentAttributeTypeList( )
    {
        Collection<AttributeType> listDocumentAttributeTypes = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                AttributeType documentAttributeType = new AttributeType( );
                documentAttributeType.setCode( daoUtil.getString( 1 ) );
                documentAttributeType.setNameKey( daoUtil.getString( 2 ) );
                documentAttributeType.setDescriptionKey( daoUtil.getString( 3 ) );
                documentAttributeType.setClassName( daoUtil.getString( 4 ) );

                listDocumentAttributeTypes.add( documentAttributeType );
            }
        }
        return listDocumentAttributeTypes;
    }

    /**
     * Load the list of Attribute Types
     * 
     * @return The Collection of the DocumentAttributeTypes
     * @param locale
     *            The locale
     */
    @Override
    public ReferenceList selectAttributeTypeList( Locale locale )
    {
        ReferenceList listAttributeTypes = new ReferenceList( );
       try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL ) )
       {
           daoUtil.executeQuery( );

           while ( daoUtil.next( ) )
           {
               AttributeType documentAttributeType = new AttributeType( );
               documentAttributeType.setLocale( locale );
               documentAttributeType.setCode( daoUtil.getString( 1 ) );
               documentAttributeType.setNameKey( daoUtil.getString( 2 ) );
               listAttributeTypes.addItem( documentAttributeType.getCode( ), documentAttributeType.getName( ) );
           }
       }
        return listAttributeTypes;
    }

    ////////////////////////////////////////////////////////////////////////
    // Attributes manager

    /**
     * Gets attributes managers list
     * 
     * @return The list of attribute managers
     */
    @Override
    public ReferenceList getAttributeManagersList( )
    {
        ReferenceList listAttributeManagers = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MANAGERS ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listAttributeManagers.addItem( daoUtil.getString( 1 ), daoUtil.getString( 2 ) );
            }
        }
        return listAttributeManagers;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Attribute type parameters

    /**
     * Load the list of attributeTypeParameters
     * 
     * @return The Collection of the AttributeTypeParameters
     * @param strAttributeTypeCode
     *            The attribute type code
     */
    @Override
    public List<AttributeTypeParameter> selectAttributeTypeParameterList( String strAttributeTypeCode )
    {
        List<AttributeTypeParameter> listAttributeTypeParameters = new ArrayList<>( );
        List<String> listDefaultValue = new ArrayList<>( );
        String strDefaultValue;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PARAMETERS ) )
        {
            daoUtil.setString( 1, strAttributeTypeCode );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                AttributeTypeParameter attributeTypeParameter = new AttributeTypeParameter( );
                attributeTypeParameter.setName( daoUtil.getString( 1 ) );
                attributeTypeParameter.setLabelKey( daoUtil.getString( 2 ) );
                attributeTypeParameter.setDescriptionKey( daoUtil.getString( 3 ) );
                strDefaultValue = daoUtil.getString( 4 );

                if ( !strDefaultValue.equals( "" ) )
                {
                    listDefaultValue.add( strDefaultValue );
                }

                attributeTypeParameter.setDefaultValue( listDefaultValue );
                listDefaultValue.clear( );

                listAttributeTypeParameters.add( attributeTypeParameter );
            }
        }
        return listAttributeTypeParameters;
    }
}
