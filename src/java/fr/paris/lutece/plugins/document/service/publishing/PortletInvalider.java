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
package fr.paris.lutece.plugins.document.service.publishing;

import fr.paris.lutece.plugins.document.business.portlet.DocumentPortletHome;
import fr.paris.lutece.plugins.document.service.DocumentEvent;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;

import java.util.List;

/**
 * PortletInvalider
 */
@ApplicationScoped
@Named( "document.PortletInvalider" )
public class PortletInvalider
{
	
    /**
     * Observe publishing events via CDI
     * @param event The publishing event
     */
    public void onPublishingEvent(@Observes PublishingEvent event)
    {
        processPublishingEvent(event);
    }
    
    /**
     * Observe document events via CDI
     * @param event The document event
     * @throws DocumentException if error occurs
     */
    public void onDocumentEvent(@Observes DocumentEvent event) throws DocumentException
    {
        processDocumentEvent(event);
    }
    
	/**
	 * Invalidate portlet on publishing events
	 * 
	 * @param event The event to process
	 */
	public void processPublishingEvent( PublishingEvent event )
	{
		int nPortletId = event.getPortletId( );
		PortletHome.invalidate( nPortletId );
	}

	/**
	 *
	 * {@inheritDoc}
	 */
	public void processDocumentEvent( DocumentEvent event )
			throws DocumentException
	{
		List < Integer > listPortletIds = DocumentPortletHome.findPortletForDocument( event.getDocument( ).getId( ) );

		// invalidating all portlets
		for( int nPortletId : listPortletIds )
		{
			PortletHome.invalidate( nPortletId );
		}
	}

	/**
	 * This method observes the initialization of the {@link ApplicationScoped}
	 * context.
	 * It ensures that this CDI beans are instantiated at the application startup.
	 *
	 * <p>
	 * This method is triggered automatically by CDI when the
	 * {@link ApplicationScoped} context is initialized,
	 * which typically occurs during the startup of the application server.
	 * </p>
	 *
	 * @param context the {@link ServletContext} that is initialized. This parameter
	 *                is observed
	 *                and injected automatically by CDI when the
	 *                {@link ApplicationScoped} context is initialized.
	 */
	public void initializedService( @Observes @Initialized( ApplicationScoped.class ) ServletContext context )
	{
		// This method is intentionally left empty to trigger CDI bean instantiation
	}
}
