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
package fr.paris.lutece.plugins.document.business;

import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.category.Category;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.i18n.Localizable;
import fr.paris.lutece.portal.service.resource.IExtendableResource;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.Strings;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * A document of the CMS.
 */
public class Document implements Localizable, IExtendableResource
{
    public static final String CODE_DOCUMENT_TYPE_DOWNLOAD = "download";
    public static final String CODE_DOCUMENT_TYPE_IMAGE = "image";
    public static final String PROPERTY_RESOURCE_TYPE = "document";
    private static final String SERVLET_DOCUMENT_PATH = "document";
    private static final String PROPERTY_DEFAULT_THUMBNAIL = "document.thumbnail.default";
    private static final String PROPERTY_RESOURCE_PROVIDER_URL = "document.resource.provider.url";
    private static final String PARAMETER_THUMBNAIL_VERSION = "&v=";

    private static final String MARK_ID = "id";
    private static final String MARK_ID_ATTRIBUTE = "id_attribute";
    private static final String EMPTY_STRING = "";
    private static final String CONSTANT_QUESTION_MARK = "?";
    private static final String CONSTANT_EQUALS = "=";

    // Variables declarations
    private int _nIdDocument;
    private String _strCodeDocumentType;
    private String _strType;
    private String _strTitle;
    private String _strSummary;
    private int _nIdCreator;
    private int _nIdMailingList;
    private java.sql.Timestamp _dateCreation;
    private java.sql.Timestamp _dateModification;
    private java.sql.Timestamp _dateValidityBegin;
    private java.sql.Timestamp _dateValidityEnd;
    private String _strComment;
    private String _strMetadata;
    private int _nIdSpace;
    private int _nIdPageTemplateDocument;
    private int _nPublishedStatus;
    private String _strSpace;
    private int _nIdState;
    private String _strState;
    private List<DocumentAttribute> _listAttributes;
    private Locale _locale;
    private List _listActions;
    private List<Category> _listCategories;
    private boolean _bSkipPortlet; // Skips HTML code generation for documents list portlet
    private boolean _bSkipCategories; // Skips HTML code generation for category list portlet
    private String _strValidatedTitle;
    private String _strValidatedSummary;

    /**
     * Returns the IdDocument
     *
     * @return The IdDocument
     */
    public int getId( )
    {
        return _nIdDocument;
    }

    /**
     * Sets the IdDocument
     *
     * @param nIdDocument
     *            The IdDocument
     */
    public void setId( int nIdDocument )
    {
        _nIdDocument = nIdDocument;
    }

    /**
     * Returns the Locale
     *
     * @return The Locale
     */
    public Locale getLocale( )
    {
        return I18nService.getDefaultLocale( ); // FIXME The document should store its locale
    }

    /**
     * Sets the Locale
     *
     * @param locale
     *            The Locale
     */
    @Override
    public void setLocale( Locale locale )
    {
        _locale = locale;
    }

    /**
     * Returns the CodeDocumentType
     *
     * @return The CodeDocumentType
     */
    public String getCodeDocumentType( )
    {
        return _strCodeDocumentType;
    }

    /**
     * Sets the CodeDocumentType
     *
     * @param strCodeDocumentType
     *            The CodeDocumentType
     */
    public void setCodeDocumentType( String strCodeDocumentType )
    {
        _strCodeDocumentType = strCodeDocumentType;
    }

    /**
     * Returns the Title
     *
     * @return The Title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Sets the Title
     *
     * @param strTitle
     *            The Title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Returns the Summary
     *
     * @return The Summary
     */
    public String getSummary( )
    {
        return ( _strSummary != null ) ? _strSummary : EMPTY_STRING;
    }

    /**
     * Sets the Summary
     *
     * @param strSummary
     *            The Summary
     */
    public void setSummary( String strSummary )
    {
        _strSummary = strSummary;
    }

    /**
     * Returns the DateCreation
     *
     * @return The DateCreation
     */
    public java.sql.Timestamp getDateCreation( )
    {
        return _dateCreation;
    }

    /**
     * Sets the DateCreation
     *
     * @param dateCreation
     *            The DateCreation
     */
    public void setDateCreation( java.sql.Timestamp dateCreation )
    {
        _dateCreation = dateCreation;
    }

    /**
     * Returns the IdCreator
     *
     * @return The IdCreator
     */
    public int getCreatorId( )
    {
        return _nIdCreator;
    }

