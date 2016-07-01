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
package fr.paris.lutece.plugins.document.business.spaces;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.i18n.Localizable;
import fr.paris.lutece.portal.service.rbac.RBACAction;

import java.util.Locale;


/**
 * Action available on a space
 */
public class SpaceAction implements Localizable, RBACAction
{
    // Variables declarations
    private String _strUrl;
    private String _strNameKey;
    private String _strDescriptionKey;
    private Locale _locale;
    private String _strIconUrl;
    private String _strPermission;

    /**
     * Implements Localizable
     * @param locale The current locale
     */
    public void setLocale( Locale locale )
    {
        _locale = locale;
    }

    /**
     * Returns the Url
     *
     * @return The Url
     */
    public String getUrl(  )
    {
        return _strUrl;
    }

    /**
     * Sets the Url
     *
     * @param strUrl The Url
     */
    public void setUrl( String strUrl )
    {
        _strUrl = strUrl;
    }

    /**
     * Returns the NameKey
     *
     * @return The NameKey
     */
    public String getNameKey(  )
    {
        return _strNameKey;
    }

    /**
     * Returns the Name
     *
     * @return The Name
     */
    public String getName(  )
    {
        return I18nService.getLocalizedString( _strNameKey, _locale );
    }

    /**
     * Sets the NameKey
     *
     * @param strNameKey The NameKey
     */
    public void setNameKey( String strNameKey )
    {
        _strNameKey = strNameKey;
    }

    /**
     * Returns the DescriptionKey
     *
     * @return The DescriptionKey
     */
    public String getDescriptionKey(  )
    {
        return _strDescriptionKey;
    }

    /**
     * Returns the Description
     *
     * @return The Description
     */
    public String getDescription(  )
    {
        return I18nService.getLocalizedString( _strDescriptionKey, _locale );
    }

    /**
     * Sets the DescriptionKey
     *
     * @param strDescriptionKey The DescriptionKey
     */
    public void setDescriptionKey( String strDescriptionKey )
    {
        _strDescriptionKey = strDescriptionKey;
    }

    /**
     * Returns the IconUrl
     *
     * @return The IconUrl
     */
    public String getIconUrl(  )
    {
        return _strIconUrl;
    }

    /**
     * Sets the IconUrl
     *
     * @param strIconUrl The IconUrl
     */
    public void setIconUrl( String strIconUrl )
    {
        _strIconUrl = strIconUrl;
    }

    /**
     * Returns the permission associated to the action
     * @return The permission
     */
    public String getPermission(  )
    {
        return _strPermission;
    }

    /**
     * Sets the Permission
     *
     * @param strPermission The Permission
     */
    public void setPermission( String strPermission )
    {
        _strPermission = strPermission;
    }
}
