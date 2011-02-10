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

import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;


/**
 * Interface for DocumentType DAO
 */
public interface IDocumentTypeDAO
{
    /**
     * Check if type has document
     *
     * @param strCode The code type
     * @return bCheck the boolean
     */
    boolean checkDocuments( String strCode );

    /**
     * Delete a record from the table
     *
     * @param strCode the code type
     */
    void delete( String strCode );

    /**
     * Load the Referencelist of documentTypes
     *
     * @return listDocumentTypes
     */
    ReferenceList getDocumentTypeList(  );

    /**
     * Insert a new record in the table.
     *
     * @param documentType The documentType object
     */
    void insert( DocumentType documentType );

    /**
     * Load the data of DocumentType from the table
     *
     * @param strDocumentTypeCode the code
     * @return the instance of the DocumentType
     */
    DocumentType load( String strDocumentTypeCode );

    /**
     * Reorder attributes
     *
     * @param nIdAttribute1 the attribute order
     * @param nOrderAttribute1 the attribute order
     * @param nIdAttribute2 the attribute order
     * @param nOrderAttribute2 the attribute order
     */
    void reorderAttributes( int nIdAttribute1, int nOrderAttribute1, int nIdAttribute2, int nOrderAttribute2 );

    /**
     * Load the list of documentTypes
     *
     * @return The Collection of the DocumentTypes
     */
    Collection<DocumentType> selectDocumentTypeList(  );

    /**
     * Update the record in the table
     *
     * @param documentType The reference of documentType
     */
    void store( DocumentType documentType );

    /**
     * Sets the admin stylesheet
     * @param baXslAdmin The stylesheet
     * @param strCodeType The code type
     */
    void setAdminStyleSheet( byte[] baXslAdmin, String strCodeType );

    /**
     * Sets the content service stylesheet
     * @param baXslContent The stylesheet
     * @param strCodeType The code type
     */
    void setContentStyleSheet( byte[] baXslContent, String strCodeType );
}
