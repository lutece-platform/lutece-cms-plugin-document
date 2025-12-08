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
package fr.paris.lutece.plugins.document.business.category;

import java.io.Serializable;

import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;

/**
 * This class defines the Category business object.
 *
 */
public class Category implements AdminWorkgroupResource, Serializable
{
	
	private static final long serialVersionUID = 1L;
	
    // Variables declarations
    private int _nId;
    private String _strName;
    private String _strDescription;
    private byte [ ] _byteIconContent;
    private String _strIconMimeType;
    private String _strWorkgroup;

    /**
     * @return the _byteIconContent
     */
    public byte [ ] getIconContent( )
    {
        return _byteIconContent;
    }

    /**
     * @param byteIconContent
     *            the _byteIconContent to set
     */
    public void setIconContent( byte [ ] byteIconContent )
    {
        _byteIconContent = byteIconContent;
    }

    /**
     * @return the _strDescription
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * @param strDescription
     *            the _strDescription to set
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * @return the _strIconMimeType
     */
    public String getIconMimeType( )
    {
        return _strIconMimeType;
    }

    /**
     * @param strIconMimeType
     *            the _strIconMimeType to set
     */
    public void setIconMimeType( String strIconMimeType )
    {
        _strIconMimeType = strIconMimeType;
    }

    /**
     * @return the _strName
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * @param strName
     *            the _strName to set
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * @return the _nId
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param nId
     *            the _nId to set
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     *
     * @return the work group associate to the category
     */
    public String getWorkgroup( )
    {
        return _strWorkgroup;
    }

    /**
     * set the work group associate to the category
     * 
     * @param workGroup
     *            the work group associate to the category
     */
    public void setWorkgroup( String workGroup )
    {
        _strWorkgroup = workGroup;
    }
}
