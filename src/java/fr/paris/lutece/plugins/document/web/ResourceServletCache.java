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
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.servlet.ServletContext;

/**
 * Cache of the validated files served by the {@link DocumentResourceServlet}. The files of a document are removed from the cache on each event
 * of this document.
 */
@ApplicationScoped
public class ResourceServletCache extends AbstractCacheableService<String, ResourceValueObject>
{
    private static final String NAME = "Document ResourceServlet Cache";
    private static final String KEY_DOC_BEGIN = "[doc:";
    private static final String KEY_ATTR_BEGIN = "[attr:";
    private static final String KEY_ITEM_CLOSE = "]";

    /**
     * Creates the cache
     */
    @PostConstruct
    void init( )
    {
        initCache( getName( ) );
    }

    /**
     * Gets the cache name
     *
     * @return The cache name
     */
    @Override
    public String getName( )
    {
        return NAME;
    }

    /**
     * Returns the cache key of a file
     *
     * @param nDocumentId
     *            The document identifier
     * @param nAttributeId
     *            The attribute identifier
     * @return The cache key
     */
    public static String getCacheKey( int nDocumentId, int nAttributeId )
    {
        return KEY_DOC_BEGIN + nDocumentId + KEY_ITEM_CLOSE + KEY_ATTR_BEGIN + nAttributeId + KEY_ITEM_CLOSE;
    }

    /**
     * Puts a file of the validated content in the cache, with the modification date of its document. Nothing is done when the cache is
     * disabled.
     *
     * @param resource
     *            The file
     * @param nDocumentId
     *            The document identifier
     * @param nAttributeId
     *            The attribute identifier
     */
    public void putValidatedResource( ResourceValueObject resource, int nDocumentId, int nAttributeId )
    {
        if ( !isCacheEnable( ) )
        {
            return;
        }

        Document document = DocumentHome.loadLastModifiedAttributes( nDocumentId );

        if ( document != null && document.getDateModification( ) != null )
        {
            resource.setLastModified( document.getDateModification( ).getTime( ) );
        }

        put( getCacheKey( nDocumentId, nAttributeId ), resource );
    }

    /**
     * Loads a file of the validated content and puts it in the cache. Nothing is done when the cache is disabled or the file does not exist.
     *
     * @param nDocumentId
     *            The document identifier
     * @param nAttributeId
     *            The attribute identifier
     */
    public void cacheValidatedResource( int nDocumentId, int nAttributeId )
    {
        if ( isCacheEnable( ) )
        {
            DocumentResource resource = DocumentHome.getValidatedResource( nDocumentId, nAttributeId );

            if ( resource != null )
            {
                putValidatedResource( new ResourceValueObject( resource ), nDocumentId, nAttributeId );
            }
        }
    }

    /**
     * Removes the files of a document from the cache when the document changes
     *
     * @param event
     *            The document event
     */
    public void onDocumentEvent( @Observes DocumentEvent event )
    {
        String strKeyPattern = KEY_DOC_BEGIN + event.getDocument( ).getId( ) + KEY_ITEM_CLOSE;

        for ( String strKey : getKeys( ) )
        {
            if ( strKey.contains( strKeyPattern ) )
            {
                remove( strKey );
            }
        }
    }

    /**
     * Creates the cache at the application startup, so that it is listed in the cache management page
     *
     * @param context
     *            The servlet context
     */
    public void initializedService( @Observes @Initialized( ApplicationScoped.class ) ServletContext context )
    {
    }
}
