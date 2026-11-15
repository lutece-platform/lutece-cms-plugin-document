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
package fr.paris.lutece.plugins.document.service.spaces;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * DocumentSpacesService tests: a space id that no longer leads to a space (space removed while the tree is built)
 */
public class DocumentSpacesServiceTest extends LuteceTestCase
{
    private static final int MISSING_SPACE_ID = 987654;

    /**
     * The spaces tree skips a space removed between the read of its id and its load
     */
    @Test
    public void testFindSpacesSkipsRemovedSpace( ) throws Exception
    {
        DocumentSpacesService service = new DocumentSpacesService( );
        StringBuffer sbXML = new StringBuffer( );

        invoke( service, "findSpaces", new Class<?>[] { StringBuffer.class, int.class, AdminUser.class },
                sbXML, MISSING_SPACE_ID, new AdminUser( ) );

        assertEquals( "", sbXML.toString( ) );
    }

    /**
     * The spaces tree filtered by document type skips a removed space
     */
    @Test
    public void testFindSpacesByCodeTypeSkipsRemovedSpace( ) throws Exception
    {
        DocumentSpacesService service = new DocumentSpacesService( );
        StringBuffer sbXML = new StringBuffer( );

        Object bValid = invoke( service, "findSpacesByCodeType",
                new Class<?>[] { StringBuffer.class, int.class, AdminUser.class, String.class },
                sbXML, MISSING_SPACE_ID, new AdminUser( ), "article" );

        assertEquals( Boolean.FALSE, bValid );
        assertEquals( "", sbXML.toString( ) );
    }

    /**
     * The label path of a space whose parent was removed is the space name
     */
    @Test
    public void testGetLabelSpacePathWithRemovedParent( )
    {
        DocumentSpace space = new DocumentSpace( );
        space.setIdParent( MISSING_SPACE_ID );
        space.setName( "Orphan space" );
        space.setDescription( "Orphan space" );
        space.setViewType( "detail" );
        space.setIdIcon( 1 );
        DocumentSpaceHome.create( space );

        try
        {
            assertEquals( "Orphan space", new DocumentSpacesService( ).getLabelSpacePath( space.getId( ), new AdminUser( ) ) );
        }
        finally
        {
            DocumentSpaceHome.remove( space.getId( ) );
        }
    }

    /**
     * The spaces browser shows the default space when the space id of the request leads to no space
     */
    @Test
    public void testGetSpacesBrowserWithMissingSpaceId( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( DocumentSpacesService.PARAMETER_BROWSER_SPACE_ID, String.valueOf( MISSING_SPACE_ID ) );

        String strHtml = new DocumentSpacesService( ).getSpacesBrowser( request, new AdminUser( ), Locale.FRENCH, false, false );

        assertNotNull( strHtml );
        assertFalse( strHtml.contains( String.valueOf( MISSING_SPACE_ID ) ) );
    }

    private static Object invoke( Object target, String strMethod, Class<?> [ ] types, Object... args ) throws Exception
    {
        Method method = DocumentSpacesService.class.getDeclaredMethod( strMethod, types );
        method.setAccessible( true );

        try
        {
            return method.invoke( target, args );
        }
        catch( InvocationTargetException e )
        {
            if ( e.getCause( ) instanceof Exception )
            {
                throw (Exception) e.getCause( );
            }
            throw e;
        }
    }
}
