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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.web.category.CategoryJspBean;
import fr.paris.lutece.plugins.document.web.publishing.DocumentPublishingJspBean;
import fr.paris.lutece.plugins.document.web.rules.DocumentRulesJspBean;
import fr.paris.lutece.plugins.document.web.spaces.DocumentSpaceJspBean;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Checks the CSRF protection of the back office actions: every action refuses a request without a valid security token
 */
public class AdminActionsSecurityTokenTest extends LuteceTestCase
{
    private static final String ATTRIBUTE_ADMIN_USER = "lutece_admin_user";
    private static final String TEST_TYPE_CODE = "csrftest";
    private static final Pattern PATTERN_TOKEN = Pattern.compile( "name=\"token\"[^>]*value=\"([^\"]+)\"|value=\"([^\"]+)\"[^>]*name=\"token\"" );

    @Inject
    private DocumentJspBean _documentJspBean;

    @Inject
    private DocumentTypeJspBean _documentTypeJspBean;

    @Inject
    private DocumentPublishingJspBean _documentPublishingJspBean;

    @Inject
    private CategoryJspBean _categoryJspBean;

    @Inject
    private DocumentRulesJspBean _documentRulesJspBean;

    @Inject
    private DocumentSpaceJspBean _documentSpaceJspBean;

    /**
     * Removes the document type created by a test
     */
    @AfterEach
    public void removeDocumentType( )
    {
        if ( DocumentTypeHome.findByPrimaryKey( TEST_TYPE_CODE ) != null )
        {
            DocumentTypeHome.remove( TEST_TYPE_CODE );
        }
    }

    /**
     * Every action redirecting to a result page refuses a request without token, with the access denied message
     *
     * @throws Exception
     *             if an action can not be invoked
     */
    @Test
    public void testActionsRefuseMissingToken( ) throws Exception
    {
        Map<Object, List<String>> mapActions = new HashMap<>( );
        mapActions.put( _documentJspBean, List.of( "doCreateDocument", "doModifyDocument", "doDeleteDocument", "doChangeState", "doValidateDocument",
                "doRemoveSelection", "doArchiveSelection", "doActionSelectionDocument", "doArchiveDocument", "doMoveDocument" ) );
        mapActions.put( _documentTypeJspBean, List.of( "doCreateDocumentType", "doModifyDocumentType", "doAddAttribute", "doModifyAttribute",
                "doDeleteDocumentType", "doDeleteAttribute", "doAttributeMoveUp", "doAttributeMoveDown", "doModifyTemplates",
                "doDiscardLegacyStylesheets", "doInsertRegularExpression", "doDeleteRegularExpression" ) );
        mapActions.put( _documentPublishingJspBean, List.of( "doAssignedDocument", "doUnAssignedDocument", "doChangeModePublication",
                "doPublishingDocument", "doUnPublishingDocument", "doModifyDocumentOrder", "doCreateAutoPublication", "doDeleteAutoPublication" ) );
        mapActions.put( _categoryJspBean, List.of( "doCreateCategory", "doModifyCategory", "doRemoveCategory" ) );
        mapActions.put( _documentRulesJspBean, List.of( "doCreateRule", "doDeleteRule" ) );
        mapActions.put( _documentSpaceJspBean, List.of( "doCreateSpace", "doModifySpace", "doDeleteSpace", "doMoveSpace" ) );

        String strAccessDenied = I18nService.getLocalizedString( Messages.USER_ACCESS_DENIED, Locale.getDefault( ) );
        int nActions = 0;

        for ( Map.Entry<Object, List<String>> entry : mapActions.entrySet( ) )
        {
            for ( String strMethod : entry.getValue( ) )
            {
                MockHttpServletRequest request = buildRequest( );
                Method method = entry.getKey( ).getClass( ).getMethod( strMethod, HttpServletRequest.class );

                Object result = method.invoke( entry.getKey( ), request );

                String strAction = entry.getKey( ).getClass( ).getSimpleName( ) + "." + strMethod;
                assertNotNull( result, strAction );
                AdminMessage message = AdminMessageService.getMessage( request );
                assertNotNull( message, strAction + " should display a message" );
                assertEquals( strAccessDenied, message.getText( Locale.getDefault( ) ), strAction + " should refuse a request without token" );
                nActions++;
            }
        }

        assertEquals( 39, nActions );
    }

    /**
     * The mass archival, which displays a page instead of redirecting, archives nothing without token
     *
     * @throws Exception
     *             if the page can not be displayed
     */
    @Test
    public void testMassArchivalRefusesMissingToken( ) throws Exception
    {
        MockHttpServletRequest request = buildRequest( );
        request.getSession( ).setAttribute( "to_archive", List.of( ) );
        _documentJspBean.init( request, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT );

        String strHtml = _documentJspBean.doMassArchivalDocument( request );

        assertNotNull( strHtml );
        assertNotNull( request.getSession( ).getAttribute( "to_archive" ), "the documents to archive should not have been processed" );
        assertNull( AdminMessageService.getMessage( request ) );
    }

    /**
     * The token of a form lets its action through once: a second use of the same token is refused
     *
     * @throws Exception
     *             if the view can not be rendered
     */
    @Test
    public void testTokenOfTheFormIsAcceptedOnce( ) throws Exception
    {
        MockHttpServletRequest request = buildRequest( );
        _documentTypeJspBean.init( request, DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT );
        String strToken = findToken( _documentTypeJspBean.getCreateDocumentType( request ) );

        request.addParameter( "token", strToken );
        request.addParameter( "code", TEST_TYPE_CODE );
        request.addParameter( "name", "CSRF test" );
        request.addParameter( "description", "CSRF test" );

        String strUrl = _documentTypeJspBean.doCreateDocumentType( request );

        assertEquals( "ModifyDocumentType.jsp?document_type_code=" + TEST_TYPE_CODE, strUrl );
        assertNotNull( DocumentTypeHome.findByPrimaryKey( TEST_TYPE_CODE ) );
        assertNull( AdminMessageService.getMessage( request ) );

        _documentTypeJspBean.doCreateDocumentType( request );

        AdminMessage message = AdminMessageService.getMessage( request );
        assertNotNull( message, "a token must not be accepted twice" );
        assertEquals( I18nService.getLocalizedString( Messages.USER_ACCESS_DENIED, Locale.getDefault( ) ), message.getText( Locale.getDefault( ) ) );
    }

    /**
     * Builds a request of the administrator, in a new session
     *
     * @return The request
     */
    private MockHttpServletRequest buildRequest( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.getSession( true ).setAttribute( ATTRIBUTE_ADMIN_USER, buildAdminUser( ) );

        return request;
    }

    /**
     * Returns the administrator with the rights of the plugin
     *
     * @return The administrator
     */
    private AdminUser buildAdminUser( )
    {
        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId( ) ) );

        Map<String, Right> mapRights = new HashMap<>( );

        for ( String strRight : List.of( DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT, DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT ) )
        {
            Right right = new Right( );
            right.setId( strRight );
            mapRights.put( strRight, right );
        }

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
        assertTrue( matcher.find( ), "the form should contain a security token" );
        assertFalse( ( matcher.group( 1 ) == null ) && ( matcher.group( 2 ) == null ) );

        return ( matcher.group( 1 ) != null ) ? matcher.group( 1 ) : matcher.group( 2 );
    }
}