    /**
     * Sets the IdCreator
     *
     * @param nIdCreator
     *            The IdCreator
     */
    public void setCreatorId( int nIdCreator )
    {
        _nIdCreator = nIdCreator;
    }

    /**
     * Returns the Date of the last Modification
     *
     * @return The Date of the last Modification
     */
    public java.sql.Timestamp getDateModification( )
    {
        return _dateModification;
    }

    /**
     * Sets the Date of the last Modification
     *
     * @param dateModification
     *            The Date of the last Modification
     */
    public void setDateModification( java.sql.Timestamp dateModification )
    {
        _dateModification = dateModification;
    }

    /**
     * Returns the begining Date of the validity period of the document
     *
     * @return The begining Date of the validity period of the document
     */
    public java.sql.Timestamp getDateValidityBegin( )
    {
        return _dateValidityBegin;
    }

    /**
     * Sets the begining Date of the validity period of the document
     *
     * @param dateValidityBegin
     *            The begining Date of the validity period of the document
     */
    public void setDateValidityBegin( java.sql.Timestamp dateValidityBegin )
    {
        _dateValidityBegin = dateValidityBegin;
    }

    /**
     * Returns the end Date of the validity period of the document
     *
     * @return The end Date of the validity period of the document
     */
    public java.sql.Timestamp getDateValidityEnd( )
    {
        return _dateValidityEnd;
    }

    /**
     * Sets the end Date of the validity period of the document
     *
     * @param dateValidityEnd
     *            The end Date of the validity period of the document
     */
    public void setDateValidityEnd( java.sql.Timestamp dateValidityEnd )
    {
        _dateValidityEnd = dateValidityEnd;
    }

    /**
     * Returns the Comment
     *
     * @return The Comment
     */
    public String getComment( )
    {
        return ( _strComment != null ) ? _strComment : EMPTY_STRING;
    }

    /**
     * Sets the Comment
     *
     * @param strComment
     *            The Comment
     */
    public void setComment( String strComment )
    {
        _strComment = strComment;
    }

    /**
     * Returns the metadata of the document, as a JSON object whose properties depend on the metadata handler of the document type
     *
     * @return The metadata, or null
     */
    public String getMetadata( )
    {
        return _strMetadata;
    }

    /**
     * Sets the metadata of the document
     *
     * @param strMetadata
     *            The metadata, as a JSON object
     */
    public void setMetadata( String strMetadata )
    {
        _strMetadata = strMetadata;
    }

    /**
     * Returns the IdSpace
     *
     * @return The IdSpace
     */
    public int getSpaceId( )
    {
        return _nIdSpace;
    }

    /**
     * Sets the IdSpace
     *
     * @param nIdSpace
     *            The IdSpace
     */
    public void setSpaceId( int nIdSpace )
    {
        _nIdSpace = nIdSpace;
    }

    /**
     * Returns the Space
     *
     * @return The Space
     */
    public String getSpace( )
    {
        return _strSpace;
    }

    /**
     * Sets the Space
     *
     * @param strSpace
     *            The Space
     */
    public void setSpace( String strSpace )
    {
        _strSpace = strSpace;
    }

    /**
     * Returns the IdState
     *
     * @return The IdState
     */
    public int getStateId( )
    {
        return _nIdState;
    }

    /**
     * Sets the IdState
     *
     * @param nIdState
     *            The IdState
     */
    public void setStateId( int nIdState )
    {
        _nIdState = nIdState;
    }

    /**
     * Returns the State
     *
     * @return The State
     */
    public String getStateKey( )
    {
        return _strState;
    }

    /**
     * Returns the State
     *
     * @return The State
     */
    public String getState( )
    {
        return I18nService.getLocalizedString( _strState, _locale );
    }

    /**
     * Sets the State
     *
     * @param strState
     *            The State
     */
    public void setStateKey( String strState )
    {
        _strState = strState;
    }

    /**
     * Returns the IdMailingList
     *
     * @return The IdMailingList
     */
    public int getMailingListId( )
    {
        return _nIdMailingList;
    }

    /**
     * Sets the IdMailingList
     *
     * @param nIdMailingList
     *            The IdMailingList
     */
    public void setMailingListId( int nIdMailingList )
    {
        _nIdMailingList = nIdMailingList;
    }

    /**
     * Returns the IdPageTemplateDocument
     *
     * @return The IdPageTemplateDocument
     */
    public int getPageTemplateDocumentId( )
    {
        return _nIdPageTemplateDocument;
    }

