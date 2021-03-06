/**
 *
 */
package fr.paris.lutece.plugins.document.business.workflow;

import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;


/**
 * This class provides Data Access methods for DocumentStateDAO interface
 */
public interface IDocumentStateDAO
{
    /**
     * Load the data of Rule from the table
     *
     * @return the instance of the Rule
     * @param nDocumentStateId
     */
    DocumentState load( int nDocumentStateId );

    /**
     * Load the list of Document States
     * @return The Reference of the Document States
     * @param locale
     */
    ReferenceList selectDocumentStatesList( Locale locale );
}
