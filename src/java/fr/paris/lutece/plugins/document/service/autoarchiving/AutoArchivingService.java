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
package fr.paris.lutece.plugins.document.service.autoarchiving;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;


/**
 *
 * AutoArchiving Service
 */
@ApplicationScoped
@Named( "document.AutoArchivingService" )
public class AutoArchivingService
{

	@Inject
	private DocumentListPortletHome _documentListPortletHome;
	
	/**
     * Returns the unique instance of the {@link AutoArchivingService} service.
     * 
     * <p>This method is deprecated and is provided for backward compatibility only. 
     * For new code, use dependency injection with {@code @Inject} to obtain the 
     * {@link AutoArchivingService} instance instead.</p>
     * 
     * @return The unique instance of {@link AutoArchivingService}.
     * 
     * @deprecated Use {@code @Inject} to obtain the {@link AutoArchivingService} 
     * instance. This method will be removed in future versions.
     */
    @Deprecated( since = "8.0", forRemoval = true )
    public static AutoArchivingService getInstance(  )
    {
    	return CDI.current( ).select( AutoArchivingService.class ).get( );
    }

    /**
     * Initialize the {@link AutoArchivingService} service
     *
     */
    public void init(  )
    {
    }

    /**
     * Execute the auto archiving process
     * @return The logs
     */
    public String processAutoArchiving(  )
    {
        StringBuffer sbLogs = new StringBuffer(  );

        sbLogs.append( "\r\n[Start] Starting Auto archiving daemon...\r\n" );

        long lDuration = System.currentTimeMillis(  );

        for ( Portlet portlet : PortletHome.findByType( _documentListPortletHome.getPortletTypeId(  ) ) )
        {
            for ( Document document :  CDI.current( ).select( PublishingService.class ).get( )
                                                       .getPublishedDocumentsByPortletId( portlet.getId(  ) ) )
            {
                if ( document.isOutOfDate(  ) )
                {
                    sbLogs.append( "\r\nArchiving Document " + document.getId(  ) + " : '" + document.getTitle(  ) +
                        "'...\r\n" );

                    try
                    {
                    	CDI.current( ).select( DocumentService.class ).get( ).archiveDocument( document, null, DocumentState.STATE_ARCHIVED );
                    }
                    catch ( DocumentException e )
                    {
                        AppLogService.error( "[AutoArchivingDaemon] Error when archiving document (id:" +
                            document.getId(  ) + "). " + e.getMessage(  ) );
                    }
                }
            }
        }

        sbLogs.append( "\r\n[End] Duration : " + ( System.currentTimeMillis(  ) - lDuration ) + " milliseconds\r\n" );

        return sbLogs.toString(  );
    }
}
