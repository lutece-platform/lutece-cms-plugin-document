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
package fr.paris.lutece.plugins.document.service;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeParameter;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.category.Category;
import fr.paris.lutece.plugins.document.business.category.CategoryHome;
import fr.paris.lutece.plugins.document.business.portlet.DocumentPortletHome;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentAction;
import fr.paris.lutece.plugins.document.business.workflow.DocumentActionHome;
import fr.paris.lutece.plugins.document.service.metadata.MetadataHandler;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.plugins.document.utils.ImageUtils;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.plugins.document.web.DocumentResourceServlet;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.content.ContentService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.xml.XmlUtil;
import fr.paris.lutece.api.user.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;

import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * This Service manages document actions (create, move, delete, validate ...)
 * and notify listeners.
 */
@ApplicationScoped
@Named( "document.DocumentService" )
public class DocumentService
{
	// PARAMETERS
	private static final String PARAMETER_DOCUMENT_TITLE = "document_title";
	private static final String PARAMETER_DOCUMENT_SUMMARY = "document_summary";
	private static final String PARAMETER_DOCUMENT_COMMENT = "document_comment";
	private static final String PARAMETER_VALIDITY_BEGIN = "document_validity_begin";
	private static final String PARAMETER_VALIDITY_END = "document_validity_end";
	private static final String PARAMETER_MAILING_LIST = "mailinglists";
	private static final String PARAMETER_PAGE_TEMPLATE_DOCUMENT_ID = "page_template_id";
	private static final String PARAMETER_SKIP_PORTLET = "document_skip_portlet";
	private static final String PARAMETER_SKIP_CATEGORIES = "document_skip_categories";
	private static final String PARAMETER_CATEGORY = "category_id";
	private static final String PARAMETER_ATTRIBUTE_UPDATE = "update_";
	private static final String PARAMETER_CROPPABLE = "_croppable";
	private static final String PARAMETER_WIDTH = "_width";

	// MESSAGES
	private static final String MESSAGE_ERROR_DATEEND_BEFORE_DATEBEGIN = "document.message.dateEndBeforeDateBegin";
	private static final String MESSAGE_INVALID_DATEEND = "document.message.invalidDateEnd";
	private static final String MESSAGE_INVALID_DATEBEGIN = "document.message.invalidDateBegin";
	private static final String MESSAGE_INVALID_DATE_BEFORE_70 = "document.message.invalidDate.before1970";
	private static final String MESSAGE_ATTRIBUTE_VALIDATION_ERROR = "document.message.attributeValidationError";
	private static final String MESSAGE_ATTRIBUTE_WIDTH_ERROR = "document.message.widthError";
	private static final String MESSAGE_ATTRIBUTE_RESIZE_ERROR = "document.message.resizeError";
	private static final String MESSAGE_EXTENSION_ERROR = "document.message.extensionError";
	private static final String MESSAGE_FILE_SIZE_ERROR = "document.message.fileSizeError";
	private static final String TAG_DOCUMENT_ID = "document-id";
	private static final String TAG_DOCUMENT_TITLE = "document-title";
	private static final String TAG_DOCUMENT_SUMMARY = "document-summary";
	private static final String TAG_DOCUMENT_DATE_BEGIN = "document-date-begin";
	private static final String TAG_DOCUMENT_DATE_END = "document-date-end";
	private static final String TAG_DOCUMENT_SKIP_PORTLET = "document-skip-portlet";
	private static final String TAG_DOCUMENT_SKIP_CATEGORIES = "document-skip-categories";
	private static final String TAG_DOCUMENT_CATEGORIES = "document-categories";
	private static final String TAG_DOCUMENT_CATEGORY = "category";
	private static final String TAG_CDATA_BEGIN = "<![CDATA[";
	private static final String TAG_CDATA_END = "]]>";
	
    private static final String CONTENT_SERVICE_NAME = "Document Content Service";
	
	@Inject
	private Event<DocumentEvent> _documentEvent;

	@Inject
	private AttributeService _attributeService;

	@Inject
	private PublishingService _publishingService;

	@Inject
	private DocumentSpacesService _documentSpacesService;

