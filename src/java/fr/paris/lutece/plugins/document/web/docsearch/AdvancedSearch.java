/*
 * Copyright (c) 2002-2023, City of Paris
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
import fr.paris.lutece.plugins.document.business.portlet.DocumentPortletHome;
import fr.paris.lutece.plugins.document.service.docsearch.DocSearchItem;
import fr.paris.lutece.plugins.document.service.docsearch.DocSearchService;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * AdvancedSearch
 */
public class AdvancedSearch implements XPageApplication
{
    private static final String TEMPLATE_ADVANCED_SEARCH = "skin/plugins/document/advanced_search.html";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_URL = "url";
    private static final String MARK_SELECTED_TYPE = "selected_type";
    private static final String MARK_TITLE = "title";
    private static final String MARK_SUMMARY = "summary";
    private static final String MARK_QUERY = "query";
    private static final String MARK_DATE_QUERY = "date_query";
    private static final String MARK_CONTENT = "content";
    private static final String MARK_TYPE_LIST = "document_type_list";
    private static final String MARK_DOC_SEARCH_ITEM = "docSearchItem";
    private static final String MARK_ID_PORTLET = "idPortlet";
    private static final String MARK_DATE_ERROR = "date_error";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_SEARCH_QUERY = "search_query";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_SUMMARY = "summary";
    private static final String PARAMETER_DATE = "date";
    private static final String PARAMETER_TYPE = "document_type";
    private static final String MESSAGE_TITLE = "document.advanced_search.title";
    private static final String MESSAGE_DATE_ERROR = "document.advanced_search.date_error";

    /**
     * Return the form for advanced search with results
     * @param request the request
     * @param plugin the plugin
     * @return the xpage
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
    {
        DocSearchService dss = DocSearchService.getInstance(  );
        XPage page = new XPage(  );
        HashMap<Object, Object> model = new HashMap<Object, Object>(  );

        String url = request.getRequestURL(  ).toString(  );
        model.put( MARK_URL, url );

        if ( request.getParameter( PARAMETER_SEARCH_QUERY ) != null )
        {
            String strSearchQuery = request.getParameter( PARAMETER_QUERY );

            boolean bTitle = false;
            boolean bSummary = false;

            if ( request.getParameter( PARAMETER_TITLE ) != null )
            {
                model.put( MARK_TITLE, true );
                bTitle = true;
            }

            if ( request.getParameter( PARAMETER_SUMMARY ) != null )
            {
                model.put( MARK_SUMMARY, true );
                bSummary = true;
            }

            String strDate = request.getParameter( PARAMETER_DATE );

            if ( ( strDate != null ) && !( strDate.equals( "" ) ) && !( isValidDate( strDate ) ) )
            {
                model.put( MARK_DATE_ERROR, I18nService.getLocalizedString( MESSAGE_DATE_ERROR, request.getLocale(  ) ) );
            }

            String strDocumentType = request.getParameter( PARAMETER_TYPE );
            DocumentType docType = null;

            if ( strDocumentType != null )
            {
                docType = DocumentTypeHome.findByPrimaryKey( strDocumentType );
                model.put( MARK_SELECTED_TYPE, strDocumentType );
            }

            List<DocSearchItem> result = dss.getSearchResults( strSearchQuery, bTitle, bSummary, strDate, docType );
            List<HashMap<String, Object>> finalResult = new ArrayList<HashMap<String, Object>>(  );
            PublishingService service = PublishingService.getInstance(  );

            Iterator<DocSearchItem> i = result.iterator(  );

            while ( i.hasNext(  ) )
            {
                DocSearchItem docSearchItem = i.next(  );

                if ( service.isPublished( IntegerUtils.convert( docSearchItem.getId(  ) ) ) )
                {
                    Collection<Portlet> c = service.getPortletsByDocumentId( docSearchItem.getId(  ) );
                    Iterator<Portlet> it = c.iterator(  );
                    int idPortlet = -1;

                    if ( it.hasNext(  ) )
                    {
                        idPortlet = it.next(  ).getId(  );
                    }

                    Page pagePortlet = null;

                    if ( idPortlet != -1 )
                    {
                        Portlet portlet = DocumentPortletHome.findByPrimaryKey( idPortlet );
                        pagePortlet = PageHome.getPage( portlet.getPageId(  ) );
                    }

                    if ( ( pagePortlet == null ) || ( pagePortlet.getRole(  ).equals( Page.ROLE_NONE ) ) ||
                            ( ( SecurityService.isAuthenticationEnable(  ) ) &&
                            ( SecurityService.getInstance(  ).isUserInRole( request, pagePortlet.getRole(  ) ) ) ) )
                    {
                        HashMap<String, Object> currentHashMap = new HashMap<String, Object>(  );
                        currentHashMap.put( MARK_DOC_SEARCH_ITEM, docSearchItem );
                        currentHashMap.put( MARK_ID_PORTLET, idPortlet );
                        finalResult.add( currentHashMap );
                    }
                }
            }

            //replace all quotes by html code
            strSearchQuery = strSearchQuery.replaceAll( "\"", "&quot;" );

            model.put( MARK_QUERY, strSearchQuery );
            model.put( MARK_DATE_QUERY, strDate );

            if ( finalResult.size(  ) > 0 )
            {
                model.put( MARK_CONTENT, finalResult );
            }
        }
        else
        {
            model.put( MARK_SELECTED_TYPE, "-1" );
        }

        model.put( MARK_TYPE_LIST, getRefListDocumentType(  ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ADVANCED_SEARCH, request.getLocale(  ), model );

        page.setContent( template.getHtml(  ) );
        page.setTitle( I18nService.getLocalizedString( MESSAGE_TITLE, request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( MESSAGE_TITLE, request.getLocale(  ) ) );

        return page;
    }

    /**
     * Check if the date is well formed.
     * @param strDate
     * @return true if the date is valid else false
     */
    private boolean isValidDate( String strDate )
    {
        SimpleDateFormat f = new SimpleDateFormat( "dd/MM/yy" );
        f.setLenient( false );

        try
        {
            f.parse( strDate );

            return true;
        }
        catch ( ParseException e )
        {
            return false;
        }
    }

    /**
     * The document referencelist
     * @return The document referencelist
     */
    public static ReferenceList getRefListDocumentType(  )
    {
        Collection<DocumentType> listDocumentType = DocumentTypeHome.findAll(  );
        ReferenceList reflistDocumentType = new ReferenceList(  );

        reflistDocumentType.addItem( -1, " " );

        for ( DocumentType documentType : listDocumentType )
        {
            reflistDocumentType.addItem( documentType.getCode(  ), documentType.getName(  ) );
        }

        return reflistDocumentType;
    }
}
