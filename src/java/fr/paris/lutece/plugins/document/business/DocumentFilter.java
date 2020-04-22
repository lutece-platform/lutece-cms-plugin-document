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
package fr.paris.lutece.plugins.document.business;


/**
 *
 */
public class DocumentFilter
{
    private static final String ALL_STRING = "all";
    private static final int ALL_INT = -1;

    // Variables declarations
    private int _nIdSpace = ALL_INT;
    private int _nIdState = ALL_INT;
    private String _strCodeDocumentType = ALL_STRING;
    private int[] _arrayCategoriesId;
    private int[] _arrayId;

    // The default value is true to assure ascendant compatibility
    private boolean _bLoadBinaries = true;
    private Boolean _bIsPublished;
    private String _dateMin;
    private String _dateMax;

    /**
     * Returns the IdSpace
     *
     * @return The IdSpace
     */
    public int getIdSpace(  )
    {
        return _nIdSpace;
    }

    /**
     * Sets the IdSpace
     *
     * @param nIdSpace The IdSpace
     */
    public void setIdSpace( int nIdSpace )
    {
        _nIdSpace = nIdSpace;
    }

    /**
     * Tell if the filter contains a criteria on the Document space
     * @return True if the filter contains a criteria on the Document space
     *         otherwise false
     */
    public boolean containsSpaceCriteria(  )
    {
        return ( _nIdSpace != ALL_INT );
    }

    /**
     * Returns the IdState
     *
     * @return The IdState
     */
    public int getIdState(  )
    {
        return _nIdState;
    }

    /**
     * Tell if the filter contains a criteria on the Document state
     * @return True if the filter contains a criteria on the Document state
     *         otherwise false
     */
    public boolean containsStateCriteria(  )
    {
        return ( _nIdState != ALL_INT );
    }

    /**
     * Sets the IdState
     *
     * @param nIdState The IdState
     */
    public void setIdState( int nIdState )
    {
        _nIdState = nIdState;
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
     * Tell if the filter contains a criteria on the Document type
     * @return True if the filter contains a criteria on the Document type
     *         otherwise false
     */
    public boolean containsDocumentTypeCriteria(  )
    {
        return ( !_strCodeDocumentType.equals( ALL_STRING ) );
    }

    /**
     * @return the _arrayCategoriesId
     */
    public int[] getCategoriesId(  )
    {
        return _arrayCategoriesId;
    }

    /**
     * @param arrayCategoriesId the _arrayCategoriesId to set
     */
    public void setCategoriesId( int[] arrayCategoriesId )
    {
        _arrayCategoriesId = arrayCategoriesId;
    }

    /**
     * Tell if the filter contains a criteria on the Category
     * @return True if the filter contains a criteria on the categories
     *         otherwise false
     */
    public boolean containsCategoriesCriteria(  )
    {
        return ( ( _arrayCategoriesId != null ) && ( _arrayCategoriesId.length != 0 ) );
    }

    /**
     * Tell if the filter contains a criteria on the Id
     * @return True if the filter contains a criteria on the Ids otherwise false
     */
    public boolean containsIdsCriteria(  )
    {
        return ( ( _arrayId != null ) && ( _arrayId.length != 0 ) );
    }

    /**
     * @return the _arrayId
     */
    public int[] getIds(  )
    {
        return _arrayId;
    }

    /**
     * @param arrayId the _arrayId to set
     */
    public void setIds( int[] arrayId )
    {
        _arrayId = arrayId;
    }

    /**
     * Get the boolean that indicates whether binaries of documents should be
     * loaded
     * @return True if binaries should be loaded, false otherwise
     */
    public boolean getLoadBinaries(  )
    {
        return _bLoadBinaries;
    }

    /**
     * Set the boolean that indicates whether binaries of documents should be
     * loaded
     * @param bLoadBinaries True if binaries should be loaded, false otherwise
     */
    public void setLoadBinaries( boolean bLoadBinaries )
    {
        this._bLoadBinaries = bLoadBinaries;
    }

    /**
     * @return the _bIsPublished
     */
    public Boolean isPublished(  )
    {
        return _bIsPublished;
    }

    /**
     * @param bIsPublished the _bIsPublished to set
     */
    public void setIsPublished( Boolean bIsPublished )
    {
        this._bIsPublished = bIsPublished;
    }

    /**
     * @return the _dateMin
     */
    public String getDateMin(  )
    {
        return _dateMin;
    }

    /**
     * @param dateMin the _dateMin to set
     */
    public void setDateMin( String dateMin )
    {
        this._dateMin = dateMin;
    }

    /**
     * @return the _dateMax
     */
    public String getDateMax(  )
    {
        return _dateMax;
    }

    /**
     * @param dateMax the _dateMax to set
     */
    public void setDateMax( String dateMax )
    {
        this._dateMax = dateMax;
    }
}
