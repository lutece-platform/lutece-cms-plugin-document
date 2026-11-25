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
package fr.paris.lutece.plugins.document.modules.rulenotifyusers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.rules.Rule;
import fr.paris.lutece.plugins.document.business.rules.RuleHome;
import fr.paris.lutece.portal.service.util.BeanUtils;
import fr.paris.lutece.portal.service.util.RemovalListenerService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Tests that a mailing list used by a notification rule can not be removed
 */
public class NotifyUsersMailingListRemovalTest extends LuteceTestCase
{
    private static final String RULE_TYPE_NOTIFY_USERS = "notifyUsers";
    private static final String MAILING_LIST_USED = "987654";
    private static final String MAILING_LIST_UNUSED = "987655";

    private Rule _rule;

    /**
     * Creates a notification rule sending to the mailing list MAILING_LIST_USED
     */
    @BeforeEach
    public void createRule( )
    {
        _rule = RuleHome.newInstance( RULE_TYPE_NOTIFY_USERS );
        _rule.setAttribute( "id_space_source", "1" );
        _rule.setAttribute( "id_mailinglist", MAILING_LIST_USED );
        _rule.setAttribute( "id_state", "3" );
        _rule.setAttribute( "message_template_key", "1" );
        RuleHome.create( _rule );
    }

    /**
     * Removes the rule created for the test
     */
    @AfterEach
    public void removeRule( )
    {
        RuleHome.remove( _rule.getId( ) );
    }

    /**
     * The listener registered at the plugin initialization refuses the removal of a mailing list used by a rule
     */
    @Test
    public void testMailingListUsedByRuleCannotBeRemoved( )
    {
        RemovalListenerService service = CDI.current( )
                .select( RemovalListenerService.class, NamedLiteral.of( BeanUtils.BEAN_MAILINGLIST_REMOVAL_SERVICE ) ).get( );
        List<String> listMessages = new ArrayList<>( );

        assertFalse( service.checkForRemoval( MAILING_LIST_USED, listMessages, Locale.FRENCH ) );
        assertFalse( listMessages.isEmpty( ), "a refusal message should be given" );
        assertTrue( service.checkForRemoval( MAILING_LIST_UNUSED, new ArrayList<>( ), Locale.FRENCH ) );
    }
}
