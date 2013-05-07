/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.document.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;


public class DocumentTypeTest extends LuteceTestCase
{
    private final static String CODEDOCUMENTTYPE1 = "Code1";
    private final static String CODEDOCUMENTTYPE2 = "Code2";
    private final static String NAME1 = "Name1";
    private final static String NAME2 = "Name2";
    private final static String DESCRIPTION1 = "Description1";
    private final static String DESCRIPTION2 = "Description2";
    private final static int THUMBNAILATTRIBUTEID1 = 1;
    private final static int THUMBNAILATTRIBUTEID2 = 2;
    private final static String DEFAULTTHUMBNAILURL1 = "DefaultThumbnailUrl1";
    private final static String DEFAULTTHUMBNAILURL2 = "DefaultThumbnailUrl2";
    private final static String ADMINXSL1 = "AdminXsl1";
    private final static String ADMINXSL2 = "AdminXsl2";
    private final static String CONTENTSERVICEXSL1 = "ContentServiceXsl1";
    private final static String CONTENTSERVICEXSL2 = "ContentServiceXsl2";
    private final static String METADATAHANDLER1 = "MetadataHandler1";
    private final static String METADATAHANDLER2 = "MetadataHandler2";

    public void testBusiness(  )
    {
        Plugin plugin = PluginService.getPlugin( "documentType" );

        // Initialize an documentType
        DocumentType documentType = new DocumentType(  );
        documentType.setCode( CODEDOCUMENTTYPE1 );
        documentType.setName( NAME1 );
        documentType.setDescription( DESCRIPTION1 );
        documentType.setThumbnailAttributeId( THUMBNAILATTRIBUTEID1 );
        documentType.setDefaultThumbnailUrl( DEFAULTTHUMBNAILURL1 );
        documentType.setAdminXsl( ADMINXSL1.getBytes(  ) );
        documentType.setContentServiceXsl( CONTENTSERVICEXSL1.getBytes(  ) );
        documentType.setMetadataHandler( METADATAHANDLER1 );

        // Create test
        DocumentTypeHome.create( documentType );

        DocumentType documentTypeStored = DocumentTypeHome.findByPrimaryKey( documentType.getCode(  ) );
        assertEquals( documentTypeStored.getCode(  ), documentType.getCode(  ) );
        assertEquals( documentTypeStored.getName(  ), documentType.getName(  ) );
        assertEquals( documentTypeStored.getDescription(  ), documentType.getDescription(  ) );
        assertEquals( documentTypeStored.getThumbnailAttributeId(  ), documentType.getThumbnailAttributeId(  ) );
        assertEquals( documentTypeStored.getDefaultThumbnailUrl(  ), documentType.getDefaultThumbnailUrl(  ) );
        //        assertEquals( documentTypeStored.getAdminXsl() , documentType.getAdminXsl() );
        //        assertEquals( documentTypeStored.getContentServiceXsl() , documentType.getContentServiceXsl() );
        assertEquals( documentTypeStored.getMetadataHandler(  ), documentType.getMetadataHandler(  ) );

        // Update test
        documentType.setName( NAME2 );
        documentType.setDescription( DESCRIPTION2 );
        documentType.setThumbnailAttributeId( THUMBNAILATTRIBUTEID2 );
        documentType.setDefaultThumbnailUrl( DEFAULTTHUMBNAILURL2 );
        documentType.setAdminXsl( ADMINXSL2.getBytes(  ) );
        documentType.setContentServiceXsl( CONTENTSERVICEXSL2.getBytes(  ) );
        documentType.setMetadataHandler( METADATAHANDLER2 );
        DocumentTypeHome.update( documentType );
        documentTypeStored = DocumentTypeHome.findByPrimaryKey( documentType.getCode(  ) );
        assertEquals( documentTypeStored.getName(  ), documentType.getName(  ) );
        assertEquals( documentTypeStored.getDescription(  ), documentType.getDescription(  ) );
        assertEquals( documentTypeStored.getThumbnailAttributeId(  ), documentType.getThumbnailAttributeId(  ) );
        assertEquals( documentTypeStored.getDefaultThumbnailUrl(  ), documentType.getDefaultThumbnailUrl(  ) );
        //        assertEquals( documentTypeStored.getAdminXsl() , documentType.getAdminXsl() );
        //        assertEquals( documentTypeStored.getContentServiceXsl() , documentType.getContentServiceXsl() );
        assertEquals( documentTypeStored.getMetadataHandler(  ), documentType.getMetadataHandler(  ) );

        // List test
        DocumentTypeHome.getDocumentTypesList(  );

        // Delete test
        DocumentTypeHome.remove( documentType.getCode(  ) );
    }
}
