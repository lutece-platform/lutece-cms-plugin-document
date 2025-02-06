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

import java.util.List;

/**
 * Rule DAO Interface
 */
public interface IRuleDAO
{
    /**
     * Delete a record from the table
     *
     * @param nRuleId
     *            The Rule Id
     */
    void delete( int nRuleId );

    /**
     * Insert a new record in the table.
     *
     *
     * @param rule
     *            The rule object
     */
    void insert( Rule rule );

    /**
     * Load the data of Rule from the table
     *
     * @param nRuleId
     *            The identifier of Rule
     * @param ruleTypesSet
     *            The rule type set
     * @return the instance of the Rule
     */
    Rule load( int nRuleId, IRuleTypesSet ruleTypesSet );

    /**
     * Load the list of rules
     * 
     * @param ruleTypesSet
     *            The rule type set
     * @return The Collection of the Rules
     */
    List<Rule> selectRuleList( IRuleTypesSet ruleTypesSet );

    /**
     * Load the list of rules specified by rule type key
     * 
     * @param strRuleTypeKey
     *            The rule type key
     * @param ruleTypesSet
     *            The rule types set
     * @return The Collection of the Rules
     */
    List<Rule> selectRuleListByRuleTypeKey( String strRuleTypeKey, IRuleTypesSet ruleTypesSet );

    /**
     * Update the record in the table
     *
     * @param rule
     *            The reference of rule
     */
    void store( Rule rule );
}
