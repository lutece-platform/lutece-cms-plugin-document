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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.LegacyStylesheets;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Tests the access to the XSL stylesheets of the versions before 7.0.0
 */
public class LegacyStylesheetServiceTest extends LuteceTestCase
{
    private static final String TYPE_ARTICLE = "article";
    private static final String TYPE_IMAGE = "image";
    private static final String STYLESHEET = "<xsl:stylesheet>&amp;</xsl:stylesheet>";

    @Inject
    private LegacyStylesheetService _service;

    private boolean _bLegacyColumns;

    /**
     * Removes the legacy columns and the datastore key added by a test
     */
    @AfterEach
    public void removeLegacyData( )
    {
        DatastoreService.removeInstanceData( LegacyStylesheetService.DATASTORE_KEY_PENDING );

        if ( _bLegacyColumns )
        {
            execute( "ALTER TABLE document_type DROP COLUMN admin_xsl" );
            execute( "ALTER TABLE document_type DROP COLUMN content_service_xsl" );
            _bLegacyColumns = false;
        }
    }

    /**
     * On a new installation the legacy columns do not exist: the service does not read them
     */
    @Test
    public void testNewInstallation( )
    {
        assertTrue( _service.getDocumentTypesWithLegacyStylesheets( ).isEmpty( ) );
        assertNull( _service.getLegacyStylesheets( TYPE_ARTICLE ) );
        _service.discardLegacyStylesheets( TYPE_ARTICLE );
    }

    /**
     * After an upgrade, the legacy stylesheets are listed and read until they are discarded, then the migration is complete
     */
    @Test
    public void testUpgradedInstallation( )
    {
        execute( "ALTER TABLE document_type ADD COLUMN admin_xsl LONGVARBINARY" );
        execute( "ALTER TABLE document_type ADD COLUMN content_service_xsl LONGVARBINARY" );
        _bLegacyColumns = true;
        storeStylesheet( TYPE_ARTICLE );
        storeStylesheet( TYPE_IMAGE );
        DatastoreService.setInstanceDataValue( LegacyStylesheetService.DATASTORE_KEY_PENDING, "true" );

        assertEquals( java.util.List.of( TYPE_ARTICLE, TYPE_IMAGE ), _service.getDocumentTypesWithLegacyStylesheets( ) );

        LegacyStylesheets stylesheets = _service.getLegacyStylesheets( TYPE_ARTICLE );
        assertEquals( STYLESHEET, stylesheets.getContentStylesheet( ) );
        assertNull( stylesheets.getAdminStylesheet( ) );

        _service.discardLegacyStylesheets( TYPE_ARTICLE );
        assertNull( _service.getLegacyStylesheets( TYPE_ARTICLE ) );
        assertTrue( _service.isPending( ) );

        _service.discardLegacyStylesheets( TYPE_IMAGE );
        assertTrue( !_service.isPending( ) );
        assertTrue( _service.getDocumentTypesWithLegacyStylesheets( ).isEmpty( ) );
    }

    /**
     * Stores a front office stylesheet for a document type
     *
     * @param strCodeType
     *            The code of the document type
     */
    private void storeStylesheet( String strCodeType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( "UPDATE document_type SET content_service_xsl = ? WHERE code_document_type = ?" ) )
        {
            daoUtil.setBytes( 1, STYLESHEET.getBytes( StandardCharsets.UTF_8 ) );
            daoUtil.setString( 2, strCodeType );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Executes a SQL statement
     *
     * @param strSql
     *            The statement
     */
    private void execute( String strSql )
    {
        try ( DAOUtil daoUtil = new DAOUtil( strSql ) )
        {
            daoUtil.executeUpdate( );
        }
    }
}
