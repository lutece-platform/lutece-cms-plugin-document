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

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.insert.InsertServiceJspBean;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectionBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to insert a link to a document
 *
 */
public class DocumentServiceJspBean extends InsertServiceJspBean implements InsertServiceSelectionBean
{
    private static final long serialVersionUID = 2694692453596836769L;

    ////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String REGEX_ID = "^[\\d]+$";

    // Templates
    private static final String TEMPLATE_SELECTOR_PAGE = "admin/plugins/document/page_selector.html";
    private static final String TEMPLATE_SELECTOR_PORTLET = "admin/plugins/document/portlet_selector.html";
    private static final String TEMPLATE_SELECTOR_DOCUMENT = "admin/plugins/document/document_selector.html";
    private static final String TEMPLATE_LINK = "admin/plugins/document/document_link.html";

    // JSP
    private static final String JSP_SELECT_PORTLET = "SelectPortlet.jsp";
    private static final String JSP_SELECT_DOCUMENT = "SelectDocument.jsp";

    // Parameters
    private static final String PARAMETER_PORTLET_ID = "portlet_id";
    private static final String PARAMETER_PAGE_ID = "page_id";
    private static final String PARAMETER_DOCUMENT_ID = "document_id";
    private static final String PARAMETER_ALT = "alt";
    private static final String PARAMETER_TARGET = "target";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_INPUT = "input";

    // Marker
    private static final String MARK_DOCUMENTS_LIST = "documents_list";
    private static final String MARK_PORTLETS_LIST = "portlets_list";
    private static final String MARK_PAGES_LIST = "pages_list";
    private static final String MARK_PORTLET_ID = "portlet_id";
    private static final String MARK_INPUT = "input";
    private static final String MARK_URL = "url";
    private static final String MARK_TARGET = "target";
    private static final String MARK_ALT = "alt";
    private static final String MARK_NAME = "name";

    // private
    private AdminUser _user;
    private String _input;

    /**
     * Initialize data
     *
     * @param request The HTTP request
     */
    public void init( HttpServletRequest request )
    {
        _user = AdminUserService.getAdminUser( request );
        _input = request.getParameter( PARAMETER_INPUT );
    }

    /**
    * Entry point of the insert service
    *
    * @param request The Http Request
    * @return The html form.
     */
    public String getInsertServiceSelectorUI( HttpServletRequest request )
    {
        return getSelectPage( request );
    }

    /**
     * Return the html form for page selection.
     *
     * @param request The HTTP request
     * @return The html form of the page selection page
     */
    public String getSelectPage( HttpServletRequest request )
    {
        init( request );

        AdminUser user = AdminUserService.getAdminUser( request );
        int nPageId = 0;
        Collection<Page> listPages = new ArrayList<Page>(  );
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );

        if ( ( strPageId != null ) && strPageId.matches( REGEX_ID ) )
        {
            nPageId = Integer.parseInt( strPageId );
        }

        Page page = PageHome.findByPrimaryKey( nPageId );

        if ( AdminWorkgroupService.isAuthorized( page, user ) )
        {
            listPages = PageHome.getChildPages( nPageId );
            listPages = AdminWorkgroupService.getAuthorizedCollection( listPages, user );
        }

        HashMap model = getDefaultModel(  );

