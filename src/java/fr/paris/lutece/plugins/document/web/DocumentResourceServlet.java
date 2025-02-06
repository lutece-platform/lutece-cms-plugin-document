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

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentResource;
import fr.paris.lutece.plugins.document.service.DocumentEvent;
import fr.paris.lutece.plugins.document.service.DocumentEventListener;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.business.resourceenhancer.ResourceEnhancer;
import fr.paris.lutece.portal.service.resource.ExtendableResourceActionHit;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet serving document file resources
 */
public class DocumentResourceServlet extends HttpServlet implements DocumentEventListener
{
    private static final long serialVersionUID = -7512201287826936428L;
    private static final String PARAMETER_DOCUMENT_ID = "id";
    private static final String PARAMETER_ATTRIBUTE_ID = "id_attribute";
    private static final String PARAMETER_WORKING_CONTENT = "working_content";
    private static final String PARAMETER_NOCACHE = "nocache";
    private static final String DEFAULT_EXPIRES_DELAY = "180000";
    private static final String PROPERTY_RESOURCE_TYPE = "document";
    private static final String PROPERTY_EXPIRES_DELAY = "document.resourceServlet.cacheControl.expires";
    private static final String KEY_DOC_BEGIN = "[doc:";
    private static final String KEY_ATTR_BEGIN = "[attr:";
    private static final String KEY_ITEM_CLOSE = "]";
    private static final String STRING_DELAY_IN_SECOND = AppPropertiesService.getProperty( PROPERTY_EXPIRES_DELAY,
            DEFAULT_EXPIRES_DELAY );
    private static final Long LONG_DELAY_IN_MILLISECOND = Long.parseLong( STRING_DELAY_IN_SECOND ) * 1000;
    private static final ResourceServletCache _cache = new ResourceServletCache(  );

    /**
     * Processes request HTTP <code>GET if-modified-since</code> methods
     * @param request The HTTP request
     * @return document last modified date
     */
    @Override
    public long getLastModified( HttpServletRequest request )
    {
        long lLastModified = -1;
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );

        if ( ( strDocumentId != null ) && ( strAttributeId != null ) )
        {
            int nDocumentId = IntegerUtils.convert( strDocumentId );
            int nAttributeId = IntegerUtils.convert( strAttributeId );
            String strKey = getCacheKey( nDocumentId, nAttributeId );

            ResourceValueObject resource = _cache.get( strKey );

            if ( _cache.isCacheEnable(  ) && ( _cache.get( strKey ) != null ) )
            {
                return resource.getLastModified(  );
            }

            Document document = DocumentHome.loadLastModifiedAttributes( nDocumentId );

            // Because Internet Explorer 6 has bogus behavior with PDF and proxy or HTTPS
            if ( ( document != null ) &&
                    !Document.CODE_DOCUMENT_TYPE_DOWNLOAD.equals( document.getCodeDocumentType(  ) ) &&
                    ( document.getDateModification(  ) != null ) )
            {
                lLastModified = document.getDateModification(  ).getTime(  );
            }
        }

        if ( lLastModified == -1 )
        {
            lLastModified = super.getLastModified( request );
        }

