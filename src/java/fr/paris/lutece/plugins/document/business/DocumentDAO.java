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

import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.category.Category;
import fr.paris.lutece.plugins.document.business.rules.Rule;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides Data Access methods for Document objects
 */
@ApplicationScoped
public final class DocumentDAO implements IDocumentDAO
{
    // Documents queries
    private static final String SQL_QUERY_SELECT = " SELECT a.id_document, a.code_document_type, a.title, a.date_creation, "
            + " a.date_modification, a.id_space , b.document_space_name , "
            + " a.id_state , c.name_key, d.document_type_name , a.document_summary, a.document_comment , a.date_validity_begin,"
            + " a.date_validity_end , a.metadata , a.id_creator, a.id_mailinglist, a.id_page_template_document, a.skip_portlet, a.skip_categories, a.validated_title, a.validated_summary FROM document a,"
            + " document_space b, document_workflow_state c, document_type d WHERE a.id_space = b.id_space AND a.id_state = c.id_state"
            + " AND a.code_document_type = d.code_document_type AND a.id_document = ?  ";
    private static final String SQL_QUERY_SELECT_FROM_SPACE_ID = " SELECT a.id_document, a.title, a.document_summary"
            + " FROM document a WHERE a.id_space = ?  ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO document ( code_document_type, title, date_creation, "
            + " date_modification, id_space, id_state, document_summary, document_comment, "
            + " date_validity_begin , date_validity_end , metadata , id_creator, "
            + " id_mailinglist, id_page_template_document, skip_portlet, skip_categories, validated_title, validated_summary ) "
            + " VALUES ( ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM document WHERE id_document = ?  ";
    private static final String SQL_QUERY_UPDATE = " UPDATE document SET code_document_type = ?, title = ?, date_creation = ?, date_modification = ?, "
            + " id_space = ?, id_state = ? , document_summary = ?, document_comment = ? , date_validity_begin = ? , date_validity_end = ? , "
            + " metadata = ? , id_creator = ?, id_mailinglist = ?, id_page_template_document = ?, skip_portlet = ?, skip_categories = ?, "
            + " validated_title = ?, validated_summary = ? WHERE id_document = ?  ";
    private static final String SQL_QUERY_SELECT_PRIMARY_KEY_BY_FILTER = " SELECT DISTINCT a.id_document, a.date_modification FROM document a "
            + " INNER JOIN document_space b ON a.id_space = b.id_space " + " INNER JOIN document_workflow_state c ON a.id_state = c.id_state "
            + " INNER JOIN document_type d ON a.code_document_type = d.code_document_type "
            + " LEFT OUTER JOIN document_category_link f ON a.id_document = f.id_document ";
    private static final String SQL_QUERY_SELECT_BY_FILTER = " SELECT DISTINCT a.id_document, a.code_document_type, a.title, "
            + " a.date_creation, a.date_modification, a.id_space , b.document_space_name , "
            + " a.id_state , c.name_key , d.document_type_name ,  a.document_summary, a.document_comment , a.date_validity_begin , a.date_validity_end , "
            + " a.metadata , a.id_creator, a.id_mailinglist , a.id_page_template_document, a.skip_portlet, a.skip_categories, a.validated_title, a.validated_summary FROM document a "
            + " INNER JOIN document_space b ON a.id_space = b.id_space " + " INNER JOIN document_workflow_state c ON a.id_state = c.id_state "
            + " INNER JOIN document_type d ON a.code_document_type = d.code_document_type "
            + " LEFT OUTER JOIN document_category_link f ON a.id_document = f.id_document ";
    private static final String SQL_QUERY_SELECT_LAST_MODIFIED_DOCUMENT_FROM_USER = " SELECT a.id_document, a.code_document_type, a.title, a.date_creation, "
            + " a.date_modification, a.id_space , b.document_space_name , "
            + " a.id_state , c.name_key, d.document_type_name , a.document_summary, a.document_comment , a.date_validity_begin , a.date_validity_end , "
            + " a.metadata , a.id_creator, a.id_mailinglist, a.id_page_template_document, a.skip_portlet, a.skip_categories, a.validated_title, a.validated_summary FROM document a"
            + " INNER JOIN document_space b ON a.id_space = b.id_space" + " INNER JOIN document_workflow_state c ON a.id_state = c.id_state"
            + " INNER JOIN document_type d ON a.code_document_type = d.code_document_type " + " INNER JOIN document_history e ON a.id_document = e.id_document "
            + " WHERE e.event_user = ? ORDER BY e.event_date DESC LIMIT 1 ";
    private static final String SQL_QUERY_SELECT_LAST_PUBLISHED_DOCUMENT = " SELECT a.id_document, a.code_document_type, a.title, a.date_creation, "
            + " a.date_modification, a.id_space , b.document_space_name , "
            + " a.id_state , c.name_key, d.document_type_name , a.document_summary, a.document_comment , a.date_validity_begin , a.date_validity_end , "
            + " a.metadata , a.id_creator, a.id_mailinglist, a.id_page_template_document, a.skip_portlet, a.skip_categories, a.validated_title, a.validated_summary FROM document a"
            + " INNER JOIN document_space b ON a.id_space = b.id_space" + " INNER JOIN document_workflow_state c ON a.id_state = c.id_state"
            + " INNER JOIN document_type d ON a.code_document_type = d.code_document_type "
            + " INNER JOIN document_published e ON a.id_document = e.id_document " + " ORDER BY e.date_publishing DESC LIMIT 1 ";
    private static final String SQL_FILTER_WHERE_CLAUSE = " WHERE ";
    private static final String SQL_FILTER_AND = " AND ";
    private static final String SQL_FILTER_DOCUMENT_TYPE = " a.code_document_type = ? ";
    private static final String SQL_FILTER_SPACE = " a.id_space = ? ";
    private static final String SQL_FILTER_STATE = " a.id_state = ? ";
    private static final String SQL_FILTER_CATEGORIES_BEGIN = " (";
    private static final String SQL_FILTER_CATEGORIES = " f.id_category = ? ";
    private static final String SQL_FILTER_CATEGORIES_NULL = " f.id_category IS NULL ";
    private static final String SQL_FILTER_CATEGORIES_OR = " OR ";
    private static final String SQL_FILTER_CATEGORIES_END = ") ";
    private static final String SQL_FILTER_ID_BEGIN = " (";
    private static final String SQL_FILTER_ID = " a.id_document = ? ";
    private static final String SQL_FILTER_ID_OR = " OR ";
    private static final String SQL_FILTER_ID_END = ") ";
    private static final String SQL_ORDER_BY_LAST_MODIFICATION = " ORDER BY a.date_modification DESC ";

