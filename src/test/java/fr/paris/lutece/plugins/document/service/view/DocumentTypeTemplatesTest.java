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
package fr.paris.lutece.plugins.document.service.view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collection;
import java.util.Locale;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Checks the templates of the document types stored in the database: they must render without falling back to the default templates
 */
public class DocumentTypeTemplatesTest extends LuteceTestCase
{
    @Inject
    private DocumentTemplateService _documentTemplateService;

    /**
     * Every stored template of the initial document types renders with a sample document of its type
     */
    @Test
    public void testStoredTemplatesAreValid( )
    {
        Collection<DocumentType> listTypes = DocumentTypeHome.findAll( );
        assertFalse( listTypes.isEmpty( ), "the initial document types should exist" );

        for ( DocumentType typeSummary : listTypes )
        {
            DocumentType type = DocumentTypeHome.findByPrimaryKey( typeSummary.getCode( ) );

            assertNull( _documentTemplateService.validateTemplate( type, type.getAdminTemplate( ), true, Locale.FRENCH ),
                    "the back office template of the type " + type.getCode( ) + " should be valid" );
            assertNull( _documentTemplateService.validateTemplate( type, type.getContentTemplate( ), false, Locale.FRENCH ),
                    "the front office template of the type " + type.getCode( ) + " should be valid" );
        }
    }

    /**
     * The default templates proposed in the type form are loaded through the core resource manager
     */
    @Test
    public void testDefaultTemplateSources( )
    {
        assertTrue( _documentTemplateService.getDefaultTemplateSource( true ).contains( "document_admin_default.labelTitle" ),
                "the default back office template should be loaded" );
        assertTrue( _documentTemplateService.getDefaultTemplateSource( false ).contains( "@documentValue value=document.title" ),
                "the default front office template should be loaded" );
    }
}
