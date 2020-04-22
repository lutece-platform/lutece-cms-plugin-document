/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.document.utils;

import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.event.ResourceEventManager;


/**
 *
 * DocumentIndexerUtils
 *
 */
public final class DocumentIndexerUtils
{
    // Indexed resource type name
    public static final String CONSTANT_TYPE_RESOURCE = "DOCUMENT_DOCUMENT";

    /**
     * Private constructor
     */
    private DocumentIndexerUtils(  )
    {
    }

    /**
     * Warn that a action has been done.
     * @param strIdResource the document id
     * @param nIdTask the key of the action to do
     * @param nIdPortlet the portlet key of the document
     */
    public static void addIndexerAction( String strIdResource, int nIdTask, int nIdPortlet )
    {
        ResourceEvent event = new ResourceEvent(  );
        event.setIdResource( String.valueOf( strIdResource ) );
        event.setTypeResource( CONSTANT_TYPE_RESOURCE );
        event.setIdPortlet( nIdPortlet );

        switch ( nIdTask )
        {
            case IndexerAction.TASK_CREATE:
                ResourceEventManager.fireAddedResource( event );

                break;

            case IndexerAction.TASK_MODIFY:
                ResourceEventManager.fireUpdatedResource( event );

                break;

            case IndexerAction.TASK_DELETE:
                ResourceEventManager.fireDeletedResource( event );

                break;

            default:
                break;
        }
    }
}
