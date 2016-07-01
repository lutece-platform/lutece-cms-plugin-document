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

import java.util.Collection;
import java.util.List;


/**
 * DocumentAttribute Interface
 */
public interface IDocumentAttributeDAO
{
    /**
     * Delete a record from the table
     *
     * @param nAttributeId The DocumentAttribute Id
     */
    void delete( int nAttributeId );

    /**
     * Returns the parameter values of an attribute
     *
     * @param nAttributeId The attribute Id
     * @param strParameterName The parameter name
     * @return The parameter values of an attribute
     */
    List<String> getAttributeParameterValues( int nAttributeId, String strParameterName );

    /**
     * Insert a new record in the table.
     *
     *
     * @param documentAttribute The documentAttribute object
     */
    void insert( DocumentAttribute documentAttribute );

    /**
     * Load the data of DocumentAttribute from the table
     *
     * @param nAttributeId The attribute Id
     * @return the instance of the DocumentAttribute
     */
    DocumentAttribute load( int nAttributeId );

    /**
     * Gets Attribute parameters values
     *
     * @param nAttributeId The attribute Id
     * @return List of attribute parameters values
     */
    List<AttributeTypeParameter> selectAttributeParametersValues( int nAttributeId );

    /**
     * Add attributes to a document
     *
     * @param documentType The document Type
     */
    void selectAttributesByDocumentType( DocumentType documentType );

    /**
     * Get all attributes of document type
         *
     * @param codeDocumentType The code document Type
     * @return listDocumentAttributes The list of all attributes of selected code document type
     */
    List<DocumentAttribute> selectAllAttributesOfDocumentType( String codeDocumentType );

    /**
     * Update the record in the table
     *
     * @param documentAttribute The document attribute
     */
    void store( DocumentAttribute documentAttribute );

    /**
     * Inserts an association between an attribute and a regular expression
     *
     * @param nIdAttribute The identifier of the document attribute
     * @param nIdExpression The identifier of the regular expression
     */
    void insertRegularExpression( int nIdAttribute, int nIdExpression );

    /**
     * Deletes an association between an attribute and a regular expression
     *
     * @param nIdAttribute The identifier of the document attribute
     * @param nIdExpression The identifier of the regular expression
     */
    void deleteRegularExpression( int nIdAttribute, int nIdExpression );

    /**
     * Loads all regular expression key associated to the attribute and returns them into a collection
     *
     * @param nIdAttribute The identifier of the document attribute
     * @return A collection of regular expression key
     */
    Collection<Integer> selectListRegularExpressionKeyByIdAttribute( int nIdAttribute );
}
