/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.document.modules.comment.web;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.modules.comment.business.DocumentComment;
import fr.paris.lutece.plugins.document.modules.comment.business.DocumentCommentHome;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.DocumentTypeResourceIdService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage documents comment features.
 */
public class DocumentCommentJspBean extends PluginAdminPageJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String RIGHT_DOCUMENT_MANAGEMENT = "DOCUMENT_MANAGEMENT";

    // Parameters
    private static final String PARAMETER_DOCUMENT_ID = "id_document";
    private static final String PARAMETER_COMMENTS_ID = "id_comment";
    private static final String PARAMETER_COMMENT_STATUS = "status";

    // Markers
    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_DOCUMENT_COMMENTS = "document_comments_list";

    // Templates    
    private static final String TEMPLATE_MANAGE_DOCUMENT_COMMENTS = "admin/plugins/document/modules/comment/manage_document_comments.html";

    // JSP
    private static final String JSP_DOCUMENT_COMMENTS = "ManageDocumentComments.jsp";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DOCUMENT_COMMENT = "module.document.comment.manage_document_comments.pageTitle";

    /**
     * Creates a new DocumentCommentJspBean object.
     */
    public DocumentCommentJspBean(  )
    {
    }

    ////////////////////////////////////////////////////////////////////////////
    // Management of comments on the admin site

    /**
     * Returns the comment management page.
     * @param request The Http request
     * @return The HTML code of the page
     */
    public String getManageComments( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_DOCUMENT_COMMENT );

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        HashMap model = new HashMap(  );

        // Retrieve the document from the database :
        int nDocumentId = Integer.parseInt( strDocumentId );
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( DocumentService.getInstance(  )
                                .isAuthorizedAdminDocument( document.getSpaceId(  ), document.getCodeDocumentType(  ),
                    DocumentTypeResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
        {
            // Retrieve the comments from the database :
            List<DocumentComment> documentComments = DocumentCommentHome.findByDocument( nDocumentId );

            model.put( MARK_DOCUMENT, document );
            model.put( MARK_DOCUMENT_COMMENTS, documentComments );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DOCUMENT_COMMENTS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes the update of the comment status
     * @param request the Http request
     * @return The Jsp URL of the process result
     */
    public String doModifyCommentStatus( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strCommentId = request.getParameter( PARAMETER_COMMENTS_ID );
        String strStatus = request.getParameter( PARAMETER_COMMENT_STATUS );
        DocumentCommentHome.updateCommentStatus( Integer.parseInt( strCommentId ), Integer.parseInt( strStatus ) );

        UrlItem url = new UrlItem( JSP_DOCUMENT_COMMENTS );
        url.addParameter( PARAMETER_DOCUMENT_ID, Integer.parseInt( strDocumentId ) );

        return url.getUrl(  );
    }

    /**
      * Processes the deleting of the comment
      * @param request the Http request
      * @return The Jsp URL of the process result
      */
    public String doRemoveComment( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strCommentId = request.getParameter( PARAMETER_COMMENTS_ID );
        DocumentCommentHome.remove( Integer.parseInt( strCommentId ) );

        UrlItem url = new UrlItem( JSP_DOCUMENT_COMMENTS );
        url.addParameter( PARAMETER_DOCUMENT_ID, Integer.parseInt( strDocumentId ) );

        return url.getUrl(  );
    }
}