    /**
     * Sets the IdPageTemplateDocument
     *
     * @param nIdPageTemplateDocument
     *            The IdPageTemplateDocument
     */
    public void setPageTemplateDocumentId( int nIdPageTemplateDocument )
    {
        _nIdPageTemplateDocument = nIdPageTemplateDocument;
    }

    /**
     * Returns attributes List
     * 
     * @return The document attributes list
     */
    public List<DocumentAttribute> getAttributes( )
    {
        return _listAttributes;
    }

    /**
     * Set the document attributes list
     * 
     * @param listAttributes
     *            The document attributes list
     */
    public void setAttributes( List<DocumentAttribute> listAttributes )
    {
        _listAttributes = listAttributes;
    }

    /**
     * Returns Actions List
     * 
     * @return The document Actions list
     */
    public List getActions( )
    {
        return _listActions;
    }

    /**
     * Set the document Actions list
     * 
     * @param listActions
     *            The document Actions list
     */
    public void setActions( List listActions )
    {
        _listActions = listActions;
    }

    /**
     * Returns the Type
     *
     * @return The Type
     */
    public String getType( )
    {
        return _strType;
    }

    /**
     * Sets the Type
     *
     * @param strType
     *            The Type
     */
    public void setType( String strType )
    {
        _strType = strType;
    }

    /**
     * @return the _listCategory
     */
    public List<Category> getCategories( )
    {
        return _listCategories;
    }

    /**
     * @param listCategory
     *            the _listCategory to set
     */
    public void setCategories( List<Category> listCategory )
    {
        _listCategories = listCategory;
    }

    /**
     * @param nPublishedStatus
     *            the nPublishedStatus to set
     */
    public void setPublishedStatus( int nPublishedStatus )
    {
        _nPublishedStatus = nPublishedStatus;
    }

    /**
     * Returns the PublishedSTatus
     *
     * @return The PublishedSTatus
     */
    public int getPublishedStatus( )
    {
        return _nPublishedStatus;
    }

    /**
     * Returns the SkipPortlet
     *
     * @return The SkipPortlet
     */
    public boolean isSkipPortlet( )
    {
        return _bSkipPortlet;
    }

    /**
     * Sets the SkipPortlet
     *
     * @param bSkipPortlet
     *            The SkipPortlet
     */
    public void setSkipPortlet( boolean bSkipPortlet )
    {
        _bSkipPortlet = bSkipPortlet;
    }

    /**
     * Returns the SkipCategories
     *
     * @return The SkipCategories
     */
    public boolean isSkipCategories( )
    {
        return _bSkipCategories;
    }

    /**
     * Sets the SkipCategories
     *
     * @param bSkipCategories
     *            The SkipCategories
     */
    public void setSkipCategories( boolean bSkipCategories )
    {
        _bSkipCategories = bSkipCategories;
    }

    /**
     * Returns the title as it was when the document was last validated
     *
     * @return The validated title
     */
    public String getValidatedTitle( )
    {
        return _strValidatedTitle;
    }

    /**
     * Sets the title as it was when the document was last validated
     *
     * @param strValidatedTitle
     *            The validated title
     */
    public void setValidatedTitle( String strValidatedTitle )
    {
        _strValidatedTitle = strValidatedTitle;
    }

    /**
     * Returns the summary as it was when the document was last validated
     *
     * @return The validated summary
     */
    public String getValidatedSummary( )
    {
        return _strValidatedSummary;
    }

    /**
     * Sets the summary as it was when the document was last validated
     *
     * @param strValidatedSummary
     *            The validated summary
     */
    public void setValidatedSummary( String strValidatedSummary )
    {
        _strValidatedSummary = strValidatedSummary;
    }

    /**
     * Returns a Thumbnail url for the document based on a document attribute or on the document type.
     * 
     * @return A Thumbnail url
     */
    public String getThumbnail( )
    {
        String strThumbnailUrl = AppPropertiesService.getProperty( PROPERTY_DEFAULT_THUMBNAIL );
        String strResourceUrl = AppPropertiesService.getProperty( PROPERTY_RESOURCE_PROVIDER_URL );

        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( getCodeDocumentType( ) );

        if ( documentType.getThumbnailAttributeId( ) != 0 )
        {
            strThumbnailUrl = strResourceUrl + getId( );

            // The browser keeps the thumbnail (the servlet sends a long max-age): a version taken from the modification date
            // changes the URL when the document is modified, so the new image is displayed without a forced reload
            if ( getDateModification( ) != null )
            {
                strThumbnailUrl += PARAMETER_THUMBNAIL_VERSION + getDateModification( ).getTime( );
            }
        }
        else
            if ( !documentType.getDefaultThumbnailUrl( ).equals( "" ) )
            {
                strThumbnailUrl = documentType.getDefaultThumbnailUrl( );
            }

        return strThumbnailUrl;
    }

