/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.document.modules.comment.web;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.modules.comment.business.DocumentComment;
import fr.paris.lutece.plugins.document.modules.comment.business.DocumentCommentHome;
import fr.paris.lutece.plugins.document.modules.comment.service.DocumentCommentPlugin;
import fr.paris.lutece.plugins.document.modules.comment.service.DocumentCommentResourceIdService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.database.AppConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Calendar Dashboard Component
 * This component displays directories
 */
public class DocumentCommentDashboardComponent extends DashboardComponent
{
    // PARAMETERS
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";

    // MARKS
    private static final String MARK_URL = "url";
    private static final String MARK_ICON = "icon";
    private static final String MARK_LAST_COMMENT = "last_comment";
    private static final String MARK_PERMISSION_COMMENT = "permission_comment";
    private static final String MARK_DOCUMENT = "document";

    // CONSTANTS
    private static final int ZONE_1 = 1;

    // TEMPALTES
    private static final String TEMPLATE_DASHBOARD_ZONE_1 = "/admin/plugins/document/modules/comment/dashboard/comment_dashboard_zone_1.html";
    private static final String TEMPLATE_DASHBOARD_OTHER_ZONE = "/admin/plugins/document/modules/comment/dashboard/comment_dashboard_other_zone.html";

    // JSP
    private static final String JSP_URL_VIEW_PLUGIN_DESCRIPTION = "jsp/admin/system/ViewPluginDescription.jsp";

    /**
     * The HTML code of the component
     * @param user The Admin User
         * @param request HttpServletRequest
     * @return The dashboard component
     */
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        Plugin plugin = PluginService.getPlugin( DocumentCommentPlugin.PLUGIN_NAME );

        if ( !( ( plugin.getDbPoolName(  ) != null ) &&
                !AppConnectionService.NO_POOL_DEFINED.equals( plugin.getDbPoolName(  ) ) ) )
        {
            return StringUtils.EMPTY;
        }

        Map<String, Object> model = new HashMap<String, Object>(  );

        // Get last comment
        DocumentComment lastComment = DocumentCommentHome.findLastComment(  );

        if ( lastComment != null )
        {
            Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( lastComment.getDocumentId(  ) );

            model.put( MARK_DOCUMENT, document );
        }

        UrlItem url = new UrlItem( JSP_URL_VIEW_PLUGIN_DESCRIPTION );
        url.addParameter( PARAMETER_PLUGIN_NAME, DocumentCommentPlugin.PLUGIN_NAME );

        model.put( MARK_LAST_COMMENT, lastComment );
        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_ICON, plugin.getIconUrl(  ) );
        model.put( MARK_PERMISSION_COMMENT,
            RBACService.isAuthorized( DocumentComment.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                DocumentCommentResourceIdService.PERMISSION_COMMENT, user ) );

        HtmlTemplate t = AppTemplateService.getTemplate( getTemplateDashboard(  ), user.getLocale(  ), model );

        return t.getHtml(  );
    }

    /**
     * Get the template
     * @return the template
     */
    private String getTemplateDashboard(  )
    {
        if ( getZone(  ) == ZONE_1 )
        {
            return TEMPLATE_DASHBOARD_ZONE_1;
        }

        return TEMPLATE_DASHBOARD_OTHER_ZONE;
    }
}
