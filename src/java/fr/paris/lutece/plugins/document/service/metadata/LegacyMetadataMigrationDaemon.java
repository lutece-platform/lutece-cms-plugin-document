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
package fr.paris.lutece.plugins.document.service.metadata;

import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.service.metadata.LegacyMetadataConverter.LegacyMetadataException;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.Map;

/**
 * Migrates the metadata of the documents from the legacy XML column to the JSON column. The upgrade script to 7.0.0 sets the datastore key
 * {@value #DATASTORE_KEY_PENDING} to true; the migration runs until every legacy metadata is converted, then sets it to false. On a new
 * installation the key does not exist and the daemon does nothing.
 */
public class LegacyMetadataMigrationDaemon extends Daemon
{
    /** Datastore key telling that the legacy metadata must be migrated */
    public static final String DATASTORE_KEY_PENDING = "document.legacyMetadataMigration.pending";

    private static final String VALUE_TRUE = "true";
    private static final String VALUE_FALSE = "false";

    /**
     * Migrates the legacy metadata if the migration is pending
     */
    @Override
    public void run( )
    {
        if ( !VALUE_TRUE.equals( DatastoreService.getInstanceDataValue( DATASTORE_KEY_PENDING, VALUE_FALSE ) ) )
        {
            setLastRunLogs( "No legacy metadata to migrate" );

            return;
        }

        int nMigrated = 0;
        int nFailed = 0;

        for ( Map.Entry<Integer, String> entry : DocumentHome.findLegacyMetadata( ).entrySet( ) )
        {
            try
            {
                DocumentHome.storeMigratedMetadata( entry.getKey( ), LegacyMetadataConverter.toJson( entry.getValue( ) ) );
                nMigrated++;
            }
            catch( LegacyMetadataException e )
            {
                AppLogService.error( "The metadata of the document {} can not be migrated : {}", entry.getKey( ), e.getMessage( ), e );
                nFailed++;
            }
        }

        if ( nFailed == 0 )
        {
            DatastoreService.setInstanceDataValue( DATASTORE_KEY_PENDING, VALUE_FALSE );
        }

        setLastRunLogs( "Legacy metadata migrated : " + nMigrated + ", failed : " + nFailed );
    }
}