    /**
     * Returns the unique instance of the {@link DocumentService} service.
     * 
     * <p>This method is deprecated and is provided for backward compatibility only. 
     * For new code, use dependency injection with {@code @Inject} to obtain the 
     * {@link DocumentService} instance instead.</p>
     * 
     * @return The unique instance of {@link DocumentService}.
     * 
     * @deprecated Use {@code @Inject} to obtain the {@link DocumentService} 
     * instance. This method will be removed in future versions.
     */
	@Deprecated( since = "8.0", forRemoval = true )
	public static DocumentService getInstance( )
	{
		return CDI.current( ).select( DocumentService.class ).get( );
	}

	/**
	 * Build an XML document that contains document's data.
	 *
	 * @param document The document
	 * @return An XML fragment containing document's data
	 */
	String buildXmlContent( Document document )
	{
		StringBuffer sbXml = new StringBuffer( );
		XmlUtil.beginElement( sbXml, document.getCodeDocumentType( ) );

		XmlUtil.addElement( sbXml, TAG_DOCUMENT_ID, document.getId( ) );
		XmlUtil.addElement( sbXml, TAG_DOCUMENT_TITLE, TAG_CDATA_BEGIN + document.getTitle( ) + TAG_CDATA_END );
		XmlUtil.addElement( sbXml, TAG_DOCUMENT_SUMMARY, TAG_CDATA_BEGIN + document.getSummary( ) + TAG_CDATA_END );
		XmlUtil.addElement( sbXml, TAG_DOCUMENT_DATE_BEGIN,
				DateUtil.getDateString( document.getDateValidityBegin( ), I18nService.getDefaultLocale( ) ) );
		XmlUtil.addElement( sbXml, TAG_DOCUMENT_DATE_END,
				DateUtil.getDateString( document.getDateValidityEnd( ), I18nService.getDefaultLocale( ) ) );
		XmlUtil.addElement( sbXml, TAG_DOCUMENT_SKIP_PORTLET,
				BooleanUtils.toStringTrueFalse( document.isSkipPortlet( ) ) );
		XmlUtil.addElement( sbXml, TAG_DOCUMENT_SKIP_CATEGORIES,
				BooleanUtils.toStringTrueFalse( document.isSkipCategories( ) ) );
		XmlUtil.addElement( sbXml, TAG_DOCUMENT_CATEGORIES, buildXmlCategories( document ) );

		DocumentType documentType = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType( ) );

		for( DocumentAttribute attribute : documentType.getAttributes( ) )
		{
			DocumentAttribute attributeDocument = getDocumentAttribute( document, attribute.getId( ) );

			if( attributeDocument != null )
			{
				AttributeManager manager = _attributeService.getManager( attributeDocument.getCodeAttributeType( ) );
				XmlUtil.addElement( sbXml, document.getCodeDocumentType( ) + "-" + attribute.getCode( ),
						manager.getAttributeXmlValue( document, attributeDocument ) );
			}
		}

		XmlUtil.endElement( sbXml, document.getCodeDocumentType( ) );