        return lLastModified;
    }

    /**
     * Put the file in cache
     * @param nDocumentId The document id
     * @param nAttributeId The attribut id
     */
    public static void putInCache( int nDocumentId, int nAttributeId )
    {
        if ( !_cache.isCacheEnable(  ) )
        {
            return;
        }

        DocumentResource resource = DocumentHome.getValidatedResource( nDocumentId, nAttributeId );
        ResourceValueObject res = new ResourceValueObject( resource );
        Document document = DocumentHome.loadLastModifiedAttributes( nDocumentId );
        long lLastModified = document.getDateModification(  ).getTime(  );
        res.setLastModified( lLastModified );
        _cache.put( getCacheKey( nDocumentId, nAttributeId ), res );
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws IOException the io exception
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = IntegerUtils.convert( strDocumentId );
        String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
        String strNoCache = request.getParameter( PARAMETER_NOCACHE );

        int nAttributeId = IntegerUtils.convert( strAttributeId );
        Boolean bWorkingContent = ( request.getParameter( PARAMETER_WORKING_CONTENT ) != null );

        String strCacheKey = getCacheKey( nDocumentId, nAttributeId );
        ResourceValueObject res;

        if ( !bWorkingContent && _cache.isCacheEnable(  ) && ( _cache.get( strCacheKey ) != null ) )
        {
            res = _cache.get( strCacheKey );
        }
        else
        {
            DocumentResource resource = getResource( nDocumentId, nAttributeId, bWorkingContent );

            if ( resource == null )
            {
                return;
            }

            res = new ResourceValueObject( resource );

            if ( _cache.isCacheEnable(  ) && !bWorkingContent )
            {
                Document document = DocumentHome.loadLastModifiedAttributes( nDocumentId );
                long lLastModified = document.getDateModification(  ).getTime(  );
                res.setLastModified( lLastModified );
                _cache.put( strCacheKey, res );
            }
        }

        ExtendableResourceActionHit.getInstance(  )
                                   .notifyActionOnResource( strDocumentId, Document.PROPERTY_RESOURCE_TYPE,
            ExtendableResourceActionHit.ACTION_DOWNLOAD );

        ResourceEnhancer.doDownloadResourceAddOn( request, PROPERTY_RESOURCE_TYPE, nDocumentId );

        // Sets content type and filename of the resource into the response
        response.setContentType( res.getContentType(  ) );

        if ( !isGraphicalContent( res.getContentType(  ) ) )
        {
            // Add the filename only if the resource isn't a flash document or an image
            response.setHeader( "Content-Disposition", "attachment;filename=\"" + res.getFilename(  ) + "\"" );
        }

        // Add Cache Control HTTP header
        if ( strNoCache != null )
        {
            response.setHeader( "Cache-Control", "no-cache" ); // HTTP 1.1
            response.setDateHeader( "Expires", 0 ); // HTTP 1.0
        }
        else
        {
            response.setHeader( "Cache-Control", "max-age=" + STRING_DELAY_IN_SECOND ); // HTTP 1.1
            response.setDateHeader( "Expires", System.currentTimeMillis(  ) + LONG_DELAY_IN_MILLISECOND ); // HTTP 1.0           
        }

        response.setContentLength( res.getContent(  ).length ); // Keep Alive connexion

        // Write the resource content
        OutputStream out = response.getOutputStream(  );
        out.write( res.getContent(  ) );

        //out.flush( );        
        //out.close(); Disabled : allow Keep Alive connexion
    }

    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws AppException the app exception
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
    {
        try
        {
            processRequest( request, response );
        }
        catch( IOException e )
        {
            throw new AppException( "Error while getting a resources", e );
        }
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws AppException the app exception
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
    {
        try
        {
            processRequest( request, response );
        }
        catch( IOException e )
        {
            throw new AppException( "Error while getting a resources", e );
        }
    }

    /** Returns a short description of the servlet.
     * @return message
     */
    @Override
    public String getServletInfo(  )
    {
        return "Servlet serving file resources of documents";
    }

    /**
     * Caclculate the cache key
     * @param nDocumentId The document id
     * @param nAttributeId The attribute id
     * @return The key
     */
    private static String getCacheKey( int nDocumentId, int nAttributeId )
    {
        StringBuilder sbKey = new StringBuilder(  );
        sbKey.append( KEY_DOC_BEGIN ).append( nDocumentId ).append( KEY_ITEM_CLOSE ).append( KEY_ATTR_BEGIN )
             .append( nAttributeId ).append( KEY_ITEM_CLOSE );

        return sbKey.toString(  );
    }

    /**
     * Get the document resource
     * @param nDocumentId The document id
     * @param nAttributeId The attribute id
     * @param bWorkingContent is a working content
     * @return The document resource
     */
    private DocumentResource getResource( int nDocumentId, int nAttributeId, boolean bWorkingContent )
    {
        DocumentResource resource;

        if ( nAttributeId != -1 )
        {
            if ( bWorkingContent )
            {
                resource = DocumentHome.getWorkingResource( nDocumentId, nAttributeId );

                if ( resource == null )
                {
                    resource = DocumentHome.getValidatedResource( nDocumentId, nAttributeId );
                }
            }
            else
            {
                resource = DocumentHome.getValidatedResource( nDocumentId, nAttributeId );
            }
        }
        else
        {
            resource = DocumentHome.getResource( nDocumentId );
        }

        return resource;
    }

    /**
     * Is the document an image or a flash object according the content type
     * @param strContentType The content type
     * @return True for an image or a flash object, otherwise false
     */
    private boolean isGraphicalContent( String strContentType )
    {
        return ( strContentType.equals( "image/jpeg" ) || strContentType.equals( "image/gif" ) ||
        strContentType.equals( "image/png" ) || strContentType.equals( "application/x-shockwave-flash" ) );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void processDocumentEvent( DocumentEvent event )
        throws DocumentException
    {
        String strKeyPattern = KEY_DOC_BEGIN + event.getDocument(  ).getId(  ) + KEY_ITEM_CLOSE;
        _cache.removeFromKeyPattern( strKeyPattern );
    }
}
