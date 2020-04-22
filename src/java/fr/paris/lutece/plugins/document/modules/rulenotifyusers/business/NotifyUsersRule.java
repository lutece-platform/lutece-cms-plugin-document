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
package fr.paris.lutece.plugins.document.modules.rulenotifyusers.business;

import fr.paris.lutece.plugins.document.business.rules.AbstractRule;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.plugins.document.business.workflow.DocumentStateHome;
import fr.paris.lutece.plugins.document.service.DocumentEvent;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.plugins.document.service.spaces.SpaceRemovalListenerService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.business.mailinglist.MailingList;
import fr.paris.lutece.portal.business.mailinglist.MailingListHome;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.mailinglist.MailingListRemovalListenerService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * This class provides a rule to notify users on document events
 */
public class NotifyUsersRule extends AbstractRule
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String NO_MAILING_LIST = "none";
    private static final String TEMPLATE_CREATE_RULE = "/admin/plugins/document/modules/rulenotifyusers/create_rule_notify_users.html";
    private static final String MARK_SPACE_SOURCE_PATH = "path_space_source";
    private static final String MARK_SPACE_SOURCE_ID = "id_space_source";
    private static final String MARK_STATE_ID = "id_state";
    private static final String MARK_MAILINGLIST_ID = "id_mailinglist";

    //    private static final String MARK_MESSAGE_TEMPLATE_KEY = "message_template_key";
    private static final String MARK_STATES_LIST = "states_list";
    private static final String MARK_MAILINGLISTS_LIST = "mailinglists_list";
    private static final String MARK_MESSAGE_TEMPLATES_LIST = "message_templates_list";
    private static final String MARK_USER = "user";
    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_URL_PREVIEW = "url_preview";
    private static final String MARK_BASE_URL = "base_url";
    private static final String PARAMETER_SPACE_SOURCE_ID = "id_space_source";
    private static final String PARAMETER_STATE_ID = "id_state";
    private static final String PARAMETER_MAILINGLIST_ID = "id_mailinglist";
    private static final String PARAMETER_MESSAGE_TEMPLATE_KEY = "message_template_key";

    // defined in rulenotifyusers_messages.properties
    private static final String PROPERTY_RULE_NAME = "module.document.rulenotifyusers.ruleName";
    private static final String PROPERTY_RULE_DESCRIPTION = "module.document.rulenotifyusers.ruleLiteral";
    private static final String PROPERTY_MAIL_SENDER_NAME = "module.document.rulenotifyusers.mailSenderName";
    private static final String PROPERTY_RULE_ERROR_MAILING_LIST_ID = "module.document.rulenotifyusers.message.create_rule_notify_users.errorMailingListId";
    private static final String PROPERTY_CHOOSE_MAILING_LIST = "module.document.rulenotifyusers.create_rule_notify_users.chooseMailingList";
    private static final String PROPERTY_RULE_ERROR_NOT_SELECT_SPACE_SOURCE = "module.document.rulenotifyusers.message.create_rule_notify_users.errorNotSelectSpaceSource";

    // defined in document-rulenotifyusers.properties
    private static final String PROPERTY_MESSAGE_TEMPLATES_ENTRIES = "document-rulenotifyusers.messages";
    private static final String PROPERTY_MESSAGE_PREFIX = "document-rulenotifyusers.message.";
    private static final String PROPERTY_MESSAGE_PREFIX_INTERNATIONALIZATION = "module.document.rulenotifyusers.notify_users_rule.message.";
    private static final String SUFFIX_TEMPLATE = ".template";
    private static final String SUFFIX_SUBJECT = ".subject";
    private static final String SUFFIX_DESCRIPTION = ".description";

    // suffix that allows to get the url used in the mail - defined in document-rulenotifyusers.properties
    private static final String SUFFIX_URL_PREVIEW = "document-rulenotifyusers.previewDocument";
    private static String[] _attributes = 
        {
            PARAMETER_SPACE_SOURCE_ID, PARAMETER_MAILINGLIST_ID, PARAMETER_STATE_ID, PARAMETER_MESSAGE_TEMPLATE_KEY,
        };
    private static NotifyUsersMailingListRemovalListener _listenerMailingList;
    private static NotifyUsersSpaceRemovalListener _listenerSpace;
    private static final String PROPERTY_PROD_BASE_URL = "lutece.prod.url";

    /**
     * Initialize the rule
     */
    public void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerMailingList == null )
        {
            _listenerMailingList = new NotifyUsersMailingListRemovalListener(  );
            MailingListRemovalListenerService.getService(  ).registerListener( _listenerMailingList );
        }

        if ( _listenerSpace == null )
        {
            _listenerSpace = new NotifyUsersSpaceRemovalListener(  );
            SpaceRemovalListenerService.getService(  ).registerListener( _listenerSpace );
        }
    }

    /**
     * Gets the Rule name key
     * @return The Rule name key
     */
    public String getNameKey(  )
    {
        return PROPERTY_RULE_NAME;
    }

    /**
     * Execute the rule
     * @param event The document event
     * @throws DocumentException raise when error occurs in event or rule
     */
    public void apply( DocumentEvent event ) throws DocumentException
    {
        try
        {
            int nSourceSpace = Integer.parseInt( getAttribute( PARAMETER_SPACE_SOURCE_ID ) );
            int nState = Integer.parseInt( getAttribute( PARAMETER_STATE_ID ) );
            int nMailingListId = Integer.parseInt( getAttribute( PARAMETER_MAILINGLIST_ID ) );

            UrlItem url = AppPathService.buildRedirectUrlItem( "", SUFFIX_URL_PREVIEW );

            if ( event.getStateId(  ) == nState )
            {
                if ( event.getSpaceId(  ) == nSourceSpace )
                {
                    Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( nMailingListId );

                    for ( Recipient recipient : listRecipients )
                    {
                        // Build the mail message
                        Map<String, Object> model = new HashMap<String, Object>(  );
                        model.put( MARK_USER, event.getUser(  ) );
                        model.put( MARK_DOCUMENT, event.getDocument(  ) );
                        model.put( MARK_URL_PREVIEW, url.getUrl(  ) );
                        model.put( MARK_BASE_URL, AppPropertiesService.getProperty( PROPERTY_PROD_BASE_URL ) );

                        String strMessageTemplate = getMessageTemplate( getAttribute( PARAMETER_MESSAGE_TEMPLATE_KEY ) );
                        String strSubject = getMessageSubject( getAttribute( PARAMETER_MESSAGE_TEMPLATE_KEY ),
                                event.getUser(  ).getLocale(  ) );
                        HtmlTemplate t = AppTemplateService.getTemplate( strMessageTemplate,
                                event.getUser(  ).getLocale(  ), model );

                        // Send Mail
                        String strSenderName = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_NAME,
                                event.getUser(  ).getLocale(  ) );
                        String strSenderEmail = MailService.getNoReplyEmail(  );

                        MailService.sendMailHtml( recipient.getEmail(  ), strSenderName, strSenderEmail, strSubject,
                            t.getHtml(  ) );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( "Error in NotifyUserRule event : " + e.getMessage(  ), e );
        }
    }

    /**
     * Gets the Rule create form
     * @param user The current user using the form
     * @param locale The current locale
     * @return The HTML form
     */
    public String getCreateForm( AdminUser user, Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        Collection<ReferenceItem> listStates = DocumentStateHome.getDocumentStatesList( locale );
        ReferenceList listMailingLists = new ReferenceList(  );

        if ( this.getAttribute( PARAMETER_STATE_ID ) != null )
        {
            model.put( MARK_STATE_ID, this.getAttribute( PARAMETER_STATE_ID ) );
        }

        if ( this.getAttribute( PARAMETER_MAILINGLIST_ID ) != null )
        {
            model.put( MARK_MAILINGLIST_ID, this.getAttribute( PARAMETER_MAILINGLIST_ID ) );
        }

        if ( this.getAttribute( PARAMETER_MESSAGE_TEMPLATE_KEY ) != null )
        {
            model.put( PARAMETER_MESSAGE_TEMPLATE_KEY, this.getAttribute( PARAMETER_MESSAGE_TEMPLATE_KEY ) );
        }

        if ( this.getAttribute( PARAMETER_SPACE_SOURCE_ID ) != null )
        {
            int nIdSpaceSource = -1;
            String strPathSpaceSource;

            try
            {
                nIdSpaceSource = Integer.parseInt( this.getAttribute( PARAMETER_SPACE_SOURCE_ID ) );
                model.put( MARK_SPACE_SOURCE_ID, nIdSpaceSource );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }

            strPathSpaceSource = DocumentSpacesService.getInstance(  ).getLabelSpacePath( nIdSpaceSource, user );
            model.put( MARK_SPACE_SOURCE_PATH, strPathSpaceSource );
        }

        listMailingLists.addItem( NO_MAILING_LIST,
            I18nService.getLocalizedString( PROPERTY_CHOOSE_MAILING_LIST, locale ) );
        listMailingLists.addAll( AdminMailingListService.getMailingLists( user ) );

        model.put( MARK_STATES_LIST, listStates );
        model.put( MARK_MAILINGLISTS_LIST, listMailingLists );
        model.put( MARK_MESSAGE_TEMPLATES_LIST, getMessageTemplatesList( locale ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_RULE, locale, model );

        return template.getHtml(  );
    }

    /**
     * Check the rule
     *
     * @return null if rule is valid, message if rule not valid
     */
    public String validateRule(  )
    {
        String strMailingListId = getAttribute( PARAMETER_MAILINGLIST_ID );
        String strSourceSpaceId = getAttribute( PARAMETER_SPACE_SOURCE_ID );

        if ( strSourceSpaceId == null )
        {
            return PROPERTY_RULE_ERROR_NOT_SELECT_SPACE_SOURCE;
        }

        if ( ( strMailingListId == null ) || !strMailingListId.matches( REGEX_ID ) )
        {
            return PROPERTY_RULE_ERROR_MAILING_LIST_ID;
        }

        return null;
    }

    /**
     * true if the user is authorized to view the rule
     * @param user the current user
     * @return true if the user is authorized to view the rule
     */
    public boolean isAuthorized( AdminUser user )
    {
        int nSourceSpaceId = IntegerUtils.convert( getAttribute( PARAMETER_SPACE_SOURCE_ID ) );

        if ( !DocumentSpacesService.getInstance(  ).isAuthorizedViewByWorkgroup( nSourceSpaceId, user ) )
        {
            return false;
        }

        return true;
    }

    /**
     * Gets all attributes of the rule
     * @return attributes of the rule
     */
    public String[] getAttributesList(  )
    {
        return _attributes;
    }

    /**
     * Gets the explicit text of the rule
     * @return The text of the rule
     */
    public String getRule(  )
    {
        int nSourceSpaceId = IntegerUtils.convert( getAttribute( PARAMETER_SPACE_SOURCE_ID ) );
        String strSourceSpace = DocumentSpacesService.getInstance(  ).getLabelSpacePath( nSourceSpaceId, getUser(  ) );
        String strMailingListId = getAttribute( PARAMETER_MAILINGLIST_ID );
        String strMailingList = null;

        if ( StringUtils.isNotBlank( strMailingListId ) && strMailingListId.matches( REGEX_ID ) )
        {
            int nMailingListId = IntegerUtils.convert( strMailingListId );
            MailingList mailinglist = MailingListHome.findByPrimaryKey( nMailingListId );

            if ( mailinglist != null )
            {
                strMailingList = mailinglist.getDescription(  );
            }
        }

        String strMessageTemplate = getMessageDescription( getAttribute( PARAMETER_MESSAGE_TEMPLATE_KEY ), getLocale(  ) );
        int nStateId = IntegerUtils.convert( getAttribute( PARAMETER_STATE_ID ) );
        DocumentState state = DocumentStateHome.findByPrimaryKey( nStateId );
        String strState = StringUtils.EMPTY;

        if ( state != null )
        {
            state.setLocale( getLocale(  ) );
            strState = state.getName(  );
        }

        String[] ruleArgs = { strSourceSpace, strState, strMailingList, strMessageTemplate };

        return I18nService.getLocalizedString( PROPERTY_RULE_DESCRIPTION, ruleArgs, getLocale(  ) );
    }

    /**
     * Get the parameter key for the mailing list Id
     * @return The parameter key
     */
    public static String getParameterKeyMailingListId(  )
    {
        return PARAMETER_MAILINGLIST_ID;
    }

    /**
     * Get the parameter key for the source space Id
     * @return The parameter key
     */
    public static String getParameterKeySourceSpaceId(  )
    {
        return PARAMETER_SPACE_SOURCE_ID;
    }

    /**
     * Get the parameter key for the state Id
     * @return The parameter key
     */
    public static String getParameterKeyStateId(  )
    {
        return PARAMETER_STATE_ID;
    }

    /**
     * Gets the list of message templates
     *
     * @param locale The current locale
     * @return A ReferenceList containing all available messages templates
     */
    private ReferenceList getMessageTemplatesList( Locale locale )
    {
        ReferenceList listTemplates = new ReferenceList(  );

        String strEntries = AppPropertiesService.getProperty( PROPERTY_MESSAGE_TEMPLATES_ENTRIES );

        // extracts each item (separated by a comma) from the list
        StringTokenizer strTokens = new StringTokenizer( strEntries, "," );

        while ( strTokens.hasMoreTokens(  ) )
        {
            String strMessageKey = strTokens.nextToken(  );
            String strTemplateDescription = getMessageDescription( strMessageKey, locale );
            listTemplates.addItem( strMessageKey, strTemplateDescription );
        }

        return listTemplates;
    }

    /**
     * Gets the message description from the
     * @param strMessageKey The message key
     * @param locale The current locale
     * @return The message description
     */
    private String getMessageDescription( String strMessageKey, Locale locale )
    {
        return I18nService.getLocalizedString( PROPERTY_MESSAGE_PREFIX_INTERNATIONALIZATION + strMessageKey +
            SUFFIX_DESCRIPTION, locale );
    }

    /**
     * Gets the message subject from the
     * @param strMessageKey The message key
     * @param locale The current locale
     * @return The message subject
     */
    private String getMessageSubject( String strMessageKey, Locale locale )
    {
        return I18nService.getLocalizedString( PROPERTY_MESSAGE_PREFIX_INTERNATIONALIZATION + strMessageKey +
            SUFFIX_SUBJECT, locale );
    }

    /**
     * Gets the message template from the
     * @param strMessageKey The message key
     * @return The message template
     */
    private String getMessageTemplate( String strMessageKey )
    {
        return AppPropertiesService.getProperty( PROPERTY_MESSAGE_PREFIX + strMessageKey + SUFFIX_TEMPLATE );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }

        if ( obj instanceof NotifyUsersRule )
        {
            NotifyUsersRule rule = (NotifyUsersRule) obj;

            if ( ( this.getAttribute( PARAMETER_SPACE_SOURCE_ID ) == null ) ||
                    ( this.getAttribute( PARAMETER_MAILINGLIST_ID ) == null ) ||
                    ( this.getAttribute( PARAMETER_STATE_ID ) == null ) ||
                    ( this.getAttribute( PARAMETER_MESSAGE_TEMPLATE_KEY ) == null ) ||
                    ( rule.getAttribute( PARAMETER_SPACE_SOURCE_ID ) == null ) ||
                    ( rule.getAttribute( PARAMETER_MAILINGLIST_ID ) == null ) ||
                    ( rule.getAttribute( PARAMETER_STATE_ID ) == null ) ||
                    ( rule.getAttribute( PARAMETER_MESSAGE_TEMPLATE_KEY ) == null ) )
            {
                return false;
            }

            if ( this.getAttribute( PARAMETER_SPACE_SOURCE_ID ).equals( rule.getAttribute( PARAMETER_SPACE_SOURCE_ID ) ) &&
                    this.getAttribute( PARAMETER_MAILINGLIST_ID ).equals( rule.getAttribute( PARAMETER_MAILINGLIST_ID ) ) &&
                    this.getAttribute( PARAMETER_STATE_ID ).equals( rule.getAttribute( PARAMETER_STATE_ID ) ) &&
                    this.getAttribute( PARAMETER_MESSAGE_TEMPLATE_KEY )
                            .equals( rule.getAttribute( PARAMETER_MESSAGE_TEMPLATE_KEY ) ) )
            {
                return true;
            }
        }

        return false;
    }
}
