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
package fr.paris.lutece.plugins.document.web;

import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.LegacyStylesheets;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeHome;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeParameter;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttributeHome;
import fr.paris.lutece.plugins.document.service.AttributeManager;
import fr.paris.lutece.plugins.document.service.AttributeService;
import fr.paris.lutece.plugins.document.service.LegacyStylesheetService;
import fr.paris.lutece.plugins.document.service.metadata.MetadataService;
import fr.paris.lutece.plugins.document.service.view.DocumentTemplateService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.cache.CacheService;
import fr.paris.lutece.portal.service.cache.CacheableService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JSP Bean for document type management
 */
@SessionScoped
@Named
public class DocumentTypeJspBean extends PluginAdminPageJspBean
{
	public static final String RIGHT_DOCUMENT_TYPES_MANAGEMENT = "DOCUMENT_TYPES_MANAGEMENT";
	private static final String TEMPLATE_MANAGE_DOCUMENT_TYPES = "admin/plugins/document/manage_document_types.html";
	private static final String TEMPLATE_CREATE_DOCUMENT_TYPE = "admin/plugins/document/create_document_type.html";
	private static final String TEMPLATE_MODIFY_DOCUMENT_TYPE = "admin/plugins/document/modify_document_type.html";
	private static final String TEMPLATE_ADD_ATTRIBUTE = "admin/plugins/document/add_document_type_attribute.html";
	private static final String TEMPLATE_MODIFY_ATTRIBUTE = "admin/plugins/document/modify_document_type_attribute.html";
	private static final String PROPERTY_PAGE_TITLE_CREATE_DOCUMENT_TYPE = "document.create_document_type.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_MODIFY_DOCUMENT_TYPE = "document.modify_document_type.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_ADD_ATTRIBUTE = "document.add_document_type_attribute.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_MODIFY_ATTRIBUTE = "document.modify_document_type_attribute.pageTitle";
	private static final String PROPERTY_CANNOT_DELETE_DOCUMENTS = "document.message.cannotRemoveTypeDocuments";
	private static final String PROPERTY_CONFIRM_DELETE_TYPE = "document.message.confirmDeleteType";
	private static final String PROPERTY_CONFIRM_DELETE_ATTRIBUTE = "document.message.confirmDeleteAttribute";
	private static final String PROPERTY_NO_THUMBNAIL_ATTRIBUTE = "document.documentType.noThumbnailAttribute";
	private static final String PROPERTY_CODE_ATTRIBUTE_BAD_FORMAT_CREATE = "document.add_document_type_attribute.codeAttribute.badFormat";
	private static final String PROPERTY_CODE_ATTRIBUTE_BAD_FORMAT_MODIFY = "document.modify_document_type_attribute.codeAttribute.badFormat";
	private static final String MESSAGE_DOCUMENT_ALREADY_EXIST = "document.message.documentType.errorAlreadyExist";
	private static final String MARK_DOCUMENT_TYPES_LIST = "document_types_list";
	private static final String MARK_THUMBNAIL_ATTRIBUTES_LIST = "thumbnail_attributes_list";
	private static final String MARK_DOCUMENT_TYPE = "document_type";
	private static final String MARK_ATTRIBUTE_TYPES_LIST = "attribute_types_list";
	private static final String MARK_DOCUMENT_TYPE_CODE = "document_type_code";
	private static final String MARK_ATTRIBUTE_TYPE_CODE = "attribute_type_code";
	private static final String MARK_ATTRIBUTE_TYPE_NAME = "attribute_type_name";
	private static final String MARK_ATTRIBUTE_EXTRAS_PARAMETERS = "attribute_parameters";
	private static final String MARK_ATTRIBUTE = "attribute";
	private static final String MARK_METADATA_HANDLERS_LIST = "metadata_handlers_list";
	private static final String MARK_REGULAR_EXPRESSION_TO_ADD_LIST = "regular_expression_to_add_list";
	private static final String MARK_REGULAR_EXPRESSION_ADDED_LIST = "regular_expression_added_list";
	private static final String PARAMETER_DOCUMENT_TYPE_CODE = "document_type_code";
	private static final String PARAMETER_NAME = "name";
	private static final String PARAMETER_OLD_CODE = "old_code";
	private static final String PARAMETER_CODE = "code";
	private static final String PARAMETER_DESCRIPTION = "description";
	private static final String PARAMETER_THUMBNAIL_ATTRIBUTE = "thumbnail_attribute";
	private static final String PARAMETER_ICON_URL = "icon_url";
	private static final String PARAMETER_REQUIRED = "required";
	private static final String PARAMETER_SEARCHABLE = "searchable";
	private static final String PARAMETER_ATTRIBUTE_TYPE_CODE = "attribute_type_code";
	private static final String PARAMETER_ATTRIBUTE_ID = "attribute_id";
	private static final String PARAMETER_INDEX = "index";
	private static final String PARAMETER_METADATA = "metadata";
	private static final String PARAMETER_ADMIN_TEMPLATE = "admin_template";
	private static final String PARAMETER_CONTENT_TEMPLATE = "content_template";
	private static final String PARAMETER_EXPRESSION_ID = "expression_id";
	private static final String PARAMETER_APPLY = "apply";
	private static final String PARAMETER_CANCEL = "cancel";
	private static final String PARAMETER_SAVE = "save";
	private static final String PARAMETER_SESSION = "session";
	private static final String JSP_MANAGE_DOCUMENT_TYPE = "jsp/admin/plugins/document/ManageDocumentTypes.jsp";
	private static final String JSP_MODIFY_DOCUMENT_TYPE = "ModifyDocumentType.jsp";
	private static final String JSP_URL_MODIFY_DOCUMENT_TYPE = "jsp/admin/plugins/document/ModifyDocumentType.jsp";
	private static final String JSP_DELETE_DOCUMENT_TYPE = "jsp/admin/plugins/document/DoDeleteDocumentType.jsp";
	private static final String JSP_DELETE_ATTRIBUTE = "jsp/admin/plugins/document/DoDeleteAttribute.jsp";
	private static final String JSP_MODIFY_DOCUMENT_TYPE_ATTRIBUTE = "ModifyDocumentTypeAttribute.jsp";
	private static final String JSP_ADD_DOCUMENT_TYPE_ATTRIBUTE = "AddDocumentTypeAttribute.jsp";
	private static final String SECURITY_TOKEN_ACTION = "jsp/admin/plugins/document/ManageDocumentTypes.jsp";
	private static final String JSP_DO_DISCARD_LEGACY_STYLESHEETS = "jsp/admin/plugins/document/DoDiscardLegacyStylesheets.jsp";
	private static final String MESSAGE_CONFIRM_DISCARD_LEGACY_STYLESHEETS = "document.message.confirmDiscardLegacyStylesheets";
	private static final String MARK_LEGACY_STYLESHEETS = "legacy_stylesheets";
	private static final String MARK_LEGACY_STYLESHEETS_TYPES = "legacy_stylesheets_types";
	private static final String MESSAGE_ADMIN_TEMPLATE_NOT_VALID = "document.message.adminTemplateNotValid";
	private static final String MESSAGE_CONTENT_TEMPLATE_NOT_VALID = "document.message.contentTemplateNotValid";
	private static final String MARK_DEFAULT_ADMIN_TEMPLATE = "default_admin_template";
	private static final String MARK_DEFAULT_CONTENT_TEMPLATE = "default_content_template";
	private static final String CHECK_ON = "on";
	// The caches of the rendered documents: document pages, portlets and portal pages
	private static final String [ ] CACHES_RENDERED_DOCUMENTS = { "Document Content Service", "PortletCacheService", "PageCacheService" };
	private static final String PATTERN_CODE = "[a-zA-Z0-9_\\-]+";
	private static final int NO_THUMBNAIL_ATTRIBUTE = 0;
	private String _strDocumentTypeCode;
	private DocumentAttribute _attribute;

