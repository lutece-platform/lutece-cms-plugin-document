/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.document.business.spaces;

import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.plugins.document.business.spaces.SpaceActionHome;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.Locale;


public class DocumentSpaceTest extends LuteceTestCase
{
    private static final int ID_PARENT = 1;
    private static final String NAME1 = "Name 1";
    private static final String NAME2 = "Name 2";
    private static final String DESCRIPTION1 = "Description 1";
    private static final String DESCRIPTION2 = "Description 2";
    private static final int ICON_ID1 = 1;
    private static final int ICON_ID2 = 2;
    private static final String VIEWTYPE1 = "ViewType 1";
    private static final String VIEWTYPE2 = "ViewType 2";
    private static final String DOCTYPE1 = "DocType 1";
    private static final String DOCTYPE2 = "DocType 2";

    public void testDocumentSpaceBusiness(  )
    {
        // Initialize an object
        DocumentSpace space = new DocumentSpace(  );
        space.setIdParent( ID_PARENT );
        space.setName( NAME1 );
        space.setDescription( DESCRIPTION1 );
        space.setIdIcon( ICON_ID1 );
        space.setViewType( VIEWTYPE1 );
        space.addAllowedDocumentType( DOCTYPE1 );
        space.addAllowedDocumentType( DOCTYPE2 );
        space.setDocumentCreationAllowed( true );

        // Create test
        DocumentSpaceHome.create( space );

        DocumentSpace spaceStored = DocumentSpaceHome.findByPrimaryKey( space.getId(  ) );
        assertEquals( spaceStored.getIdParent(  ), space.getIdParent(  ) );
        assertEquals( spaceStored.getName(  ), space.getName(  ) );
        assertEquals( spaceStored.getDescription(  ), space.getDescription(  ) );
        assertEquals( spaceStored.getIdIcon(  ), space.getIdIcon(  ) );
        assertEquals( spaceStored.getViewType(  ), space.getViewType(  ) );

        // Update test
        space.setName( NAME2 );
        space.setDescription( DESCRIPTION2 );
        space.setIdIcon( ICON_ID2 );
        space.setViewType( VIEWTYPE2 );

        DocumentSpaceHome.update( space );
        spaceStored = DocumentSpaceHome.findByPrimaryKey( space.getId(  ) );
        assertEquals( spaceStored.getIdParent(  ), space.getIdParent(  ) );
        assertEquals( spaceStored.getName(  ), space.getName(  ) );
        assertEquals( spaceStored.getDescription(  ), space.getDescription(  ) );
        assertEquals( spaceStored.getIdIcon(  ), space.getIdIcon(  ) );
        assertEquals( spaceStored.getViewType(  ), space.getViewType(  ) );

        // List Test
        DocumentSpaceHome.findAll(  );
        DocumentSpaceHome.findChilds( ID_PARENT );
        DocumentSpaceHome.getAllowedDocumentTypes( ID_PARENT );
        DocumentSpaceHome.getDocumentSpaceList(  );
        DocumentSpaceHome.getIconsList(  );
        DocumentSpaceHome.getViewTypeList( Locale.getDefault(  ) );

        // Delete test
        DocumentSpaceHome.remove( space.getId(  ) );
        spaceStored = DocumentSpaceHome.findByPrimaryKey( space.getId(  ) );
        assertNull( spaceStored );
    }

    public void testSpaceActionBusiness(  )
    {
        SpaceActionHome.getActionsList( Locale.getDefault(  ) );
    }
}
