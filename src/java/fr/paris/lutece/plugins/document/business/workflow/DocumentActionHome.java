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

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...) for DocumentAction objects
 */
public final class DocumentActionHome
{
    // Static variable pointed at the DAO instance
    private static IDocumentActionDAO _dao = SpringContextService.getBean( "document.documentActionDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DocumentActionHome(  )
    {
    }

    /**
     * Load the  Document Action
     * @param nIdAction The identifier of the action
     * @return the instance of action
     */
    public static DocumentAction findByPrimaryKey( int nIdAction )
    {
        return _dao.load( nIdAction );
    }

    /**
     * Returns the list of allowed actions for the current document
     * @param document The document
     * @param locale The locale
     * @return A list of actions for the current document
     */
    public static List<DocumentAction> getActionsList( Document document, Locale locale )
    {
        List<DocumentAction> listActions = _dao.selectActions( document );

        return I18nService.localizeCollection( listActions, locale );
    }
}
