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
package fr.paris.lutece.plugins.document.service.attributes;

import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttributeHome;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.Collection;


/**
 * Document Attributes management Service.
 */
public class DocumentAttributesService
{
    private static final String TAG_ATTRIBUTES = "attributes";
    private static final String TAG_ATTRIBUTE = "attribute";
    private static final String TAG_ATTRIBUTE_ID = "id_document_attr";
    private static final String TAG_CODE_ATTRIBUTE_TYPE = "code_attr_type";
    private static final String TAG_CODE_ATTRIBUTE = "code";
    private static final String TAG_DOCUMENT_TYPE_ATTRIBUTE_NAME = "document_type_attr_name";
    private static final String TAG_ATTRIBUTE_DESCRIPTION = "description";
    private static DocumentAttributesService _singleton = new DocumentAttributesService(  );

    /** Creates a new instance of DocumentSpacesService */
    private DocumentAttributesService(  )
    {
    }

    /**
     * Returns the unique instance of the service
     * @return the unique instance of the service
     */
    public static DocumentAttributesService getInstance(  )
    {
        return _singleton;
    }

    /**
     * Gets attributes list of a type of document as an XML document
     * @param strCodeType The code for the document type
     * @return An XML document containing attributes list
     */
    public String getXmlAttributesList( String strCodeType )
    {
        StringBuffer sbXML = new StringBuffer(  );
        XmlUtil.beginElement( sbXML, TAG_ATTRIBUTES );

        for ( DocumentAttribute attribute : getAttributesOfDocumentType( strCodeType ) )
        {
            findDocumentAttributeByCodeType( sbXML, attribute.getId(  ) );
        }

        XmlUtil.endElement( sbXML, TAG_ATTRIBUTES );

        return sbXML.toString(  );
    }

    /**
     * Get a attributes list of a selected code document type
     * @param codeDocumentType The code document type
     * @return The attributes list
     */
    private Collection<DocumentAttribute> getAttributesOfDocumentType( String codeDocumentType )
    {
        Collection<DocumentAttribute> listAttributes = DocumentAttributeHome.selectAllAttributesOfDocumentType( codeDocumentType );

        return listAttributes;
    }

    /**
    * Build recursively the XML document containing the arborescence of spaces
    *
    * @param sbXML The buffer in which adding the current space of the arborescence
    * @param nAttributeId The attribute ID
    */
    private void findDocumentAttributeByCodeType( StringBuffer sbXML, int nAttributeId )
    {
        DocumentAttribute attribute = DocumentAttributeHome.findByPrimaryKey( nAttributeId );

        XmlUtil.beginElement( sbXML, TAG_ATTRIBUTE );
        XmlUtil.addElement( sbXML, TAG_ATTRIBUTE_ID, attribute.getId(  ) );
        XmlUtil.addElement( sbXML, TAG_CODE_ATTRIBUTE_TYPE, attribute.getCodeAttributeType(  ) );
        XmlUtil.addElement( sbXML, TAG_CODE_ATTRIBUTE, attribute.getCode(  ) );
        XmlUtil.addElement( sbXML, TAG_DOCUMENT_TYPE_ATTRIBUTE_NAME, attribute.getName(  ) );
        XmlUtil.addElement( sbXML, TAG_ATTRIBUTE_DESCRIPTION, attribute.getDescription(  ) );
        XmlUtil.endElement( sbXML, TAG_ATTRIBUTE );
    }
}
