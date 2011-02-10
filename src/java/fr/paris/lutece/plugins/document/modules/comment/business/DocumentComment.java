/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.document.modules.comment.business;

import fr.paris.lutece.portal.service.rbac.RBACResource;

import java.sql.Timestamp;


/**
 * This class defines the Document Comment business object.
 */
public class DocumentComment implements RBACResource
{
    public static final String RESOURCE_TYPE = "DOCUMENT_COMMENT";
    private int _nCommentId;
    private int _nDocumentId;
    private Timestamp _dateComment;
    private String _strName;
    private String _strEmail;
    private String _strIpAddress;
    private String _strComment;
    private int _nStatus;

    /**
     * Returns the identifier of the Document
     * @return The identifier of the Document
     */
    public int getDocumentId(  )
    {
        return _nDocumentId;
    }

    /**
     * Sets the identifier of the Document
     * @param nDocumentId the new identifier
     */
    public void setDocumentId( int nDocumentId )
    {
        _nDocumentId = nDocumentId;
    }

    /**
     * Returns the date of the comment
     * @return the date of the comment
     */
    public Timestamp getDateComment(  )
    {
        return _dateComment;
    }

    /**
     * Sets the date of the comment
     * @param dateComment the new date
     */
    public void setDateComment( Timestamp dateComment )
    {
        _dateComment = dateComment;
    }

    /**
    * Returns the identifier of the object
     * @return The identifier of the object
     */
    public int getCommentId(  )
    {
        return _nCommentId;
    }

    /**
     * Sets the identifier of the object
     * @param nCommentId the new identifier
     */
    public void setCommentId( int nCommentId )
    {
        _nCommentId = nCommentId;
    }

    /**
     * Returns the status of the comment
     * @return The status of the comment (0 = unpublished, 1 = published)
     */
    public int getStatus(  )
    {
        return _nStatus;
    }

    /**
     * Sets the status of the comment
     * @param nStatus the new status (0 = unpublished, 1 = published)
     */
    public void setStatus( int nStatus )
    {
        _nStatus = nStatus;
    }

    /**
     * Returns the comment
     * @return the comment
     */
    public String getComment(  )
    {
        return _strComment;
    }

    /**
     * Sets the comment
     * @param strComment the new comment
     */
    public void setComment( String strComment )
    {
        _strComment = strComment;
    }

    /**
     * Returns the email of the user
     * @return the email of the user
     */
    public String getEmail(  )
    {
        return _strEmail;
    }

    /**
     * Sets the email of the user
     * @param strEmail the new email
     */
    public void setEmail( String strEmail )
    {
        _strEmail = strEmail;
    }

    /**
     * Returns the IP address of the user
     * @return the IP address of the user
     */
    public String getIpAddress(  )
    {
        return _strIpAddress;
    }

    /**
     * Sets the IP address of the user
     * @param strIpAddress the new IP address
     */
    public void setIpAddress( String strIpAddress )
    {
        _strIpAddress = strIpAddress;
    }

    /**
     * Returns the name of the user
     * @return the name of the user
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * Sets the name of the user
     * @param strName the new name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    ////////////////////////////////////////////////////////////////////////////
    // RBAC Resource implementation

    /**
     * Returns the Resource Type Code that identify the resource type
     * @return The Resource Type Code
     */
    public String getResourceTypeCode(  )
    {
        return RESOURCE_TYPE;
    }

    /**
     * Returns the resource Id of the current object
     * @return The resource Id of the current object
     */
    public String getResourceId(  )
    {
        return "" + getDocumentId(  );
    }

    /**
     * Creates a String representation of the object
     * @return the String representation
     */
    public String toString(  )
    {
        StringBuffer buffer = new StringBuffer(  );
        buffer.append( "DocumentComment[" );
        buffer.append( "_nCommentId = " ).append( _nCommentId );
        buffer.append( ", _nDocumentId = " ).append( _nDocumentId );
        buffer.append( ", _dateComment = " ).append( _dateComment );
        buffer.append( ", _strName = " ).append( _strName );
        buffer.append( ", _strEmail = " ).append( _strEmail );
        buffer.append( ", _strIpAddress = " ).append( _strIpAddress );
        buffer.append( ", _strComment = " ).append( _strComment );
        buffer.append( ", _nStatus = " ).append( _nStatus );
        buffer.append( "]" );

        return buffer.toString(  );
    }
}
