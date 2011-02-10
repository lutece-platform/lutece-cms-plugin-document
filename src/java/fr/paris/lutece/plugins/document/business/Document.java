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
package fr.paris.lutece.plugins.document.business;

import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.category.Category;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublication;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublicationHome;
import fr.paris.lutece.plugins.document.modules.comment.business.DocumentCommentHome;
import fr.paris.lutece.plugins.document.modules.comment.service.DocumentCommentPlugin;
import fr.paris.lutece.portal.business.resourceenhancer.ResourceEnhancer;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.i18n.Localizable;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * A document of the CMS.
 */
public class Document implements Localizable
{
    private static final String PROPERTY_DEFAULT_THUMBNAIL = "document.thumbnail.default";
    private static final String PROPERTY_RESOURCE_PROVIDER_URL = "document.resource.provider.url";
    public static final String CODE_DOCUMENT_TYPE_DOWNLOAD = "download";
    private static final String PROPERTY_RESOURCE_TYPE = "document";

    /////////////////////////////////////////////////////////////////////////////////
    // Xml Tags
    private static final String TAG_DOCUMENT = "document";
    private static final String TAG_DOCUMENT_ID = "document-id";
    private static final String TAG_DATE_PUBLICATION = "document-date-publication";
    private static final String TAG_DOCUMENT_XML_CONTENT = "document-xml-content";

    // private static final String TAG_DOCUMENT_IS_COMMENTABLE = "document-is-commentable";
    private static final String TAG_DOCUMENT_COMMENT_NB = "document-comment-nb";
    private static final String EMPTY_STRING = "";

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
    private String _strXmlWorkingContent;
    private String _strXmlValidatedContent;
    private String _strXmlMetadata;
    private int _nIdSpace;
    private int _nAcceptSiteComments;
    private int _nIsModeratedComment;
    private int _nIsEmailNotifiedComment;
    private int _nIdPageTemplateDocument;
    private int _nPublishedStatus;
    private String _strSpace;
    private int _nIdState;
    private String _strState;
    private List<DocumentAttribute> _listAttributes;
    private Locale _locale;
    private List _listActions;
    private List<Category> _listCategories;

    /**
     * Returns the IdDocument
     *
     * @return The IdDocument
     */
    public int getId(  )
    {
        return _nIdDocument;
    }

