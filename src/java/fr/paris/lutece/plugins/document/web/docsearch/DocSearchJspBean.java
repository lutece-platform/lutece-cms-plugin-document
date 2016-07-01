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
package fr.paris.lutece.plugins.document.web.docsearch;

import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.service.docsearch.DocSearchItem;
import fr.paris.lutece.plugins.document.service.docsearch.DocSearchService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * DocSearchJspBean
 */
public class DocSearchJspBean extends PluginAdminPageJspBean
{
    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 1299568272034875394L;

    ////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String TEMPLATE_RESULTS = "admin/plugins/document/docsearch/search_results.html";
    private static final String PROPERTY_SEARCH_PAGE_URL = "document.docsearch.pageSearch.baseUrl";
    private static final String PROPERTY_RESULTS_PER_PAGE = "document.docsearch.nb.docs.per.page";
    private static final String MESSAGE_INVALID_SEARCH_TERMS = "document.message.invalidSearchTerms";
    private static final String PROPERTY_PAGE_TITLE_SEARCH = "document.search_results.pageTitle";
    private static final String DEFAULT_RESULTS_PER_PAGE = "10";
    private static final String DEFAULT_PAGE_INDEX = "1";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_NB_ITEMS_PER_PAGE = "items_per_page";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_ADVANCED_SEARCH = "advanced_search";
    private static final String MARK_RESULTS_LIST = "results_list";
    private static final String MARK_QUERY = "query";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ERROR = "error";
    private static final String MARK_SELECTED_TYPE = "selected_type";
    private static final String MARK_TITLE = "title";
    private static final String MARK_SUMMARY = "summary";
    private static final String MARK_TYPE_LIST = "document_type_list";
    private static final String MARK_DATE_QUERY = "date_query";
    private static final String MARK_LOCALE = "locale";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_SUMMARY = "summary";
    private static final String PARAMETER_DATE = "date";
    private static final String PARAMETER_TYPE = "document_type";

    /**
     * Returns search results
     *
     * @param request The HTTP request.
     * @return The HTML code of the page.
     */
    public String getSearch( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_SEARCH );

        String strQuery = request.getParameter( PARAMETER_QUERY );
        String strSearchPageUrl = AppPropertiesService.getProperty( PROPERTY_SEARCH_PAGE_URL );
        String strError = StringUtils.EMPTY;
        Map<String, Object> model = new HashMap<String, Object>(  );
        Locale locale = getLocale(  );

        // Check XSS characters
        if ( ( strQuery != null ) && ( StringUtil.containsXssCharacters( strQuery ) ) )
        {
            strError = I18nService.getLocalizedString( MESSAGE_INVALID_SEARCH_TERMS, locale );
            strQuery = StringUtils.EMPTY;
        }

        String strNbItemPerPage = request.getParameter( PARAMETER_NB_ITEMS_PER_PAGE );
        String strDefaultNbItemPerPage = AppPropertiesService.getProperty( PROPERTY_RESULTS_PER_PAGE,
                DEFAULT_RESULTS_PER_PAGE );
        strNbItemPerPage = ( strNbItemPerPage != null ) ? strNbItemPerPage : strDefaultNbItemPerPage;

        int nNbItemsPerPage = IntegerUtils.convert( strNbItemPerPage );
        String strCurrentPageIndex = request.getParameter( PARAMETER_PAGE_INDEX );
        strCurrentPageIndex = ( strCurrentPageIndex != null ) ? strCurrentPageIndex : DEFAULT_PAGE_INDEX;

        int nPageIndex = IntegerUtils.convert( strCurrentPageIndex );
        int nStartIndex = ( nPageIndex - 1 ) * nNbItemsPerPage;
        List<DocSearchItem> listResults;

        UrlItem url = new UrlItem( strSearchPageUrl );

        if ( request.getParameter( PARAMETER_ADVANCED_SEARCH ) != null )
        {
            boolean bTitle = false;
            boolean bSummary = false;

            model.put( "advanced_search", true );

            if ( request.getParameter( PARAMETER_TITLE ) != null )
            {
                model.put( MARK_TITLE, true );
                bTitle = true;
                url.addParameter( PARAMETER_TITLE, "true" );
            }

            if ( request.getParameter( PARAMETER_SUMMARY ) != null )
            {
                model.put( MARK_SUMMARY, true );
                bSummary = true;
                url.addParameter( PARAMETER_SUMMARY, "true" );
            }

            String strDate = request.getParameter( PARAMETER_DATE );
            String strDocumentType = request.getParameter( PARAMETER_TYPE );
            DocumentType docType = null;

            if ( strDocumentType != null )
            {
                docType = DocumentTypeHome.findByPrimaryKey( strDocumentType );
                model.put( MARK_SELECTED_TYPE, strDocumentType );
                url.addParameter( PARAMETER_TYPE, strDocumentType );
            }
            else
            {
                model.put( MARK_SELECTED_TYPE, "-1" );
            }

            model.put( MARK_DATE_QUERY, strDate );

            if ( strDate != null )
            {
                url.addParameter( PARAMETER_DATE, strDate );
            }

            model.put( MARK_TYPE_LIST, AdvancedSearch.getRefListDocumentType(  ) );

            listResults = DocSearchService.getInstance(  ).getSearchResults( strQuery, bTitle, bSummary, strDate,
                    docType );
        }
        else
        {
            listResults = DocSearchService.getInstance(  ).getSearchResults( strQuery, nStartIndex, getUser(  ) );
        }

        url.addParameter( PARAMETER_QUERY, strQuery );
        url.addParameter( PARAMETER_NB_ITEMS_PER_PAGE, nNbItemsPerPage );

        if ( request.getParameter( PARAMETER_ADVANCED_SEARCH ) != null )
        {
            url.addParameter( PARAMETER_ADVANCED_SEARCH, "true" );
        }

        LocalizedPaginator<DocSearchItem> paginator = new LocalizedPaginator<DocSearchItem>( listResults,
                nNbItemsPerPage, url.getUrl(  ), PARAMETER_PAGE_INDEX, strCurrentPageIndex, getLocale(  ) );

        model.put( MARK_RESULTS_LIST, paginator.getPageItems(  ) );
        model.put( MARK_QUERY, strQuery );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, strNbItemPerPage );
        model.put( MARK_ERROR, strError );
        model.put( MARK_LOCALE, request.getLocale(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RESULTS, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }
}
