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
package fr.paris.lutece.plugins.document.service.publishing;


/**
 * PublishingEvent
 */
public class PublishingEvent
{
    public static final int DOCUMENT_PUBLISHED = 1;
    public static final int DOCUMENT_UNPUBLISHED = 2;
    public static final int DOCUMENT_IS_COMMENT = 3;

    // Variables declarations
    private int _nDocumentId;
    private int _nPortletId;
    private int _nType;

    /**
     * Creates a new instance of PublishingEvent
     * @param nDocumentId The id of the document
     * @param nPortletId The id of the portlet
     * @param nType The type of the event
     */
    public PublishingEvent( int nDocumentId, int nPortletId, int nType )
    {
        _nDocumentId = nDocumentId;
        _nPortletId = nPortletId;
        _nType = nType;
    }

    /**
     * Returns the EventType
     * @return The EventType
     */
    public int getEventType(  )
    {
        return _nType;
    }

    /**
     * Returns the Document Id
     * @return The Document Id
     */
    public int getnDocumentId(  )
    {
        return _nDocumentId;
    }

    /**
     * Returns the Portlet Id
     * @return The Portlet Id
     */
    public int getPortletId(  )
    {
        return _nPortletId;
    }
}
