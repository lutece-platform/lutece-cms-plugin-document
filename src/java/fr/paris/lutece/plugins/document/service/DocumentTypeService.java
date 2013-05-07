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
package fr.paris.lutece.plugins.document.service;

import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.util.xml.XmlUtil;
import java.util.Collection;


/**
 *
 *
 */
public class DocumentTypeService
{
    private static final String TAG_DOCUMENT_TYPES = "document_types";
    private static final String TAG_DOCUMENT_TYPE = "document_type";
    private static final String TAG_DOCUMENT_TYPE_CODE = "code_document_type";
    private static final String TAG_DOCUMENT_TYPE_NAME = "document_type_name";
    private static final String TAG_DOCUMENT_TYPE_DESCRIPTION = "description";
    private static final String TAG_DOCUMENT_TYPE_THUMBNAIL_ATTRIBUTE_ID = "thumbnail_attr_id";
    private static final String TAG_DOCUMENT_TYPE_DEFAULT_THUMBNAIL_URL = "default_thumbnail_url";
    private static final String TAG_DOCUMENT_TYPE_METADATA_HANDLER = "metadata_handler";

    //CONSTANTS
    private static DocumentTypeService _singleton = new DocumentTypeService(  );

    /** Creates a new instance of DocumentSpacesService */
    private DocumentTypeService(  )
    {
    }

    /**
     * Returns the unique instance of the service
     * @return the unique instance of the service
     */
    public static DocumentTypeService getInstance(  )
    {
        return _singleton;
    }

    /**
     * Gets document types list as an XML document
     * @return An XML document containing document types
     */
    public String getXmlDocumentTypesList(  )
    {
        StringBuffer sbXML = new StringBuffer(  );
        XmlUtil.beginElement( sbXML, TAG_DOCUMENT_TYPES );

        for ( DocumentType type : DocumentTypeHome.findAll(  ) )
        {
            findDocumentTypes( sbXML, type.getCode(  ) );
        }

        XmlUtil.endElement( sbXML, TAG_DOCUMENT_TYPES );

        return sbXML.toString(  );
    }

    /**
     * Build recursively the XML document containing the arborescence of document types
     *
     * @param sbXML The buffer in which adding the current space of the arborescence
     * @param strDocumentTypeCode The current type of the recursive course
     */
    private void findDocumentTypes( StringBuffer sbXML, String strDocumentTypeCode )
    {
        DocumentType type = DocumentTypeHome.findByPrimaryKey( strDocumentTypeCode );

        XmlUtil.beginElement( sbXML, TAG_DOCUMENT_TYPE );
        XmlUtil.addElement( sbXML, TAG_DOCUMENT_TYPE_CODE, type.getCode(  ) );
        XmlUtil.addElement( sbXML, TAG_DOCUMENT_TYPE_NAME, type.getName(  ) );
        XmlUtil.addElement( sbXML, TAG_DOCUMENT_TYPE_DESCRIPTION, type.getDescription(  ) );
        XmlUtil.addElement( sbXML, TAG_DOCUMENT_TYPE_THUMBNAIL_ATTRIBUTE_ID, type.getThumbnailAttributeId(  ) );
        XmlUtil.addElement( sbXML, TAG_DOCUMENT_TYPE_DEFAULT_THUMBNAIL_URL, type.getDefaultThumbnailUrl(  ) );
        XmlUtil.addElement( sbXML, TAG_DOCUMENT_TYPE_METADATA_HANDLER, type.getMetadataHandler(  ) );
        XmlUtil.endElement( sbXML, TAG_DOCUMENT_TYPE );
    }
}
