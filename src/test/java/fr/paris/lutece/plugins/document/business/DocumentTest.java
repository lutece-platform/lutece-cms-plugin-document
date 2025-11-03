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
package fr.paris.lutece.plugins.document.business;

import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import org.junit.jupiter.api.Test;
import fr.paris.lutece.test.LuteceTestCase;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;


public class DocumentTest extends LuteceTestCase
{
    private final static int Id1 = 1;
    private final static int Id2 = 2;
    private final static String CODEDOCUMENTTYPE1 = "article";
    private final static Timestamp DATECREATION1 = new Timestamp( new Date(  ).getTime(  ) );
    private final static Timestamp DATECREATION2 = new Timestamp( new Date(  ).getTime(  ) );
    private final static Timestamp DATEMODIFICATION1 = new Timestamp( new Date(  ).getTime(  ) );
    private final static Timestamp DATEMODIFICATION2 = new Timestamp( new Date(  ).getTime(  ) );
    private final static String TITLE1 = "Title1";
    private final static String TITLE2 = "Title2";
    private final static int SpaceId1 = 1;
    private final static int SpaceId2 = 2;
    private final static int StateId1 = 1;
    private final static int StateId2 = 2;
    private final static String XMLWORKINGCONTENT1 = "XmlWorkingContent1";
    private final static String XMLWORKINGCONTENT2 = "XmlWorkingContent2";
    private final static String XMLVALIDATEDCONTENT1 = "XmlValidatedContent1";
    private final static String XMLVALIDATEDCONTENT2 = "XmlValidatedContent2";
    private final static String SUMMARY1 = "Summary1";
    private final static String SUMMARY2 = "Summary2";
    private final static String COMMENT1 = "Comment1";
    private final static String COMMENT2 = "Comment2";
    private final static Timestamp DATEVALIDITYBEGIN1 = new Timestamp( new Date(  ).getTime(  ) );
    private final static Timestamp DATEVALIDITYBEGIN2 = new Timestamp( new Date(  ).getTime(  ) );
    private final static Timestamp DATEVALIDITYEND1 = new Timestamp( new Date(  ).getTime(  ) );
    private final static Timestamp DATEVALIDITYEND2 = new Timestamp( new Date(  ).getTime(  ) );
    private final static boolean SKIPPORTLET1 = true;
    private final static boolean SKIPPORTLET2 = false;
    private final static boolean SKIPCATEGORIES1 = false;
    private final static boolean SKIPCATEGORIES2 = true;
    private final static String XMLMETADATA1 = "XmlMetadata1";
    private final static String XMLMETADATA2 = "XmlMetadata2";
    private final static int CreatorId1 = 1;
    private final static int CreatorId2 = 2;

