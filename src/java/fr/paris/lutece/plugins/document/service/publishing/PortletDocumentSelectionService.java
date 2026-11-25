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
package fr.paris.lutece.plugins.document.service.publishing;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.category.Category;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.DocumentTypeResourceIdService;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.api.user.User;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.date.DateUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Selection of the documents shown by a document portlet or a document list portlet, from the portlet form
 */
@ApplicationScoped
public class PortletDocumentSelectionService
{
    /** The request parameter holding the selected documents */
    public static final String PARAMETER_DOCUMENT_ID = "id_document";

    /** The number of documents in a page of search results */
    public static final int ITEMS_PER_PAGE = 20;

    private static final String MARK_DOCUMENT_SELECTED_LIST = "document_selected_list";
    private static final String JSON_ITEMS = "items";
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_TYPE = "type";
    private static final String JSON_SPACE = "space";
    private static final String JSON_DATE_CREATION = "dateCreation";
    private static final String JSON_PAGE = "page";
    private static final String JSON_PAGE_COUNT = "pageCount";
    private static final String JSON_TOTAL = "total";
    private static final String MARK_MULTIPLE = "multiple";
    private static final String TEMPLATE_DOCUMENT_SELECT = "admin/plugins/document/portlet/document_select.html";

    @Inject
    private DocumentService _documentService;

    @Inject
    private PublishingService _publishingService;

    @Inject
    private DocumentSpacesService _documentSpacesService;

    private final ObjectMapper _mapper = new ObjectMapper( );

    /**
     * Searches the documents a user may choose in a portlet form: the documents of a type with a validated version that the user may see, the last
     * created first
     *
     * @param user
     *            The user
     * @param strCodeDocumentType
     *            The document type code of the portlet
     * @param strTitleFragment
     *            A fragment the titles contain, or null or blank for any title
     * @param nPage
     *            The page wanted, from 1, brought back into the existing pages
     * @return The page of documents, of at most {@value #ITEMS_PER_PAGE} documents without their attributes nor their categories
     */
    public DocumentSearchPage searchDocuments( AdminUser user, String strCodeDocumentType, String strTitleFragment, int nPage )
    {
        String strFragment = StringUtils.trimToEmpty( strTitleFragment );
        List<Integer> listSpaceIds = StringUtils.isBlank( strCodeDocumentType ) ? new ArrayList<>( ) : getViewableSpaceIds( user, strCodeDocumentType );
        int nTotal = DocumentHome.countWithValidatedVersion( strCodeDocumentType, strFragment, listSpaceIds );
        int nPageCount = Math.max( 1, ( nTotal + ITEMS_PER_PAGE - 1 ) / ITEMS_PER_PAGE );
        int nCurrentPage = Math.min( Math.max( nPage, 1 ), nPageCount );
        List<Document> listDocuments = ( nTotal == 0 ) ? new ArrayList<>( )
                : DocumentHome.findWithValidatedVersion( strCodeDocumentType, strFragment, listSpaceIds, ( nCurrentPage - 1 ) * ITEMS_PER_PAGE,
                        ITEMS_PER_PAGE );

        return new DocumentSearchPage( listDocuments, nCurrentPage, nPageCount, nTotal );
    }

    /**
     * Returns a page of search results as the JSON read by the document browser of the portlet forms
     *
     * @param searchPage
     *            The page of documents found
     * @param locale
     *            The locale of the dates
     * @return The JSON: the page number, the page count, the total and the items, with the id, the title, the type, the space and the creation date of
     *         a document
     */
    public String toSearchJson( DocumentSearchPage searchPage, Locale locale )
    {
        ObjectNode root = _mapper.createObjectNode( );
        root.put( JSON_PAGE, searchPage.page( ) );
        root.put( JSON_PAGE_COUNT, searchPage.pageCount( ) );
        root.put( JSON_TOTAL, searchPage.total( ) );

        ArrayNode items = root.putArray( JSON_ITEMS );

        for ( Document document : searchPage.documents( ) )
        {
            ObjectNode item = items.addObject( );
            item.put( JSON_ID, document.getId( ) );
            item.put( JSON_TITLE, getDisplayTitle( document ) );
            item.put( JSON_TYPE, document.getCodeDocumentType( ) );
            item.put( JSON_SPACE, document.getSpace( ) );
            item.put( JSON_DATE_CREATION, ( document.getDateCreation( ) != null ) ? DateUtil.getDateString( document.getDateCreation( ), locale ) : "" );
        }

        return root.toString( );
    }

