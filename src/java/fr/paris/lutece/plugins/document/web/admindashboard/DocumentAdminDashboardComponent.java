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
package fr.paris.lutece.plugins.document.web.admindashboard;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.document.service.DocumentParameterService;
import fr.paris.lutece.plugins.document.web.DocumentTypeJspBean;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.admin.AdminDashboardComponent;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.user.AdminUserResourceIdService;
import fr.paris.lutece.portal.web.dashboard.AdminDashboardJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin dashboard panel managing the advanced parameters of the document plugin
 */
public class DocumentAdminDashboardComponent extends AdminDashboardComponent
{
    /** Anchor of the panel in the admin dashboards page */
    public static final String ANCHOR_ADMIN_DASHBOARDS = "documentAdminDashboard";

    private static final String TEMPLATE_ADMIN_DASHBOARD = "admin/plugins/document/dashboard/document_adminDashboard.html";
    private static final String MARK_WORKFLOW_AUTO_VALIDATION = "workflow_auto_validation";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        if ( !user.checkRight( DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT ) || !RBACService.isAuthorized( AdminUser.RESOURCE_TYPE,
                RBAC.WILDCARD_RESOURCES_ID, AdminUserResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, (User) user ) )
        {
            return StringUtils.EMPTY;
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( SecurityTokenService.MARK_TOKEN,
                CDI.current( ).select( ISecurityTokenService.class ).get( ).getToken( request, AdminDashboardJspBean.TEMPLATE_MANAGE_DASHBOARDS ) );
        model.put( MARK_WORKFLOW_AUTO_VALIDATION, CDI.current( ).select( DocumentParameterService.class ).get( ).isWorkflowAutoValidation( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ADMIN_DASHBOARD, user.getLocale( ), model );

        return template.getHtml( );
    }
}