    @Test
    public void testBusiness(  )
    {
        // Initialize an object
        Document document = new Document(  );
        document.setId( DocumentHome.newPrimaryKey(  ) );
        document.setCodeDocumentType( CODEDOCUMENTTYPE1 );
        document.setDateCreation( DATECREATION1 );
        document.setDateModification( DATEMODIFICATION1 );
        document.setTitle( TITLE1 );
        document.setSpaceId( SpaceId1 );
        document.setStateId( StateId1 );
        document.setXmlWorkingContent( XMLWORKINGCONTENT1 );
        document.setXmlValidatedContent( XMLVALIDATEDCONTENT1 );
        document.setSummary( SUMMARY1 );
        document.setComment( COMMENT1 );
        document.setDateValidityBegin( DATEVALIDITYBEGIN1 );
        document.setDateValidityEnd( DATEVALIDITYEND1 );
        document.setSkipPortlet( SKIPPORTLET1 );
        document.setSkipCategories( SKIPCATEGORIES1 );
        document.setXmlMetadata( XMLMETADATA1 );
        document.setCreatorId( CreatorId1 );

        ArrayList<DocumentAttribute> listAttributes = new ArrayList<DocumentAttribute>(  );
        document.setAttributes( listAttributes );

        // Create test
        DocumentHome.create( document );

        Document documentStored = DocumentHome.findByPrimaryKey( document.getId(  ) );
        assertEquals( documentStored.getId(  ), document.getId(  ) );
        assertTrue( ( documentStored.getDateCreation(  ).getTime(  ) - document.getDateCreation(  ).getTime(  ) ) < 10 );
        assertTrue( ( documentStored.getDateModification(  ).getTime(  ) -
            document.getDateModification(  ).getTime(  ) ) < 10 );
        assertEquals( documentStored.getTitle(  ), document.getTitle(  ) );
        assertEquals( documentStored.getSpaceId(  ), document.getSpaceId(  ) );
        assertEquals( documentStored.getStateId(  ), document.getStateId(  ) );
        assertEquals( documentStored.getXmlWorkingContent(  ), document.getXmlWorkingContent(  ) );
        assertEquals( documentStored.getXmlValidatedContent(  ), document.getXmlValidatedContent(  ) );
        assertEquals( documentStored.getSummary(  ), document.getSummary(  ) );
        assertEquals( documentStored.getComment(  ), document.getComment(  ) );
        assertTrue( ( documentStored.getDateValidityBegin(  ).getTime(  ) -
            document.getDateValidityBegin(  ).getTime(  ) ) < 10 );
        assertTrue( ( documentStored.getDateValidityEnd(  ).getTime(  ) - document.getDateValidityEnd(  ).getTime(  ) ) < 10 );
        assertEquals( documentStored.isSkipPortlet(  ), document.isSkipPortlet(  ) );
        assertEquals( documentStored.isSkipCategories(  ), document.isSkipCategories(  ) );
        assertEquals( documentStored.getXmlMetadata(  ), document.getXmlMetadata(  ) );
        assertEquals( documentStored.getCreatorId(  ), document.getCreatorId(  ) );

        // Update test
        document.setDateCreation( DATECREATION2 );
        document.setDateModification( DATEMODIFICATION2 );
        document.setTitle( TITLE2 );
        document.setSpaceId( SpaceId2 );
        document.setStateId( StateId2 );
        document.setXmlWorkingContent( XMLWORKINGCONTENT2 );
        document.setXmlValidatedContent( XMLVALIDATEDCONTENT2 );
        document.setSummary( SUMMARY2 );
        document.setComment( COMMENT2 );
        document.setDateValidityBegin( DATEVALIDITYBEGIN2 );
        document.setDateValidityEnd( DATEVALIDITYEND2 );
        document.setSkipPortlet( SKIPPORTLET2 );
        document.setSkipCategories( SKIPCATEGORIES2 );
        document.setXmlMetadata( XMLMETADATA2 );
        document.setCreatorId( CreatorId2 );
        DocumentHome.update( document, false );
        documentStored = DocumentHome.findByPrimaryKey( document.getId(  ) );
        assertEquals( documentStored.getId(  ), document.getId(  ) );
        assertEquals( documentStored.getCodeDocumentType(  ), document.getCodeDocumentType(  ) );
        assertTrue( ( documentStored.getDateCreation(  ).getTime(  ) - document.getDateCreation(  ).getTime(  ) ) < 10 );
        assertTrue( ( documentStored.getDateModification(  ).getTime(  ) -
            document.getDateModification(  ).getTime(  ) ) < 10 );
        assertEquals( documentStored.getTitle(  ), document.getTitle(  ) );
        assertEquals( documentStored.getSpaceId(  ), document.getSpaceId(  ) );
        assertEquals( documentStored.getStateId(  ), document.getStateId(  ) );
        assertEquals( documentStored.getXmlWorkingContent(  ), document.getXmlWorkingContent(  ) );
        assertEquals( documentStored.getXmlValidatedContent(  ), document.getXmlValidatedContent(  ) );
        assertEquals( documentStored.getSummary(  ), document.getSummary(  ) );
        assertEquals( documentStored.getComment(  ), document.getComment(  ) );
        assertTrue( ( documentStored.getDateValidityBegin(  ).getTime(  ) -
            document.getDateValidityBegin(  ).getTime(  ) ) < 10 );
        assertTrue( ( documentStored.getDateValidityEnd(  ).getTime(  ) - document.getDateValidityEnd(  ).getTime(  ) ) < 10 );
        assertEquals( documentStored.isSkipPortlet(  ), document.isSkipPortlet(  ) );
        assertEquals( documentStored.isSkipCategories(  ), document.isSkipCategories(  ) );
        assertEquals( documentStored.getXmlMetadata(  ), document.getXmlMetadata(  ) );
        assertEquals( documentStored.getCreatorId(  ), document.getCreatorId(  ) );

        // List test
        DocumentHome.findAllPrimaryKeys(  );

        // Delete test
        DocumentHome.remove( document.getId(  ) );
        documentStored = DocumentHome.findByPrimaryKey( document.getId(  ) );
        assertNull( documentStored );
    }
}
