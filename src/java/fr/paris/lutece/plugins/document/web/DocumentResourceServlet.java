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
import fr.paris.lutece.plugins.document.business.DocumentResource;
import fr.paris.lutece.portal.business.resourceenhancer.ResourceEnhancer;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.IOException;
import java.io.OutputStream;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet serving document file resources
 */
public class DocumentResourceServlet extends HttpServlet
{
    private static final long serialVersionUID = -7512201287826936428L;
    private static final String PARAMETER_DOCUMENT_ID = "id";
    private static final String PARAMETER_ATTRIBUTE_ID = "id_attribute";
    private static final String PARAMETER_WORKING_CONTENT = "working_content";
    private static final String DEFAULT_FILENAME = "document";
    private static final String DEFAULT_EXPIRES_DELAY = "180000";
    private static final String PROPERTY_RESOURCE_TYPE = "document";
    private static final String PROPERTY_EXPIRES_DELAY = "document.resourceServlet.cacheControl.expires";
    private static final String STRING_DELAY_IN_SECOND = AppPropertiesService.getProperty( PROPERTY_EXPIRES_DELAY,
            DEFAULT_EXPIRES_DELAY );
    private static final Long LONG_DELAY_IN_MILLISECOND = Long.parseLong( STRING_DELAY_IN_SECOND ) * 1000;
    private static final ResourceServletCache _cache = new ResourceServletCache(  );

    /**
     * Processes request HTTP <code>GET if-modified-since</code> methods
     * @param servlet request
     * @return document last modified date
     */
    @Override
    public long getLastModified( HttpServletRequest request )
    {
        long lLastModified = -1;
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );

        if ( strDocumentId != null )
        {
            Document document = DocumentHome.loadLastModifiedAttributes( Integer.valueOf( strDocumentId ) );

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
        String strCacheKey = nDocumentId + "-" + nAttributeId;

        DocumentResource resource;

        byte[] content;
        String strContentType;
        String strFilename;

        resource = DocumentHome.getValidatedResource( nDocumentId, nAttributeId );

        strFilename = resource.getName(  );

        if ( ( strFilename == null ) || strFilename.equals( "" ) )
        {
            strFilename = DEFAULT_FILENAME;
        }

        strContentType = resource.getContentType(  );
        content = resource.getContent(  );

        if ( _cache.isCacheEnable(  ) )
        {
            ResourceValueObject r = new ResourceValueObject(  );
            r.setContent( content );
            r.setContentType( strContentType );
            r.setFilename( strFilename );
            _cache.put( strCacheKey, r );
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException the servlet Exception
     * @throws IOException the io exception
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = Integer.parseInt( strDocumentId );
        String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );

        Boolean bWorkingContent = ( request.getParameter( PARAMETER_WORKING_CONTENT ) != null );

        String strCacheKey = strDocumentId + "-" + strAttributeId;
        byte[] content;
        String strContentType;
        String strFilename;

        if ( !bWorkingContent && _cache.isCacheEnable(  ) && ( _cache.get( strCacheKey ) != null ) )
        {
            ResourceValueObject resource = _cache.get( strCacheKey );
            content = resource.getContent(  );
            strContentType = resource.getContentType(  );
            strFilename = resource.getFilename(  );
        }
        else
        {
            DocumentResource resource;

            if ( strAttributeId != null )
            {
                int nAttributeId = Integer.parseInt( strAttributeId );

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

            if ( resource == null )
            { //nothing to do if document no longer in DB

                return;
            }

            strFilename = resource.getName(  );

            if ( ( strFilename == null ) || strFilename.equals( "" ) )
            {
                strFilename = DEFAULT_FILENAME;
            }

            strContentType = resource.getContentType(  );
            content = resource.getContent(  );

            if ( _cache.isCacheEnable(  ) && !bWorkingContent )
            {
                ResourceValueObject r = new ResourceValueObject(  );
                r.setContent( content );
                r.setContentType( strContentType );
                r.setFilename( strFilename );
                _cache.put( strCacheKey, r );
            }
        }

        ResourceEnhancer.doDownloadResourceAddOn( request, PROPERTY_RESOURCE_TYPE, nDocumentId );

        // Sets content type and filename of the resource into the response
        response.setContentType( strContentType );

        if ( !( strContentType.equals( "image/jpeg" ) || strContentType.equals( "image/gif" ) ||
                strContentType.equals( "image/png" ) || strContentType.equals( "application/x-shockwave-flash" ) ) )
        {
            // Add the filename only if the resource isn't a flash document
            response.setHeader( "Content-Disposition", "attachment;filename=\"" + strFilename + "\"" );
        }

        // Add Cache Control HTTP header
        response.setHeader( "Cache-Control", "max-age=" + STRING_DELAY_IN_SECOND ); // HTTP 1.1
        response.setDateHeader( "Expires", System.currentTimeMillis(  ) + LONG_DELAY_IN_MILLISECOND ); // HTTP 1.0
        response.setContentLength( content.length ); // Keep Alive connexion

        // Write the resource content
        OutputStream out = response.getOutputStream(  );
        out.write( content );

        //out.flush( );        
        //out.close(); Disabled : allow Keep Alive connexion
    }

    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException the servlet Exception
     * @throws IOException the io exception
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        processRequest( request, response );
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException the servlet Exception
     * @throws IOException the io exception
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        processRequest( request, response );
    }

    /** Returns a short description of the servlet.
     * @return message
     */
    @Override
    public String getServletInfo(  )
    {
        return "Servlet serving file resources of documents";
    }
}