	@Inject
	@Named( "document.AttributeService" )
	private AttributeService _attributeService;

	@Inject
	RegularExpressionService _regularExpressionService;
	 
	@Inject
	private Models _model;

	@Inject
	private DocumentTemplateService _documentTemplateService;

	@Inject
	private LegacyStylesheetService _legacyStylesheetService;

	@Inject
	@Pager( listBookmark = MARK_DOCUMENT_TYPES_LIST )
	private IPager < DocumentType, Void > _pager;

	/**
	 * Gets the Document Types Management Page
	 * 
	 * @param request The HTTP request
	 * @return The Document Types Management Page
	 */
	public String getManageDocumentTypes( HttpServletRequest request )
	{
		addPaginatorToModel( request, DocumentTypeHome.findAll( ), _model );
		_model.put( MARK_LEGACY_STYLESHEETS_TYPES, _legacyStylesheetService.getDocumentTypesWithLegacyStylesheets( ) );

		// _model.put( MARK_DOCUMENT_TYPES_LIST, DocumentTypeHome.findAll( ) );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DOCUMENT_TYPES, getLocale( ), _model );

		return getAdminPage( template.getHtml( ) );

	}

	/**
	 * Add Paginator to model map
	 * 
	 * @param request        The request
	 * @param listIdPersonne list of categories id
	 **/
	private void addPaginatorToModel( HttpServletRequest request, Collection < DocumentType > listDocumentType,
			Models model )
	{
		UrlItem url = new UrlItem( JSP_MANAGE_DOCUMENT_TYPE );
		String strUrl = url.getUrl( );

		_pager.withBaseUrl( strUrl )
				.withListItem( listDocumentType.stream( ).collect( Collectors.toList( ) ) )
				.populateModels( request, model, request.getLocale( ) );
	}

