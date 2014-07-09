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
import fr.paris.lutece.portal.service.cache.CacheableService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * This Service manages document actions (create, move, delete, validate ...)
 * and notify listeners.
 */
public class DocumentService
{
    //PARAMETERS
    private static final String PARAMETER_DOCUMENT_TITLE = "document_title";
    private static final String PARAMETER_DOCUMENT_SUMMARY = "document_summary";
    private static final String PARAMETER_DOCUMENT_COMMENT = "document_comment";
    private static final String PARAMETER_VALIDITY_BEGIN = "document_validity_begin";
    private static final String PARAMETER_VALIDITY_END = "document_validity_end";
    private static final String PARAMETER_MAILING_LIST = "mailinglists";
    private static final String PARAMETER_PAGE_TEMPLATE_DOCUMENT_ID = "page_template_id";
    private static final String PARAMETER_CATEGORY = "category_id";
    private static final String PARAMETER_ATTRIBUTE_UPDATE = "update_";
    private static final String PARAMETER_CROPPABLE = "_croppable";
    private static final String PARAMETER_WIDTH = "_width";

    //MESSAGES
    private static final String MESSAGE_ERROR_DATEEND_BEFORE_DATEBEGIN = "document.message.dateEndBeforeDateBegin";
    private static final String MESSAGE_INVALID_DATEEND = "document.message.invalidDateEnd";
    private static final String MESSAGE_INVALID_DATEBEGIN = "document.message.invalidDateBegin";
    private static final String MESSAGE_INVALID_DATE_BEFORE_70 = "document.message.invalidDate.before1970";
    private static final String MESSAGE_ATTRIBUTE_VALIDATION_ERROR = "document.message.attributeValidationError";
    private static final String MESSAGE_ATTRIBUTE_WIDTH_ERROR = "document.message.widthError";
    private static final String MESSAGE_ATTRIBUTE_RESIZE_ERROR = "document.message.resizeError";
    private static final String MESSAGE_EXTENSION_ERROR = "document.message.extensionError";
    private static DocumentService _singleton = new DocumentService(  );
    private static final String TAG_DOCUMENT_ID = "document-id";
    private static final String TAG_DOCUMENT_TITLE = "document-title";
    private static final String TAG_DOCUMENT_SUMMARY = "document-summary";
    private static final String TAG_DOCUMENT_DATE_BEGIN = "document-date-begin";
    private static final String TAG_DOCUMENT_DATE_END = "document-date-end";
    private static final String TAG_DOCUMENT_CATEGORIES = "document-categories";
    private static final String TAG_DOCUMENT_CATEGORY = "category";
    private static final String TAG_CDATA_BEGIN = "<![CDATA[";
    private static final String TAG_CDATA_END = "]]>";
    private static DocumentEventListernersManager _managerEventListeners;

    /** Creates a new instance of DocumentService */
    private DocumentService(  )
    {
        _managerEventListeners = SpringContextService.getBean( "document.documentEventListernersManager" );
    }

    /**
     * Get the unique instance of the service
     *
     * @return The unique instance
     */
    public static DocumentService getInstance(  )
    {
        return _singleton;
    }

    /**
     * Build an XML document that contains document's data.
     *
     * @param document The document
     * @return An XML fragment containing document's data
     */
    String buildXmlContent( Document document )
    {
        StringBuffer sbXml = new StringBuffer(  );
        XmlUtil.beginElement( sbXml, document.getCodeDocumentType(  ) );

        XmlUtil.addElement( sbXml, TAG_DOCUMENT_ID, document.getId(  ) );
        XmlUtil.addElement( sbXml, TAG_DOCUMENT_TITLE, TAG_CDATA_BEGIN + document.getTitle(  ) + TAG_CDATA_END );
        XmlUtil.addElement( sbXml, TAG_DOCUMENT_SUMMARY, TAG_CDATA_BEGIN + document.getSummary(  ) + TAG_CDATA_END );
        XmlUtil.addElement( sbXml, TAG_DOCUMENT_DATE_BEGIN,
            DateUtil.getDateString( document.getDateValidityBegin(  ), I18nService.getDefaultLocale(  ) ) );
        XmlUtil.addElement( sbXml, TAG_DOCUMENT_DATE_END,
            DateUtil.getDateString( document.getDateValidityEnd(  ), I18nService.getDefaultLocale(  ) ) );
        XmlUtil.addElement( sbXml, TAG_DOCUMENT_CATEGORIES, buildXmlCategories( document ) );

        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType(  ) );

