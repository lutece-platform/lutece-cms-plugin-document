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


import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import fr.paris.lutece.test.LuteceTestCase;

import fr.paris.lutece.test.mocks.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * DocumentJspBean Test
 */
public class DocumentJspBeanTest extends LuteceTestCase
{
	private static final String PARAMETER_DOCUMENT_TYPE = "document_type_code";
    private static final String ATTRIBUTE_ADMIN_USER = "lutece_admin_user";

    @Inject
    private DocumentJspBean _instance;

	/**
	 * Test of getManageDocuments method, of class
	 * fr.paris.lutece.plugins.document.web.DocumentJspBean.
	 */
	@Test
	public void testGetManageDocuments( ) throws AccessDeniedException
	{
		System.out.println( "getManageDocuments" );

		MockHttpServletRequest request = new MockHttpServletRequest( );

		AdminUser user = AdminUserHome.findUserByLogin( "admin" );
		user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId( ) ) );
        Map<String, Right> mapRights = new HashMap<>( );
        Right right = new Right( );
        right.setId( DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT );
        mapRights.put( DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT, right );
        user.setRights( mapRights );
        user.setLocale( new Locale("fr", "FR", "") );
        request.getSession( true ).setAttribute( ATTRIBUTE_ADMIN_USER, user );

        _instance.init( request, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT );

		String result = _instance.getManageDocuments( request );
	}

	/**
	 * Test of getCreateDocument method, of class
	 * fr.paris.lutece.plugins.document.web.DocumentJspBean.
	 */
	public void testGetCreateDocument( ) throws AccessDeniedException
	{
		System.out.println( "getCreateDocument" );
		/*
		MockHttpServletRequest request = new MockHttpServletRequest( );
		AdminUser user = AdminUserHome.findUserByLogin( "admin" );
		user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId( ) ) );
		request.registerAdminUserWithRigth( user, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT );
		request.setParameter( PARAMETER_DOCUMENT_TYPE, "article" );

		DocumentJspBean instance = new DocumentJspBean( );

		instance.init( request, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT );
		instance.getCreateDocument( request );
		*/
	}
}
