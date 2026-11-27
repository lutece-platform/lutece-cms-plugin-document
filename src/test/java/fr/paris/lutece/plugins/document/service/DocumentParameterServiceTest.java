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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Tests the default of the automatic validation of the documents: the validation workflow (moderation) applies until an administrator turns the
 * automatic validation on, as before the version 7.0.0
 */
public class DocumentParameterServiceTest extends LuteceTestCase
{
    private static final String KEY = DocumentParameterService.DATASTORE_KEY_WORKFLOW_AUTO_VALIDATION;
    private static final Pattern INSERT_VALUE = Pattern.compile( "'" + Pattern.quote( KEY ) + "'\\s*,\\s*'(\\w+)'" );

    @Inject
    private DocumentParameterService _service;

    private String _strSavedValue;

    /**
     * Saves the value installed by the init script
     */
    @BeforeEach
    public void saveValue( )
    {
        _strSavedValue = DatastoreService.getDataValue( KEY, null );
    }

    /**
     * Restores the value installed by the init script
     */
    @AfterEach
    public void restoreValue( )
    {
        if ( _strSavedValue == null )
        {
            DatastoreService.removeData( KEY );
        }
        else
        {
            DatastoreService.setDataValue( KEY, _strSavedValue );
        }
    }

    /**
     * A fresh installation keeps the validation workflow
     */
    @Test
    public void testFreshInstallationKeepsTheValidationWorkflow( )
    {
        assertFalse( _service.isWorkflowAutoValidation( ) );
    }

    /**
     * Without saved value, the shipped default keeps the validation workflow
     */
    @Test
    public void testDefaultWithoutSavedValueKeepsTheValidationWorkflow( )
    {
        DatastoreService.removeData( KEY );
        assertFalse( _service.isWorkflowAutoValidation( ) );
    }

    /**
     * The value saved in the admin dashboard applies
     */
    @Test
    public void testSavedValueApplies( )
    {
        _service.setWorkflowAutoValidation( true );
        assertTrue( _service.isWorkflowAutoValidation( ) );
        _service.setWorkflowAutoValidation( false );
        assertFalse( _service.isWorkflowAutoValidation( ) );
    }

    /**
     * The init script and the upgrade from 6.0.1 do not turn the automatic validation on
     *
     * @throws IOException
     *             if a script can not be read
     */
    @Test
    public void testScriptsDoNotTurnTheAutomaticValidationOn( ) throws IOException
    {
        assertEquals( "false", insertedValue( "src/sql/plugins/document/core/init_core_document.sql" ) );
        assertEquals( "false", insertedValue( "src/sql/plugins/document/upgrade/update_db_core_document-6.0.1-7.0.0.sql" ) );
    }

    private static String insertedValue( String strScript ) throws IOException
    {
        Matcher matcher = INSERT_VALUE.matcher( Files.readString( Path.of( strScript ), StandardCharsets.UTF_8 ) );
        assertTrue( matcher.find( ), strScript + " inserts " + KEY );
        return matcher.group( 1 );
    }
}
