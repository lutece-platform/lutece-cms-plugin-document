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
package fr.paris.lutece.plugins.document.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentFilter;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentPageTemplate;
import fr.paris.lutece.plugins.document.business.DocumentPageTemplateHome;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.IndexerAction;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.plugins.document.business.spaces.SpaceActionHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentAction;
import fr.paris.lutece.plugins.document.business.workflow.DocumentActionHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.plugins.document.business.workflow.DocumentStateHome;
import fr.paris.lutece.plugins.document.modules.comment.service.DocumentCommentPlugin;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.DocumentTypeResourceIdService;
import fr.paris.lutece.plugins.document.service.category.CategoryService;
import fr.paris.lutece.plugins.document.service.category.CategoryService.CategoryDisplay;
import fr.paris.lutece.plugins.document.service.metadata.MetadataHandler;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.plugins.document.service.search.DocumentIndexer;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.plugins.document.utils.DocumentIndexerUtils;
import fr.paris.lutece.portal.business.resourceenhancer.ResourceEnhancer;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.sort.AttributeComparator;
import fr.paris.lutece.util.url.UrlItem;


/**
 * JspBean for document management
 */
public class DocumentJspBean extends PluginAdminPageJspBean
{
    private static final long serialVersionUID = 3884593136805763150L;
    public static final String RIGHT_DOCUMENT_MANAGEMENT = "DOCUMENT_MANAGEMENT";
    public static final String PARAMETER_SPACE_ID_FILTER = "id_space_filter";
    private static final String SPACE_TREE_XSL_UNIQUE_PREFIX = UniqueIDGenerator.getNewId(  ) + "SpacesTree";
    private static final String DOCUMENT_STYLE_PREFIX_ID = UniqueIDGenerator.getNewId(  ) + "document-";

    // Templates
    private static final String TEMPLATE_MANAGE_DOCUMENTS = "admin/plugins/document/manage_documents.html";
    private static final String TEMPLATE_CREATE_DOCUMENT = "admin/plugins/document/create_document.html";
    private static final String TEMPLATE_MODIFY_DOCUMENT = "admin/plugins/document/modify_document.html";
    private static final String TEMPLATE_PREVIEW_DOCUMENT = "admin/plugins/document/preview_document.html";
    private static final String TEMPLATE_MOVE_DOCUMENT = "admin/plugins/document/move_document.html";
    private static final String TEMPLATE_DOCUMENT_PAGE_TEMPLATE_ROW = "admin/plugins/document/page_template_list_row.html";
    private static final String TEMPLATE_FORM_CATEGORY = "admin/plugins/document/category/list_category.html";

    // Markers
    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_PREVIEW = "preview";
    private static final String MARK_DOCUMENTS_LIST = "documents_list";
    private static final String MARK_DOCUMENT_TYPES_LIST = "document_types_list";
    private static final String MARK_DOCUMENT_TYPES_FILTER_LIST = "document_types_filter_list";
    private static final String MARK_STATES_FILTER_LIST = "states_filter_list";
    private static final String MARK_SPACES_TREE = "spaces_tree";
    private static final String MARK_SPACE_ACTIONS_LIST = "space_actions_list";
    private static final String MARK_SPACE = "space";
    private static final String MARK_STATE_ID = "id_state";
    private static final String MARK_CHILD_SPACES_LIST = "child_spaces_list";
    private static final String MARK_FIELDS = "fields";
    private static final String MARK_DOCUMENT_TYPE = "document_type";
    private static final String MARK_DEFAULT_DOCUMENT_TYPE = "default_document_type";
    private static final String MARK_DEFAULT_STATE = "default_state";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_VIEW_TYPE = "view_type";
    private static final String MARK_VIEW_TYPES_LIST = "view_types_list";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_CATEGORY = "category";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_METADATA = "metadata";
    private static final String MARK_SUBMIT_BUTTON_DISABLED = "submit_button_disabled";
    private static final String MARK_DATE_VALIDITY_BEGIN = "date_validity_begin";
    private static final String MARK_DATE_VALIDITY_END = "date_validity_end";
    private static final String MARK_ACCEPT_SITE_COMMENTS = "accept_site_comments";
    private static final String MARK_IS_MODERATED_COMMENT = "is_moderated_comment";
    private static final String MARK_IS_EMAIL_NOTIFIED_COMMENT = "is_email_notified_comment";
    private static final String MARK_MAILINGLISTS_LIST = "mailinglists_list";
    private static final String MARK_DOCUMENT_PAGE_TEMPLATES_LIST = "page_template_list";
    private static final String MARK_DOCUMENT_PAGE_TEMPLATE = "document_page_template";
    private static final String MARK_INDEX_ROW = "index_row";
    private static final String MARK_DOCUMENT_PAGE_TEMPLATE_CHECKED = "checked";
    private static final String MARK_SPACES_BROWSER = "spaces_browser";
    private static final String MARK_DOCUMENT_IS_COMMENTABLE = "is_commentable";

    // Parameters    
    private static final String PARAMETER_DOCUMENT_TYPE_CODE = "document_type_code";
    private static final String PARAMETER_DOCUMENT_ID = "id_document";
    private static final String PARAMETER_STATE_ID = "id_state";
    private static final String PARAMETER_ACTION_ID = "id_action";
    private static final String PARAMETER_VIEW_TYPE = "view_type";
    private static final String PARAMETER_DOCUMENT_TYPE_CODE_FILTER = "document_type_code_filter";
    private static final String PARAMETER_STATE_ID_FILTER = "id_state_filter";
    private static final String PARAMETER_SELECTION = "selection";
    private static final String PARAMETER_DOCUMENT_SELECTION = "document_selection";

    // Properties
    private static final String PROPERTY_FILTER_ALL = "document.manage_documents.filter.labelAll";
    private static final String PROPERTY_DOCUMENTS_PER_PAGE = "document.documentsPerPage";
    private static final String PROPERTY_DEFAULT_VIEW_TYPE = "document.manageDocuments.defaultViewType";
    private static final String PROPERTY_PREVIEW_DOCUMENT_PAGE_TITLE = "document.preview_document.pageTitle";
    private static final String PROPERTY_MOVE_DOCUMENT_PAGE_TITLE = "document.move_document.pageTitle";
    private static final String PROPERTY_RESOURCE_TYPE = "document";

