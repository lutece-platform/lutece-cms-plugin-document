/**
 *
 */
package fr.paris.lutece.plugins.document.business.workflow;

import fr.paris.lutece.plugins.document.business.Document;

import java.util.List;


/**
 * This class porvides Data Access methods for DocumentActionDAO  interface
 */
public interface IDocumentActionDAO
{
    /**
    * Load the data of Document Action from the table
    * @param nIdAction The identifier of the action
    * @return the instance of action
    */
    DocumentAction load( int nIdAction );

    /**
    * Load the list of actions for a document
    *
    * @return The Collection of actions
    * @param document The document to add available actions
    */
    List<DocumentAction> selectActions( Document document );
}
