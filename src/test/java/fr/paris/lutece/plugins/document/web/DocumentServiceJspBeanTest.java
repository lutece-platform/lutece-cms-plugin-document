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
package fr.paris.lutece.plugins.document.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.insert.InsertService;
import fr.paris.lutece.portal.service.insert.InsertServiceManager;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;

/**
 * Tests the insert service of the documents, used by the rich text editor to insert a link to a document
 */
public class DocumentServiceJspBeanTest extends LuteceTestCase
{
    private static final String INSERT_SERVICE_ID = "DOCUMENT_INSERT_SERVICE";
    private static final String ATTRIBUTE_ADMIN_USER = "lutece_admin_user";
    private static final String PARAMETER_INPUT = "input";
    private static final String EDITOR_INPUT = "document_body";

    /**
     * Removes the request registered by a test
     */
    @AfterEach
    public void removeRequest( )
    {
        LocalVariables.remove( );
    }

    /**
     * The core creates the selection bean of an insert service by reflection: its selector must render without CDI injection
     */
    @Test
    public void testInsertServiceSelectorUI( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( PARAMETER_INPUT, EDITOR_INPUT );
        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setLocale( Locale.FRENCH );
        request.getSession( true ).setAttribute( ATTRIBUTE_ADMIN_USER, user );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );

        InsertService insertService = InsertServiceManager.getInsertService( INSERT_SERVICE_ID );
        assertNotNull( insertService, "the document insert service should be registered" );

        String strHtml = insertService.getSelectorUI( request );

        assertNotNull( strHtml );
        assertTrue( strHtml.contains( EDITOR_INPUT ), "the page selection form should be rendered for the editor field" );
        assertTrue( strHtml.contains( "id=\"pages_tree\"" ), "the pages should be displayed as a tree" );
    }
}
