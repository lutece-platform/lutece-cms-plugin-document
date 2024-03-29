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
package fr.paris.lutece.plugins.document.web;

import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.cache.CacheService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * ResourceServletCache
 * @deprecated This class will be removed after v2.5
 */
public class ResourceServletContentTypeCache extends AbstractCacheableService
{
    private static final String NAME = "Document ResourceServlet ContentType Cache";
    private static final String PROPERTY_CACHE = "document.resourceServlet.cache.enabled";

    /**
     * Constructor
     */
    public ResourceServletContentTypeCache(  )
    {
        String strCache = AppPropertiesService.getProperty( PROPERTY_CACHE, "true" );

        if ( strCache.equalsIgnoreCase( "true" ) )
        {
            initCache( NAME );
            CacheService.registerCacheableService( this );
        }
    }

    /**
     * Gets the cache name
     * @return The cache name
     */
    public String getName(  )
    {
        return NAME;
    }

    /**
     * Get from the cache
     * @param strKey The key
     * @return The object or null if not found
     */
    public String get( String strKey )
    {
        return (String) getFromCache( strKey );
    }

    /**
     * Put an object into the cache
     * @param strKey The key
     * @param strContentType The value
     */
    public void put( String strKey, String strContentType )
    {
        putInCache( strKey, strContentType );
    }
}
