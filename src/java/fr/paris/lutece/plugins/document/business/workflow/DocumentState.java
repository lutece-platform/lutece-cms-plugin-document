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
package fr.paris.lutece.plugins.document.business.workflow;

import fr.paris.lutece.portal.service.i18n.I18nService;

import java.util.Locale;


/**
 *
 */
public class DocumentState
{
    // Variables declarations	
    public static final int STATE_WRITING = 1;
    public static final int STATE_WAITING_FOR_APPROVAL = 2;
    public static final int STATE_VALIDATE = 3;
    public static final int STATE_REJECTED = 4;
    public static final int STATE_ARCHIVED = 5;
    public static final int STATE_IN_CHANGE = 6;
    public static final int STATE_WAITING_FOR_CHANGE_APPROVAL = 7;
    private int _nIdState;
    private Locale _locale;
    private String _strNameKey;
    private String _strDescriptionKey;

    /**
     * Returns the IdState
     * 
     * @return The IdState
     */
    public int getId( )
    {
        return _nIdState;
    }

    /**
     * Sets the IdState
     * 
     * @param nIdState The IdState
     */
    public void setId( int nIdState )
    {
        _nIdState = nIdState;
    }

    /**
     * Sets the Locale
     * 
     * @param locale the locale
     */
    public void setLocale( Locale locale )
    {
        _locale = locale;
    }

    /**
     * Returns the NameKey
     * 
     * @return The NameKey
     */
    public String getNameKey( )
    {
        return _strNameKey;
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
    public String getDescriptionKey( )
    {
        return _strDescriptionKey;
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
     * Returns the Name
     * 
     * @return The Name
     */
    public String getName( )
    {
        return I18nService.getLocalizedString( _strNameKey, _locale );
    }
}
