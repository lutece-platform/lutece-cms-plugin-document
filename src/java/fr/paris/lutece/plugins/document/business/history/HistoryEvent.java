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
package fr.paris.lutece.plugins.document.business.history;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.i18n.Localizable;

import java.util.Locale;

/**
 * This class represents the business object HistoryEvent
 */
public class HistoryEvent implements Localizable
{
    private static final String USER_UNKNOWN = "unknown user";

    // Variables declarations
    private int _nIdDocument;
    private java.sql.Timestamp _eventDate;
    private String _strEventUser;
    private String _strEventMessageKey;
    private String _strDocumentStateKey;
    private String _strSpace;
    private Locale _locale;

    /**
     * Returns the IdDocument
     *
     * @return The IdDocument
     */
    public int getIdDocument( )
    {
        return _nIdDocument;
    }

    /**
     * Sets the IdDocument
     *
     * @param nIdDocument
     *            The IdDocument
     */
    public void setIdDocument( int nIdDocument )
    {
        _nIdDocument = nIdDocument;
    }

    /**
     * Returns the Space
     *
     * @return The Space
     */
    public String getSpace( )
    {
        return _strSpace;
    }

    /**
     * Sets the Space
     *
     * @param strSpace
     *            The Space
     */
    public void setSpace( String strSpace )
    {
        _strSpace = strSpace;
    }

    /**
     * Returns the EventDate
     *
     * @return The EventDate
     */
    public java.sql.Timestamp getDate( )
    {
        return _eventDate;
    }

    /**
     * Sets the EventDate
     *
     * @param eventDate
     *            The EventDate
     */
    public void setDate( java.sql.Timestamp eventDate )
    {
        _eventDate = eventDate;
    }

    /**
     * Returns the EventUser
     *
     * @return The EventUser
     */
    public String getEventUser( )
    {
        return _strEventUser;
    }

    /**
     * Sets the EventUser
     *
     * @param strEventUser
     *            The EventUser
     */
    public void setEventUser( String strEventUser )
    {
        _strEventUser = strEventUser;
    }

    /**
     * Returns the EventMessageKey
     *
     * @return The EventMessageKey
     */
    public String getEventMessageKey( )
    {
        return _strEventMessageKey;
    }

    /**
     * Sets the EventMessageKey
     *
     * @param strEventMessageKey
     *            The EventMessageKey
     */
    public void setEventMessageKey( String strEventMessageKey )
    {
        _strEventMessageKey = strEventMessageKey;
    }

    /**
     * Returns the EventMessageKey
     *
     * @return The EventMessageKey
     */
    public String getDescription( )
    {
        String strUser = USER_UNKNOWN;
        AdminUser user = AdminUserHome.findUserByLogin( _strEventUser );

        if ( user != null )
        {
            strUser = user.getFirstName( ) + " " + user.getLastName( );
        }

        String [ ] args = {
                strUser, I18nService.getLocalizedString( _strDocumentStateKey, _locale ), _strSpace
        };

        return I18nService.getLocalizedString( _strEventMessageKey, args, _locale );
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
     * Returns the DocumentStateKey
     *
     * @return The DocumentStateKey
     */
    public String getDocumentStateKey( )
    {
        return _strDocumentStateKey;
    }

    /**
     * Sets the DocumentStateKey
     *
     * @param strDocumentStateKey
     *            The DocumentStateKey
     */
    public void setDocumentStateKey( String strDocumentStateKey )
    {
        _strDocumentStateKey = strDocumentStateKey;
    }
}
