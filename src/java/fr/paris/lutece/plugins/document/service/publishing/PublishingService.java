/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentFilter;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.IndexerAction;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublication;
import fr.paris.lutece.plugins.document.business.publication.DocumentPublicationHome;
import fr.paris.lutece.plugins.document.service.DocumentPlugin;
import fr.paris.lutece.plugins.document.service.search.DocumentIndexer;
import fr.paris.lutece.plugins.document.utils.DocumentIndexerUtils;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletType;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 * Publishing service
 */
public class PublishingService
{
    private static PublishingService _singleton = new PublishingService(  );
    private static PublishingEventListenersManager _manager;

    /** Creates a new instance of PublishingService */
    private PublishingService(  )
    {
        _manager = (PublishingEventListenersManager) SpringContextService.getPluginBean( "document",
                "document.publishingEventListenersManager" );
    }

    /**
     * Get the unique instance of the service
     * @return The unique instance
     */
    public static PublishingService getInstance(  )
    {
        return _singleton;
    }

    /**
     * Assign {@link Document} to a {@link Portlet}
     *
     * @param nDocumentId The {@link Document} identifier
     * @param nPortletId The {@link Portlet} identifier
     */
    public void assign( int nDocumentId, int nPortletId )
    {
        DocumentPublication documentPublication = new DocumentPublication(  );
        documentPublication.setPortletId( nPortletId );
        documentPublication.setDocumentId( nDocumentId );
        documentPublication.setStatus( DocumentPublication.STATUS_UNPUBLISHED );

        //FIXME LUTECE-577 : before refactoring, status value was set to null
        documentPublication.setDocumentOrder( DocumentPublication.DOCUMENT_ORDER_DEFAULT_VALUE );
        documentPublication.setDatePublishing( new Date(  ) );
        DocumentPublicationHome.create( documentPublication );
    }

    /**
     * Publishing documents assigned to a portlet at the begin of the list
     *
     * @param nDocumentId the Document id
     * @param nPortletId the portlet identifier
     */
    public void publish( int nDocumentId, int nPortletId )
    {
        // Publishing of document : set status to Published
        DocumentPublication documentPublication = DocumentPublicationHome.findByPrimaryKey( nPortletId, nDocumentId );

        if ( documentPublication != null )
        {
            documentPublication.setStatus( DocumentPublication.STATUS_PUBLISHED );
            documentPublication.setDatePublishing( new Date(  ) );
            documentPublication.setDocumentOrder( getInstance(  ).getMaxDocumentOrderByPortletId( nPortletId ) + 1 );
            DocumentPublicationHome.update( documentPublication );

            PublishingEvent event = new PublishingEvent( nDocumentId, nPortletId, PublishingEvent.DOCUMENT_PUBLISHED );

            _manager.notifyListeners( event );

            getInstance(  )
                .changeDocumentOrder( documentPublication.getDocumentId(  ), documentPublication.getPortletId(  ), 1 );
        }

        String strIdDocument = Integer.toString( nDocumentId );
        IndexationService.addIndexerAction( strIdDocument, DocumentIndexer.INDEXER_NAME,
            IndexerAction.TASK_MODIFY, nPortletId );
        
        DocumentIndexerUtils.addIndexerAction( strIdDocument, IndexerAction.TASK_MODIFY, nPortletId );
    }

