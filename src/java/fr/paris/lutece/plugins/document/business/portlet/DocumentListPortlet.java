/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.document.business.portlet;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.xml.XmlUtil;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents business objects ArticlesList Portlet
 */
public class DocumentListPortlet extends Portlet
{
    public static final String RESOURCE_ID = "DOCUMENT_LIST_PORTLET";

    /////////////////////////////////////////////////////////////////////////////////
    // Xml Tags
    private static final String TAG_DOCUMENT_LIST_PORTLET = "document-list-portlet";

    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    private String _strDocumentTypeCode;
    private int _nPortletId;
    private int[] _nArrayIdCategory;

    /**
     * Sets the identifier of the portlet type to the value specified in the
     * ArticlesListPortletHome class
     */
    public DocumentListPortlet(  )
    {
        setPortletTypeId( DocumentListPortletHome.getInstance(  ).getPortletTypeId(  ) );
    }

    /**
     * Returns the Xml code of the Document List portlet without XML heading
     *
     * @param request The HTTP Servlet request
     * @return the Xml code of the Document List portlet content
     */
    public String getXml( HttpServletRequest request )
    {
        StringBuffer strXml = new StringBuffer(  );
        XmlUtil.beginElement( strXml, TAG_DOCUMENT_LIST_PORTLET );

        for ( Document document : PublishingService.getInstance(  ).getPublishedDocumentsByPortletId( getId(  ) ) )
        {
            if ( document.isValid(  ) ) // to have only the valid documents                
            {
                strXml.append( document.getXml( request, getId(  ) ) );
            }
        }

        XmlUtil.endElement( strXml, TAG_DOCUMENT_LIST_PORTLET );

        String str = addPortletTags( strXml );

        return str;
    }

    /**
     * Returns the Xml code of the Articles List portlet with XML heading
     *
     * @param request The HTTP Servlet Request
     * @return the Xml code of the Articles List portlet
     */
    public String getXmlDocument( HttpServletRequest request )
    {
        return XmlUtil.getXmlHeader(  ) + getXml( request );
    }

    /**
     * Updates the current instance of the Articles List Portlet object
     */
    public void update(  )
    {
        DocumentListPortletHome.getInstance(  ).update( this );
    }

    /**
     * Removes the current instance of the Articles List Portlet object
     */
    public void remove(  )
    {
        DocumentListPortletHome.getInstance(  ).remove( this );
    }

    /**
     * Returns the nPortletId
     *
     * @return The nPortletId
     */
    public int getPortletId(  )
    {
        return _nPortletId;
    }

    /**
     * Sets the IdPortlet
     *
     * @param nPortletId The nPortletId
     */
    public void setPortletId( int nPortletId )
    {
        _nPortletId = nPortletId;
    }

    /**
     * Sets the parent page identifier of the portlet to the value specified in
     * parameter
     *
     * @param strDocumentTypeCode the code
     */
    public void setDocumentTypeCode( String strDocumentTypeCode )
    {
        _strDocumentTypeCode = strDocumentTypeCode;
    }

    /**
     * Returns the identifier of the parent page of the portlet
     *
     * @return the parent page identifier
     */
    public String getDocumentTypeCode(  )
    {
        return _strDocumentTypeCode;
    }

    /**
     * @return the _nIdCategory
     */
    public int[] getIdCategory(  )
    {
        return _nArrayIdCategory;
    }

    /**
     * @param arrayIdCategory the _nIdCategory to set
     */
    public void setIdCategory( int[] arrayIdCategory )
    {
        _nArrayIdCategory = arrayIdCategory;
    }
}
