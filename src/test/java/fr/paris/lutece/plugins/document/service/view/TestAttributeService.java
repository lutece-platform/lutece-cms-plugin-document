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
package fr.paris.lutece.plugins.document.service.view;

import fr.paris.lutece.plugins.document.service.AttributeManager;
import fr.paris.lutece.plugins.document.service.AttributeService;
import fr.paris.lutece.plugins.document.service.attributes.DateManager;
import fr.paris.lutece.plugins.document.service.attributes.FileManager;
import fr.paris.lutece.plugins.document.service.attributes.ImageFileManager;
import fr.paris.lutece.plugins.document.service.attributes.InternalLinkManager;
import fr.paris.lutece.plugins.document.service.attributes.MultilineManager;
import fr.paris.lutece.plugins.document.service.attributes.NumerictextManager;
import fr.paris.lutece.plugins.document.service.attributes.RichTextManager;
import fr.paris.lutece.plugins.document.service.attributes.TextManager;
import fr.paris.lutece.plugins.document.service.attributes.UrlManager;

import jakarta.enterprise.inject.Vetoed;

import java.util.HashMap;
import java.util.Map;

/**
 * Attribute service backed by the attribute managers of the plugin, without CDI nor database.
 * Vetoed: it must not replace the real service in the CDI container of the tests.
 */
@Vetoed
class TestAttributeService extends AttributeService
{
    private final Map<String, AttributeManager> _mapManagers = new HashMap<>( );

    /**
     * Registers the attribute managers of the plugin
     */
    TestAttributeService( )
    {
        _mapManagers.put( "text", new TextManager( ) );
        _mapManagers.put( "multiline", new MultilineManager( ) );
        _mapManagers.put( "richtext", new RichTextManager( ) );
        _mapManagers.put( "url", new UrlManager( ) );
        _mapManagers.put( "internallink", new InternalLinkManager( ) );
        _mapManagers.put( "date", new DateManager( ) );
        _mapManagers.put( "numerictext", new NumerictextManager( ) );
        _mapManagers.put( "file", new FileManager( ) );
        _mapManagers.put( "image", new ImageFileManager( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeManager getManager( String strAttributeType )
    {
        return _mapManagers.get( strAttributeType );
    }
}
