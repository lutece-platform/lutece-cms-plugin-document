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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.service.metadata.LegacyMetadataConverter.LegacyMetadataException;

/**
 * Tests the conversion of the legacy XML metadata into JSON metadata, and the indexing of the metadata
 */
public class LegacyMetadataConverterTest
{
    private static final String LEGACY_METADATA = "<metadata>\r\n<creator>Mairie de Paris</creator>\r\n<subject>lutece;portal</subject>\r\n"
            + "<description>A &amp; B &lt;c&gt; &quot;d&quot;</description>\r\n<language>fr</language>\r\n</metadata>";

    /**
     * Each child element of the legacy metadata becomes a property of the JSON object, in the same order, with its unescaped text
     *
     * @throws LegacyMetadataException
     *             if the conversion fails
     */
    @Test
    public void testConversion( ) throws LegacyMetadataException
    {
        assertEquals( "{\"creator\":\"Mairie de Paris\",\"subject\":\"lutece;portal\",\"description\":\"A & B <c> \\\"d\\\"\",\"language\":\"fr\"}",
                LegacyMetadataConverter.toJson( LEGACY_METADATA ) );
    }

    /**
     * Legacy metadata that is not well-formed can not be converted
     */
    @Test
    public void testInvalidMetadata( )
    {
        assertThrows( LegacyMetadataException.class, ( ) -> LegacyMetadataConverter.toJson( "<metadata><title>unclosed</metadata>" ) );
    }

    /**
     * A DOCTYPE, and thus any external entity, is refused
     */
    @Test
    public void testDoctypeRefused( )
    {
        String strXxe = "<?xml version=\"1.0\"?><!DOCTYPE metadata [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]><metadata><title>&xxe;</title></metadata>";

        assertThrows( LegacyMetadataException.class, ( ) -> LegacyMetadataConverter.toJson( strXxe ) );
    }

    /**
     * The converted metadata are indexed by their values
     *
     * @throws LegacyMetadataException
     *             if the conversion fails
     */
    @Test
    public void testIndexableText( ) throws LegacyMetadataException
    {
        assertEquals( "Mairie de Paris lutece;portal A & B <c> \"d\" fr", MetadataService.getIndexableText( LegacyMetadataConverter.toJson( LEGACY_METADATA ) ) );
        assertEquals( "", MetadataService.getIndexableText( null ) );
        assertEquals( "", MetadataService.getIndexableText( "{\"title\":\"\"}" ) );
        assertEquals( "", MetadataService.getIndexableText( "<metadata/>" ) );
    }
}
