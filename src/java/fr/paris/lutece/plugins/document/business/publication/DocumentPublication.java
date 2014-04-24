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
package fr.paris.lutece.plugins.document.business.publication;

import java.util.Date;


/**
 *
 * This class represents business objects DocumentPublication Portlet
 *
 */
public class DocumentPublication
{
    /////////////////////////////////////////////////////////////////////////////////
    // Public constants
    public static final int STATUS_PUBLISHED = 0;
    public static final int STATUS_UNPUBLISHED = 1;
    public static final int DOCUMENT_ORDER_DEFAULT_VALUE = 0; //When document is not published, set document order with this constant
    private int _nPortletId;
    private int _nDocumentId;
    private int _nDocumentOrder;
    private int _nStatus;
    private Date _datePublishing;

    /**
     * @return the _nDocumentId
     */
    public int getDocumentId(  )
    {
        return _nDocumentId;
    }

    /**
     * @param nDocumentId the _nDocumentId to set
     */
    public void setDocumentId( int nDocumentId )
    {
        _nDocumentId = nDocumentId;
    }

    /**
     * @return the _nDocumentOrder
     */
    public int getDocumentOrder(  )
    {
        return _nDocumentOrder;
    }

    /**
     * @param nDocumentOrder the _nDocumentOrder to set
     */
    public void setDocumentOrder( int nDocumentOrder )
    {
        _nDocumentOrder = nDocumentOrder;
    }

    /**
     * @return the _nPortletId
     */
    public int getPortletId(  )
    {
        return _nPortletId;
    }

    /**
     * @param nPortletId the _nPortletId to set
     */
    public void setPortletId( int nPortletId )
    {
        _nPortletId = nPortletId;
    }

    /**
     * @return the _nStatus
     */
    public int getStatus(  )
    {
        return _nStatus;
    }

    /**
     * @param nStatus the _nStatus to set
     */
    public void setStatus( int nStatus )
    {
        _nStatus = nStatus;
    }

    /**
     * @return the _datePublishing
     */
    public Date getDatePublishing(  )
    {
        return _datePublishing;
    }

    /**
     * @param datePublishing the _datePublishing to set
     */
    public void setDatePublishing( Date datePublishing )
    {
        _datePublishing = datePublishing;
    }
}