        for ( DocumentAttribute attribute : documentType.getAttributes(  ) )
        {
            DocumentAttribute attributeDocument = getDocumentAttribute( document, attribute.getId(  ) );

            if ( attributeDocument != null )
            {
                AttributeManager manager = AttributeService.getInstance(  )
                                                           .getManager( attributeDocument.getCodeAttributeType(  ) );
                XmlUtil.addElement( sbXml, document.getCodeDocumentType(  ) + "-" + attribute.getCode(  ),
                    manager.getAttributeXmlValue( document, attributeDocument ) );
            }
        }

        XmlUtil.endElement( sbXml, document.getCodeDocumentType(  ) );

        return sbXml.toString(  );
    }

    /**
     * Build XML content for the categories
     * @param document The document
     * @return An XML fragment containing document's categories
     */
    private String buildXmlCategories( Document document )
    {
        StringBuffer strListCategories = new StringBuffer(  );

        if ( ( document != null ) && ( document.getCategories(  ) != null ) && !document.getCategories(  ).isEmpty(  ) )
        {
            for ( Category category : document.getCategories(  ) )
            {
                XmlUtil.addElement( strListCategories, TAG_DOCUMENT_CATEGORY,
                    TAG_CDATA_BEGIN + category.getName(  ) + TAG_CDATA_END );
            }
        }

        return strListCategories.toString(  );
    }

    /**
     * Change the state of the document
     *
     * @param document The document
     * @param user The user doing the action
     * @param nStateId The new state Id
     * @throws DocumentException raise when error occurs in event or rule
     */
    public void changeDocumentState( Document document, AdminUser user, int nStateId )
        throws DocumentException
    {
        document.setStateId( nStateId );
        DocumentHome.update( document, false );

        notify( document.getId(  ), user, DocumentEvent.DOCUMENT_STATE_CHANGED );
    }

    /**
     * Create a new document
     *
     * @param document The document
     * @param user The user doing the action
     * @throws DocumentException raise when error occurs in event or rule
     */
    public void createDocument( Document document, AdminUser user )
        throws DocumentException
    {
        document.setId( DocumentHome.newPrimaryKey(  ) );
        document.setDateCreation( new Timestamp( new java.util.Date(  ).getTime(  ) ) );
        document.setDateModification( new Timestamp( new java.util.Date(  ).getTime(  ) ) );
        document.setXmlWorkingContent( buildXmlContent( document ) );

        DocumentHome.create( document );

        notify( document.getId(  ), user, DocumentEvent.DOCUMENT_CREATED );
    }

    /**
     * Modify a the content of a document
     *
     * @param document The document
     * @param user The user doing the action
     * @throws DocumentException raise when error occurs in event or rule
     */
    public void modifyDocument( Document document, AdminUser user )
        throws DocumentException
    {
        document.setDateModification( new Timestamp( new java.util.Date(  ).getTime(  ) ) );
        document.setXmlWorkingContent( buildXmlContent( document ) );
        DocumentHome.update( document, true );

        notify( document.getId(  ), user, DocumentEvent.DOCUMENT_CONTENT_MODIFIED );
    }

    /**
     * Validate a document
     * @param nStateId The new state id for a validated document
     * @param document The document
     * @param user The user doing the action
     * @throws DocumentException raise when error occurs in event or rule
     */
    public void validateDocument( Document document, AdminUser user, int nStateId )
        throws DocumentException
    {
        document.setStateId( nStateId );
        // Copy the working content into the validated content
        document.setXmlValidatedContent( document.getXmlWorkingContent(  ) );
        DocumentHome.update( document, false );
        DocumentHome.validateAttributes( document.getId(  ) );

        List<DocumentAttribute> listAttributes = document.getAttributes(  );

        for ( DocumentAttribute attribute : listAttributes )
        {
            DocumentResourceServlet.putInCache( document.getId(  ), attribute.getId(  ) );
        }

        for ( CacheableService cs : PortalService.getCacheableServicesList(  ) )
        {
            if ( cs.getClass(  ).equals( DocumentContentService.class ) )
            {
                for ( int nIdPortlet : DocumentPortletHome.findPortletForDocument( document.getId(  ) ) )
                {
                    ( (DocumentContentService) cs ).removeFromCache( Integer.toString( document.getId(  ) ),
                        Integer.toString( nIdPortlet ) );
                }
            }
        }

        notify( document.getId(  ), user, DocumentEvent.DOCUMENT_STATE_CHANGED );
    }

    /**
     * Archive a document
     * @param nStateId The new state id for a validated document
     * @param document The document
     * @param user The user doing the action
     * @throws DocumentException raise when error occurs in event or rule
     */
    public void archiveDocument( Document document, AdminUser user, int nStateId )
        throws DocumentException
    {
        // Get the list of portlets linked with document 
        Collection<Portlet> listPortlet = PublishingService.getInstance(  )
                                                           .getPortletsByDocumentId( Integer.toString( 
                    document.getId(  ) ) );

        for ( Portlet portlet : listPortlet )
        {
            //If document is published, unpublish it
            if ( PublishingService.getInstance(  ).isPublished( document.getId(  ), portlet.getId(  ) ) )
            {
                PublishingService.getInstance(  ).unPublish( document.getId(  ), portlet.getId(  ) );
            }

            //Unassign Document to portlet
            PublishingService.getInstance(  ).unAssign( document.getId(  ), portlet.getId(  ) );
        }

        document.setStateId( nStateId );
        DocumentHome.update( document, false );

        notify( document.getId(  ), user, DocumentEvent.DOCUMENT_STATE_CHANGED );
    }

    /**
     * Move a document from a space to another
     *
     * @param document The document
     * @param user The user doing the action
     * @param nNewSpace The Id of the destination space
     * @throws DocumentException raise when error occurs in event or rule
     */
    public void moveDocument( Document document, AdminUser user, int nNewSpace )
        throws DocumentException
    {
        document.setSpaceId( nNewSpace );
        DocumentHome.update( document, false );

        notify( document.getId(  ), user, DocumentEvent.DOCUMENT_MOVED );
    }

    /**
     * Notify an event to all listeners
     *
     * @param nDocumentId The document Id
     * @param user The user doing the action
     * @param nEventType The type of event
     * @throws DocumentException raise when error occurs in event or rule
     */
    private void notify( int nDocumentId, AdminUser user, int nEventType )
        throws DocumentException
    {
        // Reload document to have all data (ie : state_key !)
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );
        DocumentEvent event = new DocumentEvent( document, user, nEventType );

        _managerEventListeners.notifyListeners( event );
    }

    /**
     * Add to the document all permitted actions according to the current user
     * and
     * using the current locale
     * @param user The current user
     * @param document The document
     * @param locale The Locale
     */
    public void getActions( Document document, Locale locale, AdminUser user )
    {
        List<DocumentAction> listActions = DocumentActionHome.getActionsList( document, locale );
        RBACResource documentType = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType(  ) );
        listActions = (List<DocumentAction>) RBACService.getAuthorizedActionsCollection( listActions, documentType, user );

        // Get all beans from the global ApplicationContext
        List<IDocumentActionsService> listActionServices = SpringContextService.getBeansOfType( IDocumentActionsService.class );

        // Process all services
        for ( IDocumentActionsService actionService : listActionServices )
        {
            listActions.addAll( actionService.getActions( document, locale, user ) );
        }

        document.setActions( listActions );
    }

    /**
     * Get the published status of a document
     * @param document The document to get the published status of
     */
    public void getPublishedStatus( Document document )
    {
        boolean isPublished = PublishingService.getInstance(  ).isPublished( document.getId(  ) );
        int nPublishedStatus = 0;

        if ( !isPublished )
        {
            nPublishedStatus = 1;
        }

        document.setPublishedStatus( nPublishedStatus );
    }

    /**
     * Build an HTML form for the document creation for a given document type
     *
     * @param strDocumentTypeCode The Document type code
     * @param locale The Locale
     * @param strBaseUrl The base Url
     *
     * @return The HTML form
     */
    public String getCreateForm( String strDocumentTypeCode, Locale locale, String strBaseUrl )
    {
        StringBuilder sbForm = new StringBuilder(  );
        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strDocumentTypeCode );

        for ( DocumentAttribute attribute : documentType.getAttributes(  ) )
        {
            AttributeManager manager = AttributeService.getInstance(  ).getManager( attribute.getCodeAttributeType(  ) );
            sbForm.append( manager.getCreateFormHtml( attribute, locale, strBaseUrl ) );
        }

        return sbForm.toString(  );
    }

    /**
     * Build an HTML form for the document modification for a given document ID.
     * <b>Warning</b> : This method loads the binaries of the document.
     *
     * @param strDocumentId The Id of the document to modify
     * @param locale The Locale
     * @param strBaseUrl The base url
     *
     * @return The HTML form
     */
    public String getModifyForm( String strDocumentId, Locale locale, String strBaseUrl )
    {
        int nDocumentId = IntegerUtils.convert( strDocumentId );
        Document document = DocumentHome.findByPrimaryKey( nDocumentId );

        if ( document != null )
        {
            return getModifyForm( document, locale, strBaseUrl );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Build an HTML form for the document modification for a given document
     *
     * @param document The document
     * @param locale The Locale
     * @param strBaseUrl The base url
     * @return The HTML form
     */
    public String getModifyForm( Document document, Locale locale, String strBaseUrl )
    {
        StringBuilder sbForm = new StringBuilder(  );
        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType(  ) );

        for ( DocumentAttribute attribute : documentType.getAttributes(  ) )
        {
            DocumentAttribute attributeDocument = getDocumentAttribute( document, attribute.getId(  ) );

            if ( attributeDocument != null )
            {
                AttributeManager manager = AttributeService.getInstance(  )
                                                           .getManager( attributeDocument.getCodeAttributeType(  ) );
                sbForm.append( manager.getModifyFormHtml( attributeDocument, document, locale, strBaseUrl ) );
            }
            else
            {
                AttributeManager manager = AttributeService.getInstance(  )
                                                           .getManager( attribute.getCodeAttributeType(  ) );
                sbForm.append( manager.getCreateFormHtml( attribute, locale, strBaseUrl ) );
            }
        }

        return sbForm.toString(  );
    }

    /**
     * Retrieve an attribute of document from its ID
     * @param document The document
     * @param nAttributeId The attribute ID
     * @return The attribute if found, otherwise null
     */
    private DocumentAttribute getDocumentAttribute( Document document, int nAttributeId )
    {
        for ( DocumentAttribute attribute : document.getAttributes(  ) )
        {
            if ( attribute.getId(  ) == nAttributeId )
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
     * @param nIdSpace the id of the document space
     * @param strDocumentTypeId the id of the type document being considered
     * @param strPermission the permission needed
     * @param user the user trying to access the ressource
     * @return true if the user can access the given resource with the given
     *         permission, false otherwise
     */
    public boolean isAuthorizedAdminDocument( int nIdSpace, String strDocumentTypeId, String strPermission,
        AdminUser user )
    {
        DocumentSpace documentSpace = DocumentSpaceHome.findByPrimaryKey( nIdSpace );
        boolean bPermissionCreate = strPermission.equals( DocumentTypeResourceIdService.PERMISSION_CREATE )
            ? documentSpace.isDocumentCreationAllowed(  ) : true;

        return DocumentSpacesService.getInstance(  ).isAuthorizedViewByWorkgroup( documentSpace.getId(  ), user ) &&
        DocumentSpacesService.getInstance(  ).isAuthorizedViewByRole( documentSpace.getId(  ), user ) &&
        RBACService.isAuthorized( DocumentType.RESOURCE_TYPE, strDocumentTypeId, strPermission, user ) &&
        bPermissionCreate;
    }

    /**
     * Return the data of a document object
     * @param mRequest The MultipartHttpServletRequest
     * @param document The document object
     * @param locale The locale
     * @return data of document object
     */
    public String getDocumentData( MultipartHttpServletRequest mRequest, Document document, Locale locale )
    {
        String strDocumentTitle = mRequest.getParameter( PARAMETER_DOCUMENT_TITLE );
        String strDocumentSummary = mRequest.getParameter( PARAMETER_DOCUMENT_SUMMARY );
        String strDocumentComment = mRequest.getParameter( PARAMETER_DOCUMENT_COMMENT );
        String strDateValidityBegin = mRequest.getParameter( PARAMETER_VALIDITY_BEGIN );
        String strDateValidityEnd = mRequest.getParameter( PARAMETER_VALIDITY_END );
        String strMailingListId = mRequest.getParameter( PARAMETER_MAILING_LIST );
        int nMailingListId = IntegerUtils.convert( strMailingListId, 0 );
        String strPageTemplateDocumentId = mRequest.getParameter( PARAMETER_PAGE_TEMPLATE_DOCUMENT_ID );
        int nPageTemplateDocumentId = IntegerUtils.convert( strPageTemplateDocumentId, 0 );
        String[] arrayCategory = mRequest.getParameterValues( PARAMETER_CATEGORY );

        // Check for mandatory value
        if ( StringUtils.isBlank( strDocumentTitle ) || StringUtils.isBlank( strDocumentSummary ) )
        {
            return AdminMessageService.getMessageUrl( mRequest, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        // Check for illegal character character
        if ( StringUtil.containsHtmlSpecialCharacters( strDocumentTitle ) ||
                StringUtil.containsHtmlSpecialCharacters( strDocumentSummary ) )
        {
            return AdminMessageService.getMessageUrl( mRequest, Messages.MESSAGE_ILLEGAL_CHARACTER,
                AdminMessage.TYPE_STOP );
        }

        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType(  ) );
        List<DocumentAttribute> listAttributes = documentType.getAttributes(  );

        for ( DocumentAttribute attribute : listAttributes )
        {
            String strAdminMessage = setAttribute( attribute, document, mRequest, locale );

            if ( strAdminMessage != null )
            {
                return strAdminMessage;
            }
        }

        Timestamp dateValidityBegin = null;
        Timestamp dateValidityEnd = null;

        if ( ( strDateValidityBegin != null ) && !strDateValidityBegin.equals( "" ) )
        {
            Date dateBegin = DateUtil.formatDateLongYear( strDateValidityBegin, locale );

            if ( ( dateBegin == null ) )
            {
                return AdminMessageService.getMessageUrl( mRequest, MESSAGE_INVALID_DATEBEGIN, AdminMessage.TYPE_STOP );
            }

            dateValidityBegin = new Timestamp( dateBegin.getTime(  ) );

            if ( dateValidityBegin.before( new Timestamp( 0 ) ) )
            {
                return AdminMessageService.getMessageUrl( mRequest, MESSAGE_INVALID_DATE_BEFORE_70,
                    AdminMessage.TYPE_STOP );
            }
        }

        if ( ( strDateValidityEnd != null ) && !strDateValidityEnd.equals( "" ) )
        {
            Date dateEnd = DateUtil.formatDateLongYear( strDateValidityEnd, locale );

            if ( ( dateEnd == null ) )
            {
                return AdminMessageService.getMessageUrl( mRequest, MESSAGE_INVALID_DATEEND, AdminMessage.TYPE_STOP );
            }

            dateValidityEnd = new Timestamp( dateEnd.getTime(  ) );

            if ( dateValidityEnd.before( new Timestamp( 0 ) ) )
            {
                return AdminMessageService.getMessageUrl( mRequest, MESSAGE_INVALID_DATE_BEFORE_70,
                    AdminMessage.TYPE_STOP );
            }
        }

        //validate period (dateEnd > dateBegin )
        if ( ( dateValidityBegin != null ) && ( dateValidityEnd != null ) )
        {
            if ( dateValidityEnd.before( dateValidityBegin ) )
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

        MetadataHandler hMetadata = documentType.metadataHandler(  );

        if ( hMetadata != null )
        {
            document.setXmlMetadata( hMetadata.getXmlMetadata( mRequest.getParameterMap(  ) ) );
        }

        document.setAttributes( listAttributes );

        //Categories
        List<Category> listCategories = new ArrayList<Category>(  );

        if ( arrayCategory != null )
        {
            for ( String strIdCategory : arrayCategory )
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
     * @param document The {@link Document}
     * @param mRequest The multipart http request
     * @param locale The locale
     * @return an admin message if error or null else
     */
    private String setAttribute( DocumentAttribute attribute, Document document, MultipartHttpServletRequest mRequest,
        Locale locale )
    {
        String strParameterStringValue = mRequest.getParameter( attribute.getCode(  ) );
        FileItem fileParameterBinaryValue = mRequest.getFile( attribute.getCode(  ) );
        String strIsUpdatable = mRequest.getParameter( PARAMETER_ATTRIBUTE_UPDATE + attribute.getCode(  ) );
        String strToResize = mRequest.getParameter( attribute.getCode(  ) + PARAMETER_CROPPABLE );
        boolean bIsUpdatable = ( ( strIsUpdatable == null ) || strIsUpdatable.equals( "" ) ) ? false : true;
        boolean bToResize = ( ( strToResize == null ) || strToResize.equals( "" ) ) ? false : true;

        if ( strParameterStringValue != null ) // If the field is a string
        {
            // Check for mandatory value
            if ( attribute.isRequired(  ) && strParameterStringValue.trim(  ).equals( "" ) )
            {
                return AdminMessageService.getMessageUrl( mRequest, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }

            // Check for specific attribute validation
            AttributeManager manager = AttributeService.getInstance(  ).getManager( attribute.getCodeAttributeType(  ) );
            String strValidationErrorMessage = manager.validateValue( attribute.getId(  ), strParameterStringValue,
                    locale );

            if ( strValidationErrorMessage != null )
            {
                String[] listArguments = { attribute.getName(  ), strValidationErrorMessage };

                return AdminMessageService.getMessageUrl( mRequest, MESSAGE_ATTRIBUTE_VALIDATION_ERROR, listArguments,
                    AdminMessage.TYPE_STOP );
            }

            attribute.setTextValue( strParameterStringValue );
        }
        else if ( fileParameterBinaryValue != null ) // If the field is a file
        {
            attribute.setBinary( true );

            String strContentType = fileParameterBinaryValue.getContentType(  );
            byte[] bytes = fileParameterBinaryValue.get(  );
            String strFileName = fileParameterBinaryValue.getName(  );
            String strExtension = FilenameUtils.getExtension( strFileName );

            AttributeManager manager = AttributeService.getInstance(  ).getManager( attribute.getCodeAttributeType(  ) );

            if ( !bIsUpdatable )
            {
                // there is no new value then take the old file value
                DocumentAttribute oldAttribute = document.getAttribute( attribute.getCode(  ) );

                if ( ( oldAttribute != null ) && ( oldAttribute.getBinaryValue(  ) != null ) &&
                        ( oldAttribute.getBinaryValue(  ).length > 0 ) )
                {
                    bytes = oldAttribute.getBinaryValue(  );
                    strContentType = oldAttribute.getValueContentType(  );
                    strFileName = oldAttribute.getTextValue(  );
                    strExtension = FilenameUtils.getExtension( strFileName );
                }
            }

            List<AttributeTypeParameter> parameters = manager.getExtraParametersValues( locale, attribute.getId(  ) );

            String extensionList = StringUtils.EMPTY;

            if ( CollectionUtils.isNotEmpty( parameters ) &&
                    CollectionUtils.isNotEmpty( parameters.get( 0 ).getValueList(  ) ) )
            {
                extensionList = parameters.get( 0 ).getValueList(  ).get( 0 );
            }

            // Check for mandatory value
            if ( attribute.isRequired(  ) && ( ( bytes == null ) || ( bytes.length == 0 ) ) )
            {
                return AdminMessageService.getMessageUrl( mRequest, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }
            else if ( StringUtils.isNotBlank( extensionList ) && !extensionList.contains( strExtension ) )
            {
                Object[] params = new Object[2];
                params[0] = attribute.getName(  );
                params[1] = extensionList;

                return AdminMessageService.getMessageUrl( mRequest, MESSAGE_EXTENSION_ERROR, params,
                    AdminMessage.TYPE_STOP );
            }

            // Check for specific attribute validation
            String strValidationErrorMessage = manager.validateValue( attribute.getId(  ), strFileName, locale );

            if ( strValidationErrorMessage != null )
            {
                String[] listArguments = { attribute.getName(  ), strValidationErrorMessage };

                return AdminMessageService.getMessageUrl( mRequest, MESSAGE_ATTRIBUTE_VALIDATION_ERROR, listArguments,
                    AdminMessage.TYPE_STOP );
            }

            if ( bToResize && !ArrayUtils.isEmpty( bytes ) )
            {
                // Resize image
                String strWidth = mRequest.getParameter( attribute.getCode(  ) + PARAMETER_WIDTH );

                if ( StringUtils.isBlank( strWidth ) || !StringUtils.isNumeric( strWidth ) )
                {
                    String[] listArguments = 
                        {
                            attribute.getName(  ),
                            I18nService.getLocalizedString( MESSAGE_ATTRIBUTE_WIDTH_ERROR, mRequest.getLocale(  ) )
                        };

                    return AdminMessageService.getMessageUrl( mRequest, MESSAGE_ATTRIBUTE_VALIDATION_ERROR,
                        listArguments, AdminMessage.TYPE_STOP );
                }

                try
                {
                    bytes = ImageUtils.resizeImage( bytes, Integer.valueOf( strWidth ) );
                }
                catch ( IOException e )
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
}