    /**
     * UnPublishing documents assigned to a portlet
     *
     * @param nDocumentId the DocumentListPortlet identifier
     * @param nPortletId the portlet identifier
     */
    public void unPublish( int nDocumentId, int nPortletId )
    {
        // Publishing of document : set status to Unpublished
        DocumentPublication documentPublication = DocumentPublicationHome.findByPrimaryKey( nPortletId, nDocumentId );

        // Move the document at the end of the list
        int nNewOrder = getInstance(  ).getMaxDocumentOrderByPortletId( nPortletId );
        getInstance(  ).changeDocumentOrder( nDocumentId, nPortletId, nNewOrder );

        if ( documentPublication != null )
        {
            documentPublication.setStatus( DocumentPublication.STATUS_UNPUBLISHED );
            //FIXME LUTECE-577 : before refactoring, documentOrder value was set to null
            documentPublication.setDocumentOrder( DocumentPublication.DOCUMENT_ORDER_DEFAULT_VALUE );
            DocumentPublicationHome.update( documentPublication );

            PublishingEvent event = new PublishingEvent( nDocumentId, nPortletId, PublishingEvent.DOCUMENT_UNPUBLISHED );
            _manager.notifyListeners( event );
        }

        String strIdDocument = Integer.toString( nDocumentId );
        IndexationService.addIndexerAction( strIdDocument + "_" + DocumentIndexer.SHORT_NAME,
            DocumentIndexer.INDEXER_NAME, IndexerAction.TASK_DELETE, nPortletId );
        
        DocumentIndexerUtils.addIndexerAction( strIdDocument, IndexerAction.TASK_DELETE, nPortletId );
    }

    /**
     * unAssign {@link Document} to a {@link Portlet}
     *
     * @param nDocumentId The {@link Document} identifier
     * @param nPortletId The {@link Portlet} identifier
     */
    public void unAssign( int nDocumentId, int nPortletId )
    {
        DocumentPublicationHome.remove( nPortletId, nDocumentId );
    }

    /**
     * Change the {@link Document} order in a {@link Portlet}
     *
     * @param nDocumentId the {@link Document} identifier
     * @param nPortletId the {@link Portlet} identifier
     * @param nNewOrder The new place in the list
     */
    public void changeDocumentOrder( int nDocumentId, int nPortletId, int nNewOrder )
    {
        DocumentPublication documentPublication = DocumentPublicationHome.findByPrimaryKey( nPortletId, nDocumentId );

        if ( documentPublication == null )
        {
            return;
        }

        Collection<DocumentPublication> listDocumentPublication = DocumentPublicationHome.findByPortletIdAndStatus( nPortletId,
                DocumentPublication.STATUS_PUBLISHED );

        if ( nNewOrder < documentPublication.getDocumentOrder(  ) )
        {
            for ( DocumentPublication documentPublicationToUpdate : listDocumentPublication )
            {
                int nDocumentToUpdateOrder = documentPublicationToUpdate.getDocumentOrder(  );

                if ( ( nDocumentToUpdateOrder >= nNewOrder ) &&
                        ( nDocumentToUpdateOrder < documentPublication.getDocumentOrder(  ) ) )
                {
                    documentPublicationToUpdate.setDocumentOrder( nDocumentToUpdateOrder + 1 );
                    DocumentPublicationHome.update( documentPublicationToUpdate );
                }
            }
        }
        else if ( nNewOrder > documentPublication.getDocumentOrder(  ) )
        {
            for ( DocumentPublication documentPublicationToUpdate : listDocumentPublication )
            {
                int nDocumentToUpdateOrder = documentPublicationToUpdate.getDocumentOrder(  );

                if ( ( nDocumentToUpdateOrder <= nNewOrder ) &&
                        ( nDocumentToUpdateOrder > documentPublication.getDocumentOrder(  ) ) )
                {
                    documentPublicationToUpdate.setDocumentOrder( nDocumentToUpdateOrder - 1 );
                    DocumentPublicationHome.update( documentPublicationToUpdate );
                }
            }
        }

        documentPublication.setDocumentOrder( nNewOrder );
        DocumentPublicationHome.update( documentPublication );
    }