    /**
     * Sets the IdDocument
     *
     * @param nIdDocument The IdDocument
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
    public Locale getLocale(  )
    {
        return I18nService.getDefaultLocale(  ); //FIXME The document should store its locale
    }

    /**
     * Sets the Locale
     *
     * @param locale The Locale
     */
    public void setLocale( Locale locale )
    {
        _locale = locale;
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
     * Returns the Title
     *
     * @return The Title
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Sets the Title
     *
     * @param strTitle The Title
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
    public String getSummary(  )
    {
        return ( _strSummary != null ) ? _strSummary : EMPTY_STRING;
    }

    /**
     * Sets the Summary
     *
     * @param strSummary The Summary
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
    public java.sql.Timestamp getDateCreation(  )
    {
        return _dateCreation;
    }

    /**
     * Sets the DateCreation
     *
     * @param dateCreation The DateCreation
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
    public int getCreatorId(  )
    {
        return _nIdCreator;
    }

    /**
     * Sets the IdCreator
     *
     * @param nIdCreator The IdCreator
     */
    public void setCreatorId( int nIdCreator )
    {
        _nIdCreator = nIdCreator;
    }

    /**
     * Returns the Date of the last Modification
     *
     * @return The Date  of the last Modification
     */
    public java.sql.Timestamp getDateModification(  )
    {
        return _dateModification;
    }

    /**
     * Sets the Date  of the last Modification
     *
     * @param dateModification The Date  of the last Modification
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
    public java.sql.Timestamp getDateValidityBegin(  )
    {
        return _dateValidityBegin;
    }

    /**
     * Sets the begining Date of the validity period of the document
     *
     * @param dateValidityBegin The begining Date of the validity period of the document
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
    public java.sql.Timestamp getDateValidityEnd(  )
    {
        return _dateValidityEnd;
    }

    /**
     * Sets the end Date of the validity period of the document
     *
     * @param dateValidityEnd The end Date of the validity period of the document
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
    public String getComment(  )
    {
        return ( _strComment != null ) ? _strComment : EMPTY_STRING;
    }

    /**
     * Sets the Comment
     *
     * @param strComment The Comment
     */
    public void setComment( String strComment )
    {
        _strComment = strComment;
    }

    /**
     * Returns the XmlWorkingContent
     *
     * @return The XmlWorkingContent
     */
    public String getXmlWorkingContent(  )
    {
        return _strXmlWorkingContent;
    }

    /**
     * Sets the XmlWorkingContent
     *
     * @param strXmlWorkingContent The XmlWorkingContent
     */
    public void setXmlWorkingContent( String strXmlWorkingContent )
    {
        _strXmlWorkingContent = strXmlWorkingContent;
    }

    /**
     * Returns the XmlValidatedContent
     *
     * @return The XmlValidatedContent
     */
    public String getXmlValidatedContent(  )
    {
        return _strXmlValidatedContent;
    }

    /**
     * Sets the XmlValidatedContent
     *
     * @param strXmlValidatedContent The XmlValidatedContent
     */
    public void setXmlValidatedContent( String strXmlValidatedContent )
    {
        _strXmlValidatedContent = strXmlValidatedContent;
    }

    /**
     * Returns the XmlMetadata
     *
     * @return The XmlMetadata
     */
    public String getXmlMetadata(  )
    {
        return _strXmlMetadata;
    }

    /**
     * Sets the XmlMetadata
     *
     * @param strXmlMetadata The XmlMetadata
     */
    public void setXmlMetadata( String strXmlMetadata )
    {
        _strXmlMetadata = strXmlMetadata;
    }

    /**
     * Returns the IdSpace
     *
     * @return The IdSpace
     */
    public int getSpaceId(  )
    {
        return _nIdSpace;
    }

    /**
     * Sets the IdSpace
     *
     * @param nIdSpace The IdSpace
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
    public String getSpace(  )
    {
        return _strSpace;
    }

    /**
     * Sets the Space
     *
     * @param strSpace The Space
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
    public int getStateId(  )
    {
        return _nIdState;
    }

    /**
     * Sets the IdState
     *
     * @param nIdState The IdState
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
    public String getStateKey(  )
    {
        return _strState;
    }

    /**
     * Returns the State
     *
     * @return The State
     */
    public String getState(  )
    {
        return I18nService.getLocalizedString( _strState, _locale );
    }

    /**
     * Sets the State
     *
     * @param strState The State
     */
    public void setStateKey( String strState )
    {
        _strState = strState;
    }

    /**
     * Sets the nAcceptComment
     * @param nAcceptSiteComments The nAcceptComment
     */
    public void setAcceptSiteComments( int nAcceptSiteComments )
    {
        _nAcceptSiteComments = nAcceptSiteComments;
    }

    /**
     * Returns the nAcceptComment
     * @return The nAcceptComment
     */
    public int getAcceptSiteComments(  )
    {
        return _nAcceptSiteComments;
    }

    /**
     * Sets the nIsModeratedComment
     * @param nIsModeratedComment The nIsModeratedComment
     */
    public void setIsModeratedComment( int nIsModeratedComment )
    {
        _nIsModeratedComment = nIsModeratedComment;
    }

    /**
     * Returns the nIsModeratedComment
     * @return The nIsModeratedComment
     */
    public int getIsModeratedComment(  )
    {
        return _nIsModeratedComment;
    }

    /**
     * Sets the nIsEmailNotifiedComment
     * @param nIsEmailNotifiedComment The nIsEmailNotifiedComment
     */
    public void setIsEmailNotifiedComment( int nIsEmailNotifiedComment )
    {
        _nIsEmailNotifiedComment = nIsEmailNotifiedComment;
    }

    /**
     * Returns the nIsEmailNotifiedComment
     * @return The nIsEmailNotifiedComment
     */
    public int getIsEmailNotifiedComment(  )
    {
        return _nIsEmailNotifiedComment;
    }

    /**
     * Returns the IdMailingList
     *
     * @return The IdMailingList
     */
    public int getMailingListId(  )
    {
        return _nIdMailingList;
    }

    /**
     * Sets the IdMailingList
     *
     * @param nIdMailingList The IdMailingList
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
    public int getPageTemplateDocumentId(  )
    {
        return _nIdPageTemplateDocument;
    }

    /**
     * Sets the IdPageTemplateDocument
     *
     * @param nIdPageTemplateDocument The IdPageTemplateDocument
     */
    public void setPageTemplateDocumentId( int nIdPageTemplateDocument )
    {
        _nIdPageTemplateDocument = nIdPageTemplateDocument;
    }

    /**
     * Returns attributes List
     * @return The document attributes list
     */
    public List<DocumentAttribute> getAttributes(  )
    {
        return _listAttributes;
    }

    /**
     * Set the document attributes list
     * @param listAttributes The document attributes list
     */
    public void setAttributes( List<DocumentAttribute> listAttributes )
    {
        _listAttributes = listAttributes;
    }

    /**
     * Returns Actions List
     * @return The document Actions list
     */
    public List getActions(  )
    {
        return _listActions;
    }

    /**
     * Set the document Actions list
     * @param listActions The document Actions list
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
    public String getType(  )
    {
        return _strType;
    }

    /**
     * Sets the Type
     *
     * @param strType The Type
     */
    public void setType( String strType )
    {
        _strType = strType;
    }

    /**
     * @return the _listCategory
     */
    public List<Category> getCategories(  )
    {
        return _listCategories;
    }

    /**
     * @param listCategory the _listCategory to set
     */
    public void setCategories( List<Category> listCategory )
    {
        _listCategories = listCategory;
    }

    /**
     * @param nPublishedStatus the nPublishedStatus to set
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
    public int getPublishedStatus(  )
    {
        return _nPublishedStatus;
    }

    /**
     * Returns a Thumbnail url for the document based on a document attribute or
     * on the document type.
     * @return A Thumbnail url
     */
    public String getThumbnail(  )
    {
        String strThumbnailUrl = AppPropertiesService.getProperty( PROPERTY_DEFAULT_THUMBNAIL );
        String strResourceUrl = AppPropertiesService.getProperty( PROPERTY_RESOURCE_PROVIDER_URL );

        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( getCodeDocumentType(  ) );

        if ( documentType.getThumbnailAttributeId(  ) != 0 )
        {
            strThumbnailUrl = strResourceUrl + getId(  );
        }
        else if ( !documentType.getDefaultThumbnailUrl(  ).equals( "" ) )
        {
            strThumbnailUrl = documentType.getDefaultThumbnailUrl(  );
        }

        return strThumbnailUrl;
    }

    ////////////////////////////////////////////////////////////////////////////
    // XML Generation

    /**
     * Returns the xml of this document
     * @param request The HTTP Servlet request
     * @param nIdPortlet the id of the portlet where the document is published
     * @return the link xml
     */
    public String getXml( HttpServletRequest request, int nIdPortlet )
    {
        StringBuffer strXml = new StringBuffer(  );

        XmlUtil.beginElement( strXml, TAG_DOCUMENT );

        String strId = Integer.toString( getId(  ) );
        XmlUtil.addElement( strXml, TAG_DOCUMENT_ID, strId );

        if ( nIdPortlet != -1 )
        {
            DocumentPublication publication = DocumentPublicationHome.findByPrimaryKey( nIdPortlet, getId(  ) );
            String strDate = DateUtil.getDateString( publication.getDatePublishing(  ), getLocale(  ) );
            XmlUtil.addElement( strXml, TAG_DATE_PUBLICATION, strDate );
        }

        XmlUtil.addElement( strXml, TAG_DOCUMENT_XML_CONTENT, getXmlValidatedContent(  ) );

        // TODO : delete dependency from document-comment module
        if ( DocumentCommentPlugin.getPluginStatus(  ) )
        {
            XmlUtil.addElement( strXml, TAG_DOCUMENT_COMMENT_NB, getNbComment( Integer.parseInt( strId ) ) );
        }

        // additionnal info
        ResourceEnhancer.getXmlAddOn( strXml, PROPERTY_RESOURCE_TYPE, Integer.parseInt( strId ) );

        //  XmlUtil.addElement( strXml, TAG_DOCUMENT_IS_COMMENTABLE, getAcceptSiteComments(  ) );
        XmlUtil.endElement( strXml, TAG_DOCUMENT );

        return strXml.toString(  );
    }

    /**
     * Returns the xml document of this link
     *
     * @param nIdPortlet the id of the portlet where the document is published
     * @param request The HTTP servlet request
     * @return the link xml document
     */
    public String getXmlDocument( HttpServletRequest request, int nIdPortlet )
    {
        return XmlUtil.getXmlHeader(  ) + getXml( request, nIdPortlet );
    }

    /**
     * Returns a document attribute by its code
     * @param strAttributeCode The Attribute Code
     * @return the attribute object corresponding to the code
     */
    public DocumentAttribute getAttribute( String strAttributeCode )
    {
        if ( _listAttributes != null )
        {
            for ( DocumentAttribute attribute : _listAttributes )
            {
                if ( attribute.getCode(  ).equals( strAttributeCode ) )
                {
                    return attribute;
                }
            }
        }

        return null;
    }

    /**
     * Control that an document is valid, i.e. that its period of validity defined
     * by its dateValidityBegin and its dateValidityEnd is valid :
     * an document is valid if the current date > = dateValidityBegin and if current date < = dateValidityEnd
     * If the two dates are null, the test of validity will return true
     * if one of the dates is null, the result of the test will be that carried out
     * on the non null date
     * @return true if the document is valid, false otherwise
     */
    public boolean isValid(  )
    {
        java.sql.Timestamp dateValidityBegin = getDateValidityBegin(  );
        java.sql.Timestamp dateValidityEnd = getDateValidityEnd(  );

        GregorianCalendar gc = new GregorianCalendar(  );

        java.sql.Date dateToday = new java.sql.Date( gc.getTime(  ).getTime(  ) );

        if ( ( dateValidityBegin == null ) && ( dateValidityEnd == null ) )
        {
            return true;
        }
        else if ( dateValidityBegin == null )
        {
            // Return true if dateValidityEnd >= DateToday, false otherwise :
            return ( dateValidityEnd.compareTo( dateToday ) >= 0 );
        }
        else if ( dateValidityEnd == null )
        {
            // Return true if dateValidityBegin <= DateToday, false otherwise :
            return ( dateValidityBegin.compareTo( dateToday ) <= 0 );
        }
        else
        {
            // Return true if dateValidityBegin <= dateToday <= dateValidityEnd, false
            // otherwise :
            return ( ( dateValidityBegin.compareTo( dateToday ) <= 0 ) &&
            ( dateValidityEnd.compareTo( dateToday ) >= 0 ) );
        }
    }

    /**
     * Control that an document is out of date, i.e. dateValidityEnd has expired :
     * @return true if the document is out of date, false otherwise
     */
    public boolean isOutOfDate(  )
    {
        if ( getDateValidityEnd(  ) == null )
        {
            return false;
        }

        // Return false if dateValidityEnd >= DateToday, true otherwise :
        return !( getDateValidityEnd(  ).compareTo( new Date(  ) ) >= 0 );
    }

    /**
     * Return the nb of comment for a document
     * @param strDocumentId the document identifier
     * @return the nb of document
     **/
    private int getNbComment( int strDocumentId )
    {
        return DocumentCommentHome.checkCommentNb( strDocumentId );
    }
}
