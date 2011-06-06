/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.document.service;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.portal.business.user.AdminUser;


/**
 * DocumentEvent are sent by DocumentService to notify documents changes to listeners.
 */
public class DocumentEvent
{
    public static final int DOCUMENT_CREATED = 1;
    public static final int DOCUMENT_CONTENT_MODIFIED = 2;
    public static final int DOCUMENT_STATE_CHANGED = 3;
    public static final int DOCUMENT_MOVED = 4;

    // Variables declarations
    private Document _document;
    private AdminUser _user;
    private int _nType;
    private int _nSpaceId;
    private int _nStateId;

    /** Creates a new instance of DocumentEvent */
    public DocumentEvent( Document document, AdminUser user, int nType )
    {
        _document = document;
        _user = user;
        _nType = nType;
        _nSpaceId = _document.getSpaceId(  );
        _nStateId = _document.getStateId(  );
    }

    /**
     * Returns the EventType
     *
     * @return The EventType
     */
    public int getEventType(  )
    {
        return _nType;
    }

    /**
     * Returns the Document
     *
     * @return The Document
     */
    public Document getDocument(  )
    {
        return _document;
    }

    /**
     * Returns the User
     *
     * @return The User
     */
    public AdminUser getUser(  )
    {
        return _user;
    }

    /**
      * Returns the space id of the document before any rule has been applied
      *
      * @return the space id
      */
    public int getSpaceId(  )
    {
        return _nSpaceId;
    }

    /**
      * Returns the state id of the document before any rule has been applied
      *
      * @return the state id
      */
    public int getStateId(  )
    {
        return _nStateId;
    }
}
