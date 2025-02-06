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
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.right.RightHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.database.AppConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Calendar Dashboard Component
 * This component displays directories
 */
public class DocumentDashboardComponent extends DashboardComponent
{
    // PARAMETERS
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";

    // MARKS
    private static final String MARK_URL = "url";
    private static final String MARK_ICON = "icon";
    private static final String MARK_LAST_MODIFIED_DOCUMENT = "last_modified_document";
    private static final String MARK_LAST_PUBLISHED_DOCUMENT = "last_published_document";

    // TEMPALTES
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/document/dashboard/document_dashboard.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        Right right = RightHome.findByPrimaryKey( getRight(  ) );
        Plugin plugin = PluginService.getPlugin( right.getPluginName(  ) );

        if ( !( ( plugin.getDbPoolName(  ) != null ) &&
                !AppConnectionService.NO_POOL_DEFINED.equals( plugin.getDbPoolName(  ) ) ) )
        {
            return StringUtils.EMPTY;
        }

        // Get the last document the user was working on
        Document lastModifiedDocument = DocumentHome.loadLastModifiedDocumentFromUser( user.getAccessCode(  ) );

        if ( lastModifiedDocument != null )
        {
            lastModifiedDocument.setLocale( user.getLocale(  ) );
            DocumentService.getInstance(  ).getActions( lastModifiedDocument, user.getLocale(  ), user );
            DocumentService.getInstance(  ).getPublishedStatus( lastModifiedDocument );
        }

        // Get the last published document
        Document lastPublishedDocument = DocumentHome.loadLastPublishedDocument(  );

        if ( lastPublishedDocument != null )
        {
            lastPublishedDocument.setLocale( user.getLocale(  ) );
            DocumentService.getInstance(  ).getActions( lastPublishedDocument, user.getLocale(  ), user );
            DocumentService.getInstance(  ).getPublishedStatus( lastPublishedDocument );
        }

        UrlItem url = new UrlItem( right.getUrl(  ) );
        url.addParameter( PARAMETER_PLUGIN_NAME, right.getPluginName(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_LAST_MODIFIED_DOCUMENT, lastModifiedDocument );
        model.put( MARK_LAST_PUBLISHED_DOCUMENT, lastPublishedDocument );
        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_ICON, plugin.getIconUrl(  ) );

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, user.getLocale(  ), model );

        return t.getHtml(  );
    }
}