    /**
     * Returns the documents published in a portlet that a user may see, in their order
     *
     * @param nPortletId
     *            The portlet identifier
     * @param user
     *            The user
     * @return The documents
     */
    public List<Document> getShownDocuments( int nPortletId, AdminUser user )
    {
        List<Document> listDocuments = new ArrayList<>( );

        for ( Document document : _publishingService.getPublishedDocumentsByPortletId( nPortletId ) )
        {
            if ( ( document != null ) && isAuthorized( document, user ) )
            {
                listDocuments.add( document );
            }
        }

        return listDocuments;
    }

    /**
     * Returns the html of the document selector of a portlet form
     *
     * @param listSelected
     *            The documents selected, in their order
     * @param bMultiple
     *            true if several documents can be selected
     * @param locale
     *            The locale
     * @return The html code
     */
    public String getDocumentSelect( List<Document> listSelected, boolean bMultiple, Locale locale )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_DOCUMENT_SELECTED_LIST, listSelected );
        model.put( MARK_MULTIPLE, bMultiple );

        return AppTemplateService.getTemplate( TEMPLATE_DOCUMENT_SELECT, locale, model ).getHtml( );
    }

    /**
     * Returns the title shown for a document: its validated title, or its working title when it has none
     *
     * @param document
     *            The document
     * @return The title
     */
    public static String getDisplayTitle( Document document )
    {
        return StringUtils.isNotEmpty( document.getValidatedTitle( ) ) ? document.getValidatedTitle( ) : Objects.toString( document.getTitle( ), StringUtils.EMPTY );
    }

    /**
     * Returns the documents selected in a portlet form. A document the user may not see is ignored.
     *
     * @param request
     *            The http request
     * @param user
     *            The user
     * @return The selected documents, with their categories, in the order of the request
     */
    public List<Document> getSelectedDocuments( HttpServletRequest request, AdminUser user )
    {
        List<Document> listDocuments = new ArrayList<>( );
        String [ ] arrayDocumentIds = request.getParameterValues( PARAMETER_DOCUMENT_ID );

        if ( arrayDocumentIds == null )
        {
            return listDocuments;
        }

        Set<Integer> setDocumentIds = new LinkedHashSet<>( );

        for ( String strDocumentId : arrayDocumentIds )
        {
            if ( IntegerUtils.isNumeric( strDocumentId ) )
            {
                setDocumentIds.add( IntegerUtils.convert( strDocumentId ) );
            }
        }

        for ( int nDocumentId : setDocumentIds )
        {
            Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

            if ( ( document != null ) && isAuthorized( document, user ) )
            {
                listDocuments.add( document );
            }
        }

        return listDocuments;
    }

    /**
     * Checks that a document can be shown by a portlet: it must have a validated version, be of the portlet's document type, and belong to one of the
     * portlet's categories if it has some
     *
     * @param document
     *            The document, loaded with its categories
     * @param strDocumentTypeCode
     *            The portlet's document type
     * @param arrayIdCategories
     *            The portlet's categories
     * @return true if the document can be shown by the portlet
     */
    public boolean isPublishable( Document document, String strDocumentTypeCode, int [ ] arrayIdCategories )
    {
        if ( !document.hasValidatedVersion( ) || !document.getCodeDocumentType( ).equals( strDocumentTypeCode ) )
        {
            return false;
        }

        if ( arrayIdCategories.length == 0 )
        {
            return true;
        }

        List<Category> listCategories = document.getCategories( );

        if ( listCategories != null )
        {
            for ( Category category : listCategories )
            {
                for ( int nIdCategory : arrayIdCategories )
                {
                    if ( category.getId( ) == nIdCategory )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks that all the documents can be shown by a portlet
     *
     * @param listDocuments
     *            The documents, loaded with their categories
     * @param strDocumentTypeCode
     *            The portlet's document type
     * @param arrayIdCategories
     *            The portlet's categories
     * @return true if every document can be shown by the portlet
     * @see #isPublishable(Document, String, int[])
     */
    public boolean areAllPublishable( List<Document> listDocuments, String strDocumentTypeCode, int [ ] arrayIdCategories )
    {
        return listDocuments.stream( ).allMatch( document -> isPublishable( document, strDocumentTypeCode, arrayIdCategories ) );
    }

    /**
     * Makes a document the only one of a portlet: the other documents assigned to the portlet are removed from it, and the document is assigned and
     * published
     *
     * @param nDocumentId
     *            The document identifier
     * @param nPortletId
     *            The portlet identifier
     */
    public void showOnly( int nDocumentId, int nPortletId )
    {
        for ( Document assignedDocument : _publishingService.getAssignedDocumentsByPortletId( nPortletId ) )
        {
            if ( assignedDocument.getId( ) != nDocumentId )
            {
                remove( assignedDocument.getId( ), nPortletId );
            }
        }

        show( nDocumentId, nPortletId );
    }

    /**
     * Returns the identifiers of the documents a list selection would remove from a portlet: the documents published in the portlet that the user
     * may see and that are no longer selected
     *
     * @param nPortletId
     *            The portlet identifier
     * @param user
     *            The user
     * @param listIdSelected
     *            The identifiers of the documents selected
     * @return The identifiers of the documents to remove
     */
    public Set<Integer> getUnselectedPublishedIds( int nPortletId, AdminUser user, Collection<Integer> listIdSelected )
    {
        Set<Integer> setIds = new LinkedHashSet<>( );

        for ( Document document : getShownDocuments( nPortletId, user ) )
        {
            if ( !listIdSelected.contains( document.getId( ) ) )
            {
                setIds.add( document.getId( ) );
            }
        }

        return setIds;
    }

    /**
     * Applies a list selection to a portlet: the selected documents are assigned and published, in the order of the selection and before the other
     * documents of the portlet, the given unselected ones are removed. The documents the user could not choose from keep their relative order, after
     * the selected ones.
     *
     * @param listIdSelected
     *            The identifiers of the documents selected, in their order
     * @param setIdUnselected
     *            The identifiers of the documents to remove, see {@link #getUnselectedPublishedIds(int, AdminUser, Collection)}
     * @param nPortletId
     *            The portlet identifier
     */
    public void applySelection( Collection<Integer> listIdSelected, Set<Integer> setIdUnselected, int nPortletId )
    {
        for ( int nDocumentId : setIdUnselected )
        {
            remove( nDocumentId, nPortletId );
        }

        int nOrder = 1;

        for ( int nDocumentId : listIdSelected )
        {
            show( nDocumentId, nPortletId );
            _publishingService.changeDocumentOrder( nDocumentId, nPortletId, nOrder++ );
        }
    }

    /**
     * Assigns and publishes a document in a portlet, if not done yet
     *
     * @param nDocumentId
     *            The document identifier
     * @param nPortletId
     *            The portlet identifier
     */
    private void show( int nDocumentId, int nPortletId )
    {
        if ( !_publishingService.isAssigned( nDocumentId, nPortletId ) )
        {
            _publishingService.assign( nDocumentId, nPortletId );
        }

        if ( !_publishingService.isPublished( nDocumentId, nPortletId ) )
        {
            _publishingService.publish( nDocumentId, nPortletId );
        }
    }

    /**
     * Unpublishes and unassigns a document from a portlet
     *
     * @param nDocumentId
     *            The document identifier
     * @param nPortletId
     *            The portlet identifier
     */
    private void remove( int nDocumentId, int nPortletId )
    {
        if ( _publishingService.isPublished( nDocumentId, nPortletId ) )
        {
            _publishingService.unPublish( nDocumentId, nPortletId );
        }

        _publishingService.unAssign( nDocumentId, nPortletId );
    }

    /**
     * Returns the spaces whose documents of a type a user may see
     *
     * @param user
     *            The user
     * @param strCodeDocumentType
     *            The document type code
     * @return The space identifiers, empty if the user may not see the documents of the type
     * @see DocumentService#isAuthorizedAdminDocument(int, String, String, AdminUser)
     */
    private List<Integer> getViewableSpaceIds( AdminUser user, String strCodeDocumentType )
    {
        List<Integer> listSpaceIds = new ArrayList<>( );

        if ( !RBACService.isAuthorized( DocumentType.RESOURCE_TYPE, strCodeDocumentType, DocumentTypeResourceIdService.PERMISSION_VIEW, (User) user ) )
        {
            return listSpaceIds;
        }

        for ( DocumentSpace space : DocumentSpaceHome.findAll( ) )
        {
            if ( _documentSpacesService.isAuthorizedViewByWorkgroup( space.getId( ), user )
                    && _documentSpacesService.isAuthorizedViewByRole( space.getId( ), user ) )
            {
                listSpaceIds.add( space.getId( ) );
            }
        }

        return listSpaceIds;
    }

    /**
     * Tells if a user may see a document
     *
     * @param document
     *            The document
     * @param user
     *            The user
     * @return true if the user may see the document
     */
    private boolean isAuthorized( Document document, AdminUser user )
    {
        return _documentService.isAuthorizedAdminDocument( document.getSpaceId( ), document.getCodeDocumentType( ),
                DocumentTypeResourceIdService.PERMISSION_VIEW, user );
    }

    /**
     * A page of documents found by a search
     *
     * @param documents
     *            The documents of the page
     * @param page
     *            The page number, from 1
     * @param pageCount
     *            The number of pages, at least 1
     * @param total
     *            The number of documents found over all the pages
     */
    public record DocumentSearchPage( List<Document> documents, int page, int pageCount, int total )
    {
    }
}
