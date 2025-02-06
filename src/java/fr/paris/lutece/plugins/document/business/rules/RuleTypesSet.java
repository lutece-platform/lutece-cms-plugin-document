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
package fr.paris.lutece.plugins.document.business.rules;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Rules Set
 */
public class RuleTypesSet implements IRuleTypesSet
{
    private Map<String, Rule> _mapRuleTypes;

    /**
     * Sets the rule types map
     * 
     * @param mapRules
     *            The rule types map
     */
    public void setRuleTypesMap( Map<String, Rule> mapRules )
    {
        _mapRuleTypes = mapRules;
    }

    /**
     * Create a new instance of a rule of a given type
     * 
     * @param strRuleTypeKey
     *            The key name of the rule type
     * @return A new Rule instance
     */
    public Rule newInstance( String strRuleTypeKey )
    {
        Rule rule = null;

        try
        {
            rule = _mapRuleTypes.get( strRuleTypeKey ).getClass( ).newInstance( );
            rule.setRuleTypeId( strRuleTypeKey );
        }
        catch( InstantiationException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
        catch( IllegalAccessException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return rule;
    }

    /**
     * Returns the rule type of a given class type
     * 
     * @return The rule type
     */
    public String getRuleTypeKey( Rule rule )
    {
        for ( String key : _mapRuleTypes.keySet( ) )
        {
            Rule ruleType;

            try
            {
                ruleType = _mapRuleTypes.get( key ).getClass( ).newInstance( );

                if ( ruleType.getClass( ).isInstance( rule ) )
                {
                    return key;
                }
            }
            catch( InstantiationException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
            catch( IllegalAccessException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
        }

        return null;
    }

    /**
     * Returns the rule types list
     * 
     * @return The rule types list
     */
    public ReferenceList getRuleTypesList( Locale locale )
    {
        ReferenceList listRules = new ReferenceList( );

        for ( String strRuleKey : _mapRuleTypes.keySet( ) )
        {
            String strRuleNameKey = newInstance( strRuleKey ).getNameKey( );
            listRules.addItem( strRuleKey, I18nService.getLocalizedString( strRuleNameKey, locale ) );
        }

        return listRules;
    }

    /**
     * Returns all rule types
     * 
     * @return A collection of rule types
     */
    public Collection<Rule> getRuleTypes( )
    {
        return _mapRuleTypes.values( );
    }
}
