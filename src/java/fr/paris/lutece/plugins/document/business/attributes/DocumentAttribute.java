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
package fr.paris.lutece.plugins.document.business.attributes;

import java.util.List;


/**
 * This class represents the business object DocumentAttribute
 */
public class DocumentAttribute
{
    // Variables declarations
    private int _nIdDocumentAttribute;
    private String _strCodeDocumentType;
    private String _strCodeAttributeType;
    private String _strCode;
    private String _strName;
    private String _strDescription;
    private int _nAttributeOrder;
    private boolean _bRequired;
    private boolean _bSearchable;
    private List<AttributeTypeParameter> _listParameters;
    private String _strTextValue;
    private byte[] _bytes;
    private boolean _bBinary;
    private String _strValueContentType;

    /**
     * Returns the IdDocumentAttribute
     *
     * @return The IdDocumentAttribute
     */
    public int getId(  )
    {
        return _nIdDocumentAttribute;
    }

    /**
     * Sets the IdDocumentAttribute
     *
     * @param nIdDocumentAttribute The IdDocumentAttribute
     */
    public void setId( int nIdDocumentAttribute )
    {
        _nIdDocumentAttribute = nIdDocumentAttribute;
    }

    /**
     * Returns the CodeDocumentType
     *
     * @return The CodeDocumentType
     */
    public String getCodeDocumentType(  )
    {
        return _strCodeDocumentType;
    }

    /**
     * Sets the CodeDocumentType
     *
     * @param strCodeDocumentType The CodeDocumentType
     */
    public void setCodeDocumentType( String strCodeDocumentType )
    {
        _strCodeDocumentType = strCodeDocumentType;
    }

    /**
     * Returns the CodeAttributeType
     *
     * @return The CodeAttributeType
     */
    public String getCodeAttributeType(  )
    {
        return _strCodeAttributeType;
    }

    /**
     * Sets the CodeAttributeType
     *
     * @param strCodeAttributeType The CodeAttributeType
     */
    public void setCodeAttributeType( String strCodeAttributeType )
    {
        _strCodeAttributeType = strCodeAttributeType;
    }

    /**
     * Returns the Code
     *
     * @return The Code
     */
    public String getCode(  )
    {
        return _strCode;
    }

    /**
     * Sets the Code
     *
     * @param strCode The Code
     */
    public void setCode( String strCode )
    {
        _strCode = strCode;
    }

    /**
     * Returns the Name
     *
     * @return The Name
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * Sets the Name
     *
     * @param strName The Name
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
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     *
     * @param strDescription The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the AttributeOrder
     *
     * @return The AttributeOrder
     */
    public int getAttributeOrder(  )
    {
        return _nAttributeOrder;
    }

    /**
     * Sets the AttributeOrder
     *
     * @param nAttributeOrder The AttributeOrder
     */
    public void setAttributeOrder( int nAttributeOrder )
    {
        _nAttributeOrder = nAttributeOrder;
    }

    /**
     * Returns the Required
     *
     * @return The Required
     */
    public boolean isRequired(  )
    {
        return _bRequired;
    }

    /**
     * Sets if Required
     * @param bRequired The required value
     */
    public void setRequired( boolean bRequired )
    {
        _bRequired = bRequired;
    }

    /**
     * Returns the Searchable
     *
     * @return The Searchable
     */
    public boolean isSearchable(  )
    {
        return _bSearchable;
    }

    /**
     * Sets the Searchable
     * @param bSearchable The searchable value
     */
    public void setSearchable( boolean bSearchable )
    {
        _bSearchable = bSearchable;
    }

    /**
     * Returns the Binary
     *
     * @return The Binary
     */
    public boolean isBinary(  )
    {
        return _bBinary;
    }

    /**
     * Sets the Binary
     * @param bBinary The Binary value
     */
    public void setBinary( boolean bBinary )
    {
        _bBinary = bBinary;
    }

    /**
     * Sets parameters list
     * @param listParameters The parameters list
     */
    public void setParameters( List<AttributeTypeParameter> listParameters )
    {
        _listParameters = listParameters;
    }

    /**
     * Gets parameters list
     * @return The parameters list
     */
    public List<AttributeTypeParameter> getParameters(  )
    {
        return _listParameters;
    }

    /**
     * Sets the value
     * @param strTextValue The value
     */
    public void setTextValue( String strTextValue )
    {
        _strTextValue = strTextValue;
    }

    /**
     * Gets the value
     * @return The value
     */
    public String getTextValue(  )
    {
        return _strTextValue;
    }

    /**
     * Sets the value
     * @param bytes The value
     */
    public void setBinaryValue( byte[] bytes )
    {
        _bytes = bytes;
    }

    /**
     * Gets the value
     * @return The value
     */
    public byte[] getBinaryValue(  )
    {
        return _bytes;
    }

    /**
     * Sets the content type value
     * @param strValueContentType The content type value
     */
    public void setValueContentType( String strValueContentType )
    {
        _strValueContentType = strValueContentType;
    }

    /**
     * Gets the content type value
     * @return The content type value
     */
    public String getValueContentType(  )
    {
        return _strValueContentType;
    }
}
