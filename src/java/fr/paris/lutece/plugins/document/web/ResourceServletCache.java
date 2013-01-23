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
package fr.paris.lutece.plugins.document.web;

import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import net.sf.ehcache.Cache;


/**
 * ResourceServletCache
 */
public class ResourceServletCache extends AbstractCacheableService
{
    private static final String NAME = "Document ResourceServlet Cache";

    public ResourceServletCache(  )
    {
        initCache( getName(  ) );
    }

    /**
     * Gets the cache name
     * @return The cache name
     */
    public final String getName(  )
    {
        return NAME;
    }

    /**
     * Get from the cache
     * @param strKey The key
     * @return The object or null if not found
     */
    public ResourceValueObject get( String strKey )
    {
        return (ResourceValueObject) getFromCache( strKey );
    }

    /**
     * Put an object into the cache
     * @param strKey The key
     * @param resource The value
     */
    public void put( String strKey, ResourceValueObject resource )
    {
        putInCache( strKey, resource );
    }

    /**
     * Remove keys from a key pattern
     * @param strKeyPattern The key pattern
     */
    void removeFromKeyPattern(String strKeyPattern)
    {
        Cache cache = getCache();
        for( String strKey : getKeys() )
        {
            if( strKey.contains(strKeyPattern ))
            {
                cache.remove(strKey);
            }
        }
    }
}
