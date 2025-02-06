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
package fr.paris.lutece.plugins.document.business.autopublication;

import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;

/**
 * This class provides instances management methods for DocumentAutoPublication objects
 *
 */
public class DocumentAutoPublicationHome
{
    // Static variable pointed at the DAO instance
    private static IDocumentAutoPublicationDAO _dao = SpringContextService.getBean( "document.documentAutoPublicationDAO" );

    /* This class implements the Singleton design pattern. */

    /**
     * Constructor
     */
    private DocumentAutoPublicationHome( )
    {
    }

    /**
     * Insert a new document auto publication
     *
     * @param documentAutoPublication
     *            the instance of the DocumentAutoPublication object to insert
     */
    public static void add( DocumentAutoPublication documentAutoPublication )
    {
        _dao.insert( documentAutoPublication );
    }

    /**
     * Loads all data Document Auto Publication
     *
     * @return The {@link Collection} of {@link DocumentAutoPublication} object
     */
    public static Collection<DocumentAutoPublication> findAll( )
    {
        return _dao.load( );
    }

    /**
     * Loads the data of Document Auto Publication whose identifier is specified in parameter
     *
     * @param nPortletId
     *            The {@link Portlet} identifier
     * @param nSpaceId
     *            The {@link DocumentSpace} identifier
     * @return The {@link DocumentAutoPublication} object
     */
    public static DocumentAutoPublication findByPrimaryKey( int nPortletId, int nSpaceId )
    {
        return _dao.load( nPortletId, nSpaceId );
    }

    /**
     * Load the list of Document Auto Publication whose portlet identifier is specified in parameter
     *
     * @param nPortletId
     *            The {@link Portlet} identifier
     * @return The {@link Collection} of {@link DocumentAutoPublication} object
     */
    public static Collection<DocumentAutoPublication> findByPortletId( int nPortletId )
    {
        return _dao.selectByPortletId( nPortletId );
    }

    /**
     * Load the list of Document Auto Publication whose {@link DocumentSpace} identifier is specified in parameter
     *
     * @param nSpaceId
     *            The {@link DocumentSpace} identifier
     * @return The {@link Collection} of {@link DocumentAutoPublication} object
     */
    public static Collection<DocumentAutoPublication> findBySpaceId( int nSpaceId )
    {
        return _dao.selectBySpaceId( nSpaceId );
    }

    /**
     * Update the document auto publication
     *
     * @param documentAutoPublication
     *            The DocumentAutoPublication to update
     */
    public static void update( DocumentAutoPublication documentAutoPublication )
    {
        _dao.store( documentAutoPublication );
    }

    /**
     * Delete a document auto publication object
     *
     * @param nPortletId
     *            the portlet identifier
     * @param nSpaceId
     *            the space identifier
     */
    public static void remove( int nPortletId, int nSpaceId )
    {
        _dao.delete( nPortletId, nSpaceId );
    }

    /**
     * Delete All Space from a portlet
     *
     * @param nPortletId
     *            the portlet identifier
     */
    public static void removeAllSpaces( int nPortletId )
    {
        _dao.deleteAllSpaces( nPortletId );
    }

    /**
     * Check if the specified portlet is auto published
     *
     * @param nPortletId
     *            The portlet id
     * @return true if portlet is auto published, false else.
     */
    public static boolean isPortletAutoPublished( int nPortletId )
    {
        if ( _dao.selectByPortletId( nPortletId ).size( ) == 0 )
        {
            return false;
        }

        return true;
    }

    /**
     * Check if the specified Space is auto published
     *
     * @param nSpaceId
     *            The space id
     * @return true if Space is auto published, false else.
     */
    public static boolean isSpaceAutoPublished( int nSpaceId )
    {
        if ( _dao.selectBySpaceId( nSpaceId ).size( ) == 0 )
        {
            return false;
        }

        return true;
    }
}
