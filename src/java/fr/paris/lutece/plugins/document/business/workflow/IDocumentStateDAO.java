/**
 *
 */
package fr.paris.lutece.plugins.document.business.workflow;

import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;


/**
 * This class porvides Data Access methods for DocumentStateDAO interface
 */
public interface IDocumentStateDAO
{
    /**
     * Load the data of Rule from the table
     *
     * @return the instance of the Rule
     * @param nDocumentStateId
     */
    public abstract DocumentState load( int nDocumentStateId );

    /**
     * Load the list of Document States
     * @return The Reference of the Document States
     * @param locale
     */
    public abstract ReferenceList selectDocumentStatesList( Locale locale );
}