    // Jsp
    private static final String JSP_DELETE_DOCUMENT = "DoDeleteDocument.jsp";
    private static final String JSP_PREVIEW_DOCUMENT = "PreviewDocument.jsp";
    private static final String JSP_ARCHIVE_DOCUMENT = "DoArchiveDocument.jsp";
    private static final String JSP_DO_REMOVE_SELECTION = "jsp/admin/plugins/document/DoRemoveSelection.jsp";
    private static final String JSP_DO_ARCHIVE_SELECTION = "DoArchiveSelection.jsp";

    // Messages
    private static final String MESSAGE_CONFIRM_DELETE = "document.message.confirmDeleteDocument";
    private static final String MESSAGE_INVALID_DOCUMENT_ID = "document.message.invalidDocumentId";
    private static final String MESSAGE_DOCUMENT_NOT_FOUND = "document.message.documentNotFound";
    private static final String MESSAGE_DOCUMENT_NOT_AUTHORIZED = "document.message.documentNotAuthorized";
    private static final String MESSAGE_MOVING_NOT_AUTHORIZED = "document.message.movingNotAuthorized";
    private static final String MESSAGE_DOCUMENT_IS_PUBLISHED = "document.message.documentIsPublished";
    private static final String MESSAGE_ERROR_DOCUMENT_IS_PUBLISHED = "document.message.errorDocumentIsPublished";
    private static final String MESSAGE_DOCUMENT_ERROR = "document.message.documentError"; //TODO message erreur	
    private static final String MESSAGE_CONFIRM_DELETE_SELECTION = "document.message.confirmDeleteSelection";
    private static final String MESSAGE_CONFIRM_ARCHIVE_SELECTION = "document.message.selectionPublished";
    private static final String MESSAGE_ERROR_DOCUMENT_SELECTED_IS_PUBLISHED = "document.message.errorDocumentSelectedIsPublished";

    //constants
    private static final String CONSTANT_REMOVE = "remove";
    private static final String CONSTANT_VALIDATE = "validate";
    private static final String CONSTANT_ARCHIVE = "archive";
    private static final String CONSTANT_REFUSE = "refuse";
    private static final String CONSTANT_UNARCHIVE = "unarchive";
    private static final String CONSTANT_SUBMIT = "submit";
    private static final String PATH_JSP = "jsp/admin/plugins/document/";
    private static final String XSL_PARAMETER_CURRENT_SPACE = "current-space-id";
    private static final String FILTER_ALL = "-1";
    private static final String PAGE_INDEX_FIRST = "1";
    private String _strCurrentDocumentTypeFilter;
    private String _strCurrentStateFilter;
    private String _strCurrentSpaceId;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private String _strViewType;
    private int _nDefaultItemsPerPage;
    private String[] _multiSelectionValues;

