/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...) for DocumentSpace objects
 */
public final class DocumentSpaceHome
{
    // Static variable pointed at the DAO instance
    private static IDocumentSpaceDAO _dao = SpringContextService.getBean( "document.documentSpaceDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DocumentSpaceHome(  )
    {
    }

    /**
     * Creation of an instance of documentSpace
     *
     * @param documentSpace The instance of the documentSpace which contains the informations to store
     * @return The  instance of documentSpace which has been created with its primary key.
     */
    public static DocumentSpace create( DocumentSpace documentSpace )
    {
        _dao.insert( documentSpace );

        return documentSpace;
    }

    /**
     * Update of the documentSpace which is specified in parameter
     *
     * @param documentSpace The instance of the documentSpace which contains the data to store
     * @return The instance of the  documentSpace which has been updated
     */
    public static DocumentSpace update( DocumentSpace documentSpace )
    {
        _dao.store( documentSpace );

        return documentSpace;
    }

    /**
     * Remove the DocumentSpace whose identifier is specified in parameter
     *
     * @param nDocumentSpaceId
     */
    public static void remove( int nDocumentSpaceId )
    {
        _dao.delete( nDocumentSpaceId );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a documentSpace whose identifier is specified in parameter
     *
     * @param nKey The Primary key of the documentSpace
     * @return An instance of documentSpace
     */
    public static DocumentSpace findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Returns a collection of documentSpaces objects
     *
     * @return A collection of documentSpaces
     * @param nParentSpaceId
     */
    public static List<DocumentSpace> findChilds( int nParentSpaceId )
    {
        return _dao.selectChilds( nParentSpaceId, null );
    }

    /**
     * Returns a collection of documentSpaces objects by type of document
     *
     * @return A collection of documentSpaces
     * @param nParentSpaceId
     * @param strCodeType the documen type
     */
    public static List<DocumentSpace> findChildsByTypeOfDocument( int nParentSpaceId, String strCodeType )
    {
        return _dao.selectChilds( nParentSpaceId, strCodeType );
    }

    /**
     * Returns a ReferenceList of documentSpaces objects
     * @return A ReferenceList of documentSpaces
     */
    public static ReferenceList getDocumentSpaceList(  )
    {
        return _dao.getDocumentSpaceList(  );
    }

    /**
     * Returns a ReferenceList of documentSpaces objects
     *
     * @return A ReferenceList of documentSpaces
     * @param locale
     */
    public static ReferenceList getViewTypeList( Locale locale )
    {
        return _dao.getViewTypeList( locale );
    }

    /**
     * Gets a list of icons available or space customization
     * @return A list of icons
     */
    public static ReferenceList getIconsList(  )
    {
        return _dao.getIconsList(  );
    }

    /**
     * Select all spaces
     * @return A collection of all spaces.
     */
    public static Collection<DocumentSpace> findAll(  )
    {
        return _dao.selectAll(  );
    }

    /**
     * Returns all allowed document types for a given space
     * @param nSpaceId The space Id
     * @return Allowed documents types as a ReferenceList
     */
    public static ReferenceList getAllowedDocumentTypes( int nSpaceId )
    {
        return _dao.getAllowedDocumentTypes( nSpaceId );
    }
}
