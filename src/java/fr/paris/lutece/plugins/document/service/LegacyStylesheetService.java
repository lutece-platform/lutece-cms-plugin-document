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
package fr.paris.lutece.plugins.document.service;

import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.LegacyStylesheets;
import fr.paris.lutece.portal.service.datastore.DatastoreService;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

/**
 * Gives access to the XSL stylesheets of the document types from the versions before 7.0.0, until they are rewritten as FreeMarker templates and
 * discarded. The upgrade script to 7.0.0 sets the datastore key {@value #DATASTORE_KEY_PENDING} to true when some stylesheets remain; on a new
 * installation the key does not exist, the legacy columns do not exist either and they are never read.
 */
@ApplicationScoped
public class LegacyStylesheetService
{
    /** Datastore key telling that some document types still have legacy stylesheets */
    public static final String DATASTORE_KEY_PENDING = "document.legacyStylesheets.pending";

    private static final String VALUE_TRUE = "true";
    private static final String VALUE_FALSE = "false";

    /**
     * Tells if some document types still have legacy stylesheets
     *
     * @return true if legacy stylesheets remain
     */
    public boolean isPending( )
    {
        return VALUE_TRUE.equals( DatastoreService.getInstanceDataValue( DATASTORE_KEY_PENDING, VALUE_FALSE ) );
    }

    /**
     * Returns the codes of the document types that still have legacy stylesheets
     *
     * @return The codes of the document types, empty if there are none
     */
    public List<String> getDocumentTypesWithLegacyStylesheets( )
    {
        return isPending( ) ? DocumentTypeHome.findCodesWithLegacyStylesheets( ) : new ArrayList<>( );
    }

    /**
     * Returns the legacy stylesheets of a document type
     *
     * @param strCodeType
     *            The code of the document type
     * @return The stylesheets, or null if the document type has none
     */
    public LegacyStylesheets getLegacyStylesheets( String strCodeType )
    {
        if ( !isPending( ) )
        {
            return null;
        }

        LegacyStylesheets stylesheets = DocumentTypeHome.findLegacyStylesheets( strCodeType );

        return ( ( stylesheets != null ) && ( ( stylesheets.getAdminStylesheet( ) != null ) || ( stylesheets.getContentStylesheet( ) != null ) ) )
                ? stylesheets
                : null;
    }

    /**
     * Discards the legacy stylesheets of a document type, once rewritten as FreeMarker templates. When no document type has legacy stylesheets any
     * more, the legacy stylesheets are no longer read, and the upgrade script drops their columns at the next start.
     *
     * @param strCodeType
     *            The code of the document type
     */
    public void discardLegacyStylesheets( String strCodeType )
    {
        if ( !isPending( ) )
        {
            return;
        }

        DocumentTypeHome.removeLegacyStylesheets( strCodeType );

        if ( DocumentTypeHome.findCodesWithLegacyStylesheets( ).isEmpty( ) )
        {
            DatastoreService.setInstanceDataValue( DATASTORE_KEY_PENDING, VALUE_FALSE );
        }
    }
}
