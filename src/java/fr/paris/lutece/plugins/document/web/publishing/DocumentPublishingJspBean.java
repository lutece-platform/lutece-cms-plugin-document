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
package fr.paris.lutece.plugins.document.web.publishing;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentPageTemplate;
import fr.paris.lutece.plugins.document.business.DocumentPageTemplateHome;
import fr.paris.lutece.plugins.document.business.autopublication.DocumentAutoPublication;
import fr.paris.lutece.plugins.document.business.autopublication.DocumentAutoPublicationHome;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortlet;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome;
import fr.paris.lutece.plugins.document.business.portlet.DocumentPortlet;
import fr.paris.lutece.plugins.document.business.portlet.DocumentPortletHome;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublication;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentAction;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.DocumentTypeResourceIdService;
import fr.paris.lutece.plugins.document.service.autopublication.AutoPublicationService;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletType;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.portlet.PortletResourceIdService;
import fr.paris.lutece.portal.service.portlet.PortletService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 */
public class DocumentPublishingJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_DOCUMENT_MANAGEMENT = "DOCUMENT_MANAGEMENT";
    public static final String RIGHT_MANAGE_ADMIN_SITE = "CORE_ADMIN_SITE";
    private static final String REGEX_ID = "^[\\d]+$";
    private static final int MODE_PUBLICATION_STANDARD = 0;
    private static final int MODE_PUBLICATION_AUTO_PUBLICATION = 1;
    private static final String PARAMETER_DOCUMENT_ID = "id_document";
    private static final String PARAMETER_PORTLET_ID = "id_portlet";
    private static final String PARAMETER_SPACE_ID = "id_space";
    private static final String PARAMETER_MODE_PUBLICATION = "mode_publication";
    private static final String PARAMETER_OLD_MODE_PUBLICATION = "old_mode_publication";
    private static final String PARAMETER_DOCUMENT_ORDER = "document_order";
    private static final String PARAMETER_PORTLET_LIST_IDS = "list_portlet_ids";
    private static final String PARAMETER_DOCUMENT_PUBLISHED_STATUS = "status";
    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_DOCUMENT_PUBLISHED = "document_published";
    private static final String MARK_DOCUMENT_PUBLISHED_STATUS = "status";
    private static final String MARK_PUBLISHED_STATUS_VALUE = "status_published";
    private static final String MARK_UNPUBLISHED_STATUS_VALUE = "status_unpublished";
    private static final String MARK_PORTLET_LIST = "portlet_list";
    private static final String MARK_ASSIGNED_PORTLET = "assigned_portlet_list";
    private static final String MARK_ASSIGNED_PUBLICATION = "publication";
    private static final String MARK_PORTLET = "portlet";
    private static final String MARK_LIST_PAGE = "page_list";
    private static final String MARK_MODE_PUBLICATION = "mode_publication";
    private static final String MARK_ASSIGNED_DOCUMENT_LIST = "assigned_document_list";
    private static final String MARK_SPACES_BROWSER = "spaces_browser";
    private static final String MARK_DOCUMENT_AUTO_PUBLICATION = "document_auto_publication";
    private static final String MARK_SPACE_NAME = "space_name";
    private static final String MARK_NUMBER_AUTO_PUBLISHED_DOCUMENTS = "number_auto_published_documents";
    private static final String MARK_LIST_AUTO_PUBLICATION = "list_auto_publication";
    private static final String MARK_DOCUMENT_ORDER = "document_order";
    private static final String MARK_DOCUMENT_ORDER_LIST = "document_order_list";
    private static final String MARK_PUBLISHED_DOCUMENT_LIST = "published_document_list";
    private static final String MARK_PORTLET_ID = "portlet_id";
    private static final String MARK_PAGE_NAME = "page_name";
    private static final String MARK_SUBMIT_BUTTON_DISABLED = "submit_button_disabled";
    private static final String MARK_DOCUMENT_PAGE_TEMPLATE_PICTURE = "page_template_picture";
    private static final String MARK_PERMISSION_PUBLISH = "permission_publish";
    private static final String MARK_PERMISSION_ASSIGN = "permission_assign";
    private static final String PROPERTY_PUBLISHING_SPACE_PAGE_TITLE = "document.assign.pageTitle";
    private static final String PROPERTY_MANAGE_PUBLISHING = "document.portlet.publishing.pageTitle";
    private static final String PROPERTY_CREATE_AUTO_PUBLICATION = "document.portlet.publishing.pageTitle";
    private static final String TEMPLATE_DOCUMENT_PUBLISHING = "/admin/plugins/document/publishing/manage_document_publishing.html";
    private static final String TEMPLATE_PORTLET_PAGE_PATH = "/admin/plugins/document/publishing/portlet_page_path.html";
    private static final String TEMPLATE_PORTLET_PUBLISHING = "/admin/plugins/document/publishing/manage_portlet_publishing.html";
    private static final String TEMPLATE_CREATE_AUTO_PUBLICATION = "/admin/plugins/document/publishing/create_auto_publication.html";
    private static final String TEMPLATE_PUBLISHED_DOCUMENT_LIST = "/admin/plugins/document/publishing/published_document_list.html";
    private static final String TEMPLATE_ASSIGNED_DOCUMENT_LIST = "/admin/plugins/document/publishing/assigned_document_list.html";
    private static final String JSP_DOCUMENTS_ASSIGN = "ManageDocumentPublishing.jsp";
    private static final String JSP_DOCUMENTS_PUBLISHING = "ManagePublishing.jsp";
    private static final String JSP_DELETE_AUTO_PUBLICATION = "jsp/admin/plugins/document/DoDeleteAutoPublication.jsp";
    private static final String JSP_CHANGE_MODE_PUBLICATION = "jsp/admin/plugins/document/DoChangeModePublication.jsp";
    private static final String MESSAGE_AUTO_PUBLICATION_ALREADY_EXISTS = "document.message.autoPublication.alreadyExists";
    private static final String MESSAGE_CONFIRM_DELETE_AUTO_PUBLICATION = "document.message.autoPublication.confirmDeleteAutoPublication";
    private static final String MESSAGE_CONFIRM_CHANGE_MODE_PUBLICATION_STANDARD = "document.message.modePublication.confirmChangeModePublication.standard";
    private static final String MESSAGE_CONFIRM_CHANGE_MODE_PUBLICATION_AUTO_PUBLICATION = "document.message.modePublication.confirmChangeModePublication.autoPublication";

    /**
     * Returns the publish template management
     * @param request The Http request
     * @return the html code for display the modes list
     */
    public String getManageDocumentPublishing( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PUBLISHING_SPACE_PAGE_TITLE );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        Portlet portlet;
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( Integer.parseInt( strDocumentId ) );
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( DocumentService.getInstance(  )
                                .isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
        {
            Collection<ReferenceItem> listAllPortlets = getListAuthorizedPortlets( document.getId(  ),
                    document.getCodeDocumentType(  ) );

            // Set page path + portlet name
            for ( ReferenceItem item : listAllPortlets )
            {
                Map<String, Object> subModel = new HashMap<String, Object>(  );
                subModel.put( MARK_LIST_PAGE,
                    PortalService.getPagePath( PortletHome.findByPrimaryKey( Integer.parseInt( item.getCode(  ) ) )
                                                          .getPageId(  ) ) );
                subModel.put( MARK_PORTLET, item );

                HtmlTemplate subTemplate = AppTemplateService.getTemplate( TEMPLATE_PORTLET_PAGE_PATH, getLocale(  ),
                        subModel );
                item.setName( subTemplate.getHtml(  ) );
            }

            Collection<Portlet> listAssignedPortlet = PublishingService.getInstance(  )
                                                                       .getPortletsByDocumentId( strDocumentId );

            Collection<HashMap<String, Object>> listAssignedPortlets = new ArrayList<HashMap<String, Object>>(  );

            for ( Iterator<Portlet> iterator = listAssignedPortlet.iterator(  ); iterator.hasNext(  ); )
            {
                portlet = iterator.next(  );

                // Check if portlet is in Auto publication mode, if yes, delete it from iterator
                if ( !DocumentAutoPublicationHome.isPortletAutoPublished( portlet.getId(  ) ) &&
                        PortletService.getInstance(  ).isAuthorized( portlet, getUser(  ) ) )
                {
                    HashMap<String, Object> portletPublication = new HashMap<String, Object>(  );
                    portletPublication.put( MARK_PORTLET, portlet );
                    portletPublication.put( MARK_ASSIGNED_PUBLICATION,
                        PublishingService.getInstance(  ).getDocumentPublication( portlet.getId(  ), document.getId(  ) ) );

                    listAssignedPortlets.add( portletPublication );
                }
            }

            DocumentService.getInstance(  ).getActions( document, getLocale(  ), getUser(  ) );

            for ( Object action : document.getActions(  ) )
            {
                DocumentAction docAction = (DocumentAction) action;

                if ( DocumentTypeResourceIdService.PERMISSION_ASSIGN.equals( docAction.getPermission(  ) ) )
                {
                    model.put( MARK_PERMISSION_ASSIGN, 1 );
                }
                else if ( DocumentTypeResourceIdService.PERMISSION_PUBLISH.equals( docAction.getPermission(  ) ) )
                {
                    model.put( MARK_PERMISSION_PUBLISH, 1 );
                }
            }

            model.put( MARK_PORTLET_LIST, listAllPortlets );
            model.put( MARK_ASSIGNED_PORTLET, listAssignedPortlets );
            model.put( MARK_PUBLISHED_STATUS_VALUE, DocumentPublication.STATUS_PUBLISHED );
            model.put( MARK_DOCUMENT, document );
            model.put( MARK_UNPUBLISHED_STATUS_VALUE, DocumentPublication.STATUS_UNPUBLISHED );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DOCUMENT_PUBLISHING, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get the list of authorized portlets.
     *
     * Check :
     * <ul>
     *   <li>if user is authorized to manage DocumentListPortlet</li>
     *   <li>if user is authorized to manage DocumentPortlet</li>
     *   <li>For each portlet :
     *     <ul>
     *       <li>if user is authorized to manage the linked page</li>
     *       <li>if portlet isn't in autopublication mode</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @param nDocumentId The document id
     * @param strCodeDocumentType The code document type
     * @return A collection of {@link ReferenceItem}
     */
    private Collection<ReferenceItem> getListAuthorizedPortlets( int nDocumentId, String strCodeDocumentType )
    {
        Collection<ReferenceItem> listAllPortlets = new ArrayList<ReferenceItem>(  );

        // Check role PERMISSION_MANAGE for DocumentListPortlet
        if ( RBACService.isAuthorized( PortletType.RESOURCE_TYPE, DocumentListPortlet.RESOURCE_ID,
                    PortletResourceIdService.PERMISSION_MANAGE, getUser(  ) ) )
        {
            listAllPortlets.addAll( DocumentListPortletHome.findByCodeDocumentTypeAndCategory( nDocumentId,
                    strCodeDocumentType ) );
        }

        // Check role PERMISSION_MANAGE for DocumentPortlet
        if ( RBACService.isAuthorized( PortletType.RESOURCE_TYPE, DocumentPortlet.RESOURCE_ID,
                    PortletResourceIdService.PERMISSION_MANAGE, getUser(  ) ) )
        {
            listAllPortlets.addAll( DocumentPortletHome.findByCodeDocumentTypeAndCategory( nDocumentId,
                    strCodeDocumentType ) );
        }

        //check ROLE PERMISSION_MANAGE for PAGE and WORKGROUP
        Collection<ReferenceItem> listFilteredPortlets = new ArrayList<ReferenceItem>(  );

        // Check role PERMISSION_MANAGE for workgroup and page and check if portlet isn't autopublished
        for ( ReferenceItem item : listAllPortlets )
        {
            Portlet portlet = PortletHome.findByPrimaryKey( Integer.parseInt( item.getCode(  ) ) );

            if ( !DocumentAutoPublicationHome.isPortletAutoPublished( portlet.getId(  ) ) &&
                    PortletService.getInstance(  ).isAuthorized( portlet, getUser(  ) ) )
            {
                listFilteredPortlets.add( item );
            }
        }

        return listFilteredPortlets;
    }

    /**
     * Process the publishing article
     * @param request requete Http
     * @return The Jsp URL of the process result
     */
    public String doAssignedDocument( HttpServletRequest request )
    {
        // Recovery of parameters processing
        int nDocumentId = Integer.parseInt( request.getParameter( PARAMETER_DOCUMENT_ID ) );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );

        //retrieve the selected portlets ids
        String[] arrayPortletIds = request.getParameterValues( PARAMETER_PORTLET_LIST_IDS );

        if ( ( arrayPortletIds != null ) || ( strPortletId != null ) )
        {
            if ( strPortletId == null )
            {
                for ( int i = 0; i < arrayPortletIds.length; i++ )
                {
                    int nPortletId = Integer.parseInt( arrayPortletIds[i] );
                    int nStatus = Integer.parseInt( request.getParameter( PARAMETER_DOCUMENT_PUBLISHED_STATUS ) );

                    if ( !PublishingService.getInstance(  ).isAssigned( nDocumentId, nPortletId ) )
                    {
                        // Publishing of document : if status = DocumentListPortlet.STATUS_UNPUBLISHED (=1), the document is assigned, otherwize is assigned AND published
                        PublishingService.getInstance(  ).assign( nDocumentId, nPortletId );

                        if ( nStatus == DocumentPublication.STATUS_PUBLISHED )
                        {
                            PublishingService.getInstance(  ).publish( nDocumentId, nPortletId );
                        }
                    }
                }
            }
            else
            {
                int nIdPortlet = Integer.parseInt( strPortletId );
                PublishingService.getInstance(  ).publish( nDocumentId, nIdPortlet );
            }
        }

        // Display the page of publishing
        return getUrlAssignedPage( nDocumentId );
    }

    /**
     * Process of unselecting the article of publishing
     *
     * @param request requete Http
     * @return The Jsp URL of the process result
     */
    public String doUnAssignedDocument( HttpServletRequest request )
    {
        // Recovery of parameters processing
        int nDocumentId = Integer.parseInt( request.getParameter( PARAMETER_DOCUMENT_ID ) );
        int nPortletId = Integer.parseInt( request.getParameter( PARAMETER_PORTLET_ID ) );
        int nStatus = Integer.parseInt( request.getParameter( PARAMETER_DOCUMENT_PUBLISHED_STATUS ) );

        // Remove the document assigned
        if ( nStatus != DocumentPublication.STATUS_PUBLISHED )
        {
            PublishingService.getInstance(  ).unAssign( nDocumentId, nPortletId );
        }
        else
        {
            PublishingService.getInstance(  ).unPublish( nDocumentId, nPortletId );
        }

        // Display the page of publishing
        return getUrlAssignedPage( nDocumentId );
    }

    /**
     * Returns the portlet document template management
     *
     * @param request The Http request
     * @return the html code
     */
    public String getPublishingManagement( HttpServletRequest request )
    {
        String strModePublication = request.getParameter( PARAMETER_MODE_PUBLICATION );
        int nModePublication = MODE_PUBLICATION_STANDARD;

        // Set commons elements
        setPageTitleProperty( PROPERTY_MANAGE_PUBLISHING );

        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );

        Portlet portlet = PortletHome.findByPrimaryKey( nPortletId );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_PORTLET, portlet );

        Page page = PageHome.findByPrimaryKey( portlet.getPageId(  ) );
        String strPageName = page.getName(  );
        model.put( MARK_PAGE_NAME, strPageName );

        //get publication mode
        if ( DocumentAutoPublicationHome.isPortletAutoPublished( nPortletId ) )
        {
            model.put( MARK_MODE_PUBLICATION, MODE_PUBLICATION_AUTO_PUBLICATION );

            return getAutoPublicationManagement( request, model, nPortletId );
        }

        if ( ( strModePublication == null ) || !strModePublication.matches( REGEX_ID ) )
        {
            model.put( MARK_MODE_PUBLICATION, MODE_PUBLICATION_STANDARD );

            return getStandardPublication( request, model, nPortletId );
        }

        nModePublication = Integer.parseInt( strModePublication );

        model.put( MARK_MODE_PUBLICATION, nModePublication );

        switch ( nModePublication )
        {
            case MODE_PUBLICATION_AUTO_PUBLICATION:
                return getAutoPublicationManagement( request, model, nPortletId );

            default:
                return getStandardPublication( request, model, nPortletId );
        }
    }

    /**
     * Returns the portlet document template management
     *
     * @param request The Http request
     * @return the html code
     */
    public String doConfirmChangeModePublication( HttpServletRequest request )
    {
        String strOldModePublication = request.getParameter( PARAMETER_OLD_MODE_PUBLICATION );
        String strModePublication = request.getParameter( PARAMETER_MODE_PUBLICATION );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        UrlItem url = new UrlItem( JSP_CHANGE_MODE_PUBLICATION );
        url.addParameter( PARAMETER_PORTLET_ID, strPortletId );
        url.addParameter( PARAMETER_MODE_PUBLICATION, strModePublication );
        url.addParameter( PARAMETER_OLD_MODE_PUBLICATION, strOldModePublication );

        String strMessage = null;

        if ( !strOldModePublication.equals( strModePublication ) )
        {
            switch ( Integer.parseInt( strOldModePublication ) )
            {
                case MODE_PUBLICATION_AUTO_PUBLICATION:

                    if ( DocumentAutoPublicationHome.findByPortletId( nPortletId ).size(  ) > 0 )
                    {
                        strMessage = MESSAGE_CONFIRM_CHANGE_MODE_PUBLICATION_AUTO_PUBLICATION;
                    }

                    break;

                default:

                    if ( PublishingService.getInstance(  ).getAssignedDocumentsByPortletId( nPortletId ).size(  ) > 0 )
                    {
                        strMessage = MESSAGE_CONFIRM_CHANGE_MODE_PUBLICATION_STANDARD;
                    }
            }
        }

        if ( strMessage != null )
        {
            return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ),
                AdminMessage.TYPE_CONFIRMATION );
        }
        else
        {
            return doChangeModePublication( request );
        }
    }

    /**
     * Returns the portlet document template management
     *
     * @param request The Http request
     * @return the html code
     */
    public String doChangeModePublication( HttpServletRequest request )
    {
        String strModePublication = request.getParameter( PARAMETER_MODE_PUBLICATION );
        String strOldModePublication = request.getParameter( PARAMETER_OLD_MODE_PUBLICATION );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        int nModePublication = MODE_PUBLICATION_STANDARD;
        int nOldModePublication = MODE_PUBLICATION_STANDARD;

        if ( ( strModePublication != null ) && strModePublication.matches( REGEX_ID ) )
        {
            nModePublication = Integer.parseInt( strModePublication );
        }

        if ( ( strOldModePublication != null ) && strOldModePublication.matches( REGEX_ID ) )
        {
            nOldModePublication = Integer.parseInt( strOldModePublication );
        }

        if ( nOldModePublication != nModePublication )
        {
            //Process to old publication mode configuration cleaning and document unpublication
            switch ( nOldModePublication )
            {
                case MODE_PUBLICATION_AUTO_PUBLICATION:
                    DocumentAutoPublicationHome.removeAllSpaces( nPortletId );

                default:

                    for ( Document document : PublishingService.getInstance(  )
                                                               .getPublishedDocumentsByPortletId( nPortletId ) )
                    {
                        PublishingService.getInstance(  ).unPublish( document.getId(  ), nPortletId );
                    }

                    for ( Document document : PublishingService.getInstance(  )
                                                               .getAssignedDocumentsByPortletId( nPortletId ) )
                    {
                        PublishingService.getInstance(  ).unAssign( document.getId(  ), nPortletId );
                    }
            }
        }

        UrlItem url = new UrlItem( JSP_DOCUMENTS_PUBLISHING );
        url.addParameter( PARAMETER_PORTLET_ID, nPortletId );
        url.addParameter( PARAMETER_MODE_PUBLICATION, nModePublication );

        return url.getUrl(  );
    }

    /**
     * Process the publishing article
     * @param request requete Http
     * @return The Jsp URL of the process result
     */
    public String doPublishingDocument( HttpServletRequest request )
    {
        // Recovery of parameters processing
        int nDocumentId = Integer.parseInt( request.getParameter( PARAMETER_DOCUMENT_ID ) );
        int nPortletId = Integer.parseInt( request.getParameter( PARAMETER_PORTLET_ID ) );

        PublishingService.getInstance(  ).publish( nDocumentId, nPortletId );

        // Display the page of publishing
        return getUrlPublishedPage( nPortletId, nDocumentId );
    }

    /**
     * Process of unselecting the article of publishing
     *
     * @param request requete Http
     * @return The Jsp URL of the process result
     */
    public String doUnPublishingDocument( HttpServletRequest request )
    {
        // Recovery of parameters processing
        int nDocumentId = Integer.parseInt( request.getParameter( PARAMETER_DOCUMENT_ID ) );
        int nPortletId = Integer.parseInt( request.getParameter( PARAMETER_PORTLET_ID ) );
        PublishingService.getInstance(  ).unPublish( nDocumentId, nPortletId );

        // Display the page of publishing
        return getUrlPublishedPage( nPortletId, nDocumentId );
    }

    /**
     * Modifies the order in the list of contacts
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    public String doModifyDocumentOrder( HttpServletRequest request )
    {
        int nDocumentId = Integer.parseInt( request.getParameter( PARAMETER_DOCUMENT_ID ) );
        int nPortletId = Integer.parseInt( request.getParameter( PARAMETER_PORTLET_ID ) );
        int nNewOrder = Integer.parseInt( request.getParameter( PARAMETER_DOCUMENT_ORDER ) );

        PublishingService.getInstance(  ).changeDocumentOrder( nDocumentId, nPortletId, nNewOrder );

        // Display the page of publishing
        return getUrlPublishedPage( nPortletId, nDocumentId );
    }

    //////////////////////////////////////////////////////////////////////////////////
    // Auto publication

    /**
     * Generate the html code for auto publication creation page
     *
     * @param request The {@link HttpServletRequest} request
     * @return The Html code
     */
    public String getCreateAutoPublication( HttpServletRequest request )
    {
        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );
        boolean bSubmitButtonDisabled = Boolean.TRUE;

        setPageTitleProperty( PROPERTY_CREATE_AUTO_PUBLICATION );

        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_PORTLET_ID, strPortletId );

        if ( ( strSpaceId != null ) && !strSpaceId.equals( "" ) )
        {
            bSubmitButtonDisabled = Boolean.FALSE;
        }

        model.put( MARK_SUBMIT_BUTTON_DISABLED, bSubmitButtonDisabled );
        model.put( MARK_SPACES_BROWSER,
            DocumentSpacesService.getInstance(  ).getSpacesBrowser( request, getUser(  ), getLocale(  ), true, true ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_AUTO_PUBLICATION, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process to the creation of auto publication object
     * @param request The {@link HttpServletRequest} request
     * @return The Jsp URL of the process result
     */
    public String doCreateAutoPublication( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        int nSpaceId = Integer.parseInt( strSpaceId );
        DocumentAutoPublication documentAutoPublication = DocumentAutoPublicationHome.findByPrimaryKey( nPortletId,
                nSpaceId );

        if ( documentAutoPublication == null )
        {
            // Create Auto publication
            documentAutoPublication = new DocumentAutoPublication(  );
            documentAutoPublication.setIdPortlet( nPortletId );
            documentAutoPublication.setIdSpace( nSpaceId );
            DocumentAutoPublicationHome.add( documentAutoPublication );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_AUTO_PUBLICATION_ALREADY_EXISTS,
                AdminMessage.TYPE_STOP );
        }

        return getUrlPublishingModeAutoPublication( nPortletId );
    }

    /**
     * Set the confirmation message for deletion of auto publication object
     *
     * @param request The {@link HttpServletRequest} request
     * @return The Jsp URL of the process result
     */
    public String getConfirmDeleteAutoPublication( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strSpaceId = request.getParameter( PARAMETER_SPACE_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        int nSpaceId = Integer.parseInt( strSpaceId );
        UrlItem url = new UrlItem( JSP_DELETE_AUTO_PUBLICATION );
        url.addParameter( PARAMETER_PORTLET_ID, nPortletId );
        url.addParameter( PARAMETER_MODE_PUBLICATION, MODE_PUBLICATION_AUTO_PUBLICATION );
        url.addParameter( PARAMETER_SPACE_ID, nSpaceId );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_AUTO_PUBLICATION, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Process to the deletion of auto publication object
     *
     * @param request The {@link HttpServletRequest} request
     * @return The Jsp URL of the process result
     */
    public String doDeleteAutoPublication( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strSpaceId = request.getParameter( PARAMETER_SPACE_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        int nSpaceId = Integer.parseInt( strSpaceId );

        //delete auto publication
        DocumentAutoPublicationHome.remove( nPortletId, nSpaceId );

        for ( Document document : PublishingService.getInstance(  ).getPublishedDocumentsByPortletId( nPortletId ) )
        {
            if ( PublishingService.getInstance(  ).isPublished( document.getId(  ), nPortletId ) )
            {
                PublishingService.getInstance(  ).unPublish( document.getId(  ), nPortletId );
                PublishingService.getInstance(  ).unAssign( document.getId(  ), nPortletId );
            }
        }

        return getUrlPublishingModeAutoPublication( nPortletId );
    }

    //////////////////////////////////////////////////////////////////////////////////
    // Private implementation

    /**
     * Returns an html template containing the list of the portlet types
     * @param document The document object
     * @param nPortletId The Portet Identifier
     * @return The html code
     */
    private String getPublishedDocumentsList( Document document, int nPortletId )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        DocumentService.getInstance(  ).getActions( document, getLocale(  ), getUser(  ) );

        for ( Object action : document.getActions(  ) )
        {
            DocumentAction docAction = (DocumentAction) action;

            if ( docAction.getPermission(  ).equals( DocumentTypeResourceIdService.PERMISSION_PUBLISH ) )
            {
                model.put( MARK_PERMISSION_PUBLISH, 1 );
            }
        }

        DocumentPublication documentPublication = PublishingService.getInstance(  )
                                                                   .getDocumentPublication( nPortletId,
                document.getId(  ) );
        model.put( MARK_PORTLET_ID, Integer.toString( nPortletId ) );
        model.put( MARK_DOCUMENT_PUBLISHED_STATUS, Integer.toString( documentPublication.getStatus(  ) ) );
        model.put( MARK_PUBLISHED_STATUS_VALUE, Integer.toString( DocumentPublication.STATUS_PUBLISHED ) );
        model.put( MARK_DOCUMENT_PUBLISHED, document );
        model.put( MARK_DOCUMENT_ORDER_LIST, getOrdersList( nPortletId ) );
        model.put( MARK_DOCUMENT_ORDER, Integer.toString( documentPublication.getDocumentOrder(  ) ) );

        // Page Template display
        DocumentPageTemplate documentPageTemplate = DocumentPageTemplateHome.findByPrimaryKey( document.getPageTemplateDocumentId(  ) );
        model.put( MARK_DOCUMENT_PAGE_TEMPLATE_PICTURE, documentPageTemplate.getPicture(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PUBLISHED_DOCUMENT_LIST, getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Returns an html template containing the list of the portlet types
     * @param document The document object
     * @param nPortletId The Portet Identifier
     * @return The html code
     */
    private String getAssignedDocumentsList( Document document, int nPortletId )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        DocumentService.getInstance(  ).getActions( document, getLocale(  ), getUser(  ) );

        for ( Object action : document.getActions(  ) )
        {
            DocumentAction docAction = (DocumentAction) action;

            if ( docAction.getPermission(  ).equals( DocumentTypeResourceIdService.PERMISSION_ASSIGN ) )
            {
                model.put( MARK_PERMISSION_ASSIGN, 1 );
            }
            else if ( docAction.getPermission(  ).equals( DocumentTypeResourceIdService.PERMISSION_PUBLISH ) )
            {
                model.put( MARK_PERMISSION_PUBLISH, 1 );
            }
        }

        DocumentPublication documentPublication = PublishingService.getInstance(  )
                                                                   .getDocumentPublication( nPortletId,
                document.getId(  ) );
        model.put( MARK_PORTLET_ID, Integer.toString( nPortletId ) );
        model.put( MARK_DOCUMENT_PUBLISHED_STATUS, Integer.toString( documentPublication.getStatus(  ) ) );
        model.put( MARK_UNPUBLISHED_STATUS_VALUE, Integer.toString( DocumentPublication.STATUS_UNPUBLISHED ) );
        model.put( MARK_DOCUMENT_PUBLISHED, document );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ASSIGNED_DOCUMENT_LIST, getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Builts a list of sequence numbers
     * @param nPortletId the portlet identifier
     * @return the list of sequence numbers
     */
    private ReferenceList getOrdersList( int nPortletId )
    {
        int nMax = PublishingService.getInstance(  ).getMaxDocumentOrderByPortletId( nPortletId );
        ReferenceList list = new ReferenceList(  );

        for ( int i = 1; i < ( nMax + 1 ); i++ )
        {
            list.addItem( i, Integer.toString( ( i ) ) );
        }

        return list;
    }

    /**
      * Return AdminSite Url
      * @param nId The PageId
      * @return url
      */
    private String getUrlAssignedPage( int nId )
    {
        UrlItem url = new UrlItem( JSP_DOCUMENTS_ASSIGN );
        url.addParameter( PARAMETER_DOCUMENT_ID, nId );

        return url.getUrl(  );
    }

    /**
     * Return AdminSite Url
     * @param nId The PageId
     * @return url
     */
    private String getUrlPublishedPage( int nPortletId, int nDocumentId )
    {
        UrlItem url = new UrlItem( JSP_DOCUMENTS_PUBLISHING );
        url.addParameter( PARAMETER_PORTLET_ID, nPortletId );
        url.addParameter( PARAMETER_DOCUMENT_ID, nDocumentId );

        return url.getUrl(  );
    }

    /**
     * Return AdminSite Url
     * @param nPortletId The portlet Id
     * @return url
     */
    private String getUrlPublishingModeAutoPublication( int nPortletId )
    {
        UrlItem url = new UrlItem( JSP_DOCUMENTS_PUBLISHING );
        url.addParameter( PARAMETER_PORTLET_ID, nPortletId );
        url.addParameter( PARAMETER_MODE_PUBLICATION, MODE_PUBLICATION_AUTO_PUBLICATION );

        return url.getUrl(  );
    }

    /**
     * Generate html for standard publication management page
     *
     * @param request The {@link HttpServletRequest} request
     * @param model The {@link HashMap} for the template
     * @param nPortletId The portlet Id
     * @return The HTML code
     */
    private String getStandardPublication( HttpServletRequest request, Map<String, Object> model, int nPortletId )
    {
        StringBuffer strPublishedDocumentsRow = new StringBuffer(  );

        // Scan of the list
        for ( Document document : PublishingService.getInstance(  ).getAssignedDocumentsByPortletId( nPortletId ) )
        {
            strPublishedDocumentsRow.append( getPublishedDocumentsList( document, nPortletId ) );
        }

        model.put( MARK_PUBLISHED_DOCUMENT_LIST, strPublishedDocumentsRow );

        StringBuffer strAssignedDocumentsRow = new StringBuffer(  );

        // Scan of the list
        for ( Document document : PublishingService.getInstance(  ).getAssignedDocumentsByPortletId( nPortletId ) )
        {
            strAssignedDocumentsRow.append( getAssignedDocumentsList( document, nPortletId ) );
        }

        model.put( MARK_ASSIGNED_DOCUMENT_LIST, strAssignedDocumentsRow );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PORTLET_PUBLISHING, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Generate html for auto publication management page
     *
     * @param request The {@link HttpServletRequest} request
     * @param model The {@link HashMap} for the template
     * @param nPortletId The portlet Id
     * @return The HTML code
     */
    private String getAutoPublicationManagement( HttpServletRequest request, Map<String, Object> model, int nPortletId )
    {
        Collection<DocumentAutoPublication> listDocumentAutoPublication = DocumentAutoPublicationHome.findByPortletId( nPortletId );
        Collection<Map> listModels = new ArrayList<Map>(  );
        DocumentSpace documentSpace;
        AdminUser user = getUser(  );

        for ( DocumentAutoPublication documentAutoPublication : listDocumentAutoPublication )
        {
            //Check if user is authorized to view space
            if ( DocumentSpacesService.getInstance(  )
                                          .isAuthorizedViewByRole( documentAutoPublication.getIdSpace(  ), getUser(  ) ) )
            {
                Map<String, Object> subModel = new HashMap<String, Object>(  );

                if ( DocumentSpacesService.getInstance(  )
                                              .isAuthorizedViewByWorkgroup( documentAutoPublication.getIdSpace(  ), user ) )
                {
                    documentSpace = DocumentSpaceHome.findByPrimaryKey( documentAutoPublication.getIdSpace(  ) );
                    subModel.put( MARK_SPACE_NAME, ( documentSpace != null ) ? documentSpace.getName(  ) : "" );

                    int nCountDocuments = AutoPublicationService.getInstance(  )
                                                                .findCountByPortletAndSpace( documentAutoPublication.getIdPortlet(  ),
                            documentAutoPublication.getIdSpace(  ) );
                    subModel.put( MARK_NUMBER_AUTO_PUBLISHED_DOCUMENTS, nCountDocuments );
                    subModel.put( MARK_DOCUMENT_AUTO_PUBLICATION, documentAutoPublication );
                    listModels.add( subModel );
                }
            }
        }

        model.put( MARK_LIST_AUTO_PUBLICATION, listModels );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PORTLET_PUBLISHING, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }
}
