package fr.paris.lutece.plugins.document.service.spaces;

import fr.paris.lutece.plugins.document.business.autopublication.DocumentAutoPublicationDocumentSpaceRemovalListener;
import fr.paris.lutece.plugins.document.modules.rulemovespace.business.MoveSpaceSpaceRemovalListener;
import fr.paris.lutece.plugins.document.modules.rulenotifyusers.business.NotifyUsersSpaceRemovalListener;
import fr.paris.lutece.portal.service.util.RemovalListenerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

/**
 * CDI Producer for the space removal listener service.
 * Expose RemovalListenerService as a bean named "document.spaceRemovalService"
 * for CDI injection.
 */
@ApplicationScoped
public class RemovalListenerServiceProducer
{
	@Produces
	@ApplicationScoped
	@Named( "document.spaceRemovalService" )
	public RemovalListenerService spaceRemovalServiceProducer( )
	{
		RemovalListenerService service= new RemovalListenerService( );
		service.registerListener( new DocumentAutoPublicationDocumentSpaceRemovalListener( ) );
		service.registerListener( new NotifyUsersSpaceRemovalListener( ) );
		service.registerListener( new MoveSpaceSpaceRemovalListener( ) );
        return service;
	}
}