    /**
     * Check if the specified {@link Document} is published into the specified {@link Portlet}
     * @param nDocumentId The {@link Document} identifier
     * @param nPortletId The {@link Portlet} identifier
     * @return True if {@link Document} is published, false else (unpublished or not assigned)
     */
    public boolean isPublished( int nDocumentId, int nPortletId )
    {
        DocumentPublication documentPublication = DocumentPublicationHome.findByPrimaryKey( nPortletId, nDocumentId );

        return ( documentPublication != null ) &&
        ( documentPublication.getStatus(  ) == DocumentPublication.STATUS_PUBLISHED );
    }

    /**
     * Check if the specified {@link Document} is published into the specified {@link Portlet}
     * @param nDocumentId The {@link Document} identifier
     * @param nPortletId The {@link Portlet} identifier
     * @return True if {@link Document} is published, false else (unpublished or not assigned)
     */
    public boolean isPublished( int nDocumentId )
    {
        Collection<DocumentPublication> listDocumentPublication = DocumentPublicationHome.findByDocumentIdAndStatus( nDocumentId,
                DocumentPublication.STATUS_PUBLISHED );

        return ( ( listDocumentPublication.size(  ) > 0 ) );
    }

    /**
     * Check if the specified {@link Document} is assigned (unpublished or published) into at least one {@link Portlet}
     * @param nDocumentId The {@link Document} identifier
     * @return True if {@link Document} is assigned (published or unpublished), false else (not assigned)
     */
    public boolean isAssigned( int nDocumentId )
    {
        Collection<DocumentPublication> listDocumentPublication = DocumentPublicationHome.findByDocumentId( nDocumentId );

        return ( listDocumentPublication.size(  ) > 0 );
    }

    /**
     * Check if the specified {@link Document} is assigned (unpublished or published) into the specified {@link Portlet}
     * @param nDocumentId The {@link Document} identifier
     * @param nPortletId The {@link Portlet} identifier
     * @return True if {@link Document} is assigned (published or unpublished), false else (not assigned)
     */
    public boolean isAssigned( int nDocumentId, int nPortletId )
    {
        DocumentPublication documentPublication = DocumentPublicationHome.findByPrimaryKey( nPortletId, nDocumentId );

        return ( documentPublication != null );
    }

    /**
     * Return a {@link DocumentPublication} from a {@link Portlet} identifier and {@link Document} identifier
     * @param nPortletId the {@link Portlet} identifier
     * @param nDocumentId the {@link Document} identifier
     * @return a {@link DocumentPublication} or null if no object match
     */
    public DocumentPublication getDocumentPublication( int nPortletId, int nDocumentId )
    {
        return DocumentPublicationHome.findByPrimaryKey( nPortletId, nDocumentId );
    }

    /**
     * Returns a {@link Collection} of {@link Document} objects from a {@link Portlet} identifier
     * {@link Document} can be assigned or published.
     * @param nPortletId the {@link Portlet} identifier
     * @return a {@link Collection} of {@link Document} objects
     */
    public Collection<Document> getAssignedDocumentsByPortletId( int nPortletId )
    {
        Collection<DocumentPublication> listDocumentPublications = DocumentPublicationHome.findByPortletId( nPortletId );
        Collection<Document> listDocuments = new ArrayList<Document>(  );

        for ( DocumentPublication documentPublication : listDocumentPublications )
        {
            //FIXME LUTECE-577 : use a single call to DocumentHome, but be careful to the list order !
            listDocuments.add( DocumentHome.findByPrimaryKeyWithoutBinaries( documentPublication.getDocumentId(  ) ) );
        }

        //FIXME LUTECE-577 : the list was order by document_order ASC
        return listDocuments;
    }

    /**
     * Loads the list of the documents whose type is the same as the one specified in parameter
     * Return published documents from a specified portlet
     *
     * @param nPortletId the portlet  identifier
     * @return the list of the document in form of a List
     */
    public Collection<Document> getPublishedDocumentsByPortletId( int nPortletId )
    {
        Collection<DocumentPublication> listDocumentPublication = DocumentPublicationHome.findByPortletIdAndStatus( nPortletId,
                DocumentPublication.STATUS_PUBLISHED );
        Collection<Document> listDocuments = new ArrayList<Document>(  );

        for ( DocumentPublication documentPublication : listDocumentPublication )
        {
            listDocuments.add( DocumentHome.findByPrimaryKeyWithoutBinaries( documentPublication.getDocumentId(  ) ) );
        }

        return listDocuments;
    }

