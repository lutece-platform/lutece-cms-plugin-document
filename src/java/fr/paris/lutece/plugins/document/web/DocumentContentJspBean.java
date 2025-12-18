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
package fr.paris.lutece.plugins.document.web;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.util.html.HtmlTemplate;

import jakarta.enterprise.context.RequestScoped;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;


/**
 * Document Content JspBean
 */
@RequestScoped
@Named
public class DocumentContentJspBean implements java.io.Serializable
{
    //////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String XSL_UNIQUE_PREFIX = "document-";

    // Parameters
    private static final String PARAMETER_DOCUMENT_ID = "document_id";

    // Markers
    private static final String MARK_BASE_URL = "base_url";
    private static final String MARK_PREVIEW = "preview";
    private static final String MARK_PORTAL_DOMAIN = "portal_domain";

    // Templates
    private static final String TEMPLATE_PAGE_PRINT_DOCUMENT = "skin/plugins/document/page_print_document.html";

    @Inject
    private Models _model;
    
    /////////////////////////////////////////////////////////////////////////////
    // page management of printing document

    /**
     * Return the view of an document before printing
     * @param request  Http request
     * @return the HTML page
     */
    public String getPrintDocumentPage( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = IntegerUtils.convert( strDocumentId );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( document == null )
        {
            return StringUtils.EMPTY;
        }

        DocumentType type = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType(  ) );

        XmlTransformerService xmlTransformerService = new XmlTransformerService(  );

        String strPreview = xmlTransformerService.transformBySourceWithXslCache( document.getXmlValidatedContent(  ),
                type.getContentServiceXslSource(  ), XSL_UNIQUE_PREFIX + type.getContentServiceStyleSheetId(  ), null,
                null );


        _model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );
        _model.put( MARK_PREVIEW, strPreview );
        _model.put( MARK_PORTAL_DOMAIN, request.getServerName(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PAGE_PRINT_DOCUMENT, request.getLocale(  ),
                _model );

        return template.getHtml(  );
    }
}
