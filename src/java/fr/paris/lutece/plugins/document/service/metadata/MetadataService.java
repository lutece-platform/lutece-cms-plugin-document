/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.document.service.metadata;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;

import java.util.StringTokenizer;


/**
 * Metadata Service
 */
public class MetadataService
{
    public static final String NO_HANDLER = "none";
    private static final String PROPERTY_HANDLERS_LIST = "document.metadata.handlersList";
    private static final String PREFIX_HANDLER = "document.metadata.handler.";
    private static final String SUFFIX_DESCRIPTION = ".description";
    private static final String SUFFIX_BEAN_NAME = ".beanName";
    private static final String NO_HANDLER_LABEL = "None";

    ////////////////////////////////////////////////////////////////////////////
    // Metadata handling
    /**
     * Get the list of metadata handlers
     * @return The list fo metadata handlers
     */
    public static ReferenceList getMetadataHandlersList( )
    {
        ReferenceList listHandlers = new ReferenceList( );

        listHandlers.addItem( NO_HANDLER, NO_HANDLER_LABEL );

        String strHandlersList = AppPropertiesService.getProperty( PROPERTY_HANDLERS_LIST );
        StringTokenizer st = new StringTokenizer( strHandlersList );

        while ( st.hasMoreTokens( ) )
        {
            String strHandlerKey = st.nextToken( );
            listHandlers.addItem( strHandlerKey, getDescription( strHandlerKey ) );
        }

        return listHandlers;
    }

    /**
     * Get the description of a handler
     * @param strHandlerKey The key of the handler
     * @return The description of a handler
     */
    public static String getDescription( String strHandlerKey )
    {
        return AppPropertiesService.getProperty( PREFIX_HANDLER + strHandlerKey + SUFFIX_DESCRIPTION );
    }

    /**
     * Get the bean name of a handler
     * @param strHandlerKey The key of the handler
     * @return The name of the bean of a handler
     */
    public static String getBeanName( String strHandlerKey )
    {
        return AppPropertiesService.getProperty( PREFIX_HANDLER + strHandlerKey + SUFFIX_BEAN_NAME );
    }
}
