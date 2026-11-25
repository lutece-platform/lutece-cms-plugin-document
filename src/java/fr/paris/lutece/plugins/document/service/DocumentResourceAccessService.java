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
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.security.SecurityService;

import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Decides whether a request may download the files of a document (servlet/plugins/document/resource).
 * <ul>
 * <li>An administrator allowed to view the document may download its working and validated files, as in the back office preview.</li>
 * <li>Any other request may only download the validated files of a valid document published in a published portlet, whose page it is allowed
 * to see: the same rule as the display of the document in the front office.</li>
 * <li>Any other request may also download the validated files of a valid and validated document referenced by the validated content of a
 * document it may see in the front office: an image of the library inserted in an article is never published in a portlet itself.</li>
 * </ul>
 */
@ApplicationScoped
public class DocumentResourceAccessService
{
    private static final String PARAMETER_ID = "id=";

    // The legacy url (document?) or the servlet url, then the id parameter first or after another one (& or &amp;), not followed by a digit
    private static final String REGEX_RESOURCE_URL = "(?:document|servlet/plugins/document/resource)\\?[^\"'\\s<>]*?(?<=[?&;])";
    private static final String REGEX_END_OF_ID = "(?!\\d)";

    @Inject
    private DocumentService _documentService;

    @Inject
    private PublishingService _publishingService;

    /**
     * Tells if the request may download a file of a document
     *
     * @param request
     *            The HTTP request
     * @param nDocumentId
     *            The document identifier
     * @param bWorkingContent
     *            true for a file of the working content, false for a file of the validated content
     * @return true if the file may be sent
     */
    public boolean isAuthorized( HttpServletRequest request, int nDocumentId, boolean bWorkingContent )
    {
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( document == null )
        {
            return false;
        }

        AdminUser user = AdminUserService.getAdminUser( request );

        if ( ( user != null ) && _documentService.isAuthorizedAdminDocument( document.getSpaceId( ), document.getCodeDocumentType( ),
                DocumentTypeResourceIdService.PERMISSION_VIEW, user ) )
        {
            return true;
        }

        if ( bWorkingContent || !document.isValid( ) )
        {
            return false;
        }

        return isPublishedForRequest( request, document ) || ( document.hasValidatedVersion( ) && isReferencedByPublishedDocument( request, document ) );
    }

    /**
     * Tells if a document is referenced by the validated content of a valid document visible in the front office for this request
     *
     * @param request
     *            The HTTP request
     * @param document
     *            The referenced document
     * @return true if a document visible for this request references the document
     */
    private boolean isReferencedByPublishedDocument( HttpServletRequest request, Document document )
    {
        String strFragment = PARAMETER_ID + document.getId( );
        Pattern patternReference = Pattern.compile( REGEX_RESOURCE_URL + Pattern.quote( strFragment ) + REGEX_END_OF_ID );

        for ( Integer nReferencingId : DocumentHome.findIdsByValidatedTextValue( strFragment, document.getId( ) ) )
        {
            Document referencing = DocumentHome.findByPrimaryKeyWithoutBinaries( nReferencingId );

            if ( ( referencing != null ) && referencing.isValid( ) && references( referencing, patternReference )
                    && isPublishedForRequest( request, referencing ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Tells if the validated content of a document holds the url of a file of another document
     *
     * @param document
     *            The referencing document
     * @param patternReference
     *            The pattern of the url of the files of the referenced document
     * @return true if a text value of the validated content matches the pattern
     */
    private static boolean references( Document document, Pattern patternReference )
    {
        DocumentHome.loadAttributesWithoutBinaries( document, true );

        if ( document.getAttributes( ) == null )
        {
            return false;
        }

        for ( DocumentAttribute attribute : document.getAttributes( ) )
        {
            if ( ( attribute.getTextValue( ) != null ) && patternReference.matcher( attribute.getTextValue( ) ).find( ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Tells if a document is published in a published portlet whose page the request is allowed to see
     *
     * @param request
     *            The HTTP request
     * @param document
     *            The document
     * @return true if the document is visible in the front office for this request
     */
    private boolean isPublishedForRequest( HttpServletRequest request, Document document )
    {
        for ( Portlet portlet : _publishingService.getPortletsByDocumentId( Integer.toString( document.getId( ) ) ) )
        {
            if ( ( portlet.getStatus( ) == Portlet.STATUS_PUBLISHED ) && _publishingService.isPublished( document.getId( ), portlet.getId( ) )
                    && isPageVisible( request, portlet.getPageId( ) ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Tells if the request is allowed to see a page, according to the role of the page
     *
     * @param request
     *            The HTTP request
     * @param nPageId
     *            The page identifier
     * @return true if the page is visible for this request
     */
    private boolean isPageVisible( HttpServletRequest request, int nPageId )
    {
        Page page = PageHome.getPage( nPageId );

        if ( page == null )
        {
            return false;
        }

        String strRole = page.getRole( );

        if ( Page.ROLE_NONE.equals( strRole ) || !SecurityService.isAuthenticationEnable( ) )
        {
            return true;
        }

        return SecurityService.getInstance( ).isUserInRole( request, strRole );
    }
}
