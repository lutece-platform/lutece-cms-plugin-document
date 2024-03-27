/*
 * Copyright (c) 2002-2023, City of Paris
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
package fr.paris.lutece.plugins.document.business.workflow;

import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.Locale;

/**
 * This class porvides Data Access methods for DocumentStateDAO objects
 */
public class DocumentStateDAO implements IDocumentStateDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = " SELECT id_state, name_key, description_key FROM document_workflow_state WHERE id_state = ?  ";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_state , name_key , description_key FROM document_workflow_state ORDER BY state_order";

    /**
     * Load the data of Rule from the table
     *
     * @param nDocumentStateId
     *            The document state id
     * @return the instance of the Rule
     */
    public DocumentState load( int nDocumentStateId )
    {
        DocumentState documentState = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nDocumentStateId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                documentState = new DocumentState( );
                documentState.setId( daoUtil.getInt( 1 ) );
                documentState.setNameKey( daoUtil.getString( 2 ) );
                documentState.setDescriptionKey( daoUtil.getString( 3 ) );
            }

            daoUtil.free( );
        }
        return documentState;
    }

    /**
     * Load the list of Document States
     * 
     * @param locale
     *            The locale
     * @return The Reference of the Document States
     */
    public ReferenceList selectDocumentStatesList( Locale locale )
    {
        ReferenceList list = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DocumentState state = new DocumentState( );
                state.setLocale( locale );
                state.setId( daoUtil.getInt( 1 ) );
                state.setNameKey( daoUtil.getString( 2 ) );
                list.addItem( state.getId( ), state.getName( ) );
            }

            daoUtil.free( );
        }
        return list;
    }
}
