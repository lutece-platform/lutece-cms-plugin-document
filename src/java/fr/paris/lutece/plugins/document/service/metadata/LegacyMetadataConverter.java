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
package fr.paris.lutece.plugins.document.service.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Converts the legacy XML metadata of the documents (plugin-document before 7.0.0) into JSON metadata. The legacy format is a root element whose
 * child elements are the metadata properties: each child element becomes a property of the JSON object. This class is only used by the migration
 * of the existing data and will be removed with the legacy column.
 */
public final class LegacyMetadataConverter
{
    private static final String FEATURE_DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    private static final String FEATURE_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    private static final ObjectMapper MAPPER = new ObjectMapper( );

    /**
     * Private constructor
     */
    private LegacyMetadataConverter( )
    {
    }

    /**
     * Converts legacy XML metadata into a JSON object
     *
     * @param strXmlMetadata
     *            The legacy XML metadata
     * @return The JSON object
     * @throws LegacyMetadataException
     *             if the legacy metadata can not be read
     */
    public static String toJson( String strXmlMetadata ) throws LegacyMetadataException
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
            factory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
            factory.setFeature( FEATURE_DISALLOW_DOCTYPE, true );
            factory.setFeature( FEATURE_EXTERNAL_GENERAL_ENTITIES, false );
            factory.setFeature( FEATURE_EXTERNAL_PARAMETER_ENTITIES, false );
            factory.setXIncludeAware( false );
            factory.setExpandEntityReferences( false );

            org.w3c.dom.Document dom = factory.newDocumentBuilder( ).parse( new InputSource( new StringReader( strXmlMetadata ) ) );
            Map<String, String> mapMetadata = new LinkedHashMap<>( );
            NodeList listNodes = dom.getDocumentElement( ).getChildNodes( );

            for ( int i = 0; i < listNodes.getLength( ); i++ )
            {
                Node node = listNodes.item( i );

                if ( node.getNodeType( ) == Node.ELEMENT_NODE )
                {
                    mapMetadata.put( ( (Element) node ).getTagName( ), node.getTextContent( ) );
                }
            }

            return MAPPER.writeValueAsString( mapMetadata );
        }
        catch( ParserConfigurationException | SAXException | IOException e )
        {
            throw new LegacyMetadataException( "Unable to read the legacy XML metadata : " + e.getMessage( ), e );
        }
    }

    /**
     * Exception raised when legacy metadata can not be converted
     */
    public static class LegacyMetadataException extends Exception
    {
        private static final long serialVersionUID = 1L;

        /**
         * Constructor
         *
         * @param strMessage
         *            The message
         * @param cause
         *            The cause
         */
        public LegacyMetadataException( String strMessage, Throwable cause )
        {
            super( strMessage, cause );
        }
    }
}
