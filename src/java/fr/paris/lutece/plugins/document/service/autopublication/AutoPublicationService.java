/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.document.service.autopublication;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentFilter;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.autopublication.DocumentAutoPublication;
import fr.paris.lutece.plugins.document.business.autopublication.DocumentAutoPublicationHome;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortlet;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome;
import fr.paris.lutece.plugins.document.business.portlet.DocumentPortlet;
import fr.paris.lutece.plugins.document.business.portlet.DocumentPortletHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;

import java.util.ArrayList;
import java.util.Collection;


/**
 *
 * AutoPublication Service
 */
public class AutoPublicationService
{
    private static AutoPublicationService _singleton;

    /**
     * Get the auto publication service instance
     * @return The instance of the auto publication service
     */
    public static AutoPublicationService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new AutoPublicationService(  );
        }

        return _singleton;
    }

    /**
     * Initialize the {@link DocumentAutoPublication} service
     *
     */
    public void init(  )
    {
        DocumentAutoPublication.init(  );
    }

    /**
     * Process auto publication
     * @return The log of the process
     */
    public String processAutoPublishing(  )
    {
        StringBuffer sbLogs = new StringBuffer(  );

        sbLogs.append( "\r\n[Start] Starting Auto publication daemon...\r\n" );

        long lDuration = System.currentTimeMillis(  );

        for ( DocumentAutoPublication documentAutoPublication : DocumentAutoPublicationHome.findAll(  ) )
        {
            for ( Document document : findPublishableDocumentsList( documentAutoPublication.getIdPortlet(  ),
                    documentAutoPublication.getIdSpace(  ) ) )
            {
                sbLogs.append( "\r\nPublishing Document " + document.getId(  ) + " : '" + document.getTitle(  ) +
                    "'...\r\n" );
                PublishingService.getInstance(  ).assign( document.getId(  ), documentAutoPublication.getIdPortlet(  ) );
                PublishingService.getInstance(  ).publish( document.getId(  ), documentAutoPublication.getIdPortlet(  ) );
                PublishingService.getInstance(  )
                                 .changeDocumentOrder( document.getId(  ), documentAutoPublication.getIdPortlet(  ), 1 ); //Set new published document at the first place 
            }
        }

        sbLogs.append( "\r\n[End] Duration : " + ( System.currentTimeMillis(  ) - lDuration ) + " milliseconds\r\n" );

        return sbLogs.toString(  );
    }

    /**
     * Find the list of documents publishable for the specified portlet
     * The list is filtered :
     * <ul>
     * <li>by space</li>
     * <li>by state</li>
     * <li>by portlet code document type</li>
     * <li>by portlet categories</li>
     * <li>by validity period</li>
     * </ul>
     *
     * @param nPortletId The portlet Id
     * @param nSpaceId The space Id
     * @return A Collection of documents
     */
    private Collection<Document> findPublishableDocumentsList( int nPortletId, int nSpaceId )
    {
        Collection<Document> listPublishableDocuments = new ArrayList<Document>(  );
        Portlet portlet = PortletHome.findByPrimaryKey( nPortletId );
        String documentTypeCode = null;
        int[] arrayCategories = null;

        if ( DocumentListPortletHome.getInstance(  ).getPortletTypeId(  ).equals( portlet.getPortletTypeId(  ) ) ) //equivalent to : if(portlet instanceof DocumentPortlet)
        {
            DocumentListPortlet documentListPortlet = (DocumentListPortlet) portlet;
            documentTypeCode = documentListPortlet.getDocumentTypeCode(  );
            arrayCategories = documentListPortlet.getIdCategory(  );
        }

        if ( DocumentPortletHome.getInstance(  ).getPortletTypeId(  ).equals( portlet.getPortletTypeId(  ) ) )
        {
            DocumentPortlet documentPortlet = (DocumentPortlet) portlet;
            documentTypeCode = documentPortlet.getDocumentTypeCode(  );
            arrayCategories = documentPortlet.getIdCategory(  );
        }

        if ( ( documentTypeCode == null ) && ( arrayCategories == null ) ) //Error : auto publication mapped with a non document portlet !
        {
            return listPublishableDocuments;
        }

        DocumentFilter documentFilter = new DocumentFilter(  );
        documentFilter.setIdSpace( nSpaceId ); //Filter by Space
        documentFilter.setIdState( DocumentState.STATE_VALIDATE ); // Filter by state
        documentFilter.setCodeDocumentType( documentTypeCode ); //Filter by document type code
        documentFilter.setCategoriesId( arrayCategories );

        for ( Document document : DocumentHome.findByFilter( documentFilter, null ) )
        {
            if ( document.isValid(  ) &&
                    !PublishingService.getInstance(  ).isPublished( document.getId(  ), nPortletId ) ) //isValid = Check the publication period
            {
                listPublishableDocuments.add( document );
            }
        }

        return listPublishableDocuments;
    }

    /**
     * Returns count of published documents of a portlet and space
     *
     * @param nPortletId the identifier of the portlet
     * @param nSpaceId the identifier of the space
     * @return number of documents
     */
    public static int findCountByPortletAndSpace( int nPortletId, int nSpaceId )
    {
        Collection<Document> listDocuments = PublishingService.getInstance(  )
                                                              .getPublishedDocumentsByPortletId( nPortletId );
        int nCount = 0;

        for ( Document document : listDocuments )
        {
            if ( document.getSpaceId(  ) == nSpaceId )
            {
                nCount++;
            }
        }

        return nCount;
    }
}
