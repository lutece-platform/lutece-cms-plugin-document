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

import fr.paris.lutece.plugins.document.web.spaces.DocumentSpaceJspBean;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentFilter;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentMassAction;
import fr.paris.lutece.plugins.document.business.DocumentPageTemplate;
import fr.paris.lutece.plugins.document.business.DocumentPageTemplateHome;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.IndexerAction;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.plugins.document.business.spaces.SpaceAction;
import fr.paris.lutece.plugins.document.service.spaces.SpaceResourceIdService;
import fr.paris.lutece.plugins.document.business.spaces.SpaceActionHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentAction;
import fr.paris.lutece.plugins.document.business.workflow.DocumentActionHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.plugins.document.business.workflow.DocumentStateHome;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.plugins.document.service.DocumentMassActionResourceService;
import fr.paris.lutece.plugins.document.service.DocumentParameterService;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.DocumentTypeResourceIdService;
import fr.paris.lutece.plugins.document.service.category.CategoryService;
import fr.paris.lutece.plugins.document.service.category.CategoryService.CategoryDisplay;
import fr.paris.lutece.plugins.document.service.metadata.MetadataHandler;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.plugins.document.service.search.DocumentIndexer;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.plugins.document.utils.DocumentIndexerUtils;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.business.resourceenhancer.ResourceEnhancer;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.right.RightHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.PasswordResetException;
import fr.paris.lutece.plugins.document.service.view.DocumentTemplateService;
import fr.paris.lutece.plugins.document.service.view.DocumentView;
import fr.paris.lutece.plugins.document.service.view.DocumentViewService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.resource.ExtendableResourcePluginActionManager;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.sort.AttributeComparator;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.api.user.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;



/**
 * JspBean for document management
 */
