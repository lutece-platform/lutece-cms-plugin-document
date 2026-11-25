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
package fr.paris.lutece.plugins.document.service.spaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A node of the tree of the document spaces displayed in the back office
 */
public class SpaceNode implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final int _nId;
    private final String _strName;
    private final String _strDescription;
    private final String _strIconUrl;
    private final List<SpaceNode> _listChildren = new ArrayList<>( );
    private final List<String> _listAllowedDocumentTypes = new ArrayList<>( );
    private boolean _bDocumentCreationAllowed;

    /**
     * Constructor
     *
     * @param nId
     *            The space identifier
     * @param strName
     *            The space name
     * @param strDescription
     *            The space description
     * @param strIconUrl
     *            The URL of the space icon
     */
    public SpaceNode( int nId, String strName, String strDescription, String strIconUrl )
    {
        _nId = nId;
        _strName = strName;
        _strDescription = strDescription;
        _strIconUrl = strIconUrl;
    }

    /**
     * Returns the space identifier
     *
     * @return The space identifier
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Returns the space name
     *
     * @return The space name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Returns the space description
     *
     * @return The space description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Returns the URL of the space icon
     *
     * @return The URL of the space icon
     */
    public String getIconUrl( )
    {
        return _strIconUrl;
    }

    /**
     * Returns the child spaces the user is allowed to see
     *
     * @return The child nodes
     */
    public List<SpaceNode> getChildren( )
    {
        return _listChildren;
    }

    /**
     * Returns the codes of the document types allowed in the space
     *
     * @return The codes of the allowed document types
     */
    public List<String> getAllowedDocumentTypes( )
    {
        return _listAllowedDocumentTypes;
    }

    /**
     * Returns whether documents can be created in the space
     *
     * @return true if documents can be created in the space
     */
    public boolean isDocumentCreationAllowed( )
    {
        return _bDocumentCreationAllowed;
    }

    /**
     * Sets whether documents can be created in the space
     *
     * @param bDocumentCreationAllowed
     *            true if documents can be created in the space
     */
    public void setDocumentCreationAllowed( boolean bDocumentCreationAllowed )
    {
        _bDocumentCreationAllowed = bDocumentCreationAllowed;
    }
}
