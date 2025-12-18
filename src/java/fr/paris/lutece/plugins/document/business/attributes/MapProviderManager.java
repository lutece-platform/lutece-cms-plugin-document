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
package fr.paris.lutece.plugins.document.business.attributes;

import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.inject.spi.CDI;
import java.util.stream.Collectors;
import java.util.List;

/**
 *
 * MapProviderManager : manages all map providers register. <br />
 * Map providers are discovered and injected using Jakarta CDI. Such providers implement {@link IMapProvider} and must be declared as CDI beans
 * (e.g. using {@code @ApplicationScoped}, {@code @Dependent}, etc.).
 * 
 * @see IMapProvider
 *
 */
public final class MapProviderManager
{
    /**
     * Empty constructor
     */
    private MapProviderManager( )
    {
        // nothing
    }

    /**
     * Gets the mapProvider for the provided key.
     * 
     * @param strKey
     *            the key
     * @return <code>null</code> if <code>strKey</code> is blank, the map provider if found, <code>null</code> otherwise.
     * @see StringUtils#isBlank(String)
     */
    public static IMapProvider getMapProvider( String strKey )
    {
        if ( StringUtils.isBlank( strKey ) )
        {
            return null;
        }

        for ( IMapProvider mapProvider : getMapProvidersList( ) )
        {
            if ( strKey.equals( mapProvider.getKey( ) ) )
            {
                return mapProvider;
            }
        }

        AppLogService.info( "{} : No map provider found for key {}", MapProviderManager.class.getName( ), strKey );

        return null;
    }

    /**
     * Builds all available providers list
     * 
     * @return all available providers
     */
    public static List<IMapProvider> getMapProvidersList( )
    {
        return CDI.current( ).select( IMapProvider.class ).stream() .collect( Collectors.toList( ) );
    }
}
