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
package fr.paris.lutece.plugins.document.business.rules;

import fr.paris.lutece.portal.business.user.AdminUser;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Default implementation of a rule
 */
public abstract class AbstractRule implements Rule
{
    private static final String PARAMETER_RULE_TYPE_ID = "id_rule_type";

    // Variables declarations
    private int _nIdRule;
    private String _strIdRuleType;
    private Map<String, String> _mapAttributes = new HashMap<String, String>( );
    private Locale _locale;
    private AdminUser _user;

    /**
     * Returns the IdRule
     *
     * @return The IdRule
     */
    public int getId( )
    {
        return _nIdRule;
    }

    /**
     * Sets the IdRule
     *
     * @param nIdRule
     *            The IdRule
     */
    public void setId( int nIdRule )
    {
        _nIdRule = nIdRule;
    }

    /**
     * Returns the IdRuleType
     *
     * @return The IdRuleType
     */
    public String getRuleTypeId( )
    {
        return _strIdRuleType;
    }

    /**
     * Sets the IdRuleType
     *
     * @param strIdRuleType
     *            The IdRuleType
     */
    public void setRuleTypeId( String strIdRuleType )
    {
        _strIdRuleType = strIdRuleType;
    }

    /**
     * Sets the locale
     *
     * @param locale
     *            The locale
     */
    public void setLocale( Locale locale )
    {
        _locale = locale;
    }

    /**
     * Get the locale
     * 
     * @return The locale
     */
    public Locale getLocale( )
    {
        return _locale;
    }

    /**
     * Set an attribute
     * 
     * @param strAttributeName
     *            the name of the attribute
     * @param strAttributeValue
     *            the value of the attribute
     */
    public void setAttribute( String strAttributeName, String strAttributeValue )
    {
        _mapAttributes.put( strAttributeName, strAttributeValue );
    }

    /**
     * Get the value of an attribute from its name
     * 
     * @param strAttributeNamethe
     *            name of the attribute
     * @return the value of the attribute
     */
    public String getAttribute( String strAttributeName )
    {
        return _mapAttributes.get( strAttributeName );
    }

    /**
     * Read attributes from a request
     * 
     * @param request
     *            The request
     */
    public void readAttributes( HttpServletRequest request )
    {
        String strRuleTypeId = request.getParameter( PARAMETER_RULE_TYPE_ID );
        setRuleTypeId( strRuleTypeId );

        String [ ] attributes = getAttributesList( );

        for ( int i = 0; i < attributes.length; i++ )
        {
            String strAttributeValue = request.getParameter( attributes [i] );

            if ( strAttributeValue != null )
            {
                setAttribute( attributes [i], strAttributeValue );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals( Object obj );

    /**
     * Gets the current user
     * 
     * @return The the current user
     */
    public AdminUser getUser( )
    {
        return _user;
    }

    /**
     * set the current user
     * 
     * @param user
     *            the current user
     *
     */
    public void setUser( AdminUser user )
    {
        _user = user;
    }
}
