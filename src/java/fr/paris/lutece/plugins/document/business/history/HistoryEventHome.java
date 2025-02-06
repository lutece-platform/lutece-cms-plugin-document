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

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * This class provides instances management methods (create, find, ...) for HistoryEvent objects
 */
public final class HistoryEventHome
{
    // Static variable pointed at the DAO instance
    private static IHistoryEventDAO _dao = SpringContextService.getBean( "document.historyEventDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private HistoryEventHome( )
    {
    }

    /**
     * Creation of an instance of historyEvent
     *
     * @param historyEvent
     *            The instance of the historyEvent which contains the informations to store
     * @return The instance of historyEvent which has been created with its primary key.
     */
    public static HistoryEvent create( HistoryEvent historyEvent )
    {
        _dao.insert( historyEvent );

        return historyEvent;
    }

    /**
     * Remove the HistoryEvent whose identifier is specified in parameter
     *
     * @param nDocumentId
     *            The document Id
     */
    public static void remove( int nDocumentId )
    {
        _dao.delete( nDocumentId );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns a collection of historyEvents objects
     *
     * @return A collection of historyEvents
     * @param nDocumentId
     *            The document Id
     * @param locale
     *            The current locale
     */
    public static List<HistoryEvent> findByDocument( int nDocumentId, Locale locale )
    {
        List<HistoryEvent> listEvents = _dao.selectEventListByDocument( nDocumentId );

        return I18nService.localizeCollection( listEvents, locale );
    }

    /**
     * Returns a collection of historyEvents objects
     *
     * @return A collection of historyEvents
     * @param strUser
     *            The user Id
     * @param locale
     *            The current locale
     */
    public static Collection<HistoryEvent> findByUser( String strUser, Locale locale )
    {
        Collection<HistoryEvent> listEvents = _dao.selectEventListByUser( strUser );

        return I18nService.localizeCollection( listEvents, locale );
    }
}
