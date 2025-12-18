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
package fr.paris.lutece.plugins.document.business.attributes;

import fr.paris.lutece.util.ReferenceItem;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * MapProvider
 *
 */
public class MapProvider implements IMapProvider, Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;
    private String _strKey;
    private String _strDisplayedName;
    private String _strHtmlCode;
    private String _strFrontHtmlCode;
    private String _strFrontListHtmlCode;
    private String _strBackListHtmlCode;
    private boolean _bMapListSupported;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey( )
    {
        return _strKey;
    }

    /**
     * Sets the key
     * 
     * @param key
     *            the key
     */
    public void setKey( String key )
    {
        this._strKey = key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayedName( )
    {
        return _strDisplayedName;
    }

    /**
     * Set the displayed name
     * 
     * @param displayedName
     *            the displayed name
     */
    public void setDisplayedName( String displayedName )
    {
        this._strDisplayedName = displayedName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlCode( )
    {
        return _strHtmlCode;
    }

    /**
     * Sets the html code
     * 
     * @param htmlCode
     *            the html code
     */
    public void setHtmlCode( String htmlCode )
    {
        this._strHtmlCode = htmlCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceItem toRefItem( )
    {
        ReferenceItem refItem = new ReferenceItem( );

        refItem.setCode( getKey( ) );
        refItem.setName( getDisplayedName( ) );

        return refItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFrontHtmlCode( )
    {
        return _strFrontHtmlCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFrontListHtmlCode( )
    {
        return _strFrontListHtmlCode;
    }

    /**
     * Sets the front list html code
     * 
     * @param strFrontListHtmlCode
     *            the front list template
     */
    public void setFrontListHtmlCode( String strFrontListHtmlCode )
    {
        _strFrontListHtmlCode = strFrontListHtmlCode;
    }

    /**
     * Sets the front html
     * 
     * @param strFrontHtmlCode
     *            the front template
     */
    public void setFrontHtmlCode( String strFrontHtmlCode )
    {
        _strFrontHtmlCode = strFrontHtmlCode;
    }

    /**
     * Set back list html code
     * 
     * @param strBackListHtmlCode
     *            the back list template
     */
    public void setBackListHtmlCode( String strBackListHtmlCode )
    {
        _strBackListHtmlCode = strBackListHtmlCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBackListHtmlCode( )
    {
        return _strBackListHtmlCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMapListSupported( )
    {
        return _bMapListSupported;
    }

    /**
     * Set to <code>true</code> if map list is supported, <code>false</code> otherwise
     * 
     * @param bMapListSupported
     *            the new value
     */
    public void setMapListSupported( boolean bMapListSupported )
    {
        _bMapListSupported = bMapListSupported;
    }

    @Override
    public Object getParameter( int nKey )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
