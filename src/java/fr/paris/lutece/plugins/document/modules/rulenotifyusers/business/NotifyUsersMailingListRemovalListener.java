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
package fr.paris.lutece.plugins.document.modules.rulenotifyusers.business;

import fr.paris.lutece.plugins.document.business.rules.Rule;
import fr.paris.lutece.plugins.document.business.rules.RuleHome;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.RemovalListener;

import java.util.Collection;
import java.util.Locale;


/**
 * MailingList Removal Listener
 */
public class NotifyUsersMailingListRemovalListener implements RemovalListener
{
    private static final String PROPERTY_MAILING_LIST_CANNOT_BE_REMOVED = "module.document.rulenotifyusers.message.mailinglistCannotBeRemoved";

    /**
    * Check if the object can be safely removed
    * @param strId The object id
    * @return true if the pbject can be removed otherwise false
    */
    public boolean canBeRemoved( String strId )
    {
        if ( IntegerUtils.isNotNumeric( strId ) )
        {
            return true;
        }

        int nId = IntegerUtils.convert( strId );

        // Get the rule type key of the given rule type class
        Rule ruleNotifyUser = new NotifyUsersRule(  );
        String strRuleTypeKey = RuleHome.getRuleTypeKey( ruleNotifyUser );

        //Get list of rules filtered by rule type key
        Collection<Rule> listRule = RuleHome.findByRuleTypeKey( strRuleTypeKey );

        for ( Rule rule : listRule )
        {
            String strMailingListId = rule.getAttribute( NotifyUsersRule.getParameterKeyMailingListId(  ) );

            int nMailingListId = IntegerUtils.convert( strMailingListId );

            if ( nMailingListId == nId )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Gives a message explaining why the object can't be removed
     * @param id The object id
     * @param locale The current locale
     * @return The message
     */
    public String getRemovalRefusedMessage( String id, Locale locale )
    {
        // Build a message for rules using this mailing list
        return I18nService.getLocalizedString( PROPERTY_MAILING_LIST_CANNOT_BE_REMOVED, locale );
    }
}
