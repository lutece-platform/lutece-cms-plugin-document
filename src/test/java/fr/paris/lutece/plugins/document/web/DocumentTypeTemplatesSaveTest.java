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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Checks that the templates of a document type are saved as typed
 */
public class DocumentTypeTemplatesSaveTest extends LuteceTestCase
{
    private static final String ATTRIBUTE_ADMIN_USER = "lutece_admin_user";
    private static final String TYPE_ARTICLE = "article";
    private static final String ADMIN_TEMPLATE = "<div class=\"preview\"><#if document.summary?has_content><@documentValue value=document.summary /></#if> & co</div>";
    private static final String CONTENT_TEMPLATE = "<h2><@documentValue value=document.title /></h2>\n<#list document.categories as category><span>${category}</span></#list>";
    private static final Pattern PATTERN_TOKEN = Pattern.compile( "name=\"token\"[^>]*value=\"([^\"]+)\"|value=\"([^\"]+)\"[^>]*name=\"token\"" );

    @Inject
    private DocumentTypeJspBean _documentTypeJspBean;

    private String _strAdminTemplate;
    private String _strContentTemplate;

    /**
     * Saves the templates of the article type
     */
    @BeforeEach
    public void saveTemplates( )
    {
        DocumentType type = DocumentTypeHome.findByPrimaryKey( TYPE_ARTICLE );
        _strAdminTemplate = type.getAdminTemplate( );
        _strContentTemplate = type.getContentTemplate( );
    }

    /**
     * Restores the templates of the article type
     */
    @AfterEach
    public void restoreTemplates( )
    {
        DocumentTypeHome.storeTemplates( TYPE_ARTICLE, _strAdminTemplate, _strContentTemplate );
    }

    /**
     * The templates sent by the form are stored unchanged
     *
     * @throws Exception
     *             if the page can not be displayed
     */
    @Test
    public void testTemplatesAreNotSanitized( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.getSession( true ).setAttribute( ATTRIBUTE_ADMIN_USER, buildAdminUser( ) );
        request.addParameter( "document_type_code", TYPE_ARTICLE );
        _documentTypeJspBean.init( request, DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT );
        String strToken = findToken( _documentTypeJspBean.getModifyDocumentType( request ) );

        request.addParameter( "token", strToken );
        request.addParameter( "admin_template", ADMIN_TEMPLATE );
        request.addParameter( "content_template", CONTENT_TEMPLATE );
        String strUrl = _documentTypeJspBean.doModifyTemplates( request );

        assertEquals( "ModifyDocumentType.jsp?document_type_code=" + TYPE_ARTICLE, strUrl );
        DocumentType type = DocumentTypeHome.findByPrimaryKey( TYPE_ARTICLE );
        assertEquals( ADMIN_TEMPLATE, type.getAdminTemplate( ) );
        assertEquals( CONTENT_TEMPLATE, type.getContentTemplate( ) );
    }

    /**
     * Returns the administrator with the right to manage the document types
     *
     * @return The administrator
     */
    private AdminUser buildAdminUser( )
    {
        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId( ) ) );

        Map<String, Right> mapRights = new HashMap<>( );
        Right right = new Right( );
        right.setId( DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT );
        mapRights.put( DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT, right );
        user.setRights( mapRights );
        user.setLocale( Locale.getDefault( ) );

        return user;
    }

    /**
     * Returns the security token of the first form of a page
     *
     * @param strHtml
     *            The page
     * @return The token
     */
    private String findToken( String strHtml )
    {
        Matcher matcher = PATTERN_TOKEN.matcher( strHtml );
        assertTrue( matcher.find( ), "the page should contain a security token" );

        return ( matcher.group( 1 ) != null ) ? matcher.group( 1 ) : matcher.group( 2 );
    }
}