	/**
	 * Gets the document type creation page
	 * 
	 * @param request The HTTP request
	 * @return The document type creation page
	 */
	public String getCreateDocumentType( HttpServletRequest request )
	{
		setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_DOCUMENT_TYPE );

		_model.put( MARK_METADATA_HANDLERS_LIST, MetadataService.getMetadataHandlersList( getLocale( ) ) );

		_model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );
		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_DOCUMENT_TYPE, getLocale( ), _model );

		return getAdminPage( template.getHtml( ) );
	}

	/**
	 * Perform the document type creation
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doCreateDocumentType( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		String strName = request.getParameter( PARAMETER_NAME );
		String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
		String strCode = request.getParameter( PARAMETER_CODE );
		String strMetadata = request.getParameter( PARAMETER_METADATA );

		// Mandatory fields
		if( StringUtils.isBlank( strName ) || StringUtils.isBlank( strDescription ) || StringUtils.isBlank( strCode ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		// Check Accentuated Character
		if( ! checkDocumentTypeCodePattern( strCode ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MESSAGE_STRING_FORMAT, AdminMessage.TYPE_STOP );
		}

		if( DocumentTypeHome.findByPrimaryKey( strCode ) != null )
		{
			return AdminMessageService.getMessageUrl( request, MESSAGE_DOCUMENT_ALREADY_EXIST, AdminMessage.TYPE_STOP );
		}

		DocumentType documentType = new DocumentType( );
		documentType.setName( strName );
		documentType.setCode( strCode );
		documentType.setDescription( strDescription );
		documentType.setMetadataHandler( strMetadata );
		DocumentTypeHome.create( documentType );

		return JSP_MODIFY_DOCUMENT_TYPE + "?document_type_code=" + strCode;
	}

	/**
	 * Gets the document type modification page
	 * 
	 * @param request The HTTP request
	 * @return The document type modification page
	 */
	public String getModifyDocumentType( HttpServletRequest request )
	{
		setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_DOCUMENT_TYPE );

		String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

		if( strDocumentTypeCode != null )
		{
			_strDocumentTypeCode = strDocumentTypeCode;
		}
		else
		{
			strDocumentTypeCode = _strDocumentTypeCode;
		}

		DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strDocumentTypeCode );

		if( documentType == null )
		{
			return getManageDocumentTypes( request );
		}

		ReferenceList listAttributeTypes = AttributeTypeHome.getAttributeTypesList( getLocale( ) );

		_model.put( MARK_DOCUMENT_TYPE, documentType );
		_model.put( MARK_THUMBNAIL_ATTRIBUTES_LIST, getThumbnailAttributesList( documentType ) );
		_model.put( MARK_METADATA_HANDLERS_LIST, MetadataService.getMetadataHandlersList( getLocale( ) ) );
		_model.put( MARK_ATTRIBUTE_TYPES_LIST, listAttributeTypes );
		_model.put( MARK_DEFAULT_ADMIN_TEMPLATE, _documentTemplateService.getDefaultTemplateSource( true ) );
		_model.put( MARK_DEFAULT_CONTENT_TEMPLATE, _documentTemplateService.getDefaultTemplateSource( false ) );
		_model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );

		LegacyStylesheets legacyStylesheets = _legacyStylesheetService.getLegacyStylesheets( strDocumentTypeCode );

		if( legacyStylesheets != null )
		{
			_model.put( MARK_LEGACY_STYLESHEETS, legacyStylesheets );
		}

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_DOCUMENT_TYPE, getLocale( ), _model );

		return getAdminPage( template.getHtml( ) );
	}

	/**
	 * Perform document type modification creation
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doModifyDocumentType( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		String strOldCode = request.getParameter( PARAMETER_OLD_CODE );
		String strCode = request.getParameter( PARAMETER_CODE );
		String strName = request.getParameter( PARAMETER_NAME );
		String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
		String strIconUrl = request.getParameter( PARAMETER_ICON_URL );
		String strThumbnailAttribute = request.getParameter( PARAMETER_THUMBNAIL_ATTRIBUTE );
		int nThumbnailAttribute = IntegerUtils.convert( strThumbnailAttribute );
		String strMetadata = request.getParameter( PARAMETER_METADATA );

		// Check Accentuated Character
		if( ! checkDocumentTypeCodePattern( strCode ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MESSAGE_STRING_FORMAT, AdminMessage.TYPE_STOP );
		}

		// Mandatory fields
		if( StringUtils.isBlank( strName ) || StringUtils.isBlank( strDescription ) || StringUtils.isBlank( strCode ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strOldCode );
		documentType.setOldCode( strOldCode );
		documentType.setCode( strCode );
		documentType.setName( strName );
		documentType.setDefaultThumbnailUrl( strIconUrl );
		documentType.setThumbnailAttributeId( nThumbnailAttribute );
		documentType.setDescription( strDescription );
		documentType.setMetadataHandler( strMetadata );
		DocumentTypeHome.update( documentType );

		return getHomeUrl( request );
	}

	/**
	 * Gets ttribute creation page
	 * 
	 * @param request The HTTP request
	 * @return the html template
	 */
	public String getAddAttribute( HttpServletRequest request )
	{
		setPageTitleProperty( PROPERTY_PAGE_TITLE_ADD_ATTRIBUTE );

		String strAttributeTypeCode = request.getParameter( PARAMETER_ATTRIBUTE_TYPE_CODE );
		AttributeManager manager = _attributeService.getManager( strAttributeTypeCode );
		_strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

		if( manager == null || DocumentTypeHome.findByPrimaryKey( _strDocumentTypeCode ) == null )
		{
			return getManageDocumentTypes( request );
		}

		_model.put( MARK_ATTRIBUTE_TYPE_CODE, strAttributeTypeCode );
		_model.put( MARK_DOCUMENT_TYPE, DocumentTypeHome.findByPrimaryKey( _strDocumentTypeCode ) );

		// model.put( MARK_ATTRIBUTE_EXTRAS_PARAMETERS , manager.getExtraParameters(
		// getLocale() ) );
		String strSession = request.getParameter( PARAMETER_SESSION );

		if( StringUtils.isNotBlank( strSession ) )
		{
			_model.put( MARK_ATTRIBUTE, _attribute );
			_model.put( MARK_ATTRIBUTE_EXTRAS_PARAMETERS,
					HtmlMarkup.of( manager.getCreateParametersFormHtml( _attribute.getParameters( ), getLocale( ) ) ) );
		}
		else
		{
			_attribute = new DocumentAttribute( );
			_model.put( MARK_ATTRIBUTE_EXTRAS_PARAMETERS, HtmlMarkup.of( manager.getCreateParametersFormHtml( getLocale( ) ) ) );
		}

		ReferenceList listAttributeTypes = AttributeTypeHome.getAttributeTypesList( getLocale( ) );
		_model.put( MARK_ATTRIBUTE_TYPE_NAME, listAttributeTypes.toMap( ).get( strAttributeTypeCode ) );

		_model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );
		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ADD_ATTRIBUTE, getLocale( ), _model );

		return getAdminPage( template.getHtml( ) );
	}

	/**
	 * Perform attribute creation
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doAddAttribute( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		_attribute = new DocumentAttribute( );

		boolean bIsValid = validateCodeAttribute( request );

		if( ! bIsValid )
		{
			return AdminMessageService.getMessageUrl( request, PROPERTY_CODE_ATTRIBUTE_BAD_FORMAT_CREATE,
					AdminMessage.TYPE_STOP );
		}

		getAttributeData( request, _attribute );

		// The user has not clicked on "save", then there are some operations to proceed
		String strSave = request.getParameter( PARAMETER_SAVE );

		if( StringUtils.isBlank( strSave ) )
		{
			String strAttributeTypeCode = request.getParameter( PARAMETER_ATTRIBUTE_TYPE_CODE );
			UrlItem url = new UrlItem( JSP_ADD_DOCUMENT_TYPE_ATTRIBUTE );
			url.addParameter( PARAMETER_SESSION, PARAMETER_SESSION );
			url.addParameter( PARAMETER_ATTRIBUTE_TYPE_CODE, strAttributeTypeCode );
			url.addParameter( PARAMETER_DOCUMENT_TYPE_CODE, _strDocumentTypeCode );

			return url.getUrl( );
		}

		String strValidateMessage = getAttributeValidationMessage( _attribute );

		if( strValidateMessage != null )
		{
			return AdminMessageService.getMessageUrl( request, strValidateMessage, AdminMessage.TYPE_STOP );
		}

		DocumentAttributeHome.create( _attribute );

		if( request.getParameter( PARAMETER_APPLY ) != null )
		{
			UrlItem url = new UrlItem( JSP_MODIFY_DOCUMENT_TYPE_ATTRIBUTE );
			url.addParameter( PARAMETER_ATTRIBUTE_ID, _attribute.getId( ) );

			return url.getUrl( );
		}

		return JSP_MODIFY_DOCUMENT_TYPE;
	}

	/**
	 * Perform attribute modification
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doModifyAttribute( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		if( request.getParameter( PARAMETER_CANCEL ) != null )
		{
			UrlItem url = new UrlItem( JSP_MODIFY_DOCUMENT_TYPE );

			return url.getUrl( );
		}

		String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
		int nAttributeId = IntegerUtils.convert( strAttributeId );
		_attribute = DocumentAttributeHome.findByPrimaryKey( nAttributeId );

		boolean bIsValid = validateCodeAttribute( request );

		if( ! bIsValid )
		{
			return AdminMessageService.getMessageUrl( request, PROPERTY_CODE_ATTRIBUTE_BAD_FORMAT_MODIFY,
					AdminMessage.TYPE_STOP );
		}

		getAttributeData( request, _attribute );

		// The user has not clicked on "save", then there are some operations to proceed
		String strSave = request.getParameter( PARAMETER_SAVE );

		if( StringUtils.isBlank( strSave ) )
		{
			UrlItem url = new UrlItem( JSP_MODIFY_DOCUMENT_TYPE_ATTRIBUTE );
			url.addParameter( PARAMETER_SESSION, PARAMETER_SESSION );
			url.addParameter( PARAMETER_ATTRIBUTE_ID, nAttributeId );

			return url.getUrl( );
		}

		String strValidateMessage = getAttributeValidationMessage( _attribute );

		if( strValidateMessage != null )
		{
			return AdminMessageService.getMessageUrl( request, strValidateMessage, AdminMessage.TYPE_STOP );
		}

		DocumentAttributeHome.update( _attribute );

		return JSP_MODIFY_DOCUMENT_TYPE;
	}

	private void getAttributeData( HttpServletRequest request, DocumentAttribute attribute )
	{
		String strName = request.getParameter( PARAMETER_NAME );
		String strCode = request.getParameter( PARAMETER_CODE ).trim( ).toLowerCase( );
		String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
		String strRequired = request.getParameter( PARAMETER_REQUIRED );
		String strSearchable = request.getParameter( PARAMETER_SEARCHABLE );
		String strAttributeTypeCode = request.getParameter( PARAMETER_ATTRIBUTE_TYPE_CODE );
		String [ ] arrayStrValues;
		List < String > listValues = new ArrayList < String >( );
		List < AttributeTypeParameter > listParameters;
		AttributeManager manager = _attributeService.getManager( strAttributeTypeCode );

		String strSave = request.getParameter( PARAMETER_SAVE );

		if( StringUtils.isBlank( strSave ) )
		{
			listParameters = manager.getValueParameters( request, getLocale( ) );
		}
		else
		{
			listParameters = manager.getExtraParameters( getLocale( ) );

			for( AttributeTypeParameter parameter : listParameters )
			{
				arrayStrValues = request.getParameterValues( parameter.getName( ) );

				if( arrayStrValues != null )
				{
					listValues.addAll( Arrays.asList( arrayStrValues ) );
				}

				parameter.setValueList( listValues );
				listValues.clear( );
			}
		}

		attribute.setName( strName );
		attribute.setCode( strCode );
		attribute.setDescription( strDescription );
		attribute.setRequired( ( strRequired != null ) && ( strRequired.equals( CHECK_ON ) ) );
		attribute.setSearchable( ( strSearchable != null ) && ( strSearchable.equals( CHECK_ON ) ) );
		attribute.setCodeDocumentType( _strDocumentTypeCode );
		attribute.setCodeAttributeType( strAttributeTypeCode );

		if( attribute.getAttributeOrder( ) == 0 )
		{
			// Order is not defined (creation). Put this attribute at the end of the list
			DocumentType documentType = DocumentTypeHome.findByPrimaryKey( _strDocumentTypeCode );
			attribute.setAttributeOrder( documentType.getAttributes( ).size( ) + 1 );
		}

		attribute.setParameters( listParameters );
	}

	/**
	 * Validate if the code has no spaces and no caps
	 * 
	 * @param request the HTTPrequest
	 * @return true if the codeAttribute is valid, false otherwise
	 */
	private boolean validateCodeAttribute( HttpServletRequest request )
	{
		String strCode = request.getParameter( PARAMETER_CODE ).trim( ).toLowerCase( );

		if( ( strCode != null ) && ( strCode.length( ) == 0 ) )
		{
			return true;
		}

		boolean isOK = false;

		if( ( strCode != null ) && ( strCode.length( ) > 0 ) )
		{
			isOK = StringUtil.checkCodeKey( strCode );
		}

		return isOK;
	}

	private String getAttributeValidationMessage( DocumentAttribute attribute )
	{
		String strMessage = null;

		// Mandatory fields
		if( StringUtils.isBlank( attribute.getName( ) ) || StringUtils.isBlank( attribute.getDescription( ) ) ||
				StringUtils.isBlank( attribute.getCode( ) ) )
		{
			return Messages.MANDATORY_FIELDS;
		}

		AttributeManager manager = _attributeService.getManager( attribute.getCodeAttributeType( ) );
		String strValidationErrorMessageKey = manager.validateValueParameters( attribute.getParameters( ),
				getLocale( ) );

		if( strValidationErrorMessageKey != null )
		{
			return strValidationErrorMessageKey;
		}

		return strMessage;
	}

	/**
	 * Gets the modification page
	 * 
	 * @param request The HTTP request
	 * @return The modification page
	 */
	public String getModifyAttribute( HttpServletRequest request )
	{
		setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_ATTRIBUTE );

		String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
		int nAttributeId = IntegerUtils.convert( strAttributeId );
		DocumentAttribute attribute = DocumentAttributeHome.findByPrimaryKey( nAttributeId );

		if( attribute == null )
		{
			return getManageDocumentTypes( request );
		}

		_strDocumentTypeCode = attribute.getCodeDocumentType( );

		AttributeManager manager = _attributeService.getManager( attribute.getCodeAttributeType( ) );

		ReferenceList refListRegularExpressionToAdd = new ReferenceList( );
		List < RegularExpression > listRegularExpressionAdded = new ArrayList < RegularExpression >( );

		// Checks if the regularexpression plugin is enabled
		if( _regularExpressionService.isAvailable( ) )
		{
			// Gets the list of regular expressions already added
			for( Integer nExpressionId : DocumentAttributeHome
					.getListRegularExpressionKeyByIdAttribute( nAttributeId ) )
			{
				listRegularExpressionAdded.add( _regularExpressionService.getRegularExpressionByKey( nExpressionId ) );
			}

			// Defines the reference list of regular expressions which could be added
			for( RegularExpression regularExpression : _regularExpressionService.getAllRegularExpression( ) )
			{
				if( ! listRegularExpressionAdded.contains( regularExpression ) )
				{
					refListRegularExpressionToAdd.addItem( regularExpression.getIdExpression( ),
							regularExpression.getTitle( ) );
				}
			}
		}

		_model.put( MARK_DOCUMENT_TYPE_CODE, _strDocumentTypeCode );
		_model.put( MARK_DOCUMENT_TYPE, DocumentTypeHome.findByPrimaryKey( _strDocumentTypeCode ) );
		_model.put( MARK_ATTRIBUTE_TYPE_CODE, attribute.getCodeAttributeType( ) );

		// model.put( MARK_ATTRIBUTE_EXTRAS_PARAMETERS , listParameters );
		String strSession = request.getParameter( PARAMETER_SESSION );

		if( StringUtils.isNotBlank( strSession ) )
		{
			_model.put( MARK_ATTRIBUTE, _attribute );
			_model.put( MARK_ATTRIBUTE_EXTRAS_PARAMETERS,
					HtmlMarkup.of( manager.getCreateParametersFormHtml( _attribute.getParameters( ), getLocale( ) ) ) );
		}
		else
		{
			_attribute = new DocumentAttribute( );
			_model.put( MARK_ATTRIBUTE, attribute );
			_model.put( MARK_ATTRIBUTE_EXTRAS_PARAMETERS,
					HtmlMarkup.of( manager.getModifyParametersFormHtml( getLocale( ), nAttributeId ) ) );
		}

		// Regular expressions
		_model.put( MARK_REGULAR_EXPRESSION_TO_ADD_LIST, refListRegularExpressionToAdd );
		_model.put( MARK_REGULAR_EXPRESSION_ADDED_LIST, listRegularExpressionAdded );

		_model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );
		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_ATTRIBUTE, getLocale( ), _model );

		return getAdminPage( template.getHtml( ) );
	}

	/**
	 * Confirm the deletion of a document type
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doConfirmDelete( HttpServletRequest request )
	{
		String strCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );
		String strDeleteUrl = JSP_DELETE_DOCUMENT_TYPE + "?" + PARAMETER_DOCUMENT_TYPE_CODE + "=" + strCode + "&" + SecurityTokenService.PARAMETER_TOKEN + "="
				+ getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION );
		String strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_CONFIRM_DELETE_TYPE,null,null, strDeleteUrl,null,
				AdminMessage.TYPE_CONFIRMATION,null,JSP_MANAGE_DOCUMENT_TYPE );

		// Check if this type has documents
		if( DocumentTypeHome.checkDocuments( strCode ) )
		{
			strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_CANNOT_DELETE_DOCUMENTS,
					AdminMessage.TYPE_STOP );
		}

		return strUrl;
	}

	/**
	 * Perform the deletion of a document type
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doDeleteDocumentType( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		String strCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

		if( strCode != null )
		{
			DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strCode );

			List < DocumentAttribute > listDocumentAttributes = documentType.getAttributes( );

			if( listDocumentAttributes != null )
			{
				for( DocumentAttribute docAttribute : listDocumentAttributes )
				{
					DocumentAttributeHome.remove( docAttribute.getId( ) );
				}
			}

			DocumentTypeHome.remove( strCode );
		}

		return getHomeUrl( request );
	}

	/**
	 * Confirm the deletion of an attribute
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doConfirmDeleteAttribute( HttpServletRequest request )
	{
		String strCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );
		String strId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
		String strDeleteUrl = JSP_DELETE_ATTRIBUTE + "?" + PARAMETER_ATTRIBUTE_ID + "=" + strId + "&" + SecurityTokenService.PARAMETER_TOKEN + "="
				+ getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION );
		String strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_CONFIRM_DELETE_ATTRIBUTE,null,null, strDeleteUrl,null,
				AdminMessage.TYPE_CONFIRMATION,null, JSP_URL_MODIFY_DOCUMENT_TYPE + "?" + PARAMETER_DOCUMENT_TYPE_CODE + "=" + strCode );
		
		return strUrl;
	}

	/**
	 * Perform the attribute deletion
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doDeleteAttribute( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		String strId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
		int nId = IntegerUtils.convert( strId );
		DocumentAttributeHome.remove( nId );

		return JSP_MODIFY_DOCUMENT_TYPE;
	}

	/**
	 * Perform the move up action
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doAttributeMoveUp( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		String strIndex = request.getParameter( PARAMETER_INDEX );
		int nIndex = IntegerUtils.convert( strIndex );

		if( nIndex > 1 )
		{
			DocumentType documentType = DocumentTypeHome.findByPrimaryKey( _strDocumentTypeCode );
			List < DocumentAttribute > list = documentType.getAttributes( );
			DocumentAttribute attribute1 = list.get( nIndex - 1 );
			DocumentAttribute attribute2 = list.get( nIndex - 2 );
			DocumentTypeHome.reorderAttributes( attribute1.getId( ), nIndex - 1, attribute2.getId( ), nIndex );
		}

		return JSP_MODIFY_DOCUMENT_TYPE;
	}

	/**
	 * Perform the move down action
	 * 
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doAttributeMoveDown( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		String strIndex = request.getParameter( PARAMETER_INDEX );
		int nIndex = IntegerUtils.convert( strIndex );
		DocumentType documentType = DocumentTypeHome.findByPrimaryKey( _strDocumentTypeCode );
		List < DocumentAttribute > list = documentType.getAttributes( );

		if( nIndex < list.size( ) )
		{
			DocumentAttribute attribute1 = list.get( nIndex - 1 );
			DocumentAttribute attribute2 = list.get( nIndex );
			DocumentTypeHome.reorderAttributes( attribute1.getId( ), nIndex + 1, attribute2.getId( ), nIndex );
		}

		return JSP_MODIFY_DOCUMENT_TYPE;
	}

	/**
	 * Saves the FreeMarker templates of the document type. A blank template means that the default template is used. The templates are executed
	 * on the site: their modification is protected by a security token, and the caches of the rendered documents are reset.
	 * 
	 * @param request The {@link HttpServletRequest}
	 * @return The URL to go after performing the action
	 */
	public String doModifyTemplates( HttpServletRequest request )
	{
		if( ! getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		String strCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );
		DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strCode );

		if( documentType == null )
		{
			return getHomeUrl( request );
		}

		String strAdminTemplate = StringUtils.trimToNull( request.getParameter( PARAMETER_ADMIN_TEMPLATE ) );
		String strContentTemplate = StringUtils.trimToNull( request.getParameter( PARAMETER_CONTENT_TEMPLATE ) );

		String strError = _documentTemplateService.validateTemplate( documentType, strAdminTemplate, true, getLocale( ) );

		if( strError != null )
		{
			return AdminMessageService.getMessageUrl( request, MESSAGE_ADMIN_TEMPLATE_NOT_VALID, new Object [ ] { strError },
					AdminMessage.TYPE_STOP );
		}

		strError = _documentTemplateService.validateTemplate( documentType, strContentTemplate, false, getLocale( ) );

		if( strError != null )
		{
			return AdminMessageService.getMessageUrl( request, MESSAGE_CONTENT_TEMPLATE_NOT_VALID, new Object [ ] { strError },
					AdminMessage.TYPE_STOP );
		}

		DocumentTypeHome.storeTemplates( strCode, strAdminTemplate, strContentTemplate );

		// clear the caches holding the documents rendered with these templates, not every cache of the webapp
		for( String strCacheName : CACHES_RENDERED_DOCUMENTS )
		{
			CacheableService<?, ?> cache = CacheService.getCache( strCacheName );
			if( cache != null )
			{
				cache.resetCache( );
			}
		}

		UrlItem url = new UrlItem( JSP_MODIFY_DOCUMENT_TYPE );
		url.addParameter( PARAMETER_DOCUMENT_TYPE_CODE, strCode );

		return url.getUrl( );
	}

	/**
	 * Asks the confirmation of discarding the XSL stylesheets of the versions before 7.0.0 of a document type
	 * 
	 * @param request The {@link HttpServletRequest}
	 * @return The URL of the confirmation message
	 */
	public String doConfirmDiscardLegacyStylesheets( HttpServletRequest request )
	{
		String strCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

		UrlItem url = new UrlItem( JSP_DO_DISCARD_LEGACY_STYLESHEETS );
		url.addParameter( PARAMETER_DOCUMENT_TYPE_CODE, strCode );
		url.addParameter( SecurityTokenService.PARAMETER_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );

		return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DISCARD_LEGACY_STYLESHEETS, new Object [ ] { strCode }, url.getUrl( ),
				AdminMessage.TYPE_CONFIRMATION );
	}

	/**
	 * Discards the XSL stylesheets of the versions before 7.0.0 of a document type, once rewritten as FreeMarker templates
	 * 
	 * @param request The {@link HttpServletRequest}
	 * @return The URL to go after performing the action
	 */
	public String doDiscardLegacyStylesheets( HttpServletRequest request )
	{
		if( ! getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		String strCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );
		_legacyStylesheetService.discardLegacyStylesheets( strCode );

		UrlItem url = new UrlItem( JSP_MODIFY_DOCUMENT_TYPE );
		url.addParameter( PARAMETER_DOCUMENT_TYPE_CODE, strCode );

		return url.getUrl( );
	}

	private ReferenceList getThumbnailAttributesList( DocumentType documentType )
	{
		ReferenceList list = new ReferenceList( );

		String strNoAttribute = I18nService.getLocalizedString( PROPERTY_NO_THUMBNAIL_ATTRIBUTE, getLocale( ) );
		list.addItem( NO_THUMBNAIL_ATTRIBUTE, strNoAttribute );

		for( DocumentAttribute attribute : documentType.getAttributes( ) )
		{
			AttributeManager manager = _attributeService.getManager( attribute.getCodeAttributeType( ) );

			if( manager.canBeUsedAsThumbnail( ) )
			{
				list.addItem( attribute.getId( ), attribute.getName( ) );
			}
		}

		return list;
	}

	/**
	 * Inserts a regular expression in the attribute
	 *
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doInsertRegularExpression( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		// Gets the document attribute identifier
		String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
		int nAttributeId = IntegerUtils.convert( strAttributeId );

		// Gets the selected regular expression
		String strExpressionId = request.getParameter( PARAMETER_EXPRESSION_ID );

		if( IntegerUtils.isNumeric( strExpressionId ) )
		{
			int nExpressionId = IntegerUtils.convert( strExpressionId );

			boolean bIsDuplicated = false;

			for( Integer nCurrentExpression : DocumentAttributeHome.getListRegularExpressionKeyByIdAttribute(
					nAttributeId ) )
			{
				if( nExpressionId == nCurrentExpression )
				{
					bIsDuplicated = true;

					break;
				}
			}

			if( ! bIsDuplicated )
			{
				// Inserts a regular expression in the attribute
				DocumentAttributeHome.insertRegularExpression( nAttributeId, nExpressionId );
			}
		}

		UrlItem url = new UrlItem( JSP_MODIFY_DOCUMENT_TYPE_ATTRIBUTE );
		url.addParameter( PARAMETER_ATTRIBUTE_ID, nAttributeId );

		return url.getUrl( );
	}

	/**
	 * Deletes a regular expression in the attribute
	 *
	 * @param request The HTTP request
	 * @return The URL to go after performing the action
	 */
	public String doDeleteRegularExpression( HttpServletRequest request )
	{
		if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
		}

		// Gets the document attribute identifier
		String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
		int nAttributeId = IntegerUtils.convert( strAttributeId );

		// Gets the selected regular expression
		String strExpressionId = request.getParameter( PARAMETER_EXPRESSION_ID );
		int nExpressionId = IntegerUtils.convert( strExpressionId );

		// Deletes a regular expression in the attribute
		DocumentAttributeHome.deleteRegularExpression( nAttributeId, nExpressionId );

		UrlItem url = new UrlItem( JSP_MODIFY_DOCUMENT_TYPE_ATTRIBUTE );
		url.addParameter( PARAMETER_ATTRIBUTE_ID, nAttributeId );

		return url.getUrl( );
	}

	/**
	 * Check if there is no diacritics character
	 * 
	 * @param strInput The input string
	 * @return
	 */
	private boolean checkDocumentTypeCodePattern( String strInput )
	{
		return strInput.matches( PATTERN_CODE );
	}
}
