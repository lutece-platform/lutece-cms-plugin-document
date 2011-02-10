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
package fr.paris.lutece.plugins.document.business.attributes;

import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * AttributeTypeDAO Interface
 */
public interface IAttributeTypeDAO
{
    /**
     * Delete a record from the table
     *
     * @param documentAttributeType The DocumentAttributeType object
     */
    void delete( AttributeType documentAttributeType );

    /**
     * Gets attributes managers list
     *
     * @return The list of attribute managers
     */
    ReferenceList getAttributeManagersList(  );

    /**
     * Insert a new record in the table.
     *
     *
     * @param documentAttributeType The documentAttributeType object
     */
    void insert( AttributeType documentAttributeType );

    /**
     * Load the data of DocumentAttributeType from the table
     *
     *
     * @param nDocumentAttributeTypeId The identifier of DocumentAttributeType
     * @return the instance of the DocumentAttributeType
     */
    AttributeType load( int nDocumentAttributeTypeId );

    /**
     * Load the list of Attribute Types
     *
     * @param locale
     * @return The Collection of the DocumentAttributeTypes
     */
    ReferenceList selectAttributeTypeList( Locale locale );

    /**
     * Load the list of attributeTypeParameters
     *
     * @param strAttributeTypeCode
     * @return The Collection of the AttributeTypeParameters
     */
    List<AttributeTypeParameter> selectAttributeTypeParameterList( String strAttributeTypeCode );

    /**
     * Load the list of documentAttributeTypes
     *
     * @return The Collection of the DocumentAttributeTypes
     */
    Collection<AttributeType> selectDocumentAttributeTypeList(  );

    /**
     * Update the record in the table
     *
     * @param documentAttributeType The reference of documentAttributeType
     */
    void store( AttributeType documentAttributeType );
}