    private static final String SQL_QUERY_SELECT_WITH_VALIDATED_VERSION = " SELECT a.id_document, a.code_document_type, a.title, a.validated_title, a.id_space, a.id_state, "
            + " b.document_space_name, a.date_creation FROM document a INNER JOIN document_space b ON a.id_space = b.id_space ";
    private static final String SQL_QUERY_COUNT_WITH_VALIDATED_VERSION = " SELECT COUNT(*) FROM document a ";
    private static final String SQL_FILTER_WITH_VALIDATED_VERSION = " WHERE a.id_state IN ( " + DocumentState.STATE_VALIDATE + ", " + DocumentState.STATE_IN_CHANGE
            + ", " + DocumentState.STATE_WAITING_FOR_CHANGE_APPROVAL + " ) AND a.code_document_type = ? ";
    private static final String SQL_FILTER_TITLE_FRAGMENT = " AND ( LOWER( a.title ) LIKE ? OR LOWER( a.validated_title ) LIKE ? ) ";
    private static final String SQL_FILTER_SPACES_BEGIN = " AND a.id_space IN ( ";
    private static final String SQL_FILTER_SPACES_END = " ) ";
    private static final String SQL_ORDER_BY_LAST_CREATED = " ORDER BY a.date_creation DESC, a.id_document DESC LIMIT ";
    private static final String SQL_OFFSET = " OFFSET ";
    private static final String SQL_QUERY_SELECT_LEGACY_METADATA = " SELECT id_document, xml_metadata FROM document WHERE xml_metadata IS NOT NULL ";
    private static final String SQL_QUERY_UPDATE_MIGRATED_METADATA = " UPDATE document SET metadata = ?, xml_metadata = NULL WHERE id_document = ? ";

    // Select only primary keys
    private static final String SQL_QUERY_SELECT_PRIMARY_KEYS = " SELECT a.id_document FROM document a ";
    private static final String SQL_QUERY_DELETE_DOCUMENT_HISTORY = "DELETE FROM document_history WHERE id_document = ?  ";

    // Document attributes queries
    private static final String SQL_QUERY_SELECT_ATTRIBUTES = "SELECT c.id_document_attr , c.code , c.code_attr_type , "
            + "c.code_document_type , c.document_type_attr_name, c.description, c.attr_order, c.required, c.searchable , "
            + "b.text_value, b.mime_type , b.binary_value " + "FROM document a, document_content b, document_type_attr c "
            + " WHERE a.code_document_type = c.code_document_type " + " AND a.id_document = b.id_document  "
            + " AND b.id_document_attr = c.id_document_attr AND a.id_document = ? ";
    private static final String SQL_QUERY_SELECT_ATTRIBUTES_WITHOUT_BINARIES = "SELECT c.id_document_attr , c.code , c.code_attr_type , "
            + "c.code_document_type , c.document_type_attr_name, c.description, c.attr_order, c.required, c.searchable , " + "b.text_value, b.mime_type "
            + "FROM document a, document_content b, document_type_attr c " + " WHERE a.code_document_type = c.code_document_type "
            + " AND a.id_document = b.id_document  " + " AND b.id_document_attr = c.id_document_attr " + " AND a.id_document = ? AND b.validated = ?";
    private static final String SQL_QUERY_INSERT_ATTRIBUTE = "INSERT INTO document_content (id_document ,  id_document_attr , text_value , binary_value, mime_type, validated ) VALUES ( ? , ? , ? , ? , ? , ?)";
    private static final String SQL_QUERY_DELETE_ATTRIBUTES = "DELETE FROM document_content WHERE id_document = ? and validated = ? ";
    private static final String SQL_QUERY_VALIDATE_ATTRIBUTES = "UPDATE document_content SET validated = ? WHERE id_document = ?";

    // Resources queries
    private static final String SQL_QUERY_SELECT_BY_VALIDATED_TEXT_VALUE = " SELECT DISTINCT id_document FROM document_content WHERE validated = 1 AND id_document <> ? AND text_value LIKE ? ";
    private static final String SQL_QUERY_SELECT_DOCUMENT_SPECIFIC_RESOURCE = " SELECT binary_value , mime_type , text_value FROM document_content WHERE id_document = ? AND id_document_attr = ? and validated = ?";
    private static final String SQL_QUERY_SELECT_DOCUMENT_RESOURCE = "SELECT a.binary_value , a.mime_type, a.text_value FROM document_content a, document b, document_type c WHERE a.id_document = ? "
            + " AND a.id_document_attr = c.thumbnail_attr_id " + " AND a.id_document = b.id_document " + " AND b.code_document_type = c.code_document_type ";
    private static final String SQL_QUERY_SELECT_PAGE_TEMPLATE_PATH = " SELECT page_template_path FROM document_page_template " + " "
            + " WHERE id_page_template_document =  ? ";
    private static final String SQL_QUERY_SELECTALL_CATEGORY = " SELECT a.id_category, a.document_category_name, a.description, a.icon_content, a.icon_mime_type FROM document_category a, document_category_link b WHERE a.id_category=b.id_category AND b.id_document = ? ORDER BY document_category_name";
    private static final String SQL_QUERY_DELETE_LINKS_DOCUMENT = " DELETE FROM document_category_link WHERE id_document = ? ";
    private static final String SQL_QUERY_INSERT_LINK_CATEGORY_DOCUMENT = " INSERT INTO document_category_link ( id_category, id_document ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_LAST_MODIFIED = "SELECT d.code_document_type, d.date_modification FROM document d WHERE d.id_document = ?";
    private static final String SQL_QUERY_SELECT_RELATED_CATEGORY = "SELECT DISTINCT a.id_document, a.code_document_type, a.title, a.date_creation, "
            + " a.date_modification, a.id_space , b.document_space_name , "
            + " a.id_state , c.name_key, d.document_type_name , a.document_summary, a.document_comment , a.date_validity_begin , a.date_validity_end , "
            + " a.metadata , a.id_creator, a.id_mailinglist, a.id_page_template_document, a.skip_portlet, a.skip_categories, a.validated_title, a.validated_summary FROM document a "
            + " INNER JOIN document_space b ON a.id_space = b.id_space " + " INNER JOIN document_workflow_state c ON a.id_state = c.id_state "
            + " INNER JOIN document_type d ON a.code_document_type = d.code_document_type "
            + " LEFT OUTER JOIN document_category_link f ON a.id_document = f.id_document "
            + " WHERE f.id_category IN ( SELECT g.id_category FROM document_category_link g WHERE g.id_document = ?) ";




