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

import fr.paris.lutece.plugins.document.modules.rulemovespace.business.MoveSpaceRule;
import fr.paris.lutece.plugins.document.modules.rulenotifyusers.business.NotifyUsersRule;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.ReferenceList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Collection;

import jakarta.enterprise.inject.Instance;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 * Rules Set
 */
@ApplicationScoped
@Named( "document.ruleTypesSet" )
public class RuleTypesSet implements IRuleTypesSet
{
	private final String BEAN_NAME_RULEMOVESPACE = "moveSpace";
	private final String BEAN_NAME_NOTIFYUSERS = "notifyUsers";
	private java.util.List < String > _ruleTypeKeys = Arrays.asList( BEAN_NAME_RULEMOVESPACE, BEAN_NAME_NOTIFYUSERS );

	@Inject
	@Named( "document.MoveSpaceRule" )
	private Instance < MoveSpaceRule > moveSpaceRuleInstance;

	@Inject
	@Named( "document.NotifyUsersRule" )
	private Instance < NotifyUsersRule > notifyUsersRuleInstance;

	/**
	 * Init
	 * 
	 */
	public void init( )
	{

	}

	/**
	 * Create a new instance of a rule of a given type
	 * 
	 * @param strRuleTypeKey
	 *                       The key name of the rule type
	 * @return A new Rule instance
	 */
    @Override
	public Rule newInstance( String strRuleTypeKey )
	{
		Rule rule = null;
		if( BEAN_NAME_RULEMOVESPACE.equals( strRuleTypeKey ) )
		{
			rule = moveSpaceRuleInstance.get( );
		}
		else if( BEAN_NAME_NOTIFYUSERS.equals( strRuleTypeKey ) )
		{
			rule = notifyUsersRuleInstance.get( );
		}
		else
		{
			// log ou exception si besoin
			return null;
		}
		rule.setRuleTypeId( strRuleTypeKey );

		return rule;
	}

	/**
	 * Returns the rule type of a given class type
	 * 
	 * @return The rule type
	 */
    @Override
	public String getRuleTypeKey( Rule rule )
	{
		for( String key : _ruleTypeKeys )
		{
			Rule candidate = newInstance( key );
			if( candidate != null && candidate.getClass( ).isInstance( rule ) )
			{
				return key;
			}
		}
		return null;
	}

	/**
	 * Returns the rule types list
	 * 
	 * @return The rule types list
	 */
    @Override
	public ReferenceList getRuleTypesList( Locale locale )
	{
		ReferenceList listRules = new ReferenceList( );

		for( String strRuleKey : _ruleTypeKeys )
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
    @Override
	public Collection < Rule > getRuleTypes( )
	{
		return _ruleTypeKeys.stream( )
				.map( this::newInstance )
				.collect( Collectors.toList( ) );
	}
}
