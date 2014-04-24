/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...) for DocumentAttribute objects
 */
public final class DocumentAttributeHome
{
    // Static variable pointed at the DAO instance
    private static IDocumentAttributeDAO _dao = SpringContextService.getBean( "document.documentAttributeDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DocumentAttributeHome(  )
    {
    }

    /**
     * Creation of an instance of documentAttribute
     *
     * @param documentAttribute The instance of the documentAttribute which contains the informations to store
     * @return The  instance of documentAttribute which has been created with its primary key.
     */
    public static DocumentAttribute create( DocumentAttribute documentAttribute )
    {
        _dao.insert( documentAttribute );

        return documentAttribute;
    }

    /**
     * Update of the documentAttribute which is specified in parameter
     *
     * @param documentAttribute The instance of the documentAttribute which contains the data to store
     * @return The instance of the  documentAttribute which has been updated
     */
    public static DocumentAttribute update( DocumentAttribute documentAttribute )
    {
        _dao.store( documentAttribute );

        return documentAttribute;
    }

    /**
     * Remove the DocumentAttribute whose identifier is specified in parameter
     * 
     * @param nAttributeId The id of the attribute
     */
    public static void remove( int nAttributeId )
    {
        _dao.delete( nAttributeId );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a documentAttribute whose identifier is specified in parameter
     *
     * @param nKey The Primary key of the documentAttribute
     * @return An instance of documentAttribute
     */
    public static DocumentAttribute findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Returns a collection of documentAttributes objects
     * @param documentType The document type
     */
    public static void setDocumentTypeAttributes( DocumentType documentType )
    {
        _dao.selectAttributesByDocumentType( documentType );
    }

    /**
     * Get all attributes of document type
     * @param codeDocumentType The code document Type
     * @return listDocumentAttributes The list of all attributes of selected code document type
     */
    public static List<DocumentAttribute> selectAllAttributesOfDocumentType( String codeDocumentType )
    {
        return _dao.selectAllAttributesOfDocumentType( codeDocumentType );
    }

    /**
     * Get the attribute type parameters of an attribute
     * @param nAttributeId The id of the attribute to get the parameter of
     * @param locale The locale
     * @return The list of parameters
     */
    public static Collection<AttributeTypeParameter> getAttributeParametersValues( int nAttributeId, Locale locale )
    {
        Collection<AttributeTypeParameter> listParametersValues = _dao.selectAttributeParametersValues( nAttributeId );

        return I18nService.localizeCollection( listParametersValues, locale );
    }

    /**
     * Get the list of values of a parameters of an attribute
     * @param nAttributeId The id of the attribute
     * @param strParameterName The name of the parameter
     * @return The list of values of the parameter of the attribute
     */
    public static List<String> getAttributeParameterValues( int nAttributeId, String strParameterName )
    {
        return _dao.getAttributeParameterValues( nAttributeId, strParameterName );
    }

    /**
     * Inserts a regular expression in the attribute
     *
     * @param nIdAttribute The identifier of the document attribute
     * @param nIdExpression The identifier of the regular expression
     */
    public static void insertRegularExpression( int nIdAttribute, int nIdExpression )
    {
        _dao.insertRegularExpression( nIdAttribute, nIdExpression );
    }

    /**
     * Deletes a regular expression in the attribute
     *
     * @param nIdAttribute The identifier of the document attribute
     * @param nIdExpression The identifier of the regular expression
     */
    public static void deleteRegularExpression( int nIdAttribute, int nIdExpression )
    {
        _dao.deleteRegularExpression( nIdAttribute, nIdExpression );
    }

    /**
     * Loads all regular expression key associated to the attribute and returns them into a collection
     *
     * @param nIdAttribute The identifier of the document attribute
     * @return A collection of regular expression key
     */
    public static Collection<Integer> getListRegularExpressionKeyByIdAttribute( int nIdAttribute )
    {
        return _dao.selectListRegularExpressionKeyByIdAttribute( nIdAttribute );
    }
}