    /**
     * Insert a new record in the table.
     *
     * @param document
     *            The document object
     */
    @Override
    public synchronized void insert( Document document )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS ) )
        {
            daoUtil.setString( 1, document.getCodeDocumentType( ) );
            daoUtil.setString( 2, document.getTitle( ) );
            daoUtil.setTimestamp( 3, document.getDateCreation( ) );
            daoUtil.setTimestamp( 4, document.getDateModification( ) );
            daoUtil.setInt( 5, document.getSpaceId( ) );
            daoUtil.setInt( 6, document.getStateId( ) );
            daoUtil.setString( 7, document.getSummary( ) );
            daoUtil.setString( 8, document.getComment( ) );
            daoUtil.setTimestamp( 9, document.getDateValidityBegin( ) );
            daoUtil.setTimestamp( 10, document.getDateValidityEnd( ) );
            daoUtil.setString( 11, document.getMetadata( ) );
            daoUtil.setInt( 12, document.getCreatorId( ) );
            daoUtil.setInt( 13, document.getMailingListId( ) );
            daoUtil.setInt( 14, document.getPageTemplateDocumentId( ) );
            daoUtil.setBoolean( 15, document.isSkipPortlet( ) );
            daoUtil.setBoolean( 16, document.isSkipCategories( ) );
            daoUtil.setString( 17, document.getValidatedTitle( ) );
            daoUtil.setString( 18, document.getValidatedSummary( ) );

            daoUtil.executeUpdate( );
            
            if ( daoUtil.nextGeneratedKey( ) )
            {
            	document.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        insertAttributes( document );
        insertCategories( document.getCategories( ), document.getId( ) );
    }
    
    /**
     * Insert attributes
     * 
     * @param document
     *            The document object
     */
    private void insertAttributes( Document document )
    {
        List<DocumentAttribute> listAttributes = document.getAttributes( );

        for ( DocumentAttribute attribute : listAttributes )
        {
            insertAttribute( document.getId( ), attribute );
        }
    }

    /**
     *
     * @param nDocumentId
     *            the document identifier
     * @param attribute
     *            The DocumentAttribute object
     */
    private void insertAttribute( int nDocumentId, DocumentAttribute attribute )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ATTRIBUTE ) )
        {
            daoUtil.setInt( 1, nDocumentId );
            daoUtil.setInt( 2, attribute.getId( ) );

            if ( attribute.isBinary( ) )
            {
                // File attribute, save content type and data in the binary column
                daoUtil.setString( 3, attribute.getTextValue( ) );
                daoUtil.setBytes( 4, attribute.getBinaryValue( ) );
                daoUtil.setString( 5, attribute.getValueContentType( ) );
            }
            else
            {
                // Text attribute, no content type and save data in the text column
                daoUtil.setString( 3, attribute.getTextValue( ) );

                daoUtil.setBytes( 4, null );
                daoUtil.setString( 5, StringUtils.EMPTY );
            }

            daoUtil.setBoolean( 6, false );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the data of Document from the table
     *
     * @param nDocumentId
     *            The identifier of Document
     * @return the instance of the Document
     */
    @Override
    public Document load( int nDocumentId )
    {
        return loadDocument( nDocumentId, true );
    }

    /**
     * Load the data of Document from the table
     *
     * @param nDocumentId
     *            The identifier of Document
     * @return the instance of the Document
     */
    @Override
    public Document loadWithoutBinaries( int nDocumentId )
    {
        return loadDocument( nDocumentId, false );
    }

    /**
     * Load the data of Document from the table
     *
     * @param nDocumentId
     *            The identifier of Document
     * @param bBinaries
     *            load binaries
     * @return the instance of the Document
     */
    private Document loadDocument( int nDocumentId, boolean bBinaries )
    {
        Document document = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nDocumentId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                document = new Document( );
                document.setId( daoUtil.getInt( 1 ) );
                document.setCodeDocumentType( daoUtil.getString( 2 ) );
                document.setTitle( daoUtil.getString( 3 ) );
                document.setDateCreation( daoUtil.getTimestamp( 4 ) );
                document.setDateModification( daoUtil.getTimestamp( 5 ) );
                document.setSpaceId( daoUtil.getInt( 6 ) );
                document.setSpace( daoUtil.getString( 7 ) );
                document.setStateId( daoUtil.getInt( 8 ) );
                document.setStateKey( daoUtil.getString( 9 ) );
                document.setType( daoUtil.getString( 10 ) );
                document.setSummary( daoUtil.getString( 11 ) );
                document.setComment( daoUtil.getString( 12 ) );
                document.setDateValidityBegin( daoUtil.getTimestamp( 13 ) );
                document.setDateValidityEnd( daoUtil.getTimestamp( 14 ) );
                document.setMetadata( daoUtil.getString( 15 ) );
                document.setCreatorId( daoUtil.getInt( 16 ) );
                document.setMailingListId( daoUtil.getInt( 17 ) );
                document.setPageTemplateDocumentId( daoUtil.getInt( 18 ) );
                document.setSkipPortlet( daoUtil.getBoolean( 19 ) );
                document.setSkipCategories( daoUtil.getBoolean( 20 ) );
                document.setValidatedTitle( daoUtil.getString( 21 ) );
                document.setValidatedSummary( daoUtil.getString( 22 ) );
            }

        }
        if ( document != null )
        {
            if ( bBinaries )
            {
                loadAttributes( document );
            }
            else
            {
                if ( document.getStateId( ) == DocumentState.STATE_VALIDATE )
                {
                    loadAttributesWithoutBinaries( document, true );
                }
                else
                {
                    loadAttributesWithoutBinaries( document, false );
                }
            }

            document.setCategories( selectCategories( document.getId( ) ) );
        }

        return document;
    }

    /**
     * Load from space id.
     *
     * @param nSpaceId
     *            The id of the document space
     * @return the instance of the Document
     */
    @Override
    public List<Document> loadFromSpaceId( int nSpaceId )
    {
        List<Document> list;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FROM_SPACE_ID ) )
        {
            daoUtil.setInt( 1, nSpaceId );
            daoUtil.executeQuery( );

            list = new ArrayList<>( );

            while ( daoUtil.next( ) )
            {
                Document document = new Document( );
                document.setId( daoUtil.getInt( 1 ) );
                document.setTitle( daoUtil.getString( 2 ) );
                document.setSummary( daoUtil.getString( 3 ) );
                list.add( document );
            }

        }

        for ( Document d : list )
        {
            if ( d != null )
            {
                loadAttributes( d );
                d.setCategories( selectCategories( d.getId( ) ) );
            }
        }

        return list;
    }

    /**
     * Load the attributes of Document from the table
     * 
     * @param document
     *            Document object
     */
    @Override
    public void loadAttributes( Document document )
    {
        List<DocumentAttribute> listAttributes = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ATTRIBUTES ) )
        {
            daoUtil.setInt( 1, document.getId( ) );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentAttribute attribute = new DocumentAttribute( );
                attribute.setId( daoUtil.getInt( 1 ) );
                attribute.setCode( daoUtil.getString( 2 ) );
                attribute.setCodeAttributeType( daoUtil.getString( 3 ) );
                attribute.setCodeDocumentType( daoUtil.getString( 4 ) );
                attribute.setName( daoUtil.getString( 5 ) );
                attribute.setDescription( daoUtil.getString( 6 ) );
                attribute.setAttributeOrder( daoUtil.getInt( 7 ) );
                attribute.setRequired( daoUtil.getInt( 8 ) != 0 );
                attribute.setSearchable( daoUtil.getInt( 9 ) != 0 );

                String strContentType = daoUtil.getString( 11 );

                if ( StringUtils.isNotBlank( strContentType ) )
                {
                    // File attribute
                    attribute.setBinary( true );
                    attribute.setTextValue( daoUtil.getString( 10 ) );
                    attribute.setBinaryValue( daoUtil.getBytes( 12 ) );
                    attribute.setValueContentType( strContentType );
                }
                else
                {
                    // Text attribute
                    attribute.setBinary( false );
                    attribute.setTextValue( daoUtil.getString( 10 ) );
                    attribute.setValueContentType( StringUtils.EMPTY );
                }

                listAttributes.add( attribute );
            }

            document.setAttributes( listAttributes );
        }
    }

    /**
     * Load the attributes of Document from the table
     * 
     * @param document
     *            Document object
     * @param bValidated
     *            true if the content of the document must be validated, false otherwise
     */
    @Override
    public void loadAttributesWithoutBinaries( Document document, boolean bValidated )
    {
        List<DocumentAttribute> listAttributes = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ATTRIBUTES_WITHOUT_BINARIES ) )
        {
            daoUtil.setInt( 1, document.getId( ) );
            daoUtil.setBoolean( 2, bValidated );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentAttribute attribute = new DocumentAttribute( );
                attribute.setId( daoUtil.getInt( 1 ) );
                attribute.setCode( daoUtil.getString( 2 ) );
                attribute.setCodeAttributeType( daoUtil.getString( 3 ) );
                attribute.setCodeDocumentType( daoUtil.getString( 4 ) );
                attribute.setName( daoUtil.getString( 5 ) );
                attribute.setDescription( daoUtil.getString( 6 ) );
                attribute.setAttributeOrder( daoUtil.getInt( 7 ) );
                attribute.setRequired( daoUtil.getInt( 8 ) != 0 );
                attribute.setSearchable( daoUtil.getInt( 9 ) != 0 );

                String strContentType = daoUtil.getString( 11 );

                if ( StringUtils.isNotBlank( strContentType ) )
                {
                    // File attribute
                    attribute.setBinary( true );
                    attribute.setTextValue( daoUtil.getString( 10 ) );
                    attribute.setValueContentType( strContentType );
                }
                else
                {
                    // Text attribute
                    attribute.setBinary( false );
                    attribute.setTextValue( daoUtil.getString( 10 ) );
                    attribute.setValueContentType( StringUtils.EMPTY );
                }

                listAttributes.add( attribute );
            }

            document.setAttributes( listAttributes );
        }
    }

    /**
     * Delete a record from the table
     *
     * @param nDocumentId
     *            the document identifier
     */
    @Override
    public void delete( int nDocumentId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nDocumentId );

            daoUtil.executeUpdate( );

            // Delete attributes
            deleteAttributes( nDocumentId );
            // Delete categories
            deleteCategories( nDocumentId );
            // Delete history
            deleteHistory( nDocumentId );
        }
    }

    /**
     * Delete a record from the table
     * 
     * @param nDocumentId
     *            The Document identifier
     */
    private void deleteAttributes( int nDocumentId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ATTRIBUTES ) )
        {
            daoUtil.setInt( 1, nDocumentId );
            daoUtil.setBoolean( 2, false );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Delete a validated record from the table
     * 
     * @param nDocumentId
     *            The Document identifier
     */
    private void deleteValidatedAttributes( int nDocumentId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ATTRIBUTES ) )
        {
            daoUtil.setInt( 1, nDocumentId );
            daoUtil.setBoolean( 2, true );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * validate a record from the table
     * 
     * @param nDocumentId
     *            The Document identifier
     */
    @Override
    public void validateAttributes( int nDocumentId )
    {
        deleteValidatedAttributes( nDocumentId );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_VALIDATE_ATTRIBUTES ) )
        {
            daoUtil.setBoolean( 1, true );
            daoUtil.setInt( 2, nDocumentId );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Delete a record from the table
     * 
     * @param nDocumentId
     *            The Document identifier
     */
    private void deleteHistory( int nDocumentId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_DOCUMENT_HISTORY ) )
        {
            daoUtil.setInt( 1, nDocumentId );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the record in the table
     *
     * @param document
     *            The reference of document
     * @param bUpdateContent
     *            the boolean
     */
    @Override
    public void store( Document document, boolean bUpdateContent )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            daoUtil.setString( 1, document.getCodeDocumentType( ) );
            daoUtil.setString( 2, document.getTitle( ) );
            daoUtil.setTimestamp( 3, document.getDateCreation( ) );
            daoUtil.setTimestamp( 4, document.getDateModification( ) );
            daoUtil.setInt( 5, document.getSpaceId( ) );
            daoUtil.setInt( 6, document.getStateId( ) );
            daoUtil.setString( 7, document.getSummary( ) );
            daoUtil.setString( 8, document.getComment( ) );
            daoUtil.setTimestamp( 9, document.getDateValidityBegin( ) );
            daoUtil.setTimestamp( 10, document.getDateValidityEnd( ) );
            daoUtil.setString( 11, document.getMetadata( ) );
            daoUtil.setInt( 12, document.getCreatorId( ) );
            daoUtil.setInt( 13, document.getMailingListId( ) );
            daoUtil.setInt( 14, document.getPageTemplateDocumentId( ) );
            daoUtil.setBoolean( 15, document.isSkipPortlet( ) );
            daoUtil.setBoolean( 16, document.isSkipCategories( ) );
            daoUtil.setString( 17, document.getValidatedTitle( ) );
            daoUtil.setString( 18, document.getValidatedSummary( ) );
            daoUtil.setInt( 19, document.getId( ) );

            daoUtil.executeUpdate( );

            if ( bUpdateContent )
            {
                deleteAttributes( document.getId( ) );
                insertAttributes( document );
                deleteCategories( document.getId( ) );
                insertCategories( document.getCategories( ), document.getId( ) );
            }
        }
    }

    /**
     * Load the list of documents
     *
     * @return The Collection of the Document ids
     * @param filter
     *            The DocumentFilter Object
     */
    @Override
    public Collection<Integer> selectPrimaryKeysByFilter( DocumentFilter filter )
    {
        Collection<Integer> listDocumentIds = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( buildQueryFromFilter( filter ) ) )
        {
            prepareStatement( filter, daoUtil );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listDocumentIds.add( daoUtil.getInt( 1 ) );
            }
        }
        return listDocumentIds;
    }

    /**
     * Load the list of documents
     *
     * @return The Collection of the Documents
     * @param filter
     *            The DocumentFilter Object
     */
    @Override
    public List<Document> selectByFilter( DocumentFilter filter )
    {
        List<Document> listDocuments = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( buildQueryFromFilter( filter ) ) )
        {
            prepareStatement( filter, daoUtil );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Document document = new Document( );
                document.setId( daoUtil.getInt( 1 ) );
                document.setCodeDocumentType( daoUtil.getString( 2 ) );
                document.setTitle( daoUtil.getString( 3 ) );
                document.setDateCreation( daoUtil.getTimestamp( 4 ) );
                document.setDateModification( daoUtil.getTimestamp( 5 ) );
                document.setSpaceId( daoUtil.getInt( 6 ) );
                document.setSpace( daoUtil.getString( 7 ) );
                document.setStateId( daoUtil.getInt( 8 ) );
                document.setStateKey( daoUtil.getString( 9 ) );
                document.setType( daoUtil.getString( 10 ) );
                document.setSummary( daoUtil.getString( 11 ) );
                document.setComment( daoUtil.getString( 12 ) );
                document.setDateValidityBegin( daoUtil.getTimestamp( 13 ) );
                document.setDateValidityEnd( daoUtil.getTimestamp( 14 ) );
                document.setMetadata( daoUtil.getString( 15 ) );
                document.setCreatorId( daoUtil.getInt( 16 ) );
                document.setMailingListId( daoUtil.getInt( 17 ) );
                document.setPageTemplateDocumentId( daoUtil.getInt( 18 ) );
                document.setSkipPortlet( daoUtil.getBoolean( 19 ) );
                document.setSkipCategories( daoUtil.getBoolean( 20 ) );
                document.setValidatedTitle( daoUtil.getString( 21 ) );
                document.setValidatedSummary( daoUtil.getString( 22 ) );

                if ( filter.getLoadBinaries( ) )
                {
                    loadAttributes( document );
                }
                else
                {
                    loadAttributesWithoutBinaries( document, document.getStateId( ) == DocumentState.STATE_VALIDATE );
                }

                document.setCategories( selectCategories( document.getId( ) ) );

                listDocuments.add( document );
            }
        }
        return listDocuments;
    }

    /**
     * Prepare the SQL Request
     * @param filter the filters to be applied
     */
    private static void prepareStatement( DocumentFilter filter, DAOUtil daoUtil )
    {
        int nIndex = 1;

        if ( filter.containsDocumentTypeCriteria( ) )
        {
            daoUtil.setString( nIndex, filter.getCodeDocumentType( ) );
            nIndex++;
        }

        if ( filter.containsSpaceCriteria( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSpace( ) );
            nIndex++;
        }

        if ( filter.containsStateCriteria( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdState( ) );
            nIndex++;
        }

        if ( filter.containsCategoriesCriteria( ) )
        {
            for ( int nCategoryId : filter.getCategoriesId( ) )
            {
                if ( nCategoryId > 0 )
                {
                    daoUtil.setInt( nIndex, nCategoryId );
                    nIndex++;
                }
            }
        }

        if ( filter.containsIdsCriteria( ) )
        {
            for ( int nId : filter.getIds( ) )
            {
                daoUtil.setInt( nIndex, nId );
                nIndex++;
            }
        }

        //These are condition the same condition from line 872 to 889, they are preventing SQL injections
        if (StringUtils.isNotBlank( filter.getDateMin( ) ) && StringUtils.isNotBlank( filter.getDateMax( ) )){
            daoUtil.setString( nIndex, filter.getDateMax( ) );
            nIndex++;
            daoUtil.setString( nIndex, filter.getDateMin( ) );
        }else{
            if ( StringUtils.isNotBlank( filter.getDateMin( ) ) ){
                daoUtil.setString( nIndex, filter.getDateMin( ) );
            }else{
                if (StringUtils.isNotBlank( filter.getDateMax( ) )){
                    daoUtil.setString( nIndex, filter.getDateMax( ) );
                }
            }
        }
    }

    /**
     *  Build the sql query from the filter.
     * @param filter
     *      The DocumentFilter Object
     * @return sql request
     */
    private String buildQueryFromFilter( DocumentFilter filter ){
        String strSQL = SQL_QUERY_SELECT_BY_FILTER;
        List<String> wherePart = new ArrayList<>( );

        if ( filter.containsDocumentTypeCriteria( ) ){
            wherePart.add( SQL_FILTER_DOCUMENT_TYPE );
        }
        if ( filter.containsSpaceCriteria( ) )
        {
            wherePart.add( SQL_FILTER_SPACE );
        }
        if ( filter.containsStateCriteria( ) )
        {
            wherePart.add( SQL_FILTER_STATE );
        }
        if ( filter.containsCategoriesCriteria( ) )
        {
            List<String> filterCategories = new ArrayList<>( );
            for ( int nCategoryId : filter.getCategoriesId( ) )
            {
                if ( nCategoryId > 0 )
                {
                    filterCategories.add( SQL_FILTER_CATEGORIES );
                }
                else
                {
                    filterCategories.add( SQL_FILTER_CATEGORIES_NULL );
                }
            }
            wherePart.add( SQL_FILTER_CATEGORIES_BEGIN + StringUtils.join( filterCategories, SQL_FILTER_CATEGORIES_OR ) + SQL_FILTER_CATEGORIES_END );
        }
        if ( filter.containsIdsCriteria( ) )
        {
            List<String> filterIds = new ArrayList<>( );

            for ( int i = 0; i < filter.getIds( ).length; i++ )
            {
                filterIds.add( SQL_FILTER_ID );
            }
            wherePart.add( SQL_FILTER_ID_BEGIN + StringUtils.join( filterIds, SQL_FILTER_ID_OR ) + SQL_FILTER_ID_END );
        }

        if ( BooleanUtils.isFalse( filter.isPublished( ) ) )
        {
            wherePart.add( "a.id_document NOT IN (SELECT DISTINCT id_document FROM document_published) " );
        }

        if ( StringUtils.isNotBlank( filter.getDateMin( ) ) && StringUtils.isNotBlank( filter.getDateMax( ) ) )
        {
            wherePart.add( "a.date_modification < ? " + SQL_FILTER_AND+" a.date_modification > ?" );
        }
        else
        {
            if ( StringUtils.isNotBlank( filter.getDateMin( ) ) )
            {
                wherePart.add( "a.date_modification > ?" );
            }
            else
            {
                if ( StringUtils.isNotBlank( filter.getDateMax( ) ) )
                {
                    wherePart.add("a.date_modification <= ?" );
                }
            }
        }
        if ( ! wherePart.isEmpty() )
        {
            strSQL += ( SQL_FILTER_WHERE_CLAUSE + StringUtils.join(wherePart, SQL_FILTER_AND) );
        }
        strSQL += SQL_ORDER_BY_LAST_MODIFICATION;

        return strSQL;
    }

    /**
     * Load the list of documents in relation with categories of specified document
     * 
     * @param document
     *            The document with the categories
     * @return The Collection of the Documents
     */
    @Override
    public List<Document> selectByRelatedCategories( Document document )
    {
        List<Document> listDocument = new ArrayList<>( );

        if ( ( document == null ) || ( document.getId( ) <= 0 ) )
        {
            return listDocument;
        }

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_RELATED_CATEGORY ) )
        {
            daoUtil.setInt( 1, document.getId( ) );
            daoUtil.executeQuery( );

            Document returnDocument = null;

            while ( daoUtil.next( ) )
            {
                returnDocument = new Document( );
                returnDocument.setId( daoUtil.getInt( 1 ) );
                returnDocument.setCodeDocumentType( daoUtil.getString( 2 ) );
                returnDocument.setTitle( daoUtil.getString( 3 ) );
                returnDocument.setDateCreation( daoUtil.getTimestamp( 4 ) );
                returnDocument.setDateModification( daoUtil.getTimestamp( 5 ) );
                returnDocument.setSpaceId( daoUtil.getInt( 6 ) );
                returnDocument.setSpace( daoUtil.getString( 7 ) );
                returnDocument.setStateId( daoUtil.getInt( 8 ) );
                returnDocument.setStateKey( daoUtil.getString( 9 ) );
                returnDocument.setType( daoUtil.getString( 10 ) );
                returnDocument.setSummary( daoUtil.getString( 11 ) );
                returnDocument.setComment( daoUtil.getString( 12 ) );
                returnDocument.setDateValidityBegin( daoUtil.getTimestamp( 13 ) );
                returnDocument.setDateValidityEnd( daoUtil.getTimestamp( 14 ) );
                returnDocument.setMetadata( daoUtil.getString( 15 ) );
                returnDocument.setCreatorId( daoUtil.getInt( 16 ) );
                returnDocument.setMailingListId( daoUtil.getInt( 17 ) );
                returnDocument.setPageTemplateDocumentId( daoUtil.getInt( 18 ) );
                returnDocument.setSkipPortlet( daoUtil.getBoolean( 19 ) );
                returnDocument.setSkipCategories( daoUtil.getBoolean( 20 ) );
                returnDocument.setValidatedTitle( daoUtil.getString( 21 ) );
                returnDocument.setValidatedSummary( daoUtil.getString( 22 ) );

                listDocument.add( returnDocument );

                loadAttributes( document );
                document.setCategories( selectCategories( document.getId( ) ) );
            }

        }

        return listDocument;
    }

    /**
     * Load a resource (image, file, ...) corresponding to an attribute of a Document
     *
     * @param nDocumentId
     *            The Document Id
     * @return the instance of the DocumentResource
     */
    @Override
    public DocumentResource loadResource( int nDocumentId )
    {
        DocumentResource resource = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DOCUMENT_RESOURCE ) )
        {
            daoUtil.setInt( 1, nDocumentId );
            daoUtil.executeQuery( );


            if ( daoUtil.next( ) )
            {
                resource = new DocumentResource( );
                resource.setContent( daoUtil.getBytes( 1 ) );
                resource.setContentType( daoUtil.getString( 2 ) );
                resource.setName( daoUtil.getString( 3 ) );
            }
        }
        return resource;
    }

    /**
     * Load a resource (image, file, ...) corresponding to an attribute of a Document
     *
     * @param nDocumentId
     *            The Document Id
     * @param nAttributeId
     *            The Attribute Id
     * @param bValidated
     *            true if we want the validated resource
     * @return the instance of the DocumentResource
     */
    @Override
    public DocumentResource loadSpecificResource( int nDocumentId, int nAttributeId, boolean bValidated )
    {
        DocumentResource resource = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DOCUMENT_SPECIFIC_RESOURCE ) )
        {
            daoUtil.setInt( 1, nDocumentId );
            daoUtil.setInt( 2, nAttributeId );
            daoUtil.setBoolean( 3, bValidated );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                resource = new DocumentResource( );
                resource.setContent( daoUtil.getBytes( 1 ) );
                resource.setContentType( daoUtil.getString( 2 ) );
                resource.setName( daoUtil.getString( 3 ) );
            }
        }
        return resource;
    }

    /**
     * Gets all documents id
     * 
     * @return A collection of Integer
     */
    @Override
    public Collection<Integer> selectAllPrimaryKeys( )
    {
        Collection<Integer> listPrimaryKeys = new ArrayList<>( );
        String strSQL = SQL_QUERY_SELECT_PRIMARY_KEYS;

        try ( DAOUtil daoUtil = new DAOUtil( strSQL ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listPrimaryKeys.add( daoUtil.getInt( 1 ) );
            }
        }
        return listPrimaryKeys;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Integer> selectIdsByValidatedTextValue( String strFragment, int nExcludedDocumentId )
    {
        Collection<Integer> listDocumentIds = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_VALIDATED_TEXT_VALUE ) )
        {
            daoUtil.setInt( 1, nExcludedDocumentId );
            daoUtil.setString( 2, "%" + strFragment + "%" );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listDocumentIds.add( daoUtil.getInt( 1 ) );
            }
        }
        return listDocumentIds;
    }

    /**
     * Load the path of page template
     *
     * @param nIdPageTemplateDocument
     *            The identifier of page template
     * @return the page template path
     */
    @Override
    public String getPageTemplateDocumentPath( int nIdPageTemplateDocument )
    {
        String strPageTemplatePath = "";
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PAGE_TEMPLATE_PATH ) )
        {
            daoUtil.setInt( 1, nIdPageTemplateDocument );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                strPageTemplatePath = daoUtil.getString( 1 );
            }
        }
        return strPageTemplatePath;
    }

    /**
     * Select a list of Category for a specified Document id
     * 
     * @param nIdDocument
     *            The document Id
     * @return The Collection of Category (empty collection is no result)
     */
    private List<Category> selectCategories( int nIdDocument )
    {
        int nParam;
        List<Category> listCategory = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_CATEGORY ) )
        {
            daoUtil.setInt( 1, nIdDocument );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                nParam = 0;

                Category category = new Category( );
                category.setId( daoUtil.getInt( ++nParam ) );
                category.setName( daoUtil.getString( ++nParam ) );
                category.setDescription( daoUtil.getString( ++nParam ) );
                category.setIconContent( daoUtil.getBytes( ++nParam ) );
                category.setIconMimeType( daoUtil.getString( ++nParam ) );

                listCategory.add( category );
            }
        }
        return listCategory;
    }

    /**
     * Insert links between Category and id document
     * 
     * @param listCategory
     *            The list of Category
     * @param nIdDocument
     *            The id of document
     *
     */
    private void insertCategories( List<Category> listCategory, int nIdDocument )
    {
        if ( listCategory != null && !listCategory.isEmpty( ) )
        {
            try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_LINK_CATEGORY_DOCUMENT ) )
            {

                for ( Category category : listCategory )
                {
                    daoUtil.setInt( 1, category.getId( ) );
                    daoUtil.setInt( 2, nIdDocument );
                    daoUtil.executeUpdate( );
                }
            }
        }
    }

    /**
     * Delete all links for a document
     * 
     * @param nIdDocument
     *            The identifier of the object Document
     */
    private void deleteCategories( int nIdDocument )
    {
        int nParam = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LINKS_DOCUMENT ) )
        {
            daoUtil.setInt( ++nParam, nIdDocument );
            daoUtil.executeUpdate( );

        }
    }

    /**
     * Load document type and date last modification for HTTP GET conditional request ("If-Modified-Since")
     * 
     * @param nIdDocument
     *            The id of the document
     * @return the document
     */
    @Override
    public Document loadLastModifiedAttributes( int nIdDocument )
    {
        Document document = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_LAST_MODIFIED ) )
        {
            daoUtil.setInt( 1, nIdDocument );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                document = new Document( );
                document.setId( nIdDocument );
                document.setCodeDocumentType( daoUtil.getString( 1 ) );
                document.setDateModification( daoUtil.getTimestamp( 2 ) );
            }
        }
        return document;
    }

    /**
     * Load the data of last Document the user worked in from the table
     *
     * @param strUserName
     *            the user name
     * @return the instance of the Document
     */
    @Override
    public Document loadLastModifiedDocumentFromUser( String strUserName )
    {
        Document document = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LAST_MODIFIED_DOCUMENT_FROM_USER ) )
        {
            daoUtil.setString( 1, strUserName );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nIndex = 1;
                document = new Document( );
                document.setId( daoUtil.getInt( nIndex++ ) );
                document.setCodeDocumentType( daoUtil.getString( nIndex++ ) );
                document.setTitle( daoUtil.getString( nIndex++ ) );
                document.setDateCreation( daoUtil.getTimestamp( nIndex++ ) );
                document.setDateModification( daoUtil.getTimestamp( nIndex++ ) );
                document.setSpaceId( daoUtil.getInt( nIndex++ ) );
                document.setSpace( daoUtil.getString( nIndex++ ) );
                document.setStateId( daoUtil.getInt( nIndex++ ) );
                document.setStateKey( daoUtil.getString( nIndex++ ) );
                document.setType( daoUtil.getString( nIndex++ ) );
                document.setSummary( daoUtil.getString( nIndex++ ) );
                document.setComment( daoUtil.getString( nIndex++ ) );
                document.setDateValidityBegin( daoUtil.getTimestamp( nIndex++ ) );
                document.setDateValidityEnd( daoUtil.getTimestamp( nIndex++ ) );
                document.setMetadata( daoUtil.getString( nIndex++ ) );
                document.setCreatorId( daoUtil.getInt( nIndex++ ) );
                document.setMailingListId( daoUtil.getInt( nIndex++ ) );
                document.setPageTemplateDocumentId( daoUtil.getInt( nIndex++ ) );
                document.setSkipPortlet( daoUtil.getBoolean( nIndex++ ) );
                document.setSkipCategories( daoUtil.getBoolean( nIndex++ ) );
                document.setValidatedTitle( daoUtil.getString( nIndex++ ) );
                document.setValidatedSummary( daoUtil.getString( nIndex++ ) );
            }
        }
        return document;
    }

    /**
     * Load the data of last Document the user worked in from the table
     *
     * @return the instance of the Document
     */
    @Override
    public Document loadLastPublishedDocument( )
    {
        Document document = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LAST_PUBLISHED_DOCUMENT ) )
        {
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nIndex = 1;
                document = new Document( );
                document.setId( daoUtil.getInt( nIndex++ ) );
                document.setCodeDocumentType( daoUtil.getString( nIndex++ ) );
                document.setTitle( daoUtil.getString( nIndex++ ) );
                document.setDateCreation( daoUtil.getTimestamp( nIndex++ ) );
                document.setDateModification( daoUtil.getTimestamp( nIndex++ ) );
                document.setSpaceId( daoUtil.getInt( nIndex++ ) );
                document.setSpace( daoUtil.getString( nIndex++ ) );
                document.setStateId( daoUtil.getInt( nIndex++ ) );
                document.setStateKey( daoUtil.getString( nIndex++ ) );
                document.setType( daoUtil.getString( nIndex++ ) );
                document.setSummary( daoUtil.getString( nIndex++ ) );
                document.setComment( daoUtil.getString( nIndex++ ) );
                document.setDateValidityBegin( daoUtil.getTimestamp( nIndex++ ) );
                document.setDateValidityEnd( daoUtil.getTimestamp( nIndex++ ) );
                document.setMetadata( daoUtil.getString( nIndex++ ) );
                document.setCreatorId( daoUtil.getInt( nIndex++ ) );
                document.setMailingListId( daoUtil.getInt( nIndex++ ) );
                document.setPageTemplateDocumentId( daoUtil.getInt( nIndex++ ) );
                document.setSkipPortlet( daoUtil.getBoolean( nIndex++ ) );
                document.setSkipCategories( daoUtil.getBoolean( nIndex++ ) );
                document.setValidatedTitle( daoUtil.getString( nIndex++ ) );
                document.setValidatedSummary( daoUtil.getString( nIndex++ ) );
            }
        }
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Document> selectWithValidatedVersion( String strCodeDocumentType, String strTitleFragment, Collection<Integer> listSpaceIds, int nOffset,
            int nLimit )
    {
        List<Document> listDocuments = new ArrayList<>( );

        if ( listSpaceIds.isEmpty( ) )
        {
            return listDocuments;
        }

        String strSQL = SQL_QUERY_SELECT_WITH_VALIDATED_VERSION + getValidatedVersionFilter( strTitleFragment, listSpaceIds ) + SQL_ORDER_BY_LAST_CREATED
                + nLimit + SQL_OFFSET + nOffset;

        try ( DAOUtil daoUtil = new DAOUtil( strSQL ) )
        {
            setValidatedVersionFilter( daoUtil, strCodeDocumentType, strTitleFragment, listSpaceIds );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Document document = new Document( );
                document.setId( daoUtil.getInt( 1 ) );
                document.setCodeDocumentType( daoUtil.getString( 2 ) );
                document.setTitle( daoUtil.getString( 3 ) );
                document.setValidatedTitle( daoUtil.getString( 4 ) );
                document.setSpaceId( daoUtil.getInt( 5 ) );
                document.setStateId( daoUtil.getInt( 6 ) );
                document.setSpace( daoUtil.getString( 7 ) );
                document.setDateCreation( daoUtil.getTimestamp( 8 ) );
                listDocuments.add( document );
            }
        }

        return listDocuments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countWithValidatedVersion( String strCodeDocumentType, String strTitleFragment, Collection<Integer> listSpaceIds )
    {
        if ( listSpaceIds.isEmpty( ) )
        {
            return 0;
        }

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_WITH_VALIDATED_VERSION + getValidatedVersionFilter( strTitleFragment, listSpaceIds ) ) )
        {
            setValidatedVersionFilter( daoUtil, strCodeDocumentType, strTitleFragment, listSpaceIds );
            daoUtil.executeQuery( );

            return daoUtil.next( ) ? daoUtil.getInt( 1 ) : 0;
        }
    }

    /**
     * Builds the filter of the documents with a validated version
     *
     * @param strTitleFragment
     *            The fragment searched in the titles, empty for no title criteria
     * @param listSpaceIds
     *            The spaces of the documents, not empty
     * @return The SQL filter
     */
    private static String getValidatedVersionFilter( String strTitleFragment, Collection<Integer> listSpaceIds )
    {
        StringBuilder sbFilter = new StringBuilder( SQL_FILTER_WITH_VALIDATED_VERSION );

        if ( !strTitleFragment.isEmpty( ) )
        {
            sbFilter.append( SQL_FILTER_TITLE_FRAGMENT );
        }

        sbFilter.append( SQL_FILTER_SPACES_BEGIN ).append( String.join( ", ", Collections.nCopies( listSpaceIds.size( ), "?" ) ) )
                .append( SQL_FILTER_SPACES_END );

        return sbFilter.toString( );
    }

    /**
     * Sets the parameters of the filter of the documents with a validated version
     *
     * @param daoUtil
     *            The statement
     * @param strCodeDocumentType
     *            The document type code
     * @param strTitleFragment
     *            The fragment searched in the titles, empty for no title criteria
     * @param listSpaceIds
     *            The spaces of the documents
     */
    private static void setValidatedVersionFilter( DAOUtil daoUtil, String strCodeDocumentType, String strTitleFragment, Collection<Integer> listSpaceIds )
    {
        int nIndex = 1;
        daoUtil.setString( nIndex++, strCodeDocumentType );

        if ( !strTitleFragment.isEmpty( ) )
        {
            String strPattern = "%" + strTitleFragment.toLowerCase( ) + "%";
            daoUtil.setString( nIndex++, strPattern );
            daoUtil.setString( nIndex++, strPattern );
        }

        for ( int nSpaceId : listSpaceIds )
        {
            daoUtil.setInt( nIndex++, nSpaceId );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, String> selectLegacyMetadata( )
    {
        Map<Integer, String> mapMetadata = new LinkedHashMap<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LEGACY_METADATA ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                mapMetadata.put( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }
        }

        return mapMetadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeMigratedMetadata( int nDocumentId, String strMetadata )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_MIGRATED_METADATA ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, strMetadata );
            daoUtil.setInt( nIndex, nDocumentId );

            daoUtil.executeUpdate( );
        }
    }
}
