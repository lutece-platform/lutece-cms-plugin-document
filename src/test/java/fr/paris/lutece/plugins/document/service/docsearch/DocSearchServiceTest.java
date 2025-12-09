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
package fr.paris.lutece.plugins.document.service.docsearch;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import org.junit.jupiter.api.Test;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.List;


/**
 *  DocSearchService Test
 */
public class DocSearchServiceTest extends LuteceTestCase
{
    /*
	public DocSearchServiceTest( String testName )
    {
        super( testName );
    }
    */

    /**
     * Test of processIndexing method, of class fr.paris.lutece.plugins.document.service.docsearch.DocSearchService.
     */
	@Test
    public void testProcessIndexing(  )
    {
        System.out.println( "processIndexing" );

        boolean bCreate = true;
        String result = DocSearchService.getInstance(  ).processIndexing( bCreate );
    }

    /**
     * Test of getSearchResults method, of class fr.paris.lutece.plugins.document.service.docsearch.DocSearchService.
     */
    @Test
    public void testGetSearchResults(  )
    {
        System.out.println( "getSearchResults" );

        int nStartIndex = 0;
        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );

        String strQuery = "lutece";
        List<DocSearchItem> listResults = DocSearchService.getInstance(  ).getSearchResults( strQuery, nStartIndex, user );

        for ( DocSearchItem result : listResults )
        {
            System.out.println( result.getTitle(  ) );
        }

        strQuery = "lutece +space:s14";
        listResults = DocSearchService.getInstance(  ).getSearchResults( strQuery, nStartIndex, user );

        for ( DocSearchItem result : listResults )
        {
            System.out.println( result.getTitle(  ) );
        }
    }
}
