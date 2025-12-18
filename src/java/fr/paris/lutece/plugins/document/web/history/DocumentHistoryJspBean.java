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
package fr.paris.lutece.plugins.document.web.history;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.history.HistoryEvent;
import fr.paris.lutece.plugins.document.business.history.HistoryEventHome;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.DocumentTypeResourceIdService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Collection;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;


/**
 * DocumentHistoryJspBean
 */
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@RequestScoped
@Named
public class DocumentHistoryJspBean extends PluginAdminPageJspBean
{
    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = -3845929456241534490L;
    private static final String TEMPLATE_HISTORY = "admin/plugins/document/history/document_history.html";
    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_EVENTS_LIST = "events_list";
    private static final String PROPERTY_PAGE_TITLE = "document.document_history.pageTitle";
    private static final String PARAMETER_DOCUMENT_ID = "id_document";

    @Inject
    private Models _model;
    
    /**
     * Get the history page
     * @param request The request
     * @return The HTML content to display
     */
    public String getHistory( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = IntegerUtils.convert( strDocumentId );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );
        Collection<HistoryEvent> listEvents = null;

        if ( ( document != null ) &&
        		 CDI.current( ).select( DocumentService.class ).get( )
                                   .isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_VIEW_HISTORY, getUser(  ) ) )
        {
            listEvents = HistoryEventHome.findByDocument( nDocumentId, getLocale(  ) );
        }
        else
        {
            document = null;
        }

        _model.put( MARK_DOCUMENT, document );
        _model.put( MARK_EVENTS_LIST, listEvents );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_HISTORY, getLocale(  ), _model );

        return getAdminPage( template.getHtml(  ) );
    }
}
