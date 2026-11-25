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

import fr.paris.lutece.plugins.document.service.DocumentParameterService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.user.AdminUserResourceIdService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.dashboard.AdminDashboardJspBean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Saves the advanced parameters of the document plugin from the admin dashboard
 */
@RequestScoped
@Named
public class DocumentAdminDashboardJspBean extends PluginAdminPageJspBean
{
    private static final String PARAMETER_WORKFLOW_AUTO_VALIDATION = "workflow_auto_validation";
    private static final String MESSAGE_UNAUTHORIZED = "Unauthorized";

    @Inject
    private ISecurityTokenService _securityTokenService;

    @Inject
    private DocumentParameterService _documentParameterService;

    /**
     * Saves the advanced parameters
     *
     * @param request
     *            The HTTP request
     * @return The URL of the admin dashboards page
     * @throws AccessDeniedException
     *             If the security token is invalid or the user is not allowed to manage the advanced parameters
     */
    public String doModifyAdvancedParameters( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !_securityTokenService.validate( request, AdminDashboardJspBean.TEMPLATE_MANAGE_DASHBOARDS ) )
        {
            throw new AccessDeniedException( ERROR_INVALID_TOKEN );
        }

        if ( !RBACService.isAuthorized( AdminUser.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, AdminUserResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS,
                getUser( ) ) )
        {
            throw new AccessDeniedException( MESSAGE_UNAUTHORIZED );
        }

        _documentParameterService.setWorkflowAutoValidation( request.getParameter( PARAMETER_WORKFLOW_AUTO_VALIDATION ) != null );

        return getAdminDashboardsUrl( request, DocumentAdminDashboardComponent.ANCHOR_ADMIN_DASHBOARDS );
    }
}
