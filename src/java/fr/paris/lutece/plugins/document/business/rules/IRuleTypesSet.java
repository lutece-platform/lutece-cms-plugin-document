/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;


/**
 * Rule Types Sets Interface
 */
public interface IRuleTypesSet
{
    /**
     * Sets the rule types map
     * @param mapRules The rule types map
     */
    void setRuleTypesMap( Map<String, Rule> mapRules );

    /**
     * Returns the rule types list
     * @param locale the locale
     * @return The rule types list
     */
    ReferenceList getRuleTypesList( Locale locale );

    /**
     * Returns the rule type of a given class type
     * @param rule the rule
     * @return The rule type
     */
    String getRuleTypeKey( Rule rule );

    /**
     * Create a new instance of a rule of a given type
     * @param strRuleTypeKey The key name of the rule type
     * @return A new Rule instance
     */
    Rule newInstance( String strRuleTypeKey );

    /**
     * Returns all rule types
     * @return A collection of rule types
     */
    Collection<Rule> getRuleTypes(  );
}
