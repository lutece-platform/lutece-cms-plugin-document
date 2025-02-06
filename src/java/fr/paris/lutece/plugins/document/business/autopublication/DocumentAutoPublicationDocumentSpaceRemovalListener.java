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
package fr.paris.lutece.plugins.document.business.autopublication;

import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.RemovalListener;

import java.util.Collection;
import java.util.Locale;

/**
 * DocumentAutoPublicationDocumentSpaceRemovalListener
 */
public class DocumentAutoPublicationDocumentSpaceRemovalListener implements RemovalListener
{
    private static final String PROPERTY_DOCUMENT_SPACE_CANNOT_BE_REMOVED = "document.message.autoPublication.documentSpaceCannotBeRemoved";

    /**
     * Check if the object can be safely removed
     * 
     * @param strId
     *            The object id
     * @return true if the object can be removed otherwise false
     */
    public boolean canBeRemoved( String strId )
    {
        if ( IntegerUtils.isNotNumeric( strId ) )
        {
            return true;
        }

        int nId = IntegerUtils.convert( strId );

        Collection<DocumentAutoPublication> listDocumentAutoPublication = DocumentAutoPublicationHome.findAll( );

        for ( DocumentAutoPublication documentAutoPublication : listDocumentAutoPublication )
        {
            if ( documentAutoPublication.getIdSpace( ) == nId )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Gives a message explaining why the object can't be removed
     * 
     * @param strId
     *            The object id
     * @param locale
     *            The current locale
     * @return The message
     */
    public String getRemovalRefusedMessage( String strId, Locale locale )
    {
        // Build a message
        return I18nService.getLocalizedString( PROPERTY_DOCUMENT_SPACE_CANNOT_BE_REMOVED, locale );
    }
}
