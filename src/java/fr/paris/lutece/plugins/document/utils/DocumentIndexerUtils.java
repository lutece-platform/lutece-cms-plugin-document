package fr.paris.lutece.plugins.document.utils;

import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.event.ResourceEventManager;

/**
 * 
 * DocumentIndexerUtils
 *
 */
public class DocumentIndexerUtils
{
	// Indexed resource type name
	public static final String CONSTANT_TYPE_RESOURCE = "DOCUMENT_DOCUMENT";
	
	/**
     * Warn that a action has been done.
     * @param strIdResource the document id
     * @param nIdTask the key of the action to do
     * @param nIdPortlet the portlet key of the document
     */
    public static void addIndexerAction( String strIdResource, int nIdTask, int nIdPortlet )
    {
        ResourceEvent event = new ResourceEvent();
        event.setIdResource( String.valueOf( strIdResource ) );
        event.setTypeResource( CONSTANT_TYPE_RESOURCE );
        event.setIdPortlet( nIdPortlet );
        switch (nIdTask)
        {
        case IndexerAction.TASK_CREATE:
        	ResourceEventManager.fireAddedResource( event );
        	break;
        case IndexerAction.TASK_MODIFY:
        	ResourceEventManager.fireUpdatedResource( event );
        	break;
        case IndexerAction.TASK_DELETE:
        	ResourceEventManager.fireDeletedResource( event );
        	break;
        default:
        	break;
        }
    }
}