@SessionScoped
@Named
public class DocumentJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_DOCUMENT_MANAGEMENT = "DOCUMENT_MANAGEMENT";
    /** Action of the security tokens of the document management actions, also used by the document dashboard */
    public static final String SECURITY_TOKEN_ACTION = "jsp/admin/plugins/document/ManageDocuments.jsp";
    public static final String PARAMETER_SPACE_ID_FILTER = "id_space_filter";
    private static final long serialVersionUID = 3884593136805763150L;

    // Templates
    private static final String TEMPLATE_MANAGE_DOCUMENTS = "admin/plugins/document/manage_documents.html";
    private static final String TEMPLATE_SPACES_TREE = "admin/plugins/document/spaces/spaces_tree.html";
    private static final String TEMPLATE_CREATE_DOCUMENT = "admin/plugins/document/create_document.html";
    private static final String TEMPLATE_MODIFY_DOCUMENT = "admin/plugins/document/modify_document.html";
    private static final String TEMPLATE_PREVIEW_DOCUMENT = "admin/plugins/document/preview_document.html";
    private static final String TEMPLATE_MOVE_DOCUMENT = "admin/plugins/document/move_document.html";
    private static final String TEMPLATE_MOVE_DOCUMENT_FORM = "admin/plugins/document/move_document_form.html";
    private static final String TEMPLATE_DOCUMENT_PAGE_TEMPLATE_ROW = "admin/plugins/document/page_template_list_row.html";
    private static final String TEMPLATE_FORM_CATEGORY = "admin/plugins/document/category/list_category.html";
    private static final String TEMPLATE_MASS_ARCHIVAL = "admin/plugins/document/mass_archival.html";

    // Markers
    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_PREVIEW = "preview";
    private static final String MARK_DOCUMENTS_LIST = "documents_list";
    private static final String MARK_DOCUMENT_TYPES_LIST = "document_types_list";
    private static final String MARK_MASS_ACTION = "mass_action";
    private static final String MARK_DOCUMENT_TYPES_FILTER_LIST = "document_types_filter_list";
    private static final String MARK_STATES_FILTER_LIST = "states_filter_list";
    private static final String MARK_SPACES_TREE = "spaces_tree";
    private static final String MARK_SPACES_TREE_NODES = "spaces_tree_nodes";
    private static final String MARK_CURRENT_SPACE_ID = "current_space_id";
    private static final String MARK_SPACE_ACTIONS_LIST = "space_actions_list";
    private static final String MARK_SPACE = "space";
    private static final String MARK_BREADCRUMB_SPACES = "breadcrumb_spaces";
    private static final String MARK_STATE_ID = "id_state";
    private static final String MARK_CHILD_SPACES_LIST = "child_spaces_list";
    private static final String MARK_FIELDS = "fields";
    private static final String MARK_CURRENT_DATE = "current_date";
    private static final String MARK_DOCUMENT_TYPE = "document_type";
    private static final String MARK_DATE_MIN = "date_min";
    private static final String MARK_DATE_MAX = "date_max";
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
    private static final String MARK_METADATA = "metadata_form";
    private static final String MARK_DATE_VALIDITY_BEGIN = "date_validity_begin";
    private static final String MARK_DATE_VALIDITY_END = "date_validity_end";
    private static final String MARK_MAILINGLISTS_LIST = "mailinglists_list";
    private static final String MARK_DOCUMENT_PAGE_TEMPLATES_LIST = "page_template_list";
    private static final String MARK_DOCUMENT_PAGE_TEMPLATE = "document_page_template";
    private static final String MARK_INDEX_ROW = "index_row";
    private static final String MARK_DOCUMENT_PAGE_TEMPLATE_CHECKED = "checked";
    private static final String MARK_SELECTED_CRITERIA = "selected_criteria";
    private static final String MARK_BROWSER_SELECTED_SPACE_ID = "browser_selected_space_id";
    private static final String MARK_IS_FILES2DOCS_PLUGIN_ACTIVE = "is_files2docs_plugin_active";
    private static final String MARK_IS_PARSER_PLUGIN_ACTIVE = "is_parser_plugin_active";
    private static final String MARK_SPACE_TOKEN = "space_token";
    private static final String MARK_CREATE_SPACE_FORM = "create_space_form";
    private static final String MARK_MODIFY_SPACE_FORM = "modify_space_form";
    private static final String PREFIX_CREATE_SPACE = "create_space_";
    private static final String PREFIX_MODIFY_SPACE = "modify_space_";
    private static final String MARK_MOVE_SPACE_FORM = "move_space_form";
    private static final String PREFIX_MOVE_SPACE = "move_space_";
    private static final String MARK_MOVE_DOCUMENT_FORM = "move_document_form";
    private static final String PREFIX_MOVE_DOCUMENT = "move_document_";
    private static final String MARK_IN_MODAL = "in_modal";
    private static final String MARK_ID_PREFIX = "id_prefix";

    // Parameters
    private static final String PARAMETER_DOCUMENT_TYPE_CODE = "document_type_code";
    private static final String PARAMETER_DATE_MIN = "date_min";
    private static final String PARAMETER_DATE_MAX = "date_max";
    private static final String PARAMETER_DOCUMENT_ID = "id_document";
    private static final String PARAMETER_STATE_ID = "id_state";
    private static final String PARAMETER_ACTION_ID = "id_action";
    private static final String PARAMETER_VIEW_TYPE = "view_type";
    private static final String PARAMETER_DOCUMENT_TYPE_CODE_FILTER = "document_type_code_filter";
    private static final String PARAMETER_STATE_ID_FILTER = "id_state_filter";
    private static final String PARAMETER_SELECTION = "selection";
    private static final String PARAMETER_DOCUMENT_SELECTION = "document_selection";
    private static final String PARAMETER_HEADER_REFERER = "referer";
    private static final String PARAMETER_FROM_URL = "fromUrl";
    private static final String PARAMETER_ARCHIVAL_CRITERIA = "archival_criteria";

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
    private static final String JSP_DOCUMENTS_PUBLISHING = "ManageDocumentPublishing.jsp";
    private static final String JSP_MANAGE_DOCUMENTS = "jsp/admin/plugins/document/ManageDocuments.jsp";
    private static final String JSP_MASS_ARCHIVAL = "jsp/admin/plugins/document/MassArchivalDocument.jsp";
    private static final String JSP_DO_MASS_ARCHIVAL = "jsp/admin/plugins/document/DoMassArchivalDocument.jsp";

    // Messages
    private static final String MESSAGE_CONFIRM_DELETE = "document.message.confirmDeleteDocument";
    private static final String MESSAGE_CONFIRM_MASS_ARCHIVE = "document.message.confirmMassArchivalDocument";
    private static final String MESSAGE_NO_MASS_ARCHIVE = "document.message.noMassArchivalDocument";
    private static final String MESSAGE_INVALID_DOCUMENT_ID = "document.message.invalidDocumentId";
    private static final String MESSAGE_DOCUMENT_NOT_FOUND = "document.message.documentNotFound";
    private static final String MESSAGE_DOCUMENT_NOT_AUTHORIZED = "document.message.documentNotAuthorized";
    private static final String MESSAGE_MOVING_NOT_AUTHORIZED = "document.message.movingNotAuthorized";
    private static final String MESSAGE_DOCUMENT_IS_PUBLISHED = "document.message.documentIsPublished";
    private static final String MESSAGE_ERROR_DOCUMENT_IS_PUBLISHED = "document.message.errorDocumentIsPublished";
    private static final String MESSAGE_DOCUMENT_ERROR = "document.message.documentError"; //TODO message erreur
    private static final String MESSAGE_ACTION_NOT_AVAILABLE = "document.message.actionNotAvailable";
    private static final String MESSAGE_CONFIRM_DELETE_SELECTION = "document.message.confirmDeleteSelection";
    private static final String MESSAGE_CONFIRM_ARCHIVE_SELECTION = "document.message.selectionPublished";
    private static final String MESSAGE_ERROR_DOCUMENT_SELECTED_IS_PUBLISHED = "document.message.errorDocumentSelectedIsPublished";
    private static final String MESSAGE_SELECTION_EMPTY = "document.message.selectionEmpty";
    private static final String MESSAGE_SELECTION_NONE_PROCESSED = "document.message.selectionNoneProcessed";
    private static final String MESSAGE_SELECTION_PARTIALLY_PROCESSED = "document.message.selectionPartiallyProcessed";
    private static final String PROPERTY_SELECTION_ACTION_PREFIX = "document.manage_document.";

    //constants
    private static final String CONSTANT_REMOVE = "remove";
    private static final String CONSTANT_VALIDATE = "validate";
    private static final String CONSTANT_ARCHIVE = "archive";
    private static final String CONSTANT_REFUSE = "refuse";
    private static final String CONSTANT_UNARCHIVE = "unarchive";
    private static final String CONSTANT_SUBMIT = "submit";
    private static final String CONSTANT_AND = "&";
    private static final String CONSTANT_AND_HTML = "%26";
    private static final String PATH_JSP = "jsp/admin/plugins/document/";
    private static final String FILTER_ALL = "-1";
    private static final String PAGE_INDEX_FIRST = "1";
    private static final String CONSTANT_DATE_FORMAT = "dd/MM/yyyy";    
    private static final String CONSTANT_FILES2DOC_PLUGIN_NAME = "files2docs" ;
    private static final String CONSTANT_PARSER_PLUGIN_NAME = "parser" ;
    
    private String _strCurrentDocumentTypeFilter;
    private String _strCurrentStateFilter;
    private String _strCurrentSpaceId;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private String _strViewType;
    private int _nDefaultItemsPerPage;
    private String[] _multiSelectionValues;
    private String _strFeatureUrl;
    private String _strSavedReferer;

    @Inject
    private DocumentService _documentService;
    
    @Inject
    private CategoryService _categoryService;
    
    @Inject
    private DocumentSpacesService _documentSpacesService;
    
    @Inject
    private PublishingService _publishingService;
    
	@Inject
	private Models _model;

    @Inject
    private DocumentViewService _documentViewService;

    @Inject
    private DocumentTemplateService _documentTemplateService;

    @Inject
    private DocumentParameterService _documentParameterService;

	
	
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
        this._strSavedReferer = null;
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
        if ( !_documentSpacesService.isAuthorizedViewByWorkgroup( IntegerUtils.convert( _strCurrentSpaceId ), user ) ||
                !_documentSpacesService.isAuthorizedViewByRole( IntegerUtils.convert( _strCurrentSpaceId ), user ) )
        {
            filter.setIdSpace( _documentSpacesService.getUserDefaultSpace( user ) );
            _strCurrentSpaceId = Integer.toString( filter.getIdSpace(  ) );
        }

        // Build document list according criterias
        Collection<Integer> listDocumentIds = DocumentHome.findPrimaryKeysByFilter( filter, getLocale(  ) );

        // Spaces
        Map<String, Object> modelSpacesTree = new HashMap<>(  );
        modelSpacesTree.put( MARK_SPACES_TREE_NODES, _documentSpacesService.getSpacesTree( user ) );
        modelSpacesTree.put( MARK_CURRENT_SPACE_ID, _strCurrentSpaceId );

        String strSpacesTree = AppTemplateService.getTemplate( TEMPLATE_SPACES_TREE, getLocale(  ), modelSpacesTree ).getHtml(  );

        List<SpaceAction> listSpaceActions = SpaceActionHome.getActionsList( getLocale(  ) );
        int nCurrentSpaceId = IntegerUtils.convert( _strCurrentSpaceId );
        DocumentSpace currentSpace = DocumentSpaceHome.findByPrimaryKey( nCurrentSpaceId );

        if ( currentSpace != null )
        {
            listSpaceActions = (List<SpaceAction>) RBACService.getAuthorizedActionsCollection( listSpaceActions,
                    currentSpace, ( User ) getUser(  ) );
        }
        else
        {
            // the user has no space to work in
            listSpaceActions = new ArrayList<>( );
        }

        // Build filter combos
        // Document Types
        ReferenceList listDocumentTypes = DocumentSpaceHome.getAllowedDocumentTypes( IntegerUtils.convert( 
                    _strCurrentSpaceId ) );
        listDocumentTypes = RBACService.getAuthorizedReferenceList( listDocumentTypes, DocumentType.RESOURCE_TYPE,
                DocumentTypeResourceIdService.PERMISSION_VIEW, ( User ) user );
        listDocumentTypes.addItem( FILTER_ALL, I18nService.getLocalizedString( PROPERTY_FILTER_ALL, getLocale(  ) ) );

        // Documents States
        ReferenceList listStates = DocumentStateHome.getDocumentStatesList( getLocale(  ) );
        listStates.addItem( FILTER_ALL, I18nService.getLocalizedString( PROPERTY_FILTER_ALL, getLocale(  ) ) );

        // Childs spaces
        Collection<DocumentSpace> listChildSpaces = DocumentSpaceHome.findChilds( nCurrentSpaceId );
        listChildSpaces = AdminWorkgroupService.getAuthorizedCollection( listChildSpaces, ( User ) user );

        // Creation document types list for the current space
        ReferenceList listCreateDocumentTypes = DocumentSpaceHome.getAllowedDocumentTypes( IntegerUtils.convert( 
                    _strCurrentSpaceId ) );
        listCreateDocumentTypes = RBACService.getAuthorizedReferenceList( listCreateDocumentTypes,
                DocumentType.RESOURCE_TYPE, DocumentTypeResourceIdService.PERMISSION_CREATE, ( User ) user );

        LocalizedPaginator<Integer> paginator = new LocalizedPaginator<Integer>( (List<Integer>) listDocumentIds,
                _nItemsPerPage, getHomeUrl( request ), Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex,
                getLocale(  ) );

        List<Document> listDocuments = new ArrayList<Document>(  );

        for ( Integer documentId : paginator.getPageItems(  ) )
        {
            Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( documentId );

            if ( document != null )
            {
                document.setLocale( getLocale(  ) );
                _documentService.getActions( document, getLocale(  ), getUser(  ) );

                _documentService.getPublishedStatus( document );
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

        _model.put( MARK_SPACES_TREE, HtmlMarkup.of( strSpacesTree ) );
        _model.put( MARK_SPACE_ACTIONS_LIST, listSpaceActions );
        _model.put( MARK_SPACE, currentSpace );
        _model.put( MARK_BREADCRUMB_SPACES, _documentSpacesService.getSpacePath( nCurrentSpaceId, getUser(  ) ) );
        _model.put( MARK_PAGINATOR, paginator );
        _model.put( MARK_NB_ITEMS_PER_PAGE, StringUtils.EMPTY + _nItemsPerPage );
        _model.put( MARK_VIEW_TYPES_LIST, DocumentSpaceHome.getViewTypeList( getLocale(  ) ) );
        _model.put( MARK_VIEW_TYPE, _strViewType );
        _model.put( MARK_DOCUMENTS_LIST, listDocuments );
        _model.put( MARK_DOCUMENT_TYPES_FILTER_LIST, listDocumentTypes );
        _model.put( MARK_DEFAULT_DOCUMENT_TYPE, _strCurrentDocumentTypeFilter );
        _model.put( MARK_STATES_FILTER_LIST, listStates );
        _model.put( MARK_DEFAULT_STATE, _strCurrentStateFilter );
        _model.put( MARK_CHILD_SPACES_LIST, listChildSpaces );
        _model.put( MARK_DOCUMENT_TYPES_LIST, listCreateDocumentTypes );
        _model.put( MARK_CURRENT_SPACE_ID, _strCurrentSpaceId );
        _model.put( MARK_MASS_ACTION,
            RBACService.isAuthorized( new DocumentMassAction(  ),
                DocumentMassActionResourceService.PERMISSION_MASS_ARCHIVE, ( User ) user ) );
        
        _model.put( MARK_IS_FILES2DOCS_PLUGIN_ACTIVE , PluginService.isPluginEnable( CONSTANT_FILES2DOC_PLUGIN_NAME )  );
        _model.put( MARK_IS_PARSER_PLUGIN_ACTIVE , PluginService.isPluginEnable( CONSTANT_PARSER_PLUGIN_NAME )  );

        _model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );
        // Token of the spaces actions, used to move a space by drag and drop in the spaces tree
        _model.put( MARK_SPACE_TOKEN, getSecurityTokenService( ).getToken( request, DocumentSpaceJspBean.SECURITY_TOKEN_ACTION ) );

        // Forms of the create, modify and move space actions, opened in a modal
        for ( SpaceAction action : listSpaceActions )
        {
            if ( SpaceResourceIdService.PERMISSION_CREATE.equals( action.getPermission(  ) ) )
            {
                _model.put( MARK_CREATE_SPACE_FORM, HtmlMarkup.of( _documentSpacesService.getCreateSpaceForm( nCurrentSpaceId, user, getLocale(  ),
                        getSecurityTokenService( ).getToken( request, DocumentSpaceJspBean.SECURITY_TOKEN_ACTION ), true, PREFIX_CREATE_SPACE ) ) );
            }
            else if ( SpaceResourceIdService.PERMISSION_MODIFY.equals( action.getPermission(  ) ) )
            {
                _model.put( MARK_MODIFY_SPACE_FORM, HtmlMarkup.of( _documentSpacesService.getModifySpaceForm( nCurrentSpaceId, user, getLocale(  ),
                        getSecurityTokenService( ).getToken( request, DocumentSpaceJspBean.SECURITY_TOKEN_ACTION ), true, PREFIX_MODIFY_SPACE ) ) );
            }
            else if ( SpaceResourceIdService.PERMISSION_MOVE.equals( action.getPermission(  ) ) )
            {
                _model.put( MARK_MOVE_SPACE_FORM, HtmlMarkup.of( _documentSpacesService.getMoveSpaceForm( nCurrentSpaceId, user, getLocale(  ),
                        getSecurityTokenService( ).getToken( request, DocumentSpaceJspBean.SECURITY_TOKEN_ACTION ), true, PREFIX_MOVE_SPACE ) ) );
            }
        }
        // Form of the move document action, opened in a modal: rendered once when a document of the list can be moved
        boolean bMoveAllowed = listDocuments.stream(  ).anyMatch( doc -> ( doc.getActions(  ) != null ) &&
                    ( (List<?>) doc.getActions(  ) ).stream(  ).anyMatch( action -> ( action instanceof DocumentAction ) &&
                    ( ( (DocumentAction) action ).getIdAction(  ) == DocumentAction.ACTION_MOVE ) ) );

        if ( bMoveAllowed )
        {
            _model.put( MARK_MOVE_DOCUMENT_FORM, HtmlMarkup.of( getMoveDocumentForm( request, null, true ) ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DOCUMENTS, getLocale(  ), _model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Gets the document creation page
     * @param request The HTTP request
     * @return The document creation page
     */
    public String getCreateDocument( HttpServletRequest request )
    {
        saveReferer( request );

        String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

        if ( ( _strCurrentSpaceId == null ) ||
                ( !_documentService.isAuthorizedAdminDocument( IntegerUtils.convert( _strCurrentSpaceId ),
                    strDocumentTypeCode, DocumentTypeResourceIdService.PERMISSION_CREATE, getUser(  ) ) ) )
        {
            return getManageDocuments( request );
        }

        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strDocumentTypeCode );

        if ( documentType == null )
        {
            return getManageDocuments( request );
        }

        DateFormat dateFormat = new SimpleDateFormat( CONSTANT_DATE_FORMAT, getLocale(  ) );
        String strCurrentDate = dateFormat.format( new Date(  ) );

        _model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        _model.put( MARK_LOCALE, getLocale(  ).getLanguage(  ) );
        _model.put( MARK_DOCUMENT_TYPE, documentType.getCode(  ) );
        _model.put( MARK_BREADCRUMB_SPACES,
            _documentSpacesService.getSpacePath( IntegerUtils.convert( _strCurrentSpaceId ), getUser(  ) ) );

        _model.put( MARK_CATEGORY, HtmlMarkup.of( getCategoryCreateForm( request ) ) );

        _model.put( MARK_METADATA, HtmlMarkup.of( getMetadataCreateForm( request, strDocumentTypeCode ) ) );
        _model.put( MARK_FIELDS, HtmlMarkup.of(
        		_documentService.getCreateForm( strDocumentTypeCode, getLocale(  ), AppPathService.getBaseUrl( request ) ) ) );
        _model.put( MARK_CURRENT_DATE, strCurrentDate );

        // PageTemplate
        int nIndexRow = 1;
        StringBuilder strPageTemplatesRow = new StringBuilder(  );

        // Scan of the list
        for ( DocumentPageTemplate documentPageTemplate : DocumentPageTemplateHome.getPageTemplatesList(  ) )
        {
            strPageTemplatesRow.append( getTemplatesPageList( documentPageTemplate, nIndexRow == 1,
                    Integer.toString( nIndexRow ) ) );
            nIndexRow++;
        }

        Map<String, Object> model = new HashMap<>( );
        
        // additionnal create info
        ResourceEnhancer.getCreateResourceModelAddOn( model );
        
        for ( Map.Entry<String, Object> entry : model.entrySet( ) ) 
        {
            _model.put( entry.getKey( ), entry.getValue( ) );
        }

        _model.put( MARK_DOCUMENT_PAGE_TEMPLATES_LIST, HtmlMarkup.of( strPageTemplatesRow.toString(  ) ) );

        ReferenceList listMailingLists = AdminMailingListService.getMailingLists( getUser(  ) );
        _model.put( MARK_MAILINGLISTS_LIST, listMailingLists );

        _model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_DOCUMENT, getLocale(  ), _model );

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

        _model.put( MARK_CATEGORY_LIST, _categoryService.getAllCategoriesDisplay( user ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FORM_CATEGORY, getLocale(  ), _model );

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

        Collection<CategoryDisplay> listCategoryDisplay = _categoryService.getAllCategoriesDisplay( document.getCategories(  ),
                getUser(  ) );

        _model.put( MARK_CATEGORY_LIST, listCategoryDisplay );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FORM_CATEGORY, getLocale(  ), _model );

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

        return ( hMetadata != null ) ? hMetadata.getCreateForm( request ) : StringUtils.EMPTY;
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

        return ( hMetadata != null ) ? hMetadata.getModifyForm( request, document.getMetadata(  ) ) : StringUtils.EMPTY;
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
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        saveReferer( request );

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        AdminUser user = getUser(  );

        String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

        if ( !_documentService.isAuthorizedAdminDocument( IntegerUtils.convert( _strCurrentSpaceId ),
                    strDocumentTypeCode, DocumentTypeResourceIdService.PERMISSION_CREATE, user ) )
        {
            return getHomeUrl( request );
        }

        Document document = new Document(  );
        document.setCodeDocumentType( strDocumentTypeCode );

        String strError = _documentService.getDocumentData( multipartRequest, document, getLocale(  ) );

        if ( strError != null )
        {
            return strError;
        }

        document.setSpaceId( IntegerUtils.convert( _strCurrentSpaceId ) );
        document.setStateId( 1 );
        document.setCreatorId( getUser(  ).getUserId(  ) );

        try
        {
        	_documentService.createDocument( document, getUser(  ) );
        }
        catch ( DocumentException e )
        {
            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
        }

        // process
        ResourceEnhancer.doCreateResourceAddOn( request, PROPERTY_RESOURCE_TYPE, document.getId(  ) );

        //Here we simulate clicking on the validation buttons. This could should do the same
        //as having request to doStateChange for submitting and doValidateDocument for validating
        if ( _documentParameterService.isWorkflowAutoValidation(  ) )
        {
            String strDocumentId = Integer.toString( document.getId(  ) );

            try
            {
            	_documentService.changeDocumentState( document, getUser(  ), DocumentState.STATE_WAITING_FOR_APPROVAL );

                //Reload document in case listeners have modified it in the database
                document = DocumentHome.findByPrimaryKeyWithoutBinaries( IntegerUtils.convert( strDocumentId ) );

                _documentService.validateDocument( document, getUser(  ), DocumentState.STATE_VALIDATE );
            }
            catch ( DocumentException e )
            {
                return getErrorMessageUrl( request, e.getI18nMessage(  ) );
            }

            IndexationService.addIndexerAction( strDocumentId, DocumentIndexer.INDEXER_NAME, IndexerAction.TASK_MODIFY,
                IndexationService.ALL_DOCUMENT );

            DocumentIndexerUtils.addIndexerAction( strDocumentId, IndexerAction.TASK_MODIFY, IndexationService.ALL_DOCUMENT );
        }

        return getHomeUrl( request );
    }

    /**
     * Gets the document modification page
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String getModifyDocument( HttpServletRequest request )
    {
        saveReferer( request );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( IntegerUtils.convert( strDocumentId ) );

        if ( ( document == null ) ||
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageDocuments( request );
        }

        String strStateId = ( request.getParameter( PARAMETER_STATE_ID ) != null )
            ? request.getParameter( PARAMETER_STATE_ID ) : StringUtils.EMPTY;

        _model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        _model.put( MARK_LOCALE, getLocale(  ).getLanguage(  ) );
        _model.put( MARK_DOCUMENT, document );
        _model.put( MARK_BREADCRUMB_SPACES, _documentSpacesService.getSpacePath( document.getSpaceId(  ), getUser(  ) ) );

        // Date Management
        _model.put( MARK_DATE_VALIDITY_BEGIN,
            ( document.getDateValidityBegin(  ) == null ) ? StringUtils.EMPTY
                                                          : DateUtil.getDateString( 
                new Date( document.getDateValidityBegin(  ).getTime(  ) ), getLocale(  ) ) );
        _model.put( MARK_DATE_VALIDITY_END,
            ( document.getDateValidityEnd(  ) == null ) ? StringUtils.EMPTY
                                                        : DateUtil.getDateString( 
                new Date( document.getDateValidityEnd(  ).getTime(  ) ), getLocale(  ) ) );

        // PageTemplate
        int nIndexRow = 1;
        StringBuilder strPageTemplatesRow = new StringBuilder(  );

        // Scan of the list
        for ( DocumentPageTemplate documentPageTemplate : DocumentPageTemplateHome.getPageTemplatesList(  ) )
        {
            strPageTemplatesRow.append( getTemplatesPageList( documentPageTemplate,
                    documentPageTemplate.getId(  ) == document.getPageTemplateDocumentId(  ), Integer.toString( nIndexRow ) ) );
            nIndexRow++;
        }

        _model.put( MARK_DOCUMENT_PAGE_TEMPLATES_LIST, HtmlMarkup.of( strPageTemplatesRow.toString(  ) ) );

        ReferenceList listMailingLists = AdminMailingListService.getMailingLists( getUser(  ) );
        _model.put( MARK_MAILINGLISTS_LIST, listMailingLists );

        _model.put( MARK_CATEGORY, HtmlMarkup.of( getCategoryModifyForm( request, document ) ) );
        _model.put( MARK_METADATA, HtmlMarkup.of( getMetadataModifyForm( request, document ) ) );
        _model.put( MARK_FIELDS, HtmlMarkup.of(
        		_documentService.getModifyForm( document, getLocale(  ), AppPathService.getBaseUrl( request ) ) ) );
        _model.put( MARK_STATE_ID, strStateId );

        
        Map<String, Object> model = new HashMap<>( );
        
        // The extend actions as a plain list, shown in the Extensions tab of the properties column
        ExtendableResourcePluginActionManager.fillModel( request, getUser(  ), model, strDocumentId,
            Document.PROPERTY_RESOURCE_TYPE, ExtendableResourcePluginActionManager.TEMPLATE_EXTENDABLE_RESOURCE_ACTION_LIST );
        
        ResourceEnhancer.getModifyResourceModelAddOn( model, PROPERTY_RESOURCE_TYPE, document.getId(  ) );
        
        for ( Map.Entry<String, Object> entry : model.entrySet( ) ) 
        {
            _model.put( entry.getKey( ), entry.getValue( ) );
        }

        _model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_DOCUMENT, getLocale(  ), _model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyDocument( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        saveReferer( request );

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String strDocumentId = multipartRequest.getParameter( PARAMETER_DOCUMENT_ID );
        Document document = DocumentHome.findByPrimaryKey( IntegerUtils.convert( strDocumentId ) );

        if ( ( document == null ) ||
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        String strError = _documentService.getDocumentData( multipartRequest, document, getLocale(  ) );

        if ( strError != null )
        {
            return strError;
        }

        try
        {
        	_documentService.modifyDocument( document, getUser(  ) );
        }
        catch ( DocumentException e )
        {
            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
        }

        // If a state is defined, it should be set after to the document after the modification
        String strStateId = multipartRequest.getParameter( PARAMETER_STATE_ID );

        if ( IntegerUtils.isNumeric( strStateId ) )
        {
            int nStateId = IntegerUtils.convert( strStateId );

            try
            {
            	_documentService.changeDocumentState( document, getUser(  ), nStateId );
            }
            catch ( DocumentException e )
            {
                return getErrorMessageUrl( request, e.getI18nMessage(  ) );
            }
        }

        ResourceEnhancer.doModifyResourceAddOn( request, PROPERTY_RESOURCE_TYPE, document.getId(  ) );

        //Here we simulate clicking on the validation buttons. This could should do the same
        //as having request to doStateChange for changesubmitting and doValidateDocument for revalidating
        if ( _documentParameterService.isWorkflowAutoValidation(  ) )
        {
            try
            {
            	_documentService.changeDocumentState( document, getUser(  ), DocumentState.STATE_WAITING_FOR_APPROVAL );

                //Reload document in case listeners have modified it in the database
                document = DocumentHome.findByPrimaryKeyWithoutBinaries( IntegerUtils.convert( strDocumentId ) );

                _documentService.validateDocument( document, getUser(  ), DocumentState.STATE_VALIDATE );
            }
            catch ( DocumentException e )
            {
                return getErrorMessageUrl( request, e.getI18nMessage(  ) );
            }

            IndexationService.addIndexerAction( strDocumentId, DocumentIndexer.INDEXER_NAME, IndexerAction.TASK_MODIFY,
                IndexationService.ALL_DOCUMENT );

            DocumentIndexerUtils.addIndexerAction( strDocumentId, IndexerAction.TASK_MODIFY, IndexationService.ALL_DOCUMENT );
        }

        return getHomeUrl( request );
    }

    /**
     * Confirm the deletion
     * @param request The HTTP request
     * @return The URL to go after performing the confirmation
     */
    public String deleteDocument( HttpServletRequest request )
    {
        saveReferer( request );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = IntegerUtils.convert( strDocumentId );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( ( document == null ) ||
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        Object[] messageArgs = { document.getTitle(  ) };
        UrlItem url = new UrlItem( PATH_JSP + JSP_DELETE_DOCUMENT );
        url.addParameter( SecurityTokenService.PARAMETER_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );
        url.addParameter( PARAMETER_DOCUMENT_ID, nDocumentId );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE, messageArgs,null, url.getUrl(  ),null,
            AdminMessage.TYPE_CONFIRMATION, null,JSP_MANAGE_DOCUMENTS  );
    }

    /**
     * Perform the deletion
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doDeleteDocument( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        saveReferer( request );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = IntegerUtils.convert( strDocumentId );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( ( document != null ) &&
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        // Test if the document is published or assigned
        boolean bPublishedDocument = _publishingService.isAssigned( nDocumentId );

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
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        saveReferer( request );

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
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        if ( !isActionAvailable( document, nActionId ) )
        {
            // action of another state (page of the browser history, second tab): validating again an approved document would delete its content
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACTION_NOT_AVAILABLE, AdminMessage.TYPE_STOP );
        }

        try
        {
        	_documentService.changeDocumentState( document, getUser(  ), action.getFinishDocumentState(  ).getId(  ) );
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
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        saveReferer( request );

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
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        if ( !isActionAvailable( document, nActionId ) )
        {
            // action of another state (page of the browser history, second tab): validating again an approved document would delete its content
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACTION_NOT_AVAILABLE, AdminMessage.TYPE_STOP );
        }

        try
        {
        	_documentService.validateDocument( document, getUser(  ), action.getFinishDocumentState(  ).getId(  ) );
        }
        catch ( DocumentException e )
        {
            return getErrorMessageUrl( request, e.getI18nMessage(  ) );
        }

        String strIdDocument = Integer.toString( nDocumentId );
        IndexationService.addIndexerAction( strIdDocument, DocumentIndexer.INDEXER_NAME, IndexerAction.TASK_MODIFY,
            IndexationService.ALL_DOCUMENT );

        DocumentIndexerUtils.addIndexerAction( strIdDocument, IndexerAction.TASK_MODIFY, IndexationService.ALL_DOCUMENT );

        /*
         * Collection<Portlet> portlets =
         * PublishingService.getInstance().getPortletsByDocumentId
         * (Integer.toString(nDocumentId));
         * for(Portlet portlet : portlets)
         * {
         * if(PublishingService.getInstance().isPublished(nDocumentId,
         * portlet.getId()))
         * {
         * IndexationService.getInstance().addIndexerAction(portlet.getPageId(),
         * PageIndexer.INDEXER_NAME, IndexerAction.TASK_MODIFY);
         * }
         *
         * }
         */
        return getHomeUrl( request );
    }

    /**
     * Confirm the filing
     * @param request The HTTP servlet Request
     * @return The url of filing jsp
     */
    public String doConfirmArchiveDocument( HttpServletRequest request )
    {
        saveReferer( request );

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
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        UrlItem url = new UrlItem( JSP_ARCHIVE_DOCUMENT );
        url.addParameter( SecurityTokenService.PARAMETER_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );
        url.addParameter( PARAMETER_DOCUMENT_ID, nDocumentId );
        url.addParameter( PARAMETER_ACTION_ID, nActionId );

        // Test if the document is published or assigned
        boolean bPublishedDocument = _publishingService.isAssigned( nDocumentId );

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
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int nDocumentId;

        for ( String strIdDocument : _multiSelectionValues )
        {
            try
            {
                nDocumentId = Integer.parseInt( strIdDocument );

                Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

                if ( !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                            document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_DELETE,
                            getUser(  ) ) )
                {
                    return getHomeUrl( request );
                }

                if ( ( document.getStateId(  ) == DocumentState.STATE_WRITING ) ||
                        ( document.getStateId(  ) == DocumentState.STATE_ARCHIVED ) )
                {
                    // Test if the document is published or assigned
                    boolean bPublishedDocument = _publishingService.isAssigned( nDocumentId );

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
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int nDocumentId;

        for ( String strIdDocument : _multiSelectionValues )
        {
            try
            {
                nDocumentId = Integer.parseInt( strIdDocument );

                Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

                // only the validated documents can be archived, as when no document of the selection is published
                if ( ( document != null ) && ( document.getStateId(  ) == DocumentState.STATE_VALIDATE ) &&
                        isAuthorized( DocumentAction.ACTION_ARCHIVE, document ) )
                {
                    try
                    {
                        _documentService.archiveDocument( document, getUser(  ), DocumentState.STATE_ARCHIVED );
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
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strAction = request.getParameter( PARAMETER_SELECTION );
        String[] strIdDocuments = (String[]) request.getParameterMap(  ).get( PARAMETER_DOCUMENT_SELECTION );
        _multiSelectionValues = strIdDocuments;

        if ( ArrayUtils.isEmpty( strIdDocuments ) || StringUtils.isBlank( strAction ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_SELECTION_EMPTY, getHomeUrl( request ), AdminMessage.TYPE_STOP );
        }

        int nbDocumentsAffected = 0;

        if ( !ArrayUtils.isEmpty( strIdDocuments ) )
        {
            if ( strAction.equals( CONSTANT_REMOVE ) )
            {
                UrlItem url = new UrlItem( JSP_DO_REMOVE_SELECTION );
                url.addParameter( SecurityTokenService.PARAMETER_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );

                return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_SELECTION,null, null, url.getUrl(  ),null,
                    AdminMessage.TYPE_CONFIRMATION, null, JSP_MANAGE_DOCUMENTS );
            }
            else if ( strAction.equals( CONSTANT_ARCHIVE ) )
            {
                UrlItem url = new UrlItem( JSP_DO_ARCHIVE_SELECTION );
                url.addParameter( SecurityTokenService.PARAMETER_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );

                for ( String strIdDocument : _multiSelectionValues )
                {
                    int nIdDocument = IntegerUtils.convert( strIdDocument );

                    Document document = DocumentHome.findByPrimaryKey( nIdDocument );

                    // Test if the document is published or assigned
                    boolean bPublishedDocument = _publishingService.isAssigned( nIdDocument );

                    if ( ( document != null ) && ( document.getStateId(  ) == DocumentState.STATE_VALIDATE ) &&
                            ( bPublishedDocument ) )
                    {
                        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_ARCHIVE_SELECTION,
                            PATH_JSP + url.getUrl(  ), AdminMessage.TYPE_QUESTION );
                    }
                }

                for ( String strIdDocument : _multiSelectionValues )
                {
                    int nIdDocument = IntegerUtils.convert( strIdDocument );

                    Document document = DocumentHome.findByPrimaryKey( nIdDocument );

                    try
                    {
                        if ( ( document != null ) && ( document.getStateId(  ) == DocumentState.STATE_VALIDATE ) &&
                                isAuthorized( DocumentAction.ACTION_ARCHIVE, document ) )
                        {
                            _documentService.archiveDocument( document, getUser(  ), DocumentState.STATE_ARCHIVED );
                            nbDocumentsAffected++;
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
                    int nIdDocument = IntegerUtils.convert( strIdDocument );
                    int nActionId = -1;
                    Document document = DocumentHome.findByPrimaryKey( nIdDocument );

                    if ( document != null )
                    {
                        int stateId = document.getStateId(  );

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
                                    _documentService.validateDocument( document, getUser(  ),
                                        DocumentState.STATE_VALIDATE );
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
                                        _documentService.changeDocumentState( document, getUser(  ),
                                            DocumentState.STATE_REJECTED );
                                        nbDocumentsAffected++;
                                    }
                                }
                                else if ( stateId == DocumentState.STATE_WAITING_FOR_CHANGE_APPROVAL )
                                {
                                    nActionId = DocumentAction.ACTION_REFUSE_CHANGE;

                                    if ( isAuthorized( nActionId, document ) )
                                    {
                                        _documentService.changeDocumentState( document, getUser(  ),
                                            DocumentState.STATE_IN_CHANGE );
                                        nbDocumentsAffected++;
                                    }
                                }
                            }
                            catch ( DocumentException e )
                            {
                                return getErrorMessageUrl( request, e.getI18nMessage(  ) );
                            }
                        }
                        else if ( ( strAction.equals( CONSTANT_UNARCHIVE ) ) &&
                                ( stateId == DocumentState.STATE_ARCHIVED ) )
                        {
                            nActionId = DocumentAction.ACTION_UNARCHIVE;

                            try
                            {
                                if ( isAuthorized( nActionId, document ) )
                                {
                                    _documentService.changeDocumentState( document, getUser(  ),
                                        DocumentState.STATE_VALIDATE );
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
                                    _documentService.changeDocumentState( document, getUser(  ),
                                        DocumentState.STATE_WAITING_FOR_APPROVAL );
                                    nbDocumentsAffected++;
                                }
                                else if ( ( stateId == DocumentState.STATE_IN_CHANGE ) )
                                {
                                    nActionId = DocumentAction.ACTION_SUBMIT_CHANGE;

                                    if ( isAuthorized( nActionId, document ) )
                                    {
                                        _documentService.changeDocumentState( document, getUser(  ),
                                            DocumentState.STATE_WAITING_FOR_CHANGE_APPROVAL );
                                        nbDocumentsAffected++;
                                    }
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
        }

        return getSelectionResultUrl( request, strAction, strIdDocuments.length, nbDocumentsAffected );
    }

    /**
     * Builds the URL to go to after an action on a selection of documents: back to the list when every selected
     * document has been processed, otherwise a message telling how many documents the action could not be applied to
     * @param request The Http request
     * @param strAction The action applied to the selection
     * @param nbDocumentsSelected The number of selected documents
     * @param nbDocumentsAffected The number of documents the action has been applied to
     * @return The URL to redirect to
     */
    private String getSelectionResultUrl( HttpServletRequest request, String strAction, int nbDocumentsSelected, int nbDocumentsAffected )
    {
        if ( nbDocumentsAffected >= nbDocumentsSelected )
        {
            return getHomeUrl( request );
        }

        String strActionLabel = I18nService.getLocalizedString( PROPERTY_SELECTION_ACTION_PREFIX + strAction, getLocale(  ) );

        if ( nbDocumentsAffected == 0 )
        {
            Object[] messageArgs = { strActionLabel, nbDocumentsSelected };

            return AdminMessageService.getMessageUrl( request, MESSAGE_SELECTION_NONE_PROCESSED, messageArgs, getHomeUrl( request ),
                AdminMessage.TYPE_STOP );
        }

        Object[] messageArgs = { strActionLabel, nbDocumentsAffected, nbDocumentsSelected };

        return AdminMessageService.getMessageUrl( request, MESSAGE_SELECTION_PARTIALLY_PROCESSED, messageArgs, getHomeUrl( request ),
            AdminMessage.TYPE_WARNING );
    }

    /**
     * Perform the document filing
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doArchiveDocument( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        saveReferer( request );

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
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            _documentService.archiveDocument( document, getUser(  ), action.getFinishDocumentState(  ).getId(  ) );
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
        saveReferer( request );

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

        if ( !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
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
        saveReferer( request );
        setPageTitleProperty( PROPERTY_PREVIEW_DOCUMENT_PAGE_TITLE );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = IntegerUtils.convert( strDocumentId );

        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( ( document == null ) ||
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
        {
            return getManageDocuments( request );
        }

        document.setLocale( getLocale(  ) );
        _documentService.getActions( document, getLocale(  ), getUser(  ) );

        DocumentType type = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType(  ) );

        DocumentHome.loadAttributesWithoutBinaries( document, false );

        DocumentView view = _documentViewService.buildView( document, false );
        String strPreview = _documentTemplateService.renderAdmin( type, view, getLocale(  ) );

       
        _model.put( MARK_DOCUMENT, document );
        _model.put( MARK_BREADCRUMB_SPACES, _documentSpacesService.getSpacePath( document.getSpaceId(  ), getUser(  ) ) );
        _model.put( MARK_PREVIEW, HtmlMarkup.of( strPreview ) );

        Map<String, Object> model = new HashMap<>( );
        
        ExtendableResourcePluginActionManager.fillModel( request, getUser(  ), model, strDocumentId,
            Document.PROPERTY_RESOURCE_TYPE );

        for ( Map.Entry<String, Object> entry : model.entrySet( ) ) 
        {
            _model.put( entry.getKey( ), entry.getValue( ) );
        }
        
        _model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PREVIEW_DOCUMENT, getLocale(  ), _model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the changing of space
     * @param request The HTTP request
     * @return The document move page
     */
    public String getMoveDocument( HttpServletRequest request )
    {
        saveReferer( request );
        setPageTitleProperty( PROPERTY_MOVE_DOCUMENT_PAGE_TITLE );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( IntegerUtils.convert( strDocumentId ) );

        if ( ( document == null ) ||
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), DocumentTypeResourceIdService.PERMISSION_MOVE, getUser(  ) ) )
        {
            return getManageDocuments( request );
        }

        _model.put( MARK_DOCUMENT, document );
        _model.put( MARK_BREADCRUMB_SPACES, _documentSpacesService.getSpacePath( document.getSpaceId(  ), getUser(  ) ) );
        _model.put( MARK_MOVE_DOCUMENT_FORM, HtmlMarkup.of( getMoveDocumentForm( request, document, false ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MOVE_DOCUMENT, getLocale(  ), _model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Gets the form to move a document to another space
     * @param request The HTTP request
     * @param document The document to move, or null for the form of the modal, which receives the document when it opens
     * @param bModal true if the form is displayed in a modal dialog
     * @return The form
     */
    private String getMoveDocumentForm( HttpServletRequest request, Document document, boolean bModal )
    {
        Map<String, Object> model = new HashMap<>(  );
        model.put( MARK_SPACES_TREE, _documentSpacesService.getSpacesTreeRoots( getUser(  ) ) );
        model.put( MARK_DOCUMENT, document );
        model.put( MARK_IN_MODAL, bModal );
        model.put( MARK_ID_PREFIX, bModal ? PREFIX_MOVE_DOCUMENT : StringUtils.EMPTY );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, SECURITY_TOKEN_ACTION ) );

        return AppTemplateService.getTemplate( TEMPLATE_MOVE_DOCUMENT_FORM, getLocale(  ), model ).getHtml(  );
    }

    /**
     * Perform the document moving
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveDocument( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        saveReferer( request );

        boolean bTypeAllowed = Boolean.FALSE;
        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );

        if ( strSpaceId == null )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nSpaceIdDestination = IntegerUtils.convert( strSpaceId );
        DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nSpaceIdDestination );
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( IntegerUtils.convert( strDocumentId ) );

        if ( document == null )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_ERROR );
        }

        // Check if user have rights to create a document into this space
        if ( !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_MOVE, getUser(  ) ) ||
                !_documentService.isAuthorizedAdminDocument( nSpaceIdDestination, document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_MOVE, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        for ( String documentType : space.getAllowedDocumentTypes(  ) )
        {
            if ( document.getCodeDocumentType(  ).equals( documentType ) )
            {
                bTypeAllowed = Boolean.TRUE;
            }
        }

        if ( bTypeAllowed )
        {
            document.setSpaceId( nSpaceIdDestination );
            DocumentHome.update( document, false );

            return getHomeUrl( request );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_MOVING_NOT_AUTHORIZED, AdminMessage.TYPE_STOP );
    }

    /**
     * Gets the mass archival page
     * @param request The HTTP request
     * @return The mass archival page
     */
    public String getMassArchivalDocument( HttpServletRequest request )
    {
        if ( !isMassArchivalAuthorized(  ) )
        {
            return getManageDocuments( request );
        }

        // Empty previous just in case
        request.getSession(  ).removeAttribute( "to_archive" );

        String strCriteria = getParameterOrSessionAttribute( request, PARAMETER_ARCHIVAL_CRITERIA );
        String strTypeCode = getParameterOrSessionAttribute( request, PARAMETER_DOCUMENT_TYPE_CODE );
        String strDateMin = getParameterOrSessionAttribute( request, PARAMETER_DATE_MIN );
        String strDateMax = getParameterOrSessionAttribute( request, PARAMETER_DATE_MAX );

        _model.put( MARK_SELECTED_CRITERIA, Objects.toString( strCriteria, "date" ) );
        _model.put( MARK_LOCALE, getLocale(  ).getLanguage(  ) );
        _model.put( MARK_SPACES_TREE, _documentSpacesService.getSpacesTreeRoots( getUser(  ) ) );
        _model.put( MARK_BROWSER_SELECTED_SPACE_ID,
                StringUtils.defaultString( request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID ) ) );
        _model.put( MARK_DOCUMENT_TYPES_LIST, DocumentTypeHome.findAll(  ) );
        _model.put( MARK_DOCUMENT_TYPE, StringUtils.defaultString( strTypeCode ) );
        _model.put( MARK_DATE_MIN, StringUtils.defaultString( strDateMin ) );
        _model.put( MARK_DATE_MAX, StringUtils.defaultString( strDateMax ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MASS_ARCHIVAL, getLocale(  ), _model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Search the documents to archive and ask for the confirmation
     * @param request The HTTP request
     * @return The URL of the confirmation, or of the mass archival page with an error message
     */
    public String doSearchMassArchivalDocument( HttpServletRequest request )
    {
        if ( !isMassArchivalAuthorized(  ) )
        {
            return getHomeUrl( request );
        }

        request.getSession(  ).removeAttribute( "to_archive" );

        String strCriteria = request.getParameter( PARAMETER_ARCHIVAL_CRITERIA );
        String strTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );
        String strDateMin = request.getParameter( PARAMETER_DATE_MIN );
        String strDateMax = request.getParameter( PARAMETER_DATE_MAX );

        DocumentFilter filter = new DocumentFilter(  );
        filter.setIsPublished( false );

        request.getSession(  ).setAttribute( PARAMETER_ARCHIVAL_CRITERIA, strCriteria );

        if ( "date".equals( strCriteria ) )
        {
            if ( StringUtils.isBlank( strDateMin ) && StringUtils.isBlank( strDateMax ) )
            {
                return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, JSP_MASS_ARCHIVAL,
                    AdminMessage.TYPE_ERROR );
            }

            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
                SimpleDateFormat sqlSdf = new SimpleDateFormat( "yyyy-MM-dd" );

                if ( StringUtils.isNotBlank( strDateMin ) )
                {
                    Date dateMin = sdf.parse( strDateMin );
                    filter.setDateMin( sqlSdf.format( dateMin ) );
                }

                if ( StringUtils.isNotBlank( strDateMax ) )
                {
                    Date dateMax = sdf.parse( strDateMax );
                    filter.setDateMax( sqlSdf.format( dateMax ) );
                }

                request.getSession(  ).setAttribute( PARAMETER_DATE_MIN, strDateMin );
                request.getSession(  ).setAttribute( PARAMETER_DATE_MAX, strDateMax );
            }
            catch ( ParseException e )
            {
                AppLogService.error( e );
            }
        }
        else if ( "space".equals( strCriteria ) )
        {
            String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );

            if ( StringUtils.isBlank( strSpaceId ) || !StringUtils.isNumeric( strSpaceId ) )
            {
                return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, JSP_MASS_ARCHIVAL,
                    AdminMessage.TYPE_ERROR );
            }

            filter.setIdSpace( Integer.valueOf( strSpaceId ) );
        }
        else if ( "type".equals( strCriteria ) )
        {
            request.getSession(  ).setAttribute( PARAMETER_DOCUMENT_TYPE_CODE, strTypeCode );
            filter.setCodeDocumentType( strTypeCode );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, JSP_MASS_ARCHIVAL,
                AdminMessage.TYPE_ERROR );
        }

        List<Document> toArchive = new ArrayList<Document>(  );

        for ( Document doc : DocumentHome.findByFilter( filter, getLocale(  ) ) )
        {
            if ( isAuthorized( DocumentAction.ACTION_ARCHIVE, doc ) )
            {
                toArchive.add( doc );
            }
        }

        if ( CollectionUtils.isEmpty( toArchive ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_MASS_ARCHIVE, JSP_MASS_ARCHIVAL,
                AdminMessage.TYPE_ERROR );
        }

        request.getSession(  ).setAttribute( "to_archive", toArchive );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_MASS_ARCHIVE,
            new String[] { toArchive.size(  ) + "" }, JSP_DO_MASS_ARCHIVAL + "?" + SecurityTokenService.PARAMETER_TOKEN + "=" +
            getSecurityTokenService(  ).getToken( request, SECURITY_TOKEN_ACTION ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Check the mass archival permission of the current user
     * @return true if the user may archive documents in mass
     */
    private boolean isMassArchivalAuthorized(  )
    {
        return RBACService.isAuthorized( new DocumentMassAction(  ),
            DocumentMassActionResourceService.PERMISSION_MASS_ARCHIVE, (User) getUser(  ) );
    }

    /**
     * Get a request parameter, or else the session attribute of the same name, which is then removed
     * @param request The HTTP request
     * @param strName The parameter name
     * @return The value
     */
    private String getParameterOrSessionAttribute( HttpServletRequest request, String strName )
    {
        String strValue = request.getParameter( strName );

        if ( StringUtils.isBlank( strValue ) )
        {
            strValue = (String) request.getSession(  ).getAttribute( strName );
            request.getSession(  ).removeAttribute( strName );
        }

        return strValue;
    }

    /**
     * Gets the document creation page
     * @param request The HTTP request
     * @return The document creation page
     * @throws DocumentException
     */
    public String doMassArchivalDocument( HttpServletRequest request )
        throws DocumentException
    {
        if ( !getSecurityTokenService( ).validate( request, SECURITY_TOKEN_ACTION ) )
        {
            return getManageDocuments( request );
        }

        List<Document> documents = (List<Document>) request.getSession(  ).getAttribute( "to_archive" );
        request.getSession(  ).removeAttribute( "to_archive" );

        if ( documents == null )
        {
            return getManageDocuments( request );
        }

        for ( Document doc : documents )
        {
            _documentService.archiveDocument( doc, getUser(  ), DocumentState.STATE_ARCHIVED );
        }

        return getManageDocuments( request );
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
            int nStateId = IntegerUtils.convert( strStateId );
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
                int nSpaceId = _documentSpacesService.getUserDefaultSpace( getUser(  ) );
                strSpaceId = Integer.toString( nSpaceId );
            }
        }

        int nSpaceId = IntegerUtils.convert( strSpaceId );
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
        int nSpaceId = IntegerUtils.convert( _strCurrentSpaceId );
        DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nSpaceId );

        if ( space != null )
        {
            space.setViewType( strViewType );
            DocumentSpaceHome.update( space );
        }
    }

    /**
     * Tells if an action is offered for the current state of a document
     * @param document The document
     * @param nIdAction The action id
     * @return true if the action is one of the actions of the state of the document
     */
    private boolean isActionAvailable( Document document, int nIdAction )
    {
        return DocumentActionHome.getActionsList( document, getLocale(  ) ).stream(  )
                                 .anyMatch( documentAction -> documentAction.getIdAction(  ) == nIdAction );
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
     * Gets an html template displaying the patterns list available in the
     * portal for the layout
     *
     * @param documentPageTemplate The document page template
     * @param bIsTemplateChecked true if this template is checked or if it is the first row in the list (will be checked by default), false otherwise
     * @param nIndexRow the index row
     * @return The html code of the list
     */
    private String getTemplatesPageList( DocumentPageTemplate documentPageTemplate, boolean bIsTemplateChecked, String nIndexRow )
    {
        _model.put( MARK_DOCUMENT_PAGE_TEMPLATE, documentPageTemplate );

        _model.put( MARK_INDEX_ROW, nIndexRow );

        String strChecked = bIsTemplateChecked ? "checked=\"checked\"" : "";
        _model.put( MARK_DOCUMENT_PAGE_TEMPLATE_CHECKED, strChecked );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DOCUMENT_PAGE_TEMPLATE_ROW, getLocale(  ),
                _model );

        return template.getHtml(  );
    }

    private boolean isAuthorized( int nActionId, Document document )
    {
        DocumentAction action = DocumentActionHome.findByPrimaryKey( nActionId );

        if ( ( action == null ) || ( action.getFinishDocumentState(  ) == null ) || ( document == null ) ||
                !_documentService.isAuthorizedAdminDocument( document.getSpaceId(  ),
                    document.getCodeDocumentType(  ), action.getPermission(  ), getUser(  ) ) )
        {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * @return Return the URL of the menu if the user come from the menu, of the
     *         URL of the main page of this feature if he comes from anywhere
     *         else
     */
    @Override
    public String getHomeUrl( HttpServletRequest request )
    {
        if ( StringUtils.isNotBlank( _strSavedReferer ) )
        {
            return AppPathService.getBaseUrl( request ) + _strSavedReferer;
        }

        return super.getHomeUrl( request );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( HttpServletRequest request, String strRight )
        throws AccessDeniedException, PasswordResetException
    {
        Right right = RightHome.findByPrimaryKey( strRight );
        _strFeatureUrl = right.getUrl(  );
        super.init( request, strRight );
    }

    /**
     * Saves the referer to redirect after the action is performed
     * @param request The request
     */
    private void saveReferer( HttpServletRequest request )
    {
        String strFromUrl = request.getParameter( PARAMETER_FROM_URL );
        String documentId = request.getParameter("id_document");
        if ( StringUtils.isNotBlank( strFromUrl ) )
        {
            _strSavedReferer = strFromUrl.replace( CONSTANT_AND_HTML, CONSTANT_AND );
        }
        else
        {
            String strReferer = request.getHeader( PARAMETER_HEADER_REFERER );
            String strAdminMenuUrl = AppPathService.getAdminMenuUrl(  );

            if ( Strings.CS.contains( strReferer, strAdminMenuUrl ) )
            {
                _strSavedReferer = strAdminMenuUrl;
            }
            else if ( Strings.CS.contains( strReferer, _strFeatureUrl ) ||  Strings.CS.contains( strReferer, JSP_DOCUMENTS_PUBLISHING ) )
            {
                _strSavedReferer = _strFeatureUrl + "#document_" + documentId;
            }
        }
    }
}