        model.put( MARK_PAGES_LIST, listPages );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECTOR_PAGE, _user.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Select and validate the specified Page
     *
     * @param request The http request
     * @return The url of the portlet selection page
     */
    public String doSelectPage( HttpServletRequest request )
    {
        init( request );

        String strPageId = request.getParameter( PARAMETER_PAGE_ID );

        if ( ( strPageId == null ) || !strPageId.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nPageId = Integer.parseInt( strPageId );

        Page page = PageHome.findByPrimaryKey( nPageId );

        if ( page == null )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        return getSelectPortletUrl( nPageId );
    }

    /**
     * Get the url of the portlet selection page with the specified page id
     *
     * @param nPageId Id of the page
     * @return The url of the portlet selection page
     */
    private String getSelectPortletUrl( int nPageId )
    {
        UrlItem url = new UrlItem( JSP_SELECT_PORTLET );
        url.addParameter( PARAMETER_PAGE_ID, nPageId );
        url.addParameter( PARAMETER_INPUT, _input );

        return url.getUrl(  );
    }

    /**
     * Return the html form for portlet selection.
     *
     * @param request The HTTP request
     * @return The html form of the portlet selection page
     */
    public String getSelectPortlet( HttpServletRequest request )
    {
        init( request );

        String strPageId = request.getParameter( PARAMETER_PAGE_ID );

        if ( ( strPageId == null ) || !strPageId.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Page page = PageHome.findByPrimaryKey( Integer.parseInt( strPageId ) );

        Collection<Portlet> listPortletsAll = page.getPortlets(  );
        Collection<Portlet> listPortlets = new ArrayList<Portlet>(  );

        for ( Portlet portlet : listPortletsAll )
        {
            if ( portlet.getPortletTypeId(  ).equals( DocumentListPortletHome.getInstance(  ).getPortletTypeId(  ) ) )
            {
                listPortlets.add( portlet );
            }
        }

        listPortletsAll.clear(  );

        HashMap model = getDefaultModel(  );

        model.put( MARK_PORTLETS_LIST, listPortlets );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECTOR_PORTLET, _user.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Select and validate the specified Portlet
     *
     * @param request The http request
     * @return The url of the document selection page if porlet id is valid
     */
    public String doSelectPortlet( HttpServletRequest request )
    {
        init( request );

        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );

        if ( ( strPortletId == null ) || !strPortletId.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nPortletId = Integer.parseInt( strPortletId );

        Portlet portlet = PortletHome.findByPrimaryKey( nPortletId );

        if ( portlet == null )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        return getSelectDocumentUrl( nPortletId );
    }

    /**
     * Get the url of the document selection page with the specified portlet id
     *
     * @param nPortletId Id of the portlet
     * @return The url of the document selection page
     */
    private String getSelectDocumentUrl( int nPortletId )
    {
        UrlItem url = new UrlItem( JSP_SELECT_DOCUMENT );
        url.addParameter( PARAMETER_PORTLET_ID, nPortletId );
        url.addParameter( PARAMETER_INPUT, _input );

        return url.getUrl(  );
    }

    /**
     * Return the html form for document selection.
     *
     * @param request The HTTP request
     * @return The html form of the document selection page
     */
    public String getSelectDocument( HttpServletRequest request )
    {
        init( request );

        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );

        if ( ( strPortletId == null ) || !strPortletId.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nPortletId = Integer.parseInt( strPortletId );
        Collection<Document> listDocuments = PublishingService.getInstance(  )
                                                              .getPublishedDocumentsByPortletId( nPortletId );

        HashMap model = getDefaultModel(  );

        model.put( MARK_DOCUMENTS_LIST, listDocuments );
        model.put( MARK_PORTLET_ID, nPortletId );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECTOR_DOCUMENT, _user.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
    * Insert the specified url into HTML content
    *
    * @param request The HTTP request
    * @return The url
    */
    public String doInsertUrl( HttpServletRequest request )
    {
        init( request );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strTarget = request.getParameter( PARAMETER_TARGET );
        String strAlt = request.getParameter( PARAMETER_ALT );
        String strName = request.getParameter( PARAMETER_NAME );
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        Document document = null;

        if ( ( strDocumentId == null ) || !strDocumentId.matches( REGEX_ID ) || ( strPortletId == null ) ||
                !strPortletId.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        document = DocumentHome.findByPrimaryKeyWithoutBinaries( Integer.parseInt( strDocumentId ) );

        UrlItem url = new UrlItem( AppPathService.getPortalUrl(  ) );
        url.addParameter( PARAMETER_DOCUMENT_ID, document.getId(  ) );
        url.addParameter( PARAMETER_PORTLET_ID, strPortletId );
        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_TARGET, strTarget );
        model.put( MARK_ALT, strAlt );
        model.put( MARK_NAME, ( strName.length(  ) == 0 ) ? document.getTitle(  ) : strName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_LINK, null, model );

        return insertUrl( request, _input, StringEscapeUtils.escapeJavaScript( template.getHtml(  ) ) );
    }

    /**
     * Get the default model for selection templates
     *
     * @return The default model
     */
    private HashMap getDefaultModel(  )
    {
        HashMap model = new HashMap(  );
        model.put( MARK_INPUT, _input );

        return model;
    }
}