    /**
     * Returns a document attribute by its code
     * 
     * @param strAttributeCode
     *            The Attribute Code
     * @return the attribute object corresponding to the code
     */
    public DocumentAttribute getAttribute( String strAttributeCode )
    {
        if ( _listAttributes != null )
        {
            for ( DocumentAttribute attribute : _listAttributes )
            {
                if ( attribute.getCode( ).equals( strAttributeCode ) )
                {
                    return attribute;
                }
            }
        }

        return null;
    }

    /**
     * Tells if the document has a validated version: approved, or approved and being changed
     *
     * @return true if the document has a validated version
     */
    public boolean hasValidatedVersion( )
    {
        return ( _nIdState == DocumentState.STATE_VALIDATE ) || ( _nIdState == DocumentState.STATE_IN_CHANGE )
                || ( _nIdState == DocumentState.STATE_WAITING_FOR_CHANGE_APPROVAL );
    }

    /**
     * Control that an document is valid, i.e. that its period of validity defined by its dateValidityBegin and its dateValidityEnd is valid : an document is
     * valid if the current date > = dateValidityBegin and if current date < = dateValidityEnd If the two dates are null, the test of validity will return true
     * if one of the dates is null, the result of the test will be that carried out on the non null date
     * 
     * @return true if the document is valid, false otherwise
     */
    public boolean isValid( )
    {
        java.sql.Timestamp dateValidityBegin = getDateValidityBegin( );
        java.sql.Timestamp dateValidityEnd = getDateValidityEnd( );

        GregorianCalendar gc = new GregorianCalendar( );

        java.sql.Date dateToday = new java.sql.Date( gc.getTime( ).getTime( ) );

        if ( ( dateValidityBegin == null ) && ( dateValidityEnd == null ) )
        {
            return true;
        }
        else
            if ( dateValidityBegin == null )
            {
                // Return true if dateValidityEnd >= DateToday, false otherwise :
                return ( dateValidityEnd.compareTo( dateToday ) >= 0 );
            }
            else
                if ( dateValidityEnd == null )
                {
                    // Return true if dateValidityBegin <= DateToday, false otherwise :
                    return ( dateValidityBegin.compareTo( dateToday ) <= 0 );
                }
                else
                {
                    // Return true if dateValidityBegin <= dateToday <= dateValidityEnd, false
                    // otherwise :
                    return ( ( dateValidityBegin.compareTo( dateToday ) <= 0 ) && ( dateValidityEnd.compareTo( dateToday ) >= 0 ) );
                }
    }

    /**
     * Control that an document is out of date, i.e. dateValidityEnd has expired :
     * 
     * @return true if the document is out of date, false otherwise
     */
    public boolean isOutOfDate( )
    {
        if ( getDateValidityEnd( ) == null )
        {
            return false;
        }

        // Return false if dateValidityEnd >= DateToday, true otherwise :
        return !( getDateValidityEnd( ).compareTo( new Date( ) ) >= 0 );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdExtendableResource( )
    {
        return Integer.toString( _nIdDocument );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceType( )
    {
        return PROPERTY_RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceName( )
    {
        return _strTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceDescription( )
    {
        return _strSummary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceImageUrl( )
    {
        if ( Strings.CI.equals( _strCodeDocumentType, CODE_DOCUMENT_TYPE_IMAGE ) )
        {
            UrlItem urlItem = new UrlItem( SERVLET_DOCUMENT_PATH );
            urlItem.addParameter( MARK_ID, _nIdDocument );

            return urlItem.getUrl( );
        }

        if ( _listAttributes != null )
        {
            for ( DocumentAttribute attribute : _listAttributes )
            {
                if (  Strings.CS.equals( attribute.getCodeAttributeType( ), "image" ) )
                {
                    UrlItem urlItem = new UrlItem( SERVLET_DOCUMENT_PATH );
                    urlItem.addParameter( MARK_ID, _nIdDocument );
                    urlItem.addParameter( MARK_ID_ATTRIBUTE, attribute.getId( ) );

                    return urlItem.getUrl( );
                }
            }
        }

        return null;
    }
}
