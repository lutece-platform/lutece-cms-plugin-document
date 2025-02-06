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
package fr.paris.lutece.plugins.document.business.spaces;

import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;

import java.util.ArrayList;

/**
 * A Space to store documents
 */
public class DocumentSpace implements RBACResource, AdminWorkgroupResource
{
    public static final String RESOURCE_TYPE = "DOCUMENT_SPACE";

    // Variables declarations
    private int _nIdSpace;
    private int _nIdParent;
    private String _strName;
    private String _strDescription;
    private String _strViewType;
    private int _nIdIcon;
    private String _strIconUrl;
    private ArrayList<String> _listDocumentType = new ArrayList<String>( );
    private boolean _bDocumentCreationAllowed;
    private String _strWorkgroup;

    /**
     * Returns the IdSpace
     *
     * @return The IdSpace
     */
    public int getId( )
    {
        return _nIdSpace;
    }

    /**
     * Sets the IdSpace
     *
     * @param nIdSpace
     *            The IdSpace
     */
    public void setId( int nIdSpace )
    {
        _nIdSpace = nIdSpace;
    }

    /**
     * Returns the IdParent
     *
     * @return The IdParent
     */
    public int getIdParent( )
    {
        return _nIdParent;
    }

    /**
     * Sets the IdParent
     *
     * @param nIdParent
     *            The IdParent
     */
    public void setIdParent( int nIdParent )
    {
        _nIdParent = nIdParent;
    }

    /**
     * Returns the Name
     *
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     *
     * @param strName
     *            The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the Description
     *
     * @return The Description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     *
     * @param strDescription
     *            The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the ViewType
     *
     * @return The ViewType
     */
    public String getViewType( )
    {
        return _strViewType;
    }

    /**
     * Sets the ViewType
     *
     * @param strViewType
     *            The ViewType
     */
    public void setViewType( String strViewType )
    {
        _strViewType = strViewType;
    }

    /**
     * Returns the IdIcon
     *
     * @return The IdIcon
     */
    public int getIdIcon( )
    {
        return _nIdIcon;
    }

    /**
     * Sets the IdIcon
     *
     * @param nIdIcon
     *            The IdIcon
     */
    public void setIdIcon( int nIdIcon )
    {
        _nIdIcon = nIdIcon;
    }

    /**
     * Returns the IconUrl
     *
     * @return The IconUrl
     */
    public String getIconUrl( )
    {
        return _strIconUrl;
    }

    /**
     * Sets the IconUrl
     *
     * @param strIconUrl
     *            The IconUrl
     */
    public void setIconUrl( String strIconUrl )
    {
        _strIconUrl = strIconUrl;
    }

    /**
     * Returns whether or not Document Creation is Allowed for this space
     *
     * @return True if Document Creation is Allowed
     */
    public boolean isDocumentCreationAllowed( )
    {
        return _bDocumentCreationAllowed;
    }

    /**
     * Sets whether or not Document Creation is Allowed for this space
     *
     * @param bDocumentCreationAllowed
     *            The DocumentCreationAllowed value
     */
    public void setDocumentCreationAllowed( boolean bDocumentCreationAllowed )
    {
        _bDocumentCreationAllowed = bDocumentCreationAllowed;
    }

    /**
     * Add allowed document
     * 
     * @param strDocumentType
     *            The document type to allow
     */
    public void addAllowedDocumentType( String strDocumentType )
    {
        _listDocumentType.add( strDocumentType );
    }

    /**
     * Reset allowed document types list
     */
    public void resetAllowedDocumentTypesList( )
    {
        _listDocumentType.clear( );
    }

    /**
     * Returns allowed document types
     * 
     * @return Allowed document types
     */
    public String [ ] getAllowedDocumentTypes( )
    {
        String [ ] checkedItems = new String [ _listDocumentType.size( )];

        int nIndex = 0;

        for ( String strType : _listDocumentType )
        {
            checkedItems [nIndex] = strType;
            nIndex++;
        }

        return checkedItems;
    }

    ////////////////////////////////////////////////////////////////////////////
    // RBAC Resource implementation

    /**
     * Returns the Resource Type Code that identify the resource type
     * 
     * @return The Resource Type Code
     */
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * Returns the resource Id of the current object
     * 
     * @return The resource Id of the current object
     */
    public String getResourceId( )
    {
        return "" + getId( );
    }

    /**
     *
     * @return the work group associate to the DocumentSpace
     */
    public String getWorkgroup( )
    {
        return _strWorkgroup;
    }

    /**
     * set the work group associate to the DocumentSpace
     * 
     * @param workGroup
     *            the work group associate to the DocumentSpace
     */
    public void setWorkgroup( String workGroup )
    {
        _strWorkgroup = workGroup;
    }
}
