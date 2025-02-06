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

import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Locale;

/**
 * DocumentSpaceDAO Interface
 */
public interface IDocumentSpaceDAO
{
    /**
     * Delete a record from the table
     * 
     * @param nSpaceId
     *            The Id to delete
     */
    void delete( int nSpaceId );

    /**
     * Returns all allowed document types for a given space
     *
     * @param nSpaceId
     *            The space Id
     * @return Allowed documents types as a ReferenceList
     */
    ReferenceList getAllowedDocumentTypes( int nSpaceId );

    /**
     * Load the list of documentSpaces
     *
     * @return The Collection of the DocumentSpaces
     */
    ReferenceList getDocumentSpaceList( );

    /**
     * Gets a list of icons available or space customization
     *
     * @return A list of icons
     */
    ReferenceList getIconsList( );

    /**
     * Load the list of documentSpaces
     * 
     * @param locale
     *            The Locale
     * @return The Collection of the DocumentSpaces
     */
    ReferenceList getViewTypeList( Locale locale );

    /**
     * Insert a new record in the table.
     * 
     * @param space
     *            The space object
     */
    void insert( DocumentSpace space );

    /**
     * Load the data of DocumentSpace from the table
     * 
     * @param nDocumentSpaceId
     *            The identifier of DocumentSpace
     * @return the instance of the DocumentSpace
     */
    DocumentSpace load( int nDocumentSpaceId );

    /**
     * Select all spaces
     *
     * @return A collection of all spaces.
     */
    List<DocumentSpace> selectAll( );

    /**
     * Load the list of documentSpaces childs
     *
     * @param strCodeType
     *            the document type filter if needed (null if not)
     * @param nSpaceId
     *            The space identifier
     * @return The Collection of the DocumentSpaces
     */
    List<DocumentSpace> selectChilds( int nSpaceId, String strCodeType );

    /**
     * Load the list of documentSpaces authorizing the selected document type
     *
     * @param strCodeType
     *            the document type filter
     * @param createDocumentIsAllowed
     *            code to define if document creation is allowed or not
     * @return The Collection of the DocumentSpaces
     */
    List<DocumentSpace> selectSpacesAllowingDocumentCreationByDocumentType( String strCodeType, int createDocumentIsAllowed );

    /**
     * Update the record in the table
     *
     * @param space
     *            The reference of space
     */
    void store( DocumentSpace space );
}
