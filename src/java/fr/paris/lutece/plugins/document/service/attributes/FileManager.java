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
package fr.paris.lutece.plugins.document.service.attributes;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringUtils;


/**
 * File attribute implementation
 */
public class FileManager extends DefaultManager
{
    private static final String TAG_FILE_RESOURCE = "file-resource";
    private static final String TAG_DOCUMENT_ID = "resource-document-id";
    private static final String TAG_ATTRIBUTE_ID = "resource-attribute-id";
    private static final String TAG_CONTENT_TYPE = "resource-content-type";
    private static final String TAG_FILE_SIZE = "file-size";
    private static final String TEMPLATE_CREATE_ATTRIBUTE = "admin/plugins/document/attributes/create_file.html";
    private static final String TEMPLATE_MODIFY_ATTRIBUTE = "admin/plugins/document/attributes/modify_file.html";
    private static final String TEMPLATE_CREATE_PARAMETERS_ATTRIBUTE = "admin/plugins/document/attributes/create_parameters_file.html";
    private static final String TEMPLATE_MODIFY_PARAMETERS_ATTRIBUTE = "admin/plugins/document/attributes/modify_parameters_file.html";

    /**
     * Gets the template to enter the attribute value
     * @return The template to enter the attribute value
     */
    protected String getCreateTemplate(  )
    {
        return TEMPLATE_CREATE_ATTRIBUTE;
    }

    /**
     * Gets the template to modify the attribute value
     * @return The template to modify the attribute value
     */
    protected String getModifyTemplate(  )
    {
        return TEMPLATE_MODIFY_ATTRIBUTE;
    }

    /**
     * Gets the template to enter the parameters of the attribute value
     * @return The template to enter the parameters of the attribute value
     */
    protected String getCreateParametersTemplate(  )
    {
        return TEMPLATE_CREATE_PARAMETERS_ATTRIBUTE;
    }

    /**
     * Gets the template to modify the parameters of the attribute value
     * @return The template to modify the parameters of the attribute value
     */
    protected String getModifyParametersTemplate(  )
    {
        return TEMPLATE_MODIFY_PARAMETERS_ATTRIBUTE;
    }

    /**
     * Get the XML data corresponding to the attribute to build the document XML
     * content
     * @param document The document
     * @param attribute The attribute
     * @return The XML value of the attribute
     */
    public String getAttributeXmlValue( Document document, DocumentAttribute attribute )
    {
        StringBuffer sbXml = new StringBuffer(  );

        if ( ( attribute.getBinaryValue(  ) != null ) && ( attribute.getBinaryValue(  ).length != 0 ) )
        {
            XmlUtil.beginElement( sbXml, TAG_FILE_RESOURCE );
            XmlUtil.addElement( sbXml, TAG_DOCUMENT_ID, document.getId(  ) );
            XmlUtil.addElement( sbXml, TAG_ATTRIBUTE_ID, attribute.getId(  ) );
            XmlUtil.addElement( sbXml, TAG_CONTENT_TYPE, attribute.getValueContentType(  ) );
            XmlUtil.endElement( sbXml, TAG_FILE_RESOURCE );

            XmlUtil.addElement( sbXml, TAG_FILE_SIZE, attribute.getBinaryValue(  ).length );

            return sbXml.toString(  );
        }

        return StringUtils.EMPTY;
    }
}
