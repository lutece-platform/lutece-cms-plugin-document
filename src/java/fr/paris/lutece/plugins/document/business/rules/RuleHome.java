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
package fr.paris.lutece.plugins.document.business.rules;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...) for Rule objects
 */
public final class RuleHome
{
    private static IRuleDAO _dao = SpringContextService.getBean( "document.ruleDAO" );
    private static IRuleTypesSet _ruleTypesSet = SpringContextService.getBean( "document.ruleTypesSet" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private RuleHome(  )
    {
    }

    /**
     * Creation of an instance of rule
     *
     * @param rule The instance of the rule which contains the informations to store
     * @return The  instance of rule which has been created with its primary key.
     */
    public static Rule create( Rule rule )
    {
        _dao.insert( rule );

        return rule;
    }

    /**
     * Update of the rule which is specified in parameter
     *
     * @param rule The instance of the rule which contains the data to store
     * @return The instance of the  rule which has been updated
     */
    public static Rule update( Rule rule )
    {
        _dao.store( rule );

        return rule;
    }

    /**
     * Remove the Rule whose identifier is specified in parameter
     *
     * @param nRuleId The Rule Id
     */
    public static void remove( int nRuleId )
    {
        _dao.delete( nRuleId );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a rule whose identifier is specified in parameter
     *
     * @param nKey The Primary key of the rule
     * @return An instance of rule
     */
    public static Rule findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _ruleTypesSet );
    }

    /**
     * Returns a collection of rules objects
     * @param locale the current local
     * @return A collection of rules
     */
    public static List<Rule> findAll( Locale locale )
    {
        List<Rule> listRules = _dao.selectRuleList( _ruleTypesSet );

        return I18nService.localizeCollection( listRules, locale );
    }

    /**
     * Returns the rule types list
     * @param locale the Locale
     * @return The rule types list
     */
    public static ReferenceList getRuleTypesList( Locale locale )
    {
        return _ruleTypesSet.getRuleTypesList( locale );
    }

    /**
     * Create a new instance of a rule of a given type
     * @param strRuleTypeId The key name of the rule type
     * @return A new Rule instance
     */
    public static Rule newInstance( String strRuleTypeId )
    {
        return _ruleTypesSet.newInstance( strRuleTypeId );
    }

    /**
     * Returns all rule types
     * @return A collection of rule types
     */
    public static Collection<Rule> getRuleTypes(  )
    {
        return _ruleTypesSet.getRuleTypes(  );
    }

    /**
     * Returns the rule type of a given class type
     * @param rule The Rule object
     * @return The rule type
     */
    public static String getRuleTypeKey( Rule rule )
    {
        return _ruleTypesSet.getRuleTypeKey( rule );
    }

    /**
     * Returns a collection of rules objects specified by rule type key
     * @param strRuleTypeKey The rule type key
     * @return A collection of Rule
     */
    public static Collection<Rule> findByRuleTypeKey( String strRuleTypeKey )
    {
        return _dao.selectRuleListByRuleTypeKey( strRuleTypeKey, _ruleTypesSet );
    }
}
