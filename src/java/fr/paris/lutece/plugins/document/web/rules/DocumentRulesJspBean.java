/*
 * Copyright (c) 2002-2023, City of Paris
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
package fr.paris.lutece.plugins.document.web.rules;

import fr.paris.lutece.plugins.document.business.rules.Rule;
import fr.paris.lutece.plugins.document.business.rules.RuleHome;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * JSP Bean for document rules management
 */
public class DocumentRulesJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_RULES_MANAGEMENT = "DOCUMENT_RULES_MANAGEMENT";

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = -1195569288911640639L;
    private static final String JSP_DO_DELETE_RULE = "jsp/admin/plugins/document/DoDeleteRule.jsp";
    private static final String JSP_SELECT_SPACE = "jsp/admin/plugins/document/SelectSpace.jsp";
    private static final String JSP_CREATE_RULE_SESSION = "jsp/admin/plugins/document/CreateRuleSession.jsp";
    private static final String TEMPLATE_MANAGE_RULES = "/admin/plugins/document/rules/manage_rules.html";
    private static final String TEMPLATE_CREATE_RULE = "/admin/plugins/document/rules/create_rule.html";
    private static final String TEMPLATE_SELECT_SPACE = "/admin/plugins/document/rules/select_space_rule.html";
    private static final String MARK_RULES_LIST = "rules_list";
    private static final String MARK_RULE_TYPES_LIST = "rule_types_list";
    private static final String MARK_CREATE_FORM = "create_form";
    private static final String MARK_RULE_TYPE_ID = "rule_type_id";
    private static final String MARK_SPACES_BROWSER = "spaces_browser";
    private static final String MARK_SUBMIT_BUTTON_DISABLED = "submit_button_disabled";
    private static final String PARAMETER_RULE_TYPE_ID = "id_rule_type";
    private static final String PARAMETER_RULE_ID = "id_rule";
    private static final String PARAMETER_RULE_ATTRIBUTE_SELECTED = "rule_attribute_selected";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PROPERTY_CONFIRM_RULE_DELETE = "document.rules.message.confirmRuleDelete";
    private static final String PROPERTY_RULE_ALREADY_EXISTS = "document.rules.message.ruleAlreadyExists";
    private static final String PROPERTY_CREATE_RULE_PAGE_TITLE = "document.create_rule.pageTitle";
    private static final String PROPERTY_SELECT_SPACE_PAGE_TITLE = "document.create_rule.selectSpace";
    private Rule _rule;
    private String _StrRuleAttributeSelected;

    /**
     * Get rule
     * @return Rule
     */
    public Rule getRuleSession(  )
    {
        return _rule;
    }

    /**
     * Set rule
     * @param rule rule
     */
    public void setRuleSession( Rule rule )
    {
        _rule = rule;
    }

    /**
     * Get the RuleAttributeSelectedSession
     * @return RuleAttributeSelectedSession
     */
    public String getRuleAttributeSelectedSession(  )
    {
        return _StrRuleAttributeSelected;
    }

    /**
     * Set the RuleAttributeSelectedSession
     * @param ruleAttributeSelected RuleAttributeSelectedSession
     */
    public void setRuleAttributeSelectedSession( String ruleAttributeSelected )
    {
        _StrRuleAttributeSelected = ruleAttributeSelected;
    }

    /**
     * Gets the manage rules page
     * @param request The HTTP request
     * @return the manage rules page
     */
    public String getManageRules( HttpServletRequest request )
    {
        setPageTitleProperty( null );

        AdminUser user = getUser(  );

        List<Rule> listRules = RuleHome.findAll( getLocale(  ) );
        List<Rule> listRulesAuthorized = new ArrayList<Rule>(  );

        for ( Rule rule : listRules )
        {
            if ( rule.isAuthorized( user ) )
            {
                rule.setUser( user );
                listRulesAuthorized.add( rule );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_RULES_LIST, listRulesAuthorized );
        model.put( MARK_RULE_TYPES_LIST, RuleHome.getRuleTypesList( getLocale(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_RULES, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Gets the interface of spaces selection
     * @param request the request
     * @return the interface of spaces select
     */
    public String getSelectSpace( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_SELECT_SPACE_PAGE_TITLE );

        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );
        boolean bSubmitButtonDisabled = Boolean.TRUE;

        if ( StringUtils.isNotBlank( strSpaceId ) )
        {
            bSubmitButtonDisabled = Boolean.FALSE;
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        // Spaces browser
        model.put( MARK_SUBMIT_BUTTON_DISABLED, bSubmitButtonDisabled );
        model.put( MARK_SPACES_BROWSER,
            DocumentSpacesService.getInstance(  ).getSpacesBrowser( request, getUser(  ), getLocale(  ), false, true ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECT_SPACE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * perform the space selected in the rule session
     * @param request The HTTP request
     * @return The create rule page
     */
    public String doSelectSpace( HttpServletRequest request )
    {
        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );

        if ( strSpaceId == null )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nSpaceId = IntegerUtils.convert( strSpaceId );
        getRuleSession(  ).setAttribute( getRuleAttributeSelectedSession(  ), Integer.toString( nSpaceId ) );

        return AppPathService.getBaseUrl( request ) + JSP_CREATE_RULE_SESSION;
    }

    /**
     * Gets the create rule page
     * @param request The HTTP request
     * @return The create rule page
     */
    public String getCreateRule( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_CREATE_RULE_PAGE_TITLE );

        String strRuleTypeId;
        Rule rule;

        if ( getRuleSession(  ) == null )
        {
            strRuleTypeId = request.getParameter( PARAMETER_RULE_TYPE_ID );
            rule = RuleHome.newInstance( strRuleTypeId );
        }
        else
        {
            rule = getRuleSession(  );
            strRuleTypeId = rule.getRuleTypeId(  );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_CREATE_FORM, rule.getCreateForm( getUser(  ), getLocale(  ) ) );
        model.put( MARK_RULE_TYPE_ID, strRuleTypeId );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_RULE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the rule creation
     * @param request The HTTP request
     * @return The URL to go after the creation
     */
    public String doCreateRule( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strRuleTypeId = request.getParameter( PARAMETER_RULE_TYPE_ID );
            Rule rule = RuleHome.newInstance( strRuleTypeId );
            rule.readAttributes( request );

            String strValidation = rule.validateRule(  );

            if ( strValidation != null )
            {
                return AdminMessageService.getMessageUrl( request, strValidation, AdminMessage.TYPE_STOP );
            }

            // Check if rule already exist
            for ( Rule checkRule : RuleHome.findByRuleTypeKey( rule.getRuleTypeId(  ) ) )
            {
                if ( rule.equals( checkRule ) )
                {
                    return AdminMessageService.getMessageUrl( request, PROPERTY_RULE_ALREADY_EXISTS,
                        AdminMessage.TYPE_STOP );
                }
            }

            RuleHome.create( rule );
        }

        return getHomeUrl( request );
    }

    /**
     * Perform the rule in session
     * @param request The HTTP request
     * @return The URL of spaces selection
     */
    public String doSaveRuleSession( HttpServletRequest request )
    {
        String strRuleAttributeNameSelected = request.getParameter( PARAMETER_RULE_ATTRIBUTE_SELECTED );
        String strRuleTypeId = request.getParameter( PARAMETER_RULE_TYPE_ID );
        Rule rule = RuleHome.newInstance( strRuleTypeId );
        rule.readAttributes( request );
        setRuleSession( rule );
        setRuleAttributeSelectedSession( strRuleAttributeNameSelected );

        return AppPathService.getBaseUrl( request ) + JSP_SELECT_SPACE + "?" +
        DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID + "=" +
        rule.getAttribute( strRuleAttributeNameSelected );
    }

    /**
     * Confirm the deletion
     * @param request The HTTP request
     * @return The URL to go after the confirmation
     */
    public String deleteRule( HttpServletRequest request )
    {
        String strRuleId = request.getParameter( PARAMETER_RULE_ID );
        Rule rule = RuleHome.findByPrimaryKey( IntegerUtils.convert( strRuleId ) );
        UrlItem url = new UrlItem( JSP_DO_DELETE_RULE );
        url.addParameter( PARAMETER_RULE_ID, strRuleId );
        rule.setLocale( getLocale(  ) );

        String[] args = { rule.getRule(  ) };

        return AdminMessageService.getMessageUrl( request, PROPERTY_CONFIRM_RULE_DELETE, args, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the deletion
     * @param request The HTTP request
     * @return The URL to go after the deletion
     */
    public String doDeleteRule( HttpServletRequest request )
    {
        String strRuleId = request.getParameter( PARAMETER_RULE_ID );
        RuleHome.remove( IntegerUtils.convert( strRuleId ) );

        return getHomeUrl( request );
    }
}