    /**
     * Constructor
     */
    public DocumentJspBean(  )
    {
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DOCUMENTS_PER_PAGE, 10 );
    }

    /**
     * Gets the document management page
     * @param request The HTTP request
     * @return The document management page
     */
    public String getManageDocuments( HttpServletRequest request )
    {
        setPageTitleProperty( null );

        AdminUser user = getUser(  );

        // Gets new criterias or space changes from the request
        DocumentFilter filter = new DocumentFilter(  );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _strCurrentDocumentTypeFilter = getDocumentType( request, filter );
        _strCurrentStateFilter = getState( request, filter );
        _strCurrentSpaceId = getSpaceId( request, filter );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );
        _strViewType = getViewType( request );

        //Check if space is authorized, change space to default space else
        if ( !DocumentSpacesService.getInstance(  )
                                       .isAuthorizedViewByWorkgroup( Integer.parseInt( _strCurrentSpaceId ), user ) ||
                !DocumentSpacesService.getInstance(  )
                                          .isAuthorizedViewByRole( Integer.parseInt( _strCurrentSpaceId ), user ) )
        {
            filter.setIdSpace( DocumentSpacesService.getInstance(  ).getUserDefaultSpace( user ) );
            _strCurrentSpaceId = Integer.toString( filter.getIdSpace(  ) );
        }

        // Build document list according criterias
        Collection<Integer> listDocumentIds = DocumentHome.findPrimaryKeysByFilter( filter, getLocale(  ) );

        // Spaces
        String strXmlSpaces = DocumentSpacesService.getInstance(  ).getXmlSpacesList( user );
        Source sourceXsl = DocumentSpacesService.getInstance(  ).getTreeXsl(  );
        Map<String, String> htXslParameters = new HashMap<String, String>(  );
        htXslParameters.put( XSL_PARAMETER_CURRENT_SPACE, _strCurrentSpaceId );

        XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
        String strSpacesTree = xmlTransformerService.transformBySourceWithXslCache( strXmlSpaces, sourceXsl,
                SPACE_TREE_XSL_UNIQUE_PREFIX, htXslParameters, null );

        List listSpaceActions = SpaceActionHome.getActionsList( getLocale(  ) );
        int nCurrentSpaceId = Integer.parseInt( _strCurrentSpaceId );
        DocumentSpace currentSpace = DocumentSpaceHome.findByPrimaryKey( nCurrentSpaceId );
        listSpaceActions = (List) RBACService.getAuthorizedActionsCollection( listSpaceActions, currentSpace,
                getUser(  ) );

        // Build filter combos
        // Document Types
        ReferenceList listDocumentTypes = DocumentSpaceHome.getAllowedDocumentTypes( Integer.parseInt( 
                    _strCurrentSpaceId ) );
        listDocumentTypes = RBACService.getAuthorizedReferenceList( listDocumentTypes, DocumentType.RESOURCE_TYPE,
                DocumentTypeResourceIdService.PERMISSION_VIEW, user );
        listDocumentTypes.addItem( FILTER_ALL, I18nService.getLocalizedString( PROPERTY_FILTER_ALL, getLocale(  ) ) );

        // Documents States
        ReferenceList listStates = DocumentStateHome.getDocumentStatesList( getLocale(  ) );
        listStates.addItem( FILTER_ALL, I18nService.getLocalizedString( PROPERTY_FILTER_ALL, getLocale(  ) ) );

        // Childs spaces
        Collection listChildSpaces = DocumentSpaceHome.findChilds( nCurrentSpaceId );
        listChildSpaces = AdminWorkgroupService.getAuthorizedCollection( listChildSpaces, user );

        // Creation document types list for the current space
        ReferenceList listCreateDocumentTypes = DocumentSpaceHome.getAllowedDocumentTypes( Integer.parseInt( 
                    _strCurrentSpaceId ) );
        listCreateDocumentTypes = RBACService.getAuthorizedReferenceList( listCreateDocumentTypes,
                DocumentType.RESOURCE_TYPE, DocumentTypeResourceIdService.PERMISSION_CREATE, user );

        LocalizedPaginator paginator = new LocalizedPaginator( (List) listDocumentIds, _nItemsPerPage,
                getHomeUrl( request ), Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );

        List<Document> listDocuments = new ArrayList<Document>(  );

        for ( Integer documentId : (Collection<Integer>) paginator.getPageItems(  ) )
        {
            Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( documentId );

            if ( document != null )
            {
                document.setLocale( getLocale(  ) );
                DocumentService.getInstance(  ).getActions( document, getLocale(  ), getUser(  ) );

                DocumentService.getInstance(  ).getPublishedStatus( document );
                listDocuments.add( document );
            }
        }

        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );
        String strAscSort = null;

        if ( strSortedAttributeName != null )
        {
            strAscSort = request.getParameter( Parameters.SORTED_ASC );

            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );

            Collections.sort( listDocuments, new AttributeComparator( strSortedAttributeName, bIsAscSort ) );
        }

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_SPACES_TREE, strSpacesTree );
        model.put( MARK_SPACE_ACTIONS_LIST, listSpaceActions );
        model.put( MARK_SPACE, currentSpace );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_VIEW_TYPES_LIST, DocumentSpaceHome.getViewTypeList( getLocale(  ) ) );
        model.put( MARK_VIEW_TYPE, _strViewType );
        model.put( MARK_DOCUMENTS_LIST, listDocuments );
        model.put( MARK_DOCUMENT_TYPES_FILTER_LIST, listDocumentTypes );
        model.put( MARK_DEFAULT_DOCUMENT_TYPE, _strCurrentDocumentTypeFilter );
        model.put( MARK_STATES_FILTER_LIST, listStates );
        model.put( MARK_DEFAULT_STATE, _strCurrentStateFilter );
        model.put( MARK_CHILD_SPACES_LIST, listChildSpaces );
        model.put( MARK_DOCUMENT_TYPES_LIST, listCreateDocumentTypes );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DOCUMENTS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Gets the document creation page
     * @param request The HTTP request
     * @return The document creation page
     */
    public String getCreateDocument( HttpServletRequest request )
    {
        String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

        if ( ( _strCurrentSpaceId == null ) ||
                ( !DocumentService.getInstance(  )
                                      .isAuthorizedAdminDocument( Integer.parseInt( _strCurrentSpaceId ),
                    strDocumentTypeCode, DocumentTypeResourceIdService.PERMISSION_CREATE, getUser(  ) ) ) )
        {
            return getManageDocuments( request );
        }

        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strDocumentTypeCode );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale(  ).getLanguage(  ) );
        model.put( MARK_DOCUMENT_TYPE, documentType.getCode(  ) );

        model.put( MARK_CATEGORY, getCategoryCreateForm( request ) );
        model.put( MARK_METADATA, getMetadataCreateForm( request, strDocumentTypeCode ) );
        model.put( MARK_FIELDS,
            DocumentService.getInstance(  )
                           .getCreateForm( strDocumentTypeCode, getLocale(  ), AppPathService.getBaseUrl( request ) ) );

        // PageTemplate
        int nIndexRow = 1;
        StringBuffer strPageTemplatesRow = new StringBuffer(  );

        // Scan of the list
        for ( DocumentPageTemplate documentPageTemplate : DocumentPageTemplateHome.getPageTemplatesList(  ) )
        {
            strPageTemplatesRow.append( getTemplatesPageList( documentPageTemplate.getId(  ), 0,
                    Integer.toString( nIndexRow ) ) );
            nIndexRow++;
        }

        int nIsCommentable = 1;

        // TODO : delete dependency from document-comment module
        if ( DocumentCommentPlugin.getPluginStatus(  ) )
        {
            nIsCommentable = 0;
        }

        model.put( MARK_DOCUMENT_IS_COMMENTABLE, Integer.toString( nIsCommentable ) );

        // additionnal create info
        ResourceEnhancer.getCreateResourceModelAddOn( model );

        model.put( MARK_DOCUMENT_PAGE_TEMPLATES_LIST, strPageTemplatesRow );

        ReferenceList listMailingLists = AdminMailingListService.getMailingLists( getUser(  ) );
        model.put( MARK_MAILINGLISTS_LIST, listMailingLists );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_DOCUMENT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Return the part of creation form corresponding to Category
     * @param request The http request
     * @return The html form
     */
    private String getCategoryCreateForm( HttpServletRequest request )
    {
        AdminUser user = getUser(  );
        HashMap<String, Collection<CategoryDisplay>> model = new HashMap<String, Collection<CategoryDisplay>>(  );
        model.put( MARK_CATEGORY_LIST, CategoryService.getAllCategoriesDisplay( user ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FORM_CATEGORY, getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Return the part of modification form corresponding to Category
     * @param request The http request
     * @param document The Document object to modify
     * @return The html form
     */
    private String getCategoryModifyForm( HttpServletRequest request, Document document )
    {
        HashMap<String, Collection<CategoryDisplay>> model = new HashMap<String, Collection<CategoryDisplay>>(  );
        Collection<CategoryDisplay> listCategoryDisplay = CategoryService.getAllCategoriesDisplay( document.getCategories(  ),
                getUser(  ) );

        model.put( MARK_CATEGORY_LIST, listCategoryDisplay );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FORM_CATEGORY, getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Return the metadata creation form
     * @param request The http request
     * @param strDocumentTypeCode The Document type code
     * @return The html form
     */
    private String getMetadataCreateForm( HttpServletRequest request, String strDocumentTypeCode )
    {
        MetadataHandler hMetadata = getMetadataHandler( strDocumentTypeCode );

        return ( hMetadata != null ) ? hMetadata.getCreateForm( request ) : "";
    }

    /**
     * Return the metadata modify form
     * @param request The http request
     * @param document The Document
     * @return The html form
     */
    private String getMetadataModifyForm( HttpServletRequest request, Document document )
    {
        MetadataHandler hMetadata = getMetadataHandler( document.getCodeDocumentType(  ) );

        return ( hMetadata != null ) ? hMetadata.getModifyForm( request, document.getXmlMetadata(  ) ) : "";
    }

    /**
     * Return the metadata handler
     * @param strDocumentTypeCode The Document type code
     * @return The metadata handler for a document type
     */
    private MetadataHandler getMetadataHandler( String strDocumentTypeCode )
    {
        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strDocumentTypeCode );

        return documentType.metadataHandler(  );
    }

    /**
     * Perform the creation
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateDocument( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        AdminUser user = getUser(  );

        String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

        if ( !DocumentService.getInstance(  )
                                 .isAuthorizedAdminDocument( Integer.parseInt( _strCurrentSpaceId ),
                    strDocumentTypeCode, DocumentTypeResourceIdService.PERMISSION_CREATE, user ) )
        {
            return getHomeUrl( request );
        }

        Document document = new Document(  );
        document.setCodeDocumentType( strDocumentTypeCode );

        String strError = DocumentService.getInstance(  ).getDocumentData( multipartRequest, document, getLocale(  ) );

        if ( strError != null )
        {
            return strError;
        }

        document.setSpaceId( Integer.parseInt( _strCurrentSpaceId ) );
        document.setStateId( 1 );
        document.setCreatorId( getUser(  ).getUserId(  ) );

        try
        {
            DocumentService.getInstance(  ).createDocument( document, getUser(  ) );
        }
        catch ( DocumentException e )
        {
            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
        }

        // process
        ResourceEnhancer.doCreateResourceAddOn( request, PROPERTY_RESOURCE_TYPE, document.getId(  ) );

        return getHomeUrl( request );
    }

    /**
     * Gets the document modification page
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String getModifyDocument( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( Integer.parseInt( strDocumentId ) );

        if ( !DocumentService.getInstance(  )
                                 .isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageDocuments( request );
        }

        String strStateId = ( request.getParameter( PARAMETER_STATE_ID ) != null )
            ? request.getParameter( PARAMETER_STATE_ID ) : "";
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale(  ).getLanguage(  ) );
        model.put( MARK_DOCUMENT, document );

        // Date Management
        model.put( MARK_DATE_VALIDITY_BEGIN,
            ( document.getDateValidityBegin(  ) == null ) ? ""
                                                          : DateUtil.getDateString( 
                new Date( document.getDateValidityBegin(  ).getTime(  ) ), getLocale(  ) ) );
        model.put( MARK_DATE_VALIDITY_END,
            ( document.getDateValidityEnd(  ) == null ) ? ""
                                                        : DateUtil.getDateString( 
                new Date( document.getDateValidityEnd(  ).getTime(  ) ), getLocale(  ) ) );

        // Site Comments management
        model.put( MARK_ACCEPT_SITE_COMMENTS, document.getAcceptSiteComments(  ) );
        model.put( MARK_IS_MODERATED_COMMENT, document.getIsModeratedComment(  ) );

        // Notification
        model.put( MARK_IS_EMAIL_NOTIFIED_COMMENT, document.getIsEmailNotifiedComment(  ) );

        // PageTemplate
        int nIndexRow = 1;
        StringBuffer strPageTemplatesRow = new StringBuffer(  );

        // Scan of the list
        for ( DocumentPageTemplate documentPageTemplate : DocumentPageTemplateHome.getPageTemplatesList(  ) )
        {
            strPageTemplatesRow.append( getTemplatesPageList( documentPageTemplate.getId(  ),
                    document.getPageTemplateDocumentId(  ), Integer.toString( nIndexRow ) ) );
            nIndexRow++;
        }

        model.put( MARK_DOCUMENT_PAGE_TEMPLATES_LIST, strPageTemplatesRow );

        ReferenceList listMailingLists = AdminMailingListService.getMailingLists( getUser(  ) );
        model.put( MARK_MAILINGLISTS_LIST, listMailingLists );

        model.put( MARK_CATEGORY, getCategoryModifyForm( request, document ) );
        model.put( MARK_METADATA, getMetadataModifyForm( request, document ) );
        model.put( MARK_FIELDS,
            DocumentService.getInstance(  )
                           .getModifyForm( strDocumentId, getLocale(  ), AppPathService.getBaseUrl( request ) ) );
        model.put( MARK_STATE_ID, strStateId );

        int nIsCommentable = 1;

        // TODO : delete dependency from document-comment module
        if ( DocumentCommentPlugin.getPluginStatus(  ) )
        {
            nIsCommentable = 0;
        }

        model.put( MARK_DOCUMENT_IS_COMMENTABLE, Integer.toString( nIsCommentable ) );

        ResourceEnhancer.getModifyResourceModelAddOn( model, PROPERTY_RESOURCE_TYPE, document.getId(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_DOCUMENT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyDocument( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String strDocumentId = multipartRequest.getParameter( PARAMETER_DOCUMENT_ID );
        Document document = DocumentHome.findByPrimaryKey( Integer.parseInt( strDocumentId ) );

        if ( !DocumentService.getInstance(  )
                                 .isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        String strError = DocumentService.getInstance(  ).getDocumentData( multipartRequest, document, getLocale(  ) );

        if ( strError != null )
        {
            return strError;
        }

        try
        {
            DocumentService.getInstance(  ).modifyDocument( document, getUser(  ) );
        }
        catch ( DocumentException e )
        {
            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
        }

        // If a state is defined, it should be set after to the document after the modification
        String strStateId = multipartRequest.getParameter( PARAMETER_STATE_ID );

        if ( ( strStateId != null ) && ( !strStateId.equals( "" ) ) )
        {
            int nStateId = Integer.parseInt( strStateId );

            try
            {
                DocumentService.getInstance(  ).changeDocumentState( document, getUser(  ), nStateId );
            }
            catch ( DocumentException e )
            {
                return getErrorMessageUrl( request, e.getI18nMessage(  ) );
            }
        }

        ResourceEnhancer.doModifyResourceAddOn( request, PROPERTY_RESOURCE_TYPE, document.getId(  ) );

        return getHomeUrl( request );
    }

    /**
     * Confirm the deletion
     * @param request The HTTP request
     * @return The URL to go after performing the confirmation
     */
    public String deleteDocument( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = Integer.parseInt( strDocumentId );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( !DocumentService.getInstance(  )
                                 .isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        Object[] messageArgs = { document.getTitle(  ) };
        UrlItem url = new UrlItem( PATH_JSP + JSP_DELETE_DOCUMENT );
        url.addParameter( PARAMETER_DOCUMENT_ID, nDocumentId );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE, messageArgs, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the deletion
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doDeleteDocument( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = Integer.parseInt( strDocumentId );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( !DocumentService.getInstance(  )
                                 .isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        // Test if the document is published or assigned
        boolean bPublishedDocument = PublishingService.getInstance(  ).isAssigned( nDocumentId );

        if ( bPublishedDocument )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DOCUMENT_IS_PUBLISHED,
                AdminMessage.TYPE_STOP );
        }

        DocumentHome.remove( nDocumentId );

        ResourceEnhancer.doDeleteResourceAddOn( request, PROPERTY_RESOURCE_TYPE, nDocumentId );

        return getHomeUrl( request );
    }

    /**
     * Perform the changing of state
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doChangeState( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strActionId = request.getParameter( PARAMETER_ACTION_ID );
        int nDocumentId = -1;
        int nActionId = -1;

        try
        {
            nDocumentId = Integer.parseInt( strDocumentId );
            nActionId = Integer.parseInt( strActionId );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );
        DocumentAction action = DocumentActionHome.findByPrimaryKey( nActionId );

        if ( ( action == null ) || ( action.getFinishDocumentState(  ) == null ) || ( document == null ) ||
                !DocumentService.getInstance(  )
                                    .isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            DocumentService.getInstance(  )
                           .changeDocumentState( document, getUser(  ), action.getFinishDocumentState(  ).getId(  ) );
        }
        catch ( DocumentException e )
        {
            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
        }

        return getHomeUrl( request );
    }

    /**
     * Perform the document validation
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doValidateDocument( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strActionId = request.getParameter( PARAMETER_ACTION_ID );
        int nDocumentId = -1;
        int nActionId = -1;

        try
        {
            nDocumentId = Integer.parseInt( strDocumentId );
            nActionId = Integer.parseInt( strActionId );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );
        DocumentAction action = DocumentActionHome.findByPrimaryKey( nActionId );

        if ( ( action == null ) || ( action.getFinishDocumentState(  ) == null ) || ( document == null ) ||
                !DocumentService.getInstance(  )
                                    .isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            DocumentService.getInstance(  )
                           .validateDocument( document, getUser(  ), action.getFinishDocumentState(  ).getId(  ) );
        }
        catch ( DocumentException e )
        {
            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
        }

        String strIdDocument = Integer.toString( nDocumentId );
        IndexationService.addIndexerAction( strIdDocument, DocumentIndexer.INDEXER_NAME,
            IndexerAction.TASK_MODIFY, IndexationService.ALL_DOCUMENT );

        DocumentIndexerUtils.addIndexerAction( strIdDocument, IndexerAction.TASK_MODIFY, IndexationService.ALL_DOCUMENT );
        
        /*Collection<Portlet> portlets = PublishingService.getInstance().getPortletsByDocumentId(Integer.toString(nDocumentId));
        for(Portlet portlet : portlets)
        {
                if(PublishingService.getInstance().isPublished(nDocumentId,         portlet.getId()))
                {
                         IndexationService.getInstance().addIndexerAction(portlet.getPageId(), PageIndexer.INDEXER_NAME, IndexerAction.TASK_MODIFY);
                }
        
        }*/
        return getHomeUrl( request );
    }

    /**
     * Confirm the filing
     * @param request The HTTP servlet Request
     * @return The url of filing jsp
     */
    public String doConfirmArchiveDocument( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strActionId = request.getParameter( PARAMETER_ACTION_ID );
        int nDocumentId = -1;
        int nActionId = -1;

        try
        {
            nDocumentId = Integer.parseInt( strDocumentId );
            nActionId = Integer.parseInt( strActionId );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );
        DocumentAction action = DocumentActionHome.findByPrimaryKey( nActionId );

        if ( ( action == null ) || ( action.getFinishDocumentState(  ) == null ) || ( document == null ) ||
                !DocumentService.getInstance(  )
                                    .isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        UrlItem url = new UrlItem( JSP_ARCHIVE_DOCUMENT );
        url.addParameter( PARAMETER_DOCUMENT_ID, nDocumentId );
        url.addParameter( PARAMETER_ACTION_ID, nActionId );

        // Test if the document is published or assigned
        boolean bPublishedDocument = PublishingService.getInstance(  ).isAssigned( nDocumentId );

        if ( bPublishedDocument )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_DOCUMENT_IS_PUBLISHED,
                PATH_JSP + url.getUrl(  ), AdminMessage.TYPE_QUESTION );
        }

        return url.getUrl(  );
    }

    /**
     * Remove selection
     * @param request The HTTP request
     * @return The forward url
     */
    public String doRemoveSelection( HttpServletRequest request )
    {
        int nDocumentId;

        for ( String strIdDocument : _multiSelectionValues )
        {
            try
            {
                nDocumentId = Integer.parseInt( strIdDocument );

                Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

                if ( !DocumentService.getInstance(  )
                                         .isAuthorizedAdminDocument( document.getSpaceId(  ),
                            document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_DELETE,
                            getUser(  ) ) )
                {
                    return getHomeUrl( request );
                }

                if ( ( document.getStateId(  ) == DocumentState.STATE_WRITING ) ||
                        ( document.getStateId(  ) == DocumentState.STATE_ARCHIVED ) )
                {
                    // Test if the document is published or assigned
                    boolean bPublishedDocument = PublishingService.getInstance(  ).isAssigned( nDocumentId );

                    if ( bPublishedDocument )
                    {
                        return AdminMessageService.getMessageUrl( request,
                            MESSAGE_ERROR_DOCUMENT_SELECTED_IS_PUBLISHED, AdminMessage.TYPE_STOP );
                    }

                    DocumentHome.remove( nDocumentId );
                }
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        return getHomeUrl( request );
    }

    /**
     * Unpublish and archive selected documents
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doArchiveSelection( HttpServletRequest request )
    {
        int nDocumentId;

        for ( String strIdDocument : _multiSelectionValues )
        {
            try
            {
                nDocumentId = Integer.parseInt( strIdDocument );

                Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

                if ( isAuthorized( DocumentAction.ACTION_ARCHIVE, document ) )
                {
                    try
                    {
                        DocumentService.getInstance(  )
                                       .archiveDocument( document, getUser(  ), DocumentState.STATE_ARCHIVED );
                    }
                    catch ( DocumentException e )
                    {
                        return getErrorMessageUrl( request, e.getI18nMessage(  ) );
                    }
                }
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        return getHomeUrl( request );
    }

    /**
     * Processes document action for multiselection
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doActionSelectionDocument( HttpServletRequest request )
    {
        String strAction = request.getParameter( PARAMETER_SELECTION );
        String[] strIdDocuments = (String[]) request.getParameterMap(  ).get( PARAMETER_DOCUMENT_SELECTION );
        _multiSelectionValues = strIdDocuments;

        int nbDocumentsAffected = 0;

        if ( strAction.equals( CONSTANT_REMOVE ) )
        {
            UrlItem url = new UrlItem( JSP_DO_REMOVE_SELECTION );

            return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_SELECTION, url.getUrl(  ),
                AdminMessage.TYPE_CONFIRMATION );
        }
        else if ( strAction.equals( CONSTANT_ARCHIVE ) )
        {
            UrlItem url = new UrlItem( JSP_DO_ARCHIVE_SELECTION );

            for ( String strIdDocument : _multiSelectionValues )
            {
                int nIdDocument = Integer.parseInt( strIdDocument );

                Document document = DocumentHome.findByPrimaryKey( nIdDocument );

                // Test if the document is published or assigned
                boolean bPublishedDocument = PublishingService.getInstance(  ).isAssigned( nIdDocument );

                if ( ( document.getStateId(  ) == DocumentState.STATE_VALIDATE ) && ( bPublishedDocument ) )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_ARCHIVE_SELECTION,
                        PATH_JSP + url.getUrl(  ), AdminMessage.TYPE_QUESTION );
                }
            }

            for ( String strIdDocument : _multiSelectionValues )
            {
                int nIdDocument = Integer.parseInt( strIdDocument );

                Document document = DocumentHome.findByPrimaryKey( nIdDocument );

                try
                {
                    if ( ( document.getStateId(  ) == DocumentState.STATE_VALIDATE ) &&
                            isAuthorized( DocumentAction.ACTION_ARCHIVE, document ) )
                    {
                        DocumentService.getInstance(  )
                                       .archiveDocument( document, getUser(  ), DocumentState.STATE_ARCHIVED );
                    }
                }
                catch ( DocumentException e )
                {
                    return getErrorMessageUrl( request, e.getI18nMessage(  ) );
                }
            }
        }
        else
        {
            for ( String strIdDocument : strIdDocuments )
            {
                int nIdDocument = Integer.parseInt( strIdDocument );
                int nActionId = -1;
                Document document = DocumentHome.findByPrimaryKey( nIdDocument );

                int stateId = document.getStateId(  );

                if ( document != null )
                {
                    if ( ( strAction.equals( CONSTANT_VALIDATE ) ) &&
                            ( ( stateId == DocumentState.STATE_WAITING_FOR_APPROVAL ) ||
                            ( stateId == DocumentState.STATE_WAITING_FOR_CHANGE_APPROVAL ) ) )
                    {
                        try
                        {
                            //set the action
                            if ( stateId == DocumentState.STATE_WAITING_FOR_APPROVAL )
                            {
                                nActionId = DocumentAction.ACTION_VALIDATE;
                            }
                            else if ( stateId == DocumentState.STATE_WAITING_FOR_CHANGE_APPROVAL )
                            {
                                nActionId = DocumentAction.ACTION_VALIDATE_CHANGE;
                            }

                            if ( isAuthorized( nActionId, document ) )
                            {
                                DocumentService.getInstance(  )
                                               .validateDocument( document, getUser(  ), DocumentState.STATE_VALIDATE );
                                nbDocumentsAffected++;
                            }
                        }
                        catch ( DocumentException e )
                        {
                            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
                        }
                    }
                    else if ( ( strAction.equals( CONSTANT_REFUSE ) ) &&
                            ( ( stateId == DocumentState.STATE_WAITING_FOR_APPROVAL ) ||
                            ( stateId == DocumentState.STATE_WAITING_FOR_CHANGE_APPROVAL ) ) )
                    {
                        try
                        {
                            if ( stateId == DocumentState.STATE_WAITING_FOR_APPROVAL )
                            {
                                nActionId = DocumentAction.ACTION_REFUSE;

                                if ( isAuthorized( nActionId, document ) )
                                {
                                    DocumentService.getInstance(  )
                                                   .changeDocumentState( document, getUser(  ),
                                        DocumentState.STATE_REJECTED );
                                }
                            }
                            else if ( stateId == DocumentState.STATE_WAITING_FOR_CHANGE_APPROVAL )
                            {
                                nActionId = DocumentAction.ACTION_REFUSE_CHANGE;

                                if ( isAuthorized( nActionId, document ) )
                                {
                                    DocumentService.getInstance(  )
                                                   .changeDocumentState( document, getUser(  ),
                                        DocumentState.STATE_IN_CHANGE );
                                }
                            }

                            nbDocumentsAffected++;
                        }
                        catch ( DocumentException e )
                        {
                            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
                        }
                    }
                    else if ( ( strAction.equals( CONSTANT_UNARCHIVE ) ) && ( stateId == DocumentState.STATE_ARCHIVED ) )
                    {
                        nActionId = DocumentAction.ACTION_UNARCHIVE;

                        try
                        {
                            if ( isAuthorized( nActionId, document ) )
                            {
                                DocumentService.getInstance(  )
                                               .changeDocumentState( document, getUser(  ), DocumentState.STATE_VALIDATE );
                                nbDocumentsAffected++;
                            }
                        }
                        catch ( DocumentException e )
                        {
                            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
                        }
                    }
                    else if ( strAction.equals( CONSTANT_SUBMIT ) )
                    {
                        try
                        {
                            if ( ( stateId == DocumentState.STATE_WRITING ) ||
                                    ( stateId == DocumentState.STATE_REJECTED ) )
                            {
                                nActionId = DocumentAction.ACTION_SUBMIT;
                                DocumentService.getInstance(  )
                                               .changeDocumentState( document, getUser(  ),
                                    DocumentState.STATE_WAITING_FOR_APPROVAL );
                                nbDocumentsAffected++;
                            }
                            else if ( ( stateId == DocumentState.STATE_IN_CHANGE ) )
                            {
                                nActionId = DocumentAction.ACTION_SUBMIT_CHANGE;

                                if ( isAuthorized( nActionId, document ) )
                                {
                                    DocumentService.getInstance(  )
                                                   .changeDocumentState( document, getUser(  ),
                                        DocumentState.STATE_WAITING_FOR_CHANGE_APPROVAL );
                                }

                                nbDocumentsAffected++;
                            }
                        }
                        catch ( DocumentException e )
                        {
                            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
                        }
                    }
                }
            }
        }

        return getHomeUrl( request );
    }

    /**
     * Perform the document filing
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doArchiveDocument( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strActionId = request.getParameter( PARAMETER_ACTION_ID );

        int nDocumentId = -1;
        int nActionId = -1;

        try
        {
            nDocumentId = Integer.parseInt( strDocumentId );
            nActionId = Integer.parseInt( strActionId );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );
        DocumentAction action = DocumentActionHome.findByPrimaryKey( nActionId );

        if ( ( action == null ) || ( action.getFinishDocumentState(  ) == null ) || ( document == null ) ||
                !DocumentService.getInstance(  )
                                    .isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            DocumentService.getInstance(  )
                           .archiveDocument( document, getUser(  ), action.getFinishDocumentState(  ).getId(  ) );
        }
        catch ( DocumentException e )
        {
            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
        }

        return getHomeUrl( request );
    }

    /**
     * Perform a document search by Id
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doSearchDocumentById( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId;

        try
        {
            nDocumentId = Integer.parseInt( strDocumentId );
        }
        catch ( NumberFormatException e )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_DOCUMENT_ID, AdminMessage.TYPE_STOP );
        }

        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( document == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_DOCUMENT_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        if ( !DocumentService.getInstance(  )
                                 .isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_DOCUMENT_NOT_AUTHORIZED, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_PREVIEW_DOCUMENT );
        url.addParameter( PARAMETER_DOCUMENT_ID, nDocumentId );

        return url.getUrl(  );
    }

    /**
     * Get the preview page
     * @param request The HTTP request
     * @return The preview page
     */
    public String getPreviewDocument( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PREVIEW_DOCUMENT_PAGE_TITLE );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = Integer.parseInt( strDocumentId );

        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( ( document == null ) ||
                !DocumentService.getInstance(  )
                                    .isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
        {
            return getManageDocuments( request );
        }

        document.setLocale( getLocale(  ) );
        DocumentService.getInstance(  ).getActions( document, getLocale(  ), getUser(  ) );

        DocumentType type = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType(  ) );

        XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
        String strPreview = xmlTransformerService.transformBySourceWithXslCache( document.getXmlWorkingContent(  ),
                type.getAdminXslSource(  ), DOCUMENT_STYLE_PREFIX_ID + type.getAdminStyleSheetId(  ), null, null );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_DOCUMENT, document );
        model.put( MARK_PREVIEW, strPreview );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PREVIEW_DOCUMENT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the changing of space
     * @param request The HTTP request
     * @return The document move page
     */
    public String getMoveDocument( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MOVE_DOCUMENT_PAGE_TITLE );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( Integer.parseInt( strDocumentId ) );

        if ( ( document == null ) ||
                !DocumentService.getInstance(  )
                                    .isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_MOVE, getUser(  ) ) )
        {
            return getManageDocuments( request );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );
        boolean bSubmitButtonDisabled = Boolean.TRUE;

        if ( ( strSpaceId != null ) && !strSpaceId.equals( "" ) )
        {
            bSubmitButtonDisabled = Boolean.FALSE;
        }

        // Spaces browser
        model.put( MARK_SPACES_BROWSER,
            DocumentSpacesService.getInstance(  ).getSpacesBrowser( request, getUser(  ), getLocale(  ), true, true ) );
        model.put( MARK_DOCUMENT, document );
        model.put( MARK_SUBMIT_BUTTON_DISABLED, bSubmitButtonDisabled );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MOVE_DOCUMENT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the document moving
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveDocument( HttpServletRequest request )
    {
        boolean bTypeAllowed = Boolean.FALSE;
        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );

        if ( strSpaceId == null )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nSpaceIdDestination = Integer.parseInt( strSpaceId );
        DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nSpaceIdDestination );
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( Integer.parseInt( strDocumentId ) );

        // Check if the specified document type is authorized for this space
        for ( String documentType : space.getAllowedDocumentTypes(  ) )
        {
            if ( document.getCodeDocumentType(  ).equals( documentType ) )
            {
                bTypeAllowed = Boolean.TRUE;
            }
        }

        // Check if user have rights to create a document into this space
        if ( ( document == null ) ||
                !DocumentService.getInstance(  )
                                    .isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_MOVE, getUser(  ) ) ||
                !DocumentService.getInstance(  )
                                    .isAuthorizedAdminDocument( nSpaceIdDestination, document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_MOVE, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        if ( bTypeAllowed )
        {
            document.setSpaceId( nSpaceIdDestination );
            DocumentHome.update( document, false );

            return getHomeUrl( request );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MOVING_NOT_AUTHORIZED, AdminMessage.TYPE_STOP );
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Private implementation to manage UI parameters
    private String getViewType( HttpServletRequest request )
    {
        String strViewType = request.getParameter( PARAMETER_VIEW_TYPE );

        if ( strViewType == null )
        {
            if ( _strViewType != null )
            {
                strViewType = _strViewType;
            }
            else
            {
                strViewType = AppPropertiesService.getProperty( PROPERTY_DEFAULT_VIEW_TYPE );
            }
        }
        else
        {
            updateSpaceView( strViewType );
        }

        return strViewType;
    }

    /**
     * View action
     * @param nSpaceId the space identifier
     */
    private void setView( int nSpaceId )
    {
        DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nSpaceId );

        if ( space != null )
        {
            _strViewType = space.getViewType(  );
        }
    }

    /**
     * Return document type filter
     * @param request The HttpServletRequest
     * @param filter The DocumentFilter object
     * @return the document type
     */
    private String getDocumentType( HttpServletRequest request, DocumentFilter filter )
    {
        // Filter for document type
        String strCodeDocumentTypeFilter = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE_FILTER );

        if ( strCodeDocumentTypeFilter == null )
        {
            if ( _strCurrentDocumentTypeFilter != null )
            {
                strCodeDocumentTypeFilter = _strCurrentDocumentTypeFilter;
            }
            else
            {
                strCodeDocumentTypeFilter = FILTER_ALL;
            }
        }

        if ( !strCodeDocumentTypeFilter.equals( FILTER_ALL ) )
        {
            filter.setCodeDocumentType( strCodeDocumentTypeFilter );
        }

        if ( !strCodeDocumentTypeFilter.equals( _strCurrentDocumentTypeFilter ) )
        {
            resetPageIndex(  );
        }

        return strCodeDocumentTypeFilter;
    }

    /**
     * Return State
     * @param request The HttpsServletRequest
     * @param filter The DocumentFilter object
     * @return the space identifier
     */
    private String getState( HttpServletRequest request, DocumentFilter filter )
    {
        String strStateId = request.getParameter( PARAMETER_STATE_ID_FILTER );

        if ( strStateId == null )
        {
            if ( _strCurrentStateFilter != null )
            {
                strStateId = _strCurrentStateFilter;
            }
            else
            {
                strStateId = FILTER_ALL;
            }
        }

        if ( !strStateId.equals( FILTER_ALL ) )
        {
            int nStateId = Integer.parseInt( strStateId );
            filter.setIdState( nStateId );
        }

        if ( !strStateId.equals( _strCurrentStateFilter ) )
        {
            resetPageIndex(  );
        }

        return strStateId;
    }

    /**
     * Return Space identifier
     * @param request The HttpsServletRequest
     * @param filter The DocumentFilter object
     * @return the space identifier
     */
    private String getSpaceId( HttpServletRequest request, DocumentFilter filter )
    {
        String strSpaceId = request.getParameter( PARAMETER_SPACE_ID_FILTER );

        if ( strSpaceId == null )
        {
            if ( ( _strCurrentSpaceId != null ) && !_strCurrentSpaceId.equals( "-1" ) )
            {
                strSpaceId = _strCurrentSpaceId;
            }
            else
            {
                int nSpaceId = DocumentSpacesService.getInstance(  ).getUserDefaultSpace( getUser(  ) );
                strSpaceId = "" + nSpaceId;
            }
        }

        int nSpaceId = Integer.parseInt( strSpaceId );
        filter.setIdSpace( nSpaceId );

        if ( !strSpaceId.equals( _strCurrentSpaceId ) )
        {
            // Reset the page index
            resetPageIndex(  );

            // Sets the view corresponding to the new space
            setView( nSpaceId );
        }

        return strSpaceId;
    }

    /**
     * Reset Page Index
     */
    private void resetPageIndex(  )
    {
        _strCurrentPageIndex = PAGE_INDEX_FIRST;
    }

    /**
     * Update Space view
     * @param strViewType The type of view
     */
    private void updateSpaceView( String strViewType )
    {
        int nSpaceId = Integer.parseInt( _strCurrentSpaceId );
        DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nSpaceId );
        space.setViewType( strViewType );
        DocumentSpaceHome.update( space );
    }

    /**
     * return admin message url for generic error with specific action message
     * @param request The HTTPrequest
     * @param strI18nMessage The i18n message
     * @return The admin message url
     */
    private String getErrorMessageUrl( HttpServletRequest request, String strI18nMessage )
    {
        return AdminMessageService.getMessageUrl( request, MESSAGE_DOCUMENT_ERROR,
            new String[] { I18nService.getLocalizedString( strI18nMessage, getLocale(  ) ) }, AdminMessage.TYPE_ERROR );
    }

    /**
     * Gets an html template displaying the patterns list available in the portal for the layout
     *
     * @param nTemplatePageId The identifier of the layout to select in the list
     * @param nPageTemplateDocumentId The page template id
     * @param nIndexRow the index row
     * @return The html code of the list
     */
    private String getTemplatesPageList( int nTemplatePageId, int nPageTemplateDocumentId, String nIndexRow )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        DocumentPageTemplate documentPageTemplate = DocumentPageTemplateHome.findByPrimaryKey( nTemplatePageId );
        model.put( MARK_DOCUMENT_PAGE_TEMPLATE, documentPageTemplate );

        model.put( MARK_INDEX_ROW, nIndexRow );

        String strChecked = ( documentPageTemplate.getId(  ) == nPageTemplateDocumentId ) ? "checked=\"checked\"" : "";
        model.put( MARK_DOCUMENT_PAGE_TEMPLATE_CHECKED, strChecked );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DOCUMENT_PAGE_TEMPLATE_ROW, getLocale(  ),
                model );

        return template.getHtml(  );
    }

    private boolean isAuthorized( int nActionId, Document document )
    {
        DocumentAction action = DocumentActionHome.findByPrimaryKey( nActionId );

        if ( ( action == null ) || ( action.getFinishDocumentState(  ) == null ) || ( document == null ) ||
                !DocumentService.getInstance(  )
                                    .isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return false;
        }

        return true;
    }
}
