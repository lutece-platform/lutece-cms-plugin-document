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
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublication;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.AliasPortlet;
import fr.paris.lutece.portal.business.portlet.AliasPortletHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.resourceenhancer.ResourceEnhancer;
import fr.paris.lutece.portal.business.style.ModeHome;
import fr.paris.lutece.portal.service.cache.ICacheKeyService;
import fr.paris.lutece.portal.service.cache.Lutece107Cache;
import fr.paris.lutece.portal.service.cache.LuteceCacheManager;
import fr.paris.lutece.portal.service.content.ContentService;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.page.PortletCacheService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.PortalJspBean;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;

import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import javax.cache.configuration.Factory;


/**
 *
 */

public class DocumentContentService extends ContentService
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String CONTENT_SERVICE_NAME = "Document Content Service";
    private static final String SLASH = "/";
    private static final int MODE_ADMIN = 1;
    private static final String CONSTANT_DEFAULT_PORTLET_DOCUMENT_LIST_XSL = "WEB-INF/xsl/normal/portlet_document_list.xsl";
    private static final String DOCUMENT_STYLE_PREFIX_ID = "document-";
    private static final String LOCALE_EN = "en";
    private static final String LOCALE_FR = "fr";

    // XML tags
    private static final String XML_TAG_CONTENT = "content";
    private static final String XML_TAG_SITE_LOCALE = "site_locale";

    // Parameters
    private static final String PARAMETER_DOCUMENT_ID = "document_id";
    private static final String PARAMETER_SITE_PATH = "site-path";
    private static final String PARAMETER_PUBLICATION_DATE = "publication-date";
    private static final String PARAMETER_SITE_LOCALE = "site_locale";

    // Markers
    private static final String MARK_PUBLICATION = "publication";
    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_PORTLET = "portlet";
    private static final String MARK_CATEGORY = "categories";
    private static final String MARK_DOCUMENT_ID = "document_id";
    private static final String MARK_PORTLET_ID = "portlet_id";
    private static final String MARK_PORTLET_ID_LIST = "portlet_id_list";
    private static final String MARK_DOCUMENT_CATEGORIES_LIST = "document_categories_list";
    private static final String MARK_URL_LOGIN = "url_login";
    private static final String MARKER_TARGET = "target";
    private static final String MARK_IS_EXTEND_INSTALLED = "isExtendInstalled";

    // Templates
    private static final String TEMPLATE_DOCUMENT_PAGE_DEFAULT = "/skin/plugins/document/document_content_service.html";
    private static final String TEMPLATE_DOCUMENT_CATEGORIES = "/skin/plugins/document/document_categories.html";

    //Properties
    private static final String PROPERTY_DEFAULT_PORTLET_DOCUMENT_LIST_XSL = "document.contentService.defaultPortletDocumentListXSL";
    private static final String PROPERTY_CACHE_ENABLED = "document.cache.enabled";
    private static final String TARGET_TOP = "target=_top";
    private static final String PROPERTY_RESOURCE_TYPE = "document";

    //Portlet cache
    //Should be equal to PortletCacheService.CACHE_PORTLET_PREFIX without the final semicolon
    private static final String PARAMETER_PORTLET = "portlet";
    private static final String PORTLET_CACHE_KEY_SUFFIX = "[documentContentService]";
    private static final String SERVICE_NAME_PORTLET_CACHE_KEY_SERVICE = "portletCacheKeyService";
    private boolean _bInit;
       
    private PublishingService _publishingService;
    
    private PortletCacheService _cachePortlets;
    
    private ICacheKeyService _cksPortlet; //PortletCacheKeyService type

    private Lutece107Cache<String, String> _cache;

    /**
     * Initializes the service
     */
    private void init(  )
    {
    	
    	_publishingService = CDI.current( ).select( PublishingService.class ).get( );
    	
    	_cachePortlets =  CDI.current( ).select( PortletCacheService.class ).get( );
    	
    	_cksPortlet = CDI.current( ).select( ICacheKeyService.class, NamedLiteral.of( SERVICE_NAME_PORTLET_CACHE_KEY_SERVICE ) ).get( );   	
    	
    	//LuteceCache creation
    	LuteceCacheManager cacheManager= CDI.current( ).select( LuteceCacheManager.class ).get( ); 
    	_cache = cacheManager.createCache(  CONTENT_SERVICE_NAME, String.class, String.class );

    	
        // Initialize the cache according property value. 
        // If the property isn't found the default is true
        String strCache = AppPropertiesService.getProperty( PROPERTY_CACHE_ENABLED, "true" );

        if ( strCache.equalsIgnoreCase( "true" ) )
        {
            _cache.enableCache( true );
        }
        else
        {
            _cache.enableCache( false );
        }
        
        _bInit = true;
    }

    
    /**
     * Returns the document page for a given document and a given portlet. The
     * page is built from XML data or retrieved
     * from the cache if it's enable and the document in it.
     *
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @return The HTML code of the page as a String.
     * @throws UserNotSignedException If the user is not signed
     * @throws SiteMessageException occurs when a site message need to be
     *             displayed
     */
    @Override
    public String getPage( HttpServletRequest request, int nMode )
        throws UserNotSignedException, SiteMessageException
    {
        if ( !_bInit )
        {
            init(  );
        }

        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strPortletId = request.getParameter( Parameters.PORTLET_ID );
        String strSiteLocale = request.getParameter( PARAMETER_SITE_LOCALE );

        if ( ( strSiteLocale == null ) || !strSiteLocale.equalsIgnoreCase( LOCALE_EN ) )
        {
            strSiteLocale = LOCALE_FR;
        }

        String strKey = getKey( strDocumentId, strPortletId, strSiteLocale, nMode );
        String strPage = (String) _cache.get( strKey );

        if ( strPage == null )
        {
            AppLogService.debug( " -- Page generation {} : doc={} portletid={} site_locale={} nMode={}",
                    strKey, strDocumentId, strPortletId, strSiteLocale, nMode );
            strPage = buildPage( request, strDocumentId, strPortletId, strSiteLocale, nMode );

            if ( IntegerUtils.isNumeric( strDocumentId ) )
            {
                int nDocumentId = IntegerUtils.convert( strDocumentId );
                Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

                if ( ( document != null ) )
                {
                    _cache.put( strKey, strPage );
                }
            }
        }
        else
        {
            AppLogService.debug( "Page read from cache {}", strKey );
        }

        return strPage;
    }

    /**
     * Build the document page
     * @param request The HTTP Request
     * @param strDocumentId The document ID
     * @param strPortletId The portlet ID
     * @param strSiteLocale the site locale code
     * @param nMode The current mode
     * @return
     * @throws fr.paris.lutece.portal.service.security.UserNotSignedException
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     */
    private String buildPage( HttpServletRequest request, String strDocumentId, String strPortletId,
        String strSiteLocale, int nMode ) throws UserNotSignedException, SiteMessageException
    {
        int nPortletId;
        int nDocumentId;
        boolean bPortletExist = false;
        Map<String, String> mapXslParams = new HashMap<String, String>(  );

        try
        {
            nPortletId = Integer.parseInt( strPortletId );
            nDocumentId = Integer.parseInt( strDocumentId );
        }
        catch ( NumberFormatException nfe )
        {
            return PortalService.getDefaultPage( request, nMode );
        }

        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( ( document == null ) || ( !document.isValid(  ) ) )
        {
            return PortalService.getDefaultPage( request, nMode );
        }

        DocumentType type = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType(  ) );
        DocumentPublication documentPublication = _publishingService.getDocumentPublication( nPortletId, nDocumentId );

        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( documentPublication != null )
        {
            // Check if portlet is an alias portlet
            boolean bIsAlias = DocumentListPortletHome.checkIsAliasPortlet( documentPublication.getPortletId(  ) );

            if ( bIsAlias && ( documentPublication.getPortletId(  ) != nPortletId ) )
            {
                AliasPortlet alias = (AliasPortlet) AliasPortletHome.findByPrimaryKey( nPortletId );
                nPortletId = alias.getAliasId(  );
                strPortletId = Integer.toString( nPortletId );
            }

            if ( ( documentPublication.getPortletId(  ) == nPortletId ) &&
                    ( documentPublication.getStatus(  ) == DocumentPublication.STATUS_PUBLISHED ) )
            {
                bPortletExist = true;
            }

            // The publication informations are available in Xsl (only publication date) and in template (full DocumentPublication object)
            mapXslParams.put( PARAMETER_PUBLICATION_DATE,
                DateUtil.getDateString( documentPublication.getDatePublishing(  ), request.getLocale(  ) ) );
            model.put( MARK_PUBLICATION, documentPublication );
        }

        if ( bPortletExist )
        {
            // Fill a PageData structure for those elements
            PageData data = new PageData(  );
            data.setName( document.getTitle(  ) );
            data.setPagePath( PortalService.getXPagePathContent( document.getTitle(  ), 0, request ) );

            Portlet portlet = PortletHome.findByPrimaryKey( nPortletId );
            Page page = PageHome.getPage( portlet.getPageId(  ) );
            String strRole = page.getRole(  );

            if ( !strRole.equals( Page.ROLE_NONE ) && SecurityService.getInstance( ).isAuthenticationEnable( ) )
            {
                LuteceUser user = SecurityService.getInstance().getRegisteredUser( request );

                if ( ( user == null ) && ( !SecurityService.getInstance().isExternalAuthentication(  ) ) )
                {
                    // The user is not registered and identify itself with the Portal authentication
                    String strAccessControledTemplate = SecurityService.getInstance().getAccessControledTemplate(  );
                    HashMap<String, Object> modelAccessControledTemplate = new HashMap<String, Object>(  );
                    String strLoginUrl = SecurityService.getInstance().getLoginPageUrl(  );
                    modelAccessControledTemplate.put( MARK_URL_LOGIN, strLoginUrl );

                    HtmlTemplate tAccessControled = AppTemplateService.getTemplate( strAccessControledTemplate,
                            request.getLocale(  ), modelAccessControledTemplate );
                    data.setContent( tAccessControled.getHtml(  ) );

                    return PortalService.buildPageContent( data, nMode, request );
                }

                if ( !SecurityService.getInstance().isUserInRole( request, strRole ) )
                {
                    // The user doesn't have the correct role
                    String strAccessDeniedTemplate = SecurityService.getInstance().getAccessDeniedTemplate(  );
                    HtmlTemplate tAccessDenied = AppTemplateService.getTemplate( strAccessDeniedTemplate,
                            request.getLocale(  ) );
                    data.setContent( tAccessDenied.getHtml(  ) );

                    return PortalService.buildPageContent( data, nMode, request );
                }
            }

            // Get request paramaters and store them in a hashtable
            Enumeration<?> enumParam = request.getParameterNames(  );
            Hashtable<String, String> htParamRequest = new Hashtable<String, String>(  );
            String paramName = "";

            while ( enumParam.hasMoreElements(  ) )
            {
                paramName = (String) enumParam.nextElement(  );
                htParamRequest.put( paramName, request.getParameter( paramName ) );
            }

            XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
            StringBuffer strXml = new StringBuffer(  );
            XmlUtil.beginElement( strXml, XML_TAG_CONTENT );
            XmlUtil.addElement( strXml, XML_TAG_SITE_LOCALE, strSiteLocale );
            strXml.append( document.getXmlValidatedContent(  ) );
            XmlUtil.endElement( strXml, XML_TAG_CONTENT );

            String strDocument = xmlTransformerService.transformBySourceWithXslCache( strXml.toString(  ),
                    type.getContentServiceXslSource(  ), DOCUMENT_STYLE_PREFIX_ID + type.getStyleSheetId( nMode ),
                    htParamRequest, null );

            model.put( MARK_DOCUMENT, strDocument );
            
            if ( !document.isSkipPortlet() )
            {
            	model.put( MARK_PORTLET, getPortlet( request, strPortletId, nMode ) );
            }
            
            if ( !document.isSkipCategories() )
            {
            	model.put( MARK_CATEGORY, getRelatedDocumentsPortlet( request, document, nPortletId, nMode ) );
            }
            
            model.put( MARK_DOCUMENT_ID, strDocumentId );
            model.put( MARK_PORTLET_ID, strPortletId );
            model.put( MARK_IS_EXTEND_INSTALLED, PortalService.isExtendActivated(  ) );

            // Additional page info
            ResourceEnhancer.buildPageAddOn( model, PROPERTY_RESOURCE_TYPE, nDocumentId, strPortletId, request );

            HtmlTemplate template = AppTemplateService.getTemplate( getTemplatePage( document ), request.getLocale(  ),
                    model );

            data.setContent( template.getHtml(  ) );

            return PortalService.buildPageContent( data, nMode, request );
        }

        //portlet does not exists
        return PortalService.getDefaultPage( request, nMode );
    }

    /**
     * Analyzes request parameters to see if the request should be handled by
     * the current Content Service
     *
     * @param request The HTTP request
     * @return true if this ContentService should handle this request
     */
    @Override
    public boolean isInvoked( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strIdPortlet = request.getParameter( Parameters.PORTLET_ID );

        if ( ( strDocumentId != null ) && ( strDocumentId.length(  ) > 0 ) && ( strIdPortlet != null ) &&
                ( strIdPortlet.length(  ) > 0 ) )
        {
            return true;
        }

        return false;
    }

    /**
     * Returns the Content Service name
     *
     * @return The name as a String
     */
    @Override
    public String getName(  )
    {
        return CONTENT_SERVICE_NAME;
    }

    /**
     * Return the template page
     * @param document
     * @return the template
     */
    private String getTemplatePage( Document document )
    {
        if ( document.getPageTemplateDocumentId(  ) != 0 )
        {
            String strPageTemplateDocument = DocumentHome.getPageTemplateDocumentPath( document.getPageTemplateDocumentId(  ) );

            return strPageTemplateDocument;
        }
        else
        {
            return TEMPLATE_DOCUMENT_PAGE_DEFAULT;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Comments implementation
    /**
     * Gets the documents list portlet containing the document
     *
     * @param strPortletId The ID of the documents list portlet where the
     *            document has been published.
     * @param nMode The current mode.
     * @param request The Http request
     * @return The HTML code of the documents list portlet as a String
     * @throws SiteMessageException IF a message need to be displayed
     */
    private String getPortlet( HttpServletRequest request, String strPortletId, int nMode )
        throws SiteMessageException
    {
        try
        {
            int nPortletId = Integer.parseInt( strPortletId );

            Portlet portlet = PortletHome.findByPrimaryKey( nPortletId );

            // Selection of the XSL stylesheet
            // byte[] baXslSource = portlet.getXslSource( nMode );

            //FIXME Temporary solution (see LUTECE-824)
            String strFilePath = AppPropertiesService.getProperty( PROPERTY_DEFAULT_PORTLET_DOCUMENT_LIST_XSL,
                    CONSTANT_DEFAULT_PORTLET_DOCUMENT_LIST_XSL );

            if ( strFilePath == null )
            {
                return StringUtils.EMPTY;
            }

            if ( !strFilePath.startsWith( SLASH ) )
            {
                strFilePath = SLASH + strFilePath;
            }

            String strFileName = strFilePath.substring( strFilePath.lastIndexOf( SLASH ) + 1 );
            strFilePath = strFilePath.substring( 0, strFilePath.lastIndexOf( SLASH ) + 1 );

            FileInputStream fis = AppPathService.getResourceAsStream( strFilePath, strFileName );
            Source xslSource = new StreamSource( fis );

            // Get request paramaters and store them in a hashtable
            Enumeration<?> enumParam = request.getParameterNames(  );
            Hashtable<String, String> htParamRequest = new Hashtable<String, String>(  );
            String paramName = "";

            while ( enumParam.hasMoreElements(  ) )
            {
                paramName = (String) enumParam.nextElement(  );
                htParamRequest.put( paramName, request.getParameter( paramName ) );
            }

            Properties outputProperties = ModeHome.getOuputXslProperties( nMode );

            // Add a path param for choose url to use in admin or normal mode
            if ( nMode != MODE_ADMIN )
            {
                htParamRequest.put( PARAMETER_SITE_PATH, AppPathService.getPortalUrl(  ) );
            }
            else
            {
                htParamRequest.put( PARAMETER_SITE_PATH, AppPathService.getAdminPortalUrl(  ) );
                htParamRequest.put( MARKER_TARGET, TARGET_TOP );
            }

            if ( _cachePortlets !=null && _cachePortlets.isCacheEnable(  ) )
            {
                LuteceUser user = null;

                if ( SecurityService.isAuthenticationEnable(  ) )
                {
                    user = SecurityService.getInstance().getRegisteredUser( request );
                }

                boolean bCanBeCached = ( user != null ) ? ( portlet.canBeCachedForConnectedUsers(  ) )
                                                        : ( portlet.canBeCachedForAnonymousUsers(  ) );

                if ( bCanBeCached )
                {
                    //To delete keys when portlets are modified through _cachePortlets implementing PortletEventListener
                    htParamRequest.put( PARAMETER_PORTLET, String.valueOf( portlet.getId(  ) ) );

                    //Add [documentContentService] to not clash with PageService keys because we don't synchronize
                    String strKey =_cksPortlet.getKey( htParamRequest, nMode, user ) + PORTLET_CACHE_KEY_SUFFIX;

                    // get portlet from cache
                    String strPortlet = (String) _cachePortlets.get( strKey );

                    if ( strPortlet == null )
                    {
                        // only one thread can evaluate the page
                        synchronized ( strKey )
                        {
                            // can be useful if an other thread had evaluate the
                            // porlet
                            strPortlet = (String) _cachePortlets.get( strKey );

                            // ignore checkstyle, this double verification is useful
                            // when page cache has been created when thread is
                            // blocked on synchronized
                            if ( strPortlet == null )
                            {
                                String strXml = portlet.getXmlDocument( request );

                                XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
                                String strXslUniquePrefix = DOCUMENT_STYLE_PREFIX_ID + strFilePath + strFileName;

                                strPortlet = xmlTransformerService.transformBySourceWithXslCache( strXml, xslSource,
                                        strXslUniquePrefix, htParamRequest, outputProperties );

                                _cachePortlets.put( strKey, strPortlet );
                            }
                        }
                    }

                    return strPortlet;
                }
            }

            String strXml = portlet.getXmlDocument( request );

            XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
            String strXslUniquePrefix = DOCUMENT_STYLE_PREFIX_ID + strFilePath + strFileName;

            return xmlTransformerService.transformBySourceWithXslCache( strXml, xslSource, strXslUniquePrefix,
                htParamRequest, outputProperties );
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

    /**
     * Gets the category list portlet linked with the document
     *
     * @param request The Http request
     * @param document The document
     * @param nPortletId The ID of the documents list portlet where the document
     *            has been published.
     * @param nMode The current mode.
     * @return The HTML code of the categories list portlet as a String
     */
    private String getRelatedDocumentsPortlet( HttpServletRequest request, Document document, int nPortletId, int nMode )
    {
        if ( ( nMode != MODE_ADMIN ) && ( document.getCategories(  ) != null ) &&
                ( document.getCategories(  ).size(  ) > 0 ) )
        {
            HashMap<String, Object> model = new HashMap<String, Object>(  );
            List<Document> listRelatedDocument = DocumentHome.findByRelatedCategories( document, request.getLocale(  ) );

            List<Document> listDocument = new ArrayList<Document>(  );
            ReferenceList listDocumentPortlet = new ReferenceList(  );

            // Create list of related documents from the specified categories of input document 
            for ( Document relatedDocument : listRelatedDocument )
            {
                // Get list of portlets for each document
                for ( Portlet portlet : _publishingService.getPortletsByDocumentId( Integer.toString( 
                            relatedDocument.getId(  ) ) ) )
                {
                    // Check if document and portlet are published and document is not the input document 
                    if ( ( _publishingService.isPublished( relatedDocument.getId(  ), portlet.getId(  ) ) ) &&
                            ( portlet.getStatus(  ) == Portlet.STATUS_PUBLISHED ) && ( relatedDocument.isValid(  ) ) &&
                            ( relatedDocument.getId(  ) != document.getId(  ) ) )
                    {
                        listDocumentPortlet.addItem( Integer.toString( relatedDocument.getId(  ) ),
                            Integer.toString( portlet.getId(  ) ) );
                        listDocument.add( relatedDocument );

                        break;
                    }
                }
            }

            model.put( MARK_DOCUMENT_CATEGORIES_LIST, listDocument );
            model.put( MARK_PORTLET_ID_LIST, listDocumentPortlet );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DOCUMENT_CATEGORIES,
                    request.getLocale(  ), model );

            return template.getHtml(  );
        }
        else
        {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Build the Cache key for pages
     *
     * @param strDocumentId The id of the document
     * @param strPortletId The id of the portlet of the document
     * @param strSiteLocale The site locale
     * @param nMode The mode
     * @return The key for articles pages as a String.
     */
    private String getKey( String strDocumentId, String strPortletId, String strSiteLocale, int nMode )
    {
        return "D" + strDocumentId + "P" + strPortletId + "L" + strSiteLocale + "M" + nMode;
    }

    /**
     * Remove a document from the cache
     * @param strDocumentId the document id
     * @param strPortletId the portlet id
     */
    public void removeFromCache( String strDocumentId, String strPortletId )
    {
        if ( _cache != null && _cache.isCacheEnable( ) )
        {
            String strKey = getKey( strDocumentId, strPortletId, LOCALE_FR, PortalJspBean.MODE_HTML );

            _cache.remove( strKey );

            strKey = getKey( strDocumentId, strPortletId, LOCALE_EN, PortalJspBean.MODE_HTML );

            _cache.remove( strKey );
        }
    }
	
	public void loachInit( )
	{
		if( !_bInit )
		{
			init( );
		}
	}
}