    /**
     * Loads the list of the documents whose filter and date publication is specified
     * Return published documents since the publication date. The is also filtered with the documentFilter
     *
     * @param datePublishing The start publication date
     * @param documentFilter The filter for the published documents. The filter can be null or empty. The array of Ids will not be taked in account.
     * @param locale The locale is used to get the list of documents with the findByFilter method
     * @return the list of the document in form of a List. return null if datePublishing is null
     */
    public Collection<Document> getPublishedDocumentsSinceDate( Date datePublishing, DocumentFilter documentFilter,
        Locale locale )
    {
        if ( datePublishing == null )
        {
            return null;
        }

        Collection<DocumentPublication> listDocumentPublication = DocumentPublicationHome.findSinceDatePublishingAndStatus( datePublishing,
                DocumentPublication.STATUS_PUBLISHED );
        int[] arrayIds = new int[listDocumentPublication.size(  )];
        int i = 0;
        DocumentFilter publishedDocumentFilter = documentFilter;

        if ( publishedDocumentFilter == null )
        {
            publishedDocumentFilter = new DocumentFilter(  );
        }

        for ( DocumentPublication documentPublication : listDocumentPublication )
        {
            arrayIds[i++] = documentPublication.getDocumentId(  );
        }

        publishedDocumentFilter.setIds( arrayIds );

        Collection<Document> listDocuments = DocumentHome.findByFilter( publishedDocumentFilter, locale );

        return listDocuments;
    }

    /**
     * Loads the list of the portlets whoes contain Document specified by id
     *
     * @param strDocumentId the document identifier
     * @return the {@link Collection} of the portlets
     */
    public Collection<Portlet> getPortletsByDocumentId( String strDocumentId )
    {
        Collection<DocumentPublication> listDocumentPublication = DocumentPublicationHome.findByDocumentId( Integer.parseInt( 
                    strDocumentId ) );
        Collection<Portlet> listPortlets = new ArrayList<Portlet>(  );

        for ( DocumentPublication documentPublication : listDocumentPublication )
        {
            listPortlets.add( PortletHome.findByPrimaryKey( documentPublication.getPortletId(  ) ) );
        }

        return listPortlets;
    }

    /**
     * Loads the list of portlets who contain published documents
     *
     * @return the {@link Collection} of the portlets
     */
    public Collection<Portlet> getPublishedPortlets(  )
    {
        Plugin plugin = PluginService.getPlugin( DocumentPlugin.PLUGIN_NAME );
        Collection<Portlet> listPortletsAll = new ArrayList<Portlet>(  );
        Collection<Portlet> listPortlets = new ArrayList<Portlet>(  );

        for ( PortletType portletType : plugin.getPortletTypes(  ) )
        {
            listPortletsAll.addAll( PortletHome.findByType( portletType.getId(  ) ) );
        }

        for ( Portlet portlet : listPortletsAll )
        {
            if ( ( DocumentPublicationHome.findByPortletId( portlet.getId(  ) ).size(  ) > 0 ) &&
                    ( portlet.getStatus(  ) == Portlet.STATUS_PUBLISHED ) )
            {
                listPortlets.add( portlet );
            }
        }

        return listPortlets;
    }

    /**
     * Get the max document order from a {@link Portlet} id
     * @param nPortletId the {@link Portlet} identifer
     * @return The max document order
     */
    public int getMaxDocumentOrderByPortletId( int nPortletId )
    {
        return DocumentPublicationHome.findMaxDocumentOrderByPortletId( nPortletId );
    }
}
