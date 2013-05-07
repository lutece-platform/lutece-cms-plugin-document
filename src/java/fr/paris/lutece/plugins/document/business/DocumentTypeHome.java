/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;


/**
 * This class provides instances management methods (create, find, ...) for DocumentType objects
 */
public final class DocumentTypeHome
{
    // Static variable pointed at the DAO instance
    private static IDocumentTypeDAO _dao = SpringContextService.getBean( "document.documentTypeDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DocumentTypeHome(  )
    {
    }

    /**
     * Creation of an instance of documentType
     *
     * @param documentType The instance of the documentType which contains the informations to store
     * @return The  instance of documentType which has been created with its primary key.
     */
    public static DocumentType create( DocumentType documentType )
    {
        _dao.insert( documentType );

        return documentType;
    }

    /**
     * Update of the documentType which is specified in parameter
     *
     * @param documentType The instance of the documentType which contains the data to store
     * @return The instance of the  documentType which has been updated
     */
    public static DocumentType update( DocumentType documentType )
    {
        _dao.store( documentType );

        return documentType;
    }

    /**
     * Remove the DocumentType whose identifier is specified in parameter
     *
     * @param strCode
     */
    public static void remove( String strCode )
    {
        _dao.delete( strCode );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a documentType whose identifier is specified in parameter
     *
     * @return An instance of documentType
     * @param strCode
     */
    public static DocumentType findByPrimaryKey( String strCode )
    {
        return _dao.load( strCode );
    }

    /**
     * Returns a collection of documentTypes objects
     * @return A collection of documentTypes
     */
    public static Collection<DocumentType> findAll(  )
    {
        return _dao.selectDocumentTypeList(  );
    }

    /**
     * Checks if type has documents
     * @param strCode
     * @return
     */
    public static boolean checkDocuments( String strCode )
    {
        return _dao.checkDocuments( strCode );
    }

    /**
     * Reorder Attributes
     *
     * @param nIdAttribute1
     * @param nOrderAttribute1
     * @param nIdAttribute2
     * @param nOrderAttribute2
     */
    public static void reorderAttributes( int nIdAttribute1, int nOrderAttribute1, int nIdAttribute2,
        int nOrderAttribute2 )
    {
        _dao.reorderAttributes( nIdAttribute1, nOrderAttribute1, nIdAttribute2, nOrderAttribute2 );
    }

    /**
     * Get document types list
     * @return  document types list
     */
    public static ReferenceList getDocumentTypesList(  )
    {
        return _dao.getDocumentTypeList(  );
    }

    /**
     * Sets the admin stylesheet
     * @param baXslAdmin The stylesheet
     * @param strCodeType The code type
     */
    public static void setAdminStyleSheet( byte[] baXslAdmin, String strCodeType )
    {
        _dao.setAdminStyleSheet( baXslAdmin, strCodeType );
    }

    /**
     * Sets the content service stylesheet
     * @param baXslContent The stylesheet
     * @param strCodeType The code type
     */
    public static void setContentStyleSheet( byte[] baXslContent, String strCodeType )
    {
        _dao.setContentStyleSheet( baXslContent, strCodeType );
    }
}