		return sbXml.toString( );
	}

	/**
	 * Build XML content for the categories
	 * 
	 * @param document The document
	 * @return An XML fragment containing document's categories
	 */
	private String buildXmlCategories( Document document )
	{
		StringBuffer strListCategories = new StringBuffer( );

		if( ( document != null ) && ( document.getCategories( ) != null ) && ! document.getCategories( ).isEmpty( ) )
		{
			for( Category category : document.getCategories( ) )
			{
				XmlUtil.addElement( strListCategories, TAG_DOCUMENT_CATEGORY,
						TAG_CDATA_BEGIN + category.getName( ) + TAG_CDATA_END );
			}
		}

		return strListCategories.toString( );
	}

	/**
	 * Change the state of the document
	 *
	 * @param document The document
	 * @param user     The user doing the action
	 * @param nStateId The new state Id
	 * @throws DocumentException raise when error occurs in event or rule
	 */
	public void changeDocumentState( Document document, AdminUser user, int nStateId )
			throws DocumentException
	{
		document.setStateId( nStateId );
		DocumentHome.update( document, false );

		notify( document.getId( ), user, DocumentEvent.DOCUMENT_STATE_CHANGED );
	}

	/**
	 * Create a new document
	 *
	 * @param document The document
	 * @param user     The user doing the action
	 * @throws DocumentException raise when error occurs in event or rule
	 */
	public void createDocument( Document document, AdminUser user )
			throws DocumentException
	{
		document.setDateCreation( new Timestamp( new java.util.Date( ).getTime( ) ) );
		document.setDateModification( new Timestamp( new java.util.Date( ).getTime( ) ) );
		document.setXmlWorkingContent( buildXmlContent( document ) );

		DocumentHome.create( document );

		notify( document.getId( ), user, DocumentEvent.DOCUMENT_CREATED );
	}

	/**
	 * Modify a the content of a document
	 *
	 * @param document The document
	 * @param user     The user doing the action
	 * @throws DocumentException raise when error occurs in event or rule
	 */
	public void modifyDocument( Document document, AdminUser user )
			throws DocumentException
	{
		document.setDateModification( new Timestamp( new java.util.Date( ).getTime( ) ) );
		document.setXmlWorkingContent( buildXmlContent( document ) );
		DocumentHome.update( document, true );

		notify( document.getId( ), user, DocumentEvent.DOCUMENT_CONTENT_MODIFIED );
	}

	/**
	 * Validate a document
	 * 
	 * @param nStateId The new state id for a validated document
	 * @param document The document
	 * @param user     The user doing the action
	 * @throws DocumentException raise when error occurs in event or rule
	 */
	public void validateDocument( Document document, AdminUser user, int nStateId )
			throws DocumentException
	{
		document.setStateId( nStateId );
		// Copy the working content into the validated content
		document.setXmlValidatedContent( document.getXmlWorkingContent( ) );
		DocumentHome.update( document, false );
		DocumentHome.validateAttributes( document.getId( ) );

		List < DocumentAttribute > listAttributes = document.getAttributes( );

		for( DocumentAttribute attribute : listAttributes )
		{
			DocumentResourceServlet.putInCache( document.getId( ), attribute.getId( ) );
		}

		DocumentContentService dc = null;
		
		Collection<ContentService> listContentServices = PortalService.getContentServicesList( );
		
		for ( ContentService cs : listContentServices ) 
		{
		    if ( cs.getName( ).equals( CONTENT_SERVICE_NAME ) ) 
		    {
		    	dc = ( DocumentContentService ) cs;
		    	break;
		    }
		}
		
		if( dc!= null )
		{
			
			dc.loachInit( );
			
			for( int nIdPortlet : DocumentPortletHome.findPortletForDocument( document.getId( ) ) )
			{
				dc.removeFromCache( Integer.toString( document.getId( ) ),
						Integer.toString( nIdPortlet ) );
			}
		}

		notify( document.getId( ), user, DocumentEvent.DOCUMENT_STATE_CHANGED );
	}

	/**
	 * Archive a document
	 * 
	 * @param nStateId The new state id for a validated document
	 * @param document The document
	 * @param user     The user doing the action
	 * @throws DocumentException raise when error occurs in event or rule
	 */
	public void archiveDocument( Document document, AdminUser user, int nStateId )
			throws DocumentException
	{
		// Get the list of portlets linked with document
		Collection < Portlet > listPortlet = _publishingService.getPortletsByDocumentId( Integer.toString(
				document.getId( ) ) );

		for( Portlet portlet : listPortlet )
		{
			// If document is published, unpublish it
			if( _publishingService.isPublished( document.getId( ), portlet.getId( ) ) )
			{
				_publishingService.unPublish( document.getId( ), portlet.getId( ) );
			}

			// Unassign Document to portlet
			_publishingService.unAssign( document.getId( ), portlet.getId( ) );
		}

		document.setStateId( nStateId );
		DocumentHome.update( document, false );

		notify( document.getId( ), user, DocumentEvent.DOCUMENT_STATE_CHANGED );
	}

	/**
	 * Move a document from a space to another
	 *
	 * @param document  The document
	 * @param user      The user doing the action
	 * @param nNewSpace The Id of the destination space
	 * @throws DocumentException raise when error occurs in event or rule
	 */
	public void moveDocument( Document document, AdminUser user, int nNewSpace )
			throws DocumentException
	{
		document.setSpaceId( nNewSpace );
		DocumentHome.update( document, false );

		notify( document.getId( ), user, DocumentEvent.DOCUMENT_MOVED );
	}

	/**
	 * Notify an event to all listeners
	 *
	 * @param nDocumentId The document Id
	 * @param user        The user doing the action
	 * @param nEventType  The type of event
	 * @throws DocumentException raise when error occurs in event or rule
	 */
	private void notify( int nDocumentId, AdminUser user, int nEventType )
			throws DocumentException
	{
		// Reload document to have all data (ie : state_key !)
		Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );
		DocumentEvent event = new DocumentEvent( document, user, nEventType );

		_documentEvent.fire(event);

	}

	/**
	 * Add to the document all permitted actions according to the current user
	 * and
	 * using the current locale
	 * 
	 * @param user     The current user
	 * @param document The document
	 * @param locale   The Locale
	 */
	public void getActions( Document document, Locale locale, AdminUser user )
	{
		List < DocumentAction > listActions = DocumentActionHome.getActionsList( document, locale );
		RBACResource documentType = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType( ) );
		listActions = ( List < DocumentAction > ) RBACService.getAuthorizedActionsCollection( listActions, documentType,
				( User ) user );

		// Get all beans from the global ApplicationContext
		List < IDocumentActionsService > listActionServices = CDI.current( ).select( IDocumentActionsService.class )
				.stream( )
				.collect( Collectors.toList( ) );

		// Process all services
		for( IDocumentActionsService actionService : listActionServices )
		{
			listActions.addAll( actionService.getActions( document, locale, user ) );
		}

		document.setActions( listActions );
	}

	/**
	 * Get the published status of a document
	 * 
	 * @param document The document to get the published status of
	 */
	public void getPublishedStatus( Document document )
	{
		boolean isPublished = _publishingService.isPublished( document.getId( ) );
		int nPublishedStatus = 0;

		if( ! isPublished )
		{
			nPublishedStatus = 1;
		}

		document.setPublishedStatus( nPublishedStatus );
	}

	/**
	 * Build an HTML form for the document creation for a given document type
	 *
	 * @param strDocumentTypeCode The Document type code
	 * @param locale              The Locale
	 * @param strBaseUrl          The base Url
	 *
	 * @return The HTML form
	 */
	public String getCreateForm( String strDocumentTypeCode, Locale locale, String strBaseUrl )
	{
		StringBuilder sbForm = new StringBuilder( );
		DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strDocumentTypeCode );

		for( DocumentAttribute attribute : documentType.getAttributes( ) )
		{
			AttributeManager manager = _attributeService.getManager( attribute.getCodeAttributeType( ) );
			sbForm.append( manager.getCreateFormHtml( attribute, locale, strBaseUrl ) );
		}

		return sbForm.toString( );
	}

	/**
	 * Build an HTML form for the document modification for a given document ID.
	 * <b>Warning</b> : This method loads the binaries of the document.
	 *
	 * @param strDocumentId The Id of the document to modify
	 * @param locale        The Locale
	 * @param strBaseUrl    The base url
	 *
	 * @return The HTML form
	 */
	public String getModifyForm( String strDocumentId, Locale locale, String strBaseUrl )
	{
		int nDocumentId = IntegerUtils.convert( strDocumentId );
		Document document = DocumentHome.findByPrimaryKey( nDocumentId );

		if( document != null )
		{
			return getModifyForm( document, locale, strBaseUrl );
		}

		return StringUtils.EMPTY;
	}

	/**
	 * Build an HTML form for the document modification for a given document
	 *
	 * @param document   The document
	 * @param locale     The Locale
	 * @param strBaseUrl The base url
	 * @return The HTML form
	 */
	public String getModifyForm( Document document, Locale locale, String strBaseUrl )
	{
		StringBuilder sbForm = new StringBuilder( );
		DocumentType documentType = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType( ) );

		for( DocumentAttribute attribute : documentType.getAttributes( ) )
		{
			DocumentAttribute attributeDocument = getDocumentAttribute( document, attribute.getId( ) );

			if( attributeDocument != null )
			{
				AttributeManager manager = _attributeService.getManager( attributeDocument.getCodeAttributeType( ) );
				sbForm.append( manager.getModifyFormHtml( attributeDocument, document, locale, strBaseUrl ) );
			}
			else
			{
				AttributeManager manager = _attributeService.getManager( attribute.getCodeAttributeType( ) );
				sbForm.append( manager.getCreateFormHtml( attribute, locale, strBaseUrl ) );
			}
		}

		return sbForm.toString( );
	}

	/**
	 * Retrieve an attribute of document from its ID
	 * 
	 * @param document     The document
	 * @param nAttributeId The attribute ID
	 * @return The attribute if found, otherwise null
	 */
	private DocumentAttribute getDocumentAttribute( Document document, int nAttributeId )
	{
		for( DocumentAttribute attribute : document.getAttributes( ) )
		{
			if( attribute.getId( ) == nAttributeId )
			{
				return attribute;
			}
		}

		return null;
	}

	/**
	 * Check that a given user is allowed to access a document type for a given
	 * permission in a document space specified in parameter
	 * If permission is document creation, check if document creation is allowed
	 * for the specified space.
	 * 
	 * @param nIdSpace          the id of the document space
	 * @param strDocumentTypeId the id of the type document being considered
	 * @param strPermission     the permission needed
	 * @param user              the user trying to access the ressource
	 * @return true if the user can access the given resource with the given
	 *         permission, false otherwise
	 */
	public boolean isAuthorizedAdminDocument( int nIdSpace, String strDocumentTypeId, String strPermission,
			AdminUser user )
	{
		DocumentSpace documentSpace = DocumentSpaceHome.findByPrimaryKey( nIdSpace );
		boolean bPermissionCreate = strPermission.equals( DocumentTypeResourceIdService.PERMISSION_CREATE )
				? documentSpace.isDocumentCreationAllowed( )
				: true;

		return _documentSpacesService.isAuthorizedViewByWorkgroup( documentSpace.getId( ), user ) &&
				_documentSpacesService.isAuthorizedViewByRole( documentSpace.getId( ), user ) &&
				RBACService.isAuthorized( DocumentType.RESOURCE_TYPE, strDocumentTypeId, strPermission, ( User ) user ) &&
				bPermissionCreate;
	}

	/**
	 * Return the data of a document object
	 * 
	 * @param mRequest The MultipartHttpServletRequest
	 * @param document The document object
	 * @param locale   The locale
	 * @return data of document object
	 */
	public String getDocumentData( MultipartHttpServletRequest mRequest, Document document, Locale locale )
	{
		String strDocumentTitle = mRequest.getParameter( PARAMETER_DOCUMENT_TITLE );
		String strDocumentSummary = mRequest.getParameter( PARAMETER_DOCUMENT_SUMMARY );
		String strDocumentComment = mRequest.getParameter( PARAMETER_DOCUMENT_COMMENT );
		String strDateValidityEnd = mRequest.getParameter( PARAMETER_VALIDITY_END );
		String strDateValidityBegin = mRequest.getParameter( PARAMETER_VALIDITY_BEGIN );
		String strMailingListId = mRequest.getParameter( PARAMETER_MAILING_LIST );
		int nMailingListId = IntegerUtils.convert( strMailingListId, 0 );
		String strPageTemplateDocumentId = mRequest.getParameter( PARAMETER_PAGE_TEMPLATE_DOCUMENT_ID );
		int nPageTemplateDocumentId = IntegerUtils.convert( strPageTemplateDocumentId, 0 );
		String strSkipPortlet = mRequest.getParameter( PARAMETER_SKIP_PORTLET );
		boolean bSkipPortlet = ( ( strSkipPortlet == null ) || "".equals( strSkipPortlet ) ) ? false : true;
		String strSkipCategories = mRequest.getParameter( PARAMETER_SKIP_CATEGORIES );
		boolean bSkipCategories = ( ( strSkipCategories == null ) || "".equals( strSkipCategories ) ) ? false : true;
		String [ ] arrayCategory = mRequest.getParameterValues( PARAMETER_CATEGORY );

		// Check for mandatory value
		if( StringUtils.isBlank( strDocumentTitle ) || StringUtils.isBlank( strDocumentSummary ) )
		{
			return AdminMessageService.getMessageUrl( mRequest, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		// Check for illegal character character
		if( StringUtil.containsHtmlSpecialCharacters( strDocumentTitle ) ||
				StringUtil.containsHtmlSpecialCharacters( strDocumentSummary ) )
		{
			return AdminMessageService.getMessageUrl( mRequest, Messages.MESSAGE_ILLEGAL_CHARACTER,
					AdminMessage.TYPE_STOP );
		}

		DocumentType documentType = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType( ) );
		List < DocumentAttribute > listAttributes = documentType.getAttributes( );

		for( DocumentAttribute attribute : listAttributes )
		{
			String strAdminMessage = setAttribute( attribute, document, mRequest, locale );

			if( strAdminMessage != null )
			{
				return strAdminMessage;
			}
		}

		Timestamp dateValidityBegin = null;
		Timestamp dateValidityEnd = null;

		if( ( strDateValidityBegin != null ) && ! strDateValidityBegin.equals( "" ) )
		{
			Date dateBegin = DateUtil.parseIsoDate( strDateValidityBegin );

			if( ( dateBegin == null ) )
			{
				return AdminMessageService.getMessageUrl( mRequest, MESSAGE_INVALID_DATEBEGIN, AdminMessage.TYPE_STOP );
			}

			dateValidityBegin = new Timestamp( dateBegin.getTime( ) );

			if( dateValidityBegin.before( new Timestamp( 0 ) ) )
			{
				return AdminMessageService.getMessageUrl( mRequest, MESSAGE_INVALID_DATE_BEFORE_70,
						AdminMessage.TYPE_STOP );
			}
		}

		if( ( strDateValidityEnd != null ) && ! strDateValidityEnd.equals( "" ) )
		{
			Date dateEnd = DateUtil.parseIsoDate( strDateValidityEnd );

			if( ( dateEnd == null ) )
			{
				return AdminMessageService.getMessageUrl( mRequest, MESSAGE_INVALID_DATEEND, AdminMessage.TYPE_STOP );
			}

			dateValidityEnd = new Timestamp( dateEnd.getTime( ) );

			if( dateValidityEnd.before( new Timestamp( 0 ) ) )
			{
				return AdminMessageService.getMessageUrl( mRequest, MESSAGE_INVALID_DATE_BEFORE_70,
						AdminMessage.TYPE_STOP );
			}
		}

		// validate period (dateEnd > dateBegin )
		if( ( dateValidityBegin != null ) && ( dateValidityEnd != null ) )
		{
			if( dateValidityEnd.before( dateValidityBegin ) )
			{
				return AdminMessageService.getMessageUrl( mRequest, MESSAGE_ERROR_DATEEND_BEFORE_DATEBEGIN,
						AdminMessage.TYPE_STOP );
			}
		}

		document.setTitle( strDocumentTitle );
		document.setSummary( strDocumentSummary );
		document.setComment( strDocumentComment );
		document.setDateValidityBegin( dateValidityBegin );
		document.setDateValidityEnd( dateValidityEnd );
		document.setMailingListId( nMailingListId );
		document.setPageTemplateDocumentId( nPageTemplateDocumentId );
		document.setSkipPortlet( bSkipPortlet );
		document.setSkipCategories( bSkipCategories );

		MetadataHandler hMetadata = documentType.metadataHandler( );

		if( hMetadata != null )
		{
			document.setXmlMetadata( hMetadata.getXmlMetadata( mRequest.getParameterMap( ) ) );
		}

		document.setAttributes( listAttributes );

		// Categories
		List < Category > listCategories = new ArrayList < Category >( );

		if( arrayCategory != null )
		{
			for( String strIdCategory : arrayCategory )
			{
				listCategories.add( CategoryHome.find( IntegerUtils.convert( strIdCategory ) ) );
			}
		}

		document.setCategories( listCategories );

		return null; // No error
	}

	/**
	 * Update the specify attribute with the mRequest parameters
	 *
	 * @param attribute The {@link DocumentAttribute} to update
	 * @param document  The {@link Document}
	 * @param mRequest  The multipart http request
	 * @param locale    The locale
	 * @return an admin message if error or null else
	 */
	private String setAttribute( DocumentAttribute attribute, Document document, MultipartHttpServletRequest mRequest,
			Locale locale )
	{
		String strParameterStringValue = mRequest.getParameter( attribute.getCode( ) );
		MultipartItem fileParameterBinaryValue = mRequest.getFile( attribute.getCode( ) );
		String strIsUpdatable = mRequest.getParameter( PARAMETER_ATTRIBUTE_UPDATE + attribute.getCode( ) );
		String strToResize = mRequest.getParameter( attribute.getCode( ) + PARAMETER_CROPPABLE );
		boolean bIsUpdatable = ( ( strIsUpdatable == null ) || strIsUpdatable.equals( "" ) ) ? false : true;
		boolean bToResize = ( ( strToResize == null ) || strToResize.equals( "" ) ) ? false : true;

		if( strParameterStringValue != null ) // If the field is a string
		{
			// Check for mandatory value
			if( attribute.isRequired( ) && strParameterStringValue.trim( ).equals( "" ) )
			{
				return AdminMessageService.getMessageUrl( mRequest, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
			}

			// Check for specific attribute validation
			AttributeManager manager = _attributeService.getManager( attribute.getCodeAttributeType( ) );
			String strValidationErrorMessage = manager.validateValue( attribute.getId( ), strParameterStringValue,
					locale );

			if( strValidationErrorMessage != null )
			{
				String [ ] listArguments =
				{ attribute.getName( ), strValidationErrorMessage };

				return AdminMessageService.getMessageUrl( mRequest, MESSAGE_ATTRIBUTE_VALIDATION_ERROR, listArguments,
						AdminMessage.TYPE_STOP );
			}

			attribute.setTextValue( strParameterStringValue );
		}
		else if( fileParameterBinaryValue != null ) // If the field is a file
		{
			attribute.setBinary( true );

			String strContentType = fileParameterBinaryValue.getContentType( );
			byte [ ] bytes = fileParameterBinaryValue.get( );
			String strFileName = fileParameterBinaryValue.getName( );
			String strExtension = FilenameUtils.getExtension( strFileName );

			AttributeManager manager = _attributeService.getManager( attribute.getCodeAttributeType( ) );

			if( ! bIsUpdatable )
			{
				// there is no new value then take the old file value
				DocumentAttribute oldAttribute = document.getAttribute( attribute.getCode( ) );

				if( ( oldAttribute != null ) && ( oldAttribute.getBinaryValue( ) != null ) &&
						( oldAttribute.getBinaryValue( ).length > 0 ) )
				{
					bytes = oldAttribute.getBinaryValue( );
					strContentType = oldAttribute.getValueContentType( );
					strFileName = oldAttribute.getTextValue( );
					strExtension = FilenameUtils.getExtension( strFileName );
				}
			}

			List < AttributeTypeParameter > parameters = manager.getExtraParametersValues( locale, attribute.getId( ) );

			String extensionList = StringUtils.EMPTY;

			if( CollectionUtils.isNotEmpty( parameters ) &&
					CollectionUtils.isNotEmpty( parameters.get( 0 ).getValueList( ) ) )
			{
				extensionList = parameters.get( 0 ).getValueList( ).get( 0 );
			}

			// Check for mandatory value
			if( attribute.isRequired( ) && ( ( bytes == null ) || ( bytes.length == 0 ) ) )
			{
				return AdminMessageService.getMessageUrl( mRequest, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
			}
			// Check file size against database limit
			else if( ( bytes != null ) && ( bytes.length > 0 ) )
			{
				long maxFileSize = getMaxAllowedFileSize( );
				if( bytes.length > maxFileSize )
				{
					Object [ ] params = new Object [ 3 ];
					params [0] = attribute.getName( );
					params [1] = formatFileSize( bytes.length );
					params [2] = formatFileSize( maxFileSize );

					return AdminMessageService.getMessageUrl( mRequest, MESSAGE_FILE_SIZE_ERROR, params,
							AdminMessage.TYPE_STOP );
				}
			}
			else if( StringUtils.isNotBlank( extensionList ) && ! extensionList.contains( strExtension ) )
			{
				Object [ ] params = new Object [ 2 ];
				params [0] = attribute.getName( );
				params [1] = extensionList;

				return AdminMessageService.getMessageUrl( mRequest, MESSAGE_EXTENSION_ERROR, params,
						AdminMessage.TYPE_STOP );
			}

			// Check for specific attribute validation
			String strValidationErrorMessage = manager.validateValue( attribute.getId( ), strFileName, locale );

			if( strValidationErrorMessage != null )
			{
				String [ ] listArguments =
				{ attribute.getName( ), strValidationErrorMessage };

				return AdminMessageService.getMessageUrl( mRequest, MESSAGE_ATTRIBUTE_VALIDATION_ERROR, listArguments,
						AdminMessage.TYPE_STOP );
			}

			if( bToResize && ! ArrayUtils.isEmpty( bytes ) )
			{
				// Resize image
				String strWidth = mRequest.getParameter( attribute.getCode( ) + PARAMETER_WIDTH );

				if( StringUtils.isBlank( strWidth ) || ! StringUtils.isNumeric( strWidth ) )
				{
					String [ ] listArguments =
					{
							attribute.getName( ),
							I18nService.getLocalizedString( MESSAGE_ATTRIBUTE_WIDTH_ERROR, mRequest.getLocale( ) )
					};

					return AdminMessageService.getMessageUrl( mRequest, MESSAGE_ATTRIBUTE_VALIDATION_ERROR,
							listArguments, AdminMessage.TYPE_STOP );
				}

				try
				{
					bytes = ImageUtils.resizeImage( bytes, Integer.valueOf( strWidth ) );
				}
				catch( IOException e )
				{
					return AdminMessageService.getMessageUrl( mRequest, MESSAGE_ATTRIBUTE_RESIZE_ERROR,
							AdminMessage.TYPE_STOP );
				}
			}

			attribute.setBinaryValue( bytes );
			attribute.setValueContentType( strContentType );
			attribute.setTextValue( strFileName );
		}

		return null;
	}

	/**
	 * Get the maximum allowed file size from MySQL max_allowed_packet configuration.
	 * This method queries the database to get the actual limit and caches it.
	 * 
	 * @return the maximum file size in bytes (defaults to 16MB if unable to retrieve)
	 */
	private long getMaxAllowedFileSize( )
	{
		try
		{
			// Query MySQL for max_allowed_packet value
			String strMaxPacket = DocumentHome.getMaxAllowedPacket( );
			if( StringUtils.isNotBlank( strMaxPacket ) )
			{
				// Parse the value (can be in format like "16777216" or "16M")
				long maxSize = parseMaxPacketSize( strMaxPacket );
				// Return 90% of max_allowed_packet to leave room for query overhead
				return (long) ( maxSize * 0.9 );
			}
		}
		catch( Exception e )
		{
			// Log error but don't fail, use default value
			AppLogService.error( "Unable to retrieve max_allowed_packet from MySQL, using default 16MB", e );
		}
		
		// Default: 16MB (MySQL default)
		return 16 * 1024 * 1024;
	}

	/**
	 * Parse MySQL max_allowed_packet value which can be in different formats
	 * (numeric bytes or with K/M/G suffix).
	 * 
	 * @param strValue the value to parse
	 * @return the size in bytes
	 */
	private long parseMaxPacketSize( String strValue )
	{
		strValue = strValue.trim( ).toUpperCase( );
		
		try
		{
			// Check if it has a suffix (K, M, G)
			if( strValue.matches( "\\d+[KMG]" ) )
			{
				long number = Long.parseLong( strValue.substring( 0, strValue.length( ) - 1 ) );
				char suffix = strValue.charAt( strValue.length( ) - 1 );
				
				switch( suffix )
				{
					case 'K':
						return number * 1024;
					case 'M':
						return number * 1024 * 1024;
					case 'G':
						return number * 1024 * 1024 * 1024;
				}
			}
			
			// No suffix, parse as bytes
			return Long.parseLong( strValue );
		}
		catch( NumberFormatException e )
		{
			AppLogService.error( "Unable to parse max_allowed_packet value: " + strValue, e );
			return 16 * 1024 * 1024; // Default 16MB
		}
	}

	/**
	 * Format file size in human-readable format (KB, MB, GB).
	 * 
	 * @param size the size in bytes
	 * @return formatted string like "2.5 MB"
	 */
	private String formatFileSize( long size )
	{
		if( size < 1024 )
		{
			return size + " B";
		}
		else if( size < 1024 * 1024 )
		{
			return String.format( "%.2f KB", size / 1024.0 );
		}
		else if( size < 1024 * 1024 * 1024 )
		{
			return String.format( "%.2f MB", size / ( 1024.0 * 1024.0 ) );
		}
		else
		{
			return String.format( "%.2f GB", size / ( 1024.0 * 1024.0 * 1024.0 ) );
		}
	}

	/**
	 * This method observes the initialization of the {@link ApplicationScoped}
	 * context.
	 * It ensures that this CDI beans are instantiated at the application startup.
	 *
	 * <p>
	 * This method is triggered automatically by CDI when the
	 * {@link ApplicationScoped} context is initialized,
	 * which typically occurs during the startup of the application server.
	 * </p>
	 *
	 * @param context the {@link ServletContext} that is initialized. This parameter
	 *                is observed
	 *                and injected automatically by CDI when the
	 *                {@link ApplicationScoped} context is initialized.
	 */
	public void initializedService( @Observes @Initialized( ApplicationScoped.class ) ServletContext context )
	{
		// This method is intentionally left empty to trigger CDI bean instantiation
	}
}
