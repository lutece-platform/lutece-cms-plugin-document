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
package fr.paris.lutece.plugins.document.service.history;

import fr.paris.lutece.plugins.document.business.history.HistoryEvent;
import fr.paris.lutece.plugins.document.business.history.HistoryEventHome;
import fr.paris.lutece.plugins.document.service.DocumentEvent;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;

import java.sql.Timestamp;

/**
 * This service logs document changes history
 */
@ApplicationScoped
@Named( "document.DocumentHistoryService" )
public class DocumentHistoryService
{
	private static final String PROPERTY_MESSAGE_DOCUMENT_CREATED = "document.history.message.documentCreated";
	private static final String PROPERTY_MESSAGE_DOCUMENT_STATE_MODIFIED = "document.history.message.documentStateModified";
	private static final String PROPERTY_MESSAGE_DOCUMENT_MODIFIED = "document.history.message.documentModified";
	private static final String PROPERTY_MESSAGE_DOCUMENT_MOVED = "document.history.message.documentMoved";

	/** Creates a new instance of WorkflowEngine */
	public DocumentHistoryService( )
	{
	}

	/**
	 * Returns the unique instance of the {@link DocumentHistoryService} service.
	 * 
	 * <p>
	 * This method is deprecated and is provided for backward compatibility only.
	 * For new code, use dependency injection with {@code @Inject} to obtain the
	 * {@link DocumentHistoryService} instance instead.
	 * </p>
	 * 
	 * @return The unique instance of {@link DocumentHistoryService}.
	 * 
	 * @deprecated Use {@code @Inject} to obtain the {@link DocumentHistoryService}
	 *             instance. This method will be removed in future versions.
	 */
	@Deprecated( since = "8.0", forRemoval = true )
	public static DocumentHistoryService getInstance( )
	{
		return CDI.current( ).select( DocumentHistoryService.class ).get( );
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
	 * Process a document event
	 * 
	 * @param event The event to process
	 * @throws DocumentException Exception occurs when error in event or rule
	 */
	public void processDocumentEvent( DocumentEvent event )
			throws DocumentException
	{
		try
		{
			HistoryEvent historyEvent = new HistoryEvent( );
			historyEvent.setDate( new Timestamp( new java.util.Date( ).getTime( ) ) );

			String strUser = ( event.getUser( ) != null ) ? event.getUser( ).getAccessCode( ) : ""; // TODO : reconsider
			historyEvent.setEventUser( strUser );
			historyEvent.setIdDocument( event.getDocument( ).getId( ) );
			historyEvent.setDocumentStateKey( event.getDocument( ).getStateKey( ) );
			historyEvent.setSpace( event.getDocument( ).getSpace( ) );

			String strMessageKey;

			switch( event.getEventType( ) )
			{
				case DocumentEvent.DOCUMENT_CREATED :
					strMessageKey = PROPERTY_MESSAGE_DOCUMENT_CREATED;

					break;

				case DocumentEvent.DOCUMENT_CONTENT_MODIFIED :
					strMessageKey = PROPERTY_MESSAGE_DOCUMENT_MODIFIED;

					break;

				case DocumentEvent.DOCUMENT_STATE_CHANGED :
					strMessageKey = PROPERTY_MESSAGE_DOCUMENT_STATE_MODIFIED;

					break;

				case DocumentEvent.DOCUMENT_MOVED :
					strMessageKey = PROPERTY_MESSAGE_DOCUMENT_MOVED;

					break;

				default :
					strMessageKey = PROPERTY_MESSAGE_DOCUMENT_MODIFIED;

					break;
			}

			historyEvent.setEventMessageKey( strMessageKey );
			HistoryEventHome.create( historyEvent );
		}
		catch( Exception e )
		{
			AppLogService.error( "Error in History event : {}", e.getMessage( ), e );
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
