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

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

/**
 * DocumentLegacyServletRedirectFilter, a filter to redirect previous style of URLs
 * for document resources (changed in lutece V6) to the new urls.
 *
 * By default it will redirect like this:
 *     document?id=XXX -> servlet/plugins/document/resource?id=xxx
 *
 */
public class DocumentLegacyServletRedirectFilter implements Filter
{

    private static final String PROPERTY_RESOURCE_PROVIDER_URL = "document.resource.provider.url";
    private static final String DEFAULT_RESOURCE_SERVLET =  "servlet/plugins/document/resource";

    /**
     * {@inheritDoc}
     */
    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain ) throws IOException, ServletException
    {
        HttpServletRequest httpServletRequest = ( HttpServletRequest ) servletRequest;
        HttpServletResponse httpServletResponse = ( HttpServletResponse ) servletResponse;
        String strNewServletResource = AppPropertiesService.getProperty( PROPERTY_RESOURCE_PROVIDER_URL );
        if ( strNewServletResource != null ) {
            int nIdx = strNewServletResource.indexOf( '?' );
            if ( nIdx != -1 ) {
                strNewServletResource = strNewServletResource.substring( 0, nIdx );
            }
        } else {
            strNewServletResource = DEFAULT_RESOURCE_SERVLET;
        }

        UrlItem urlItem = new UrlItem( strNewServletResource );
        for ( Entry<String, String[]> entryParameter: httpServletRequest.getParameterMap( ).entrySet( ) ) {
            for ( String value: entryParameter.getValue( ) ) {
                urlItem.addParameter( entryParameter.getKey( ), value );
            }
        }

        httpServletResponse.setStatus( HttpServletResponse.SC_MOVED_PERMANENTLY );
        httpServletResponse.setHeader( "Location", urlItem.getUrl( ) );
    }

    @Override
    public void destroy( ) {
        //Do Nothing
    }

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException {
        //Do Nothing
    }
}
