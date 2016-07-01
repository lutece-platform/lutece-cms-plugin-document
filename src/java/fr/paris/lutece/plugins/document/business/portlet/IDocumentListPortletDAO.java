/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

/*
 * IDocumentListPortletDAO.java
 *
 * Created on 10 octobre 2006, 15:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.document.business.portlet;

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.ReferenceItem;

import java.util.Collection;


/**
 *
 */
public interface IDocumentListPortletDAO extends IPortletInterfaceDAO
{
    /**
     * Deletes records for a portlet identifier in the tables portlet_articles_list, published_article_portlet,
     * auto_publishing
     *
     *
     * @param nPortletId the portlet identifier
     */
    void delete( int nPortletId );

    /**
     * Insert a new record in the table portlet_articles_list
     *
     *
     * @param portlet the instance of the Portlet object to insert
     */
    void insert( Portlet portlet );

    /**
     * Loads the data of Document List Portlet whose identifier is specified in parameter
     *
     *
     * @param nPortletId The Portlet identifier
     * @return theDocumentListPortlet object
     */
    Portlet load( int nPortletId );

    /**
     * Load the list of documentTypes
     * @param nDocumentId the document ID
     * @param strCodeDocumentType The code
     * @param pOrder order of the portlets
     * @param pFilter The portlet filter
     * @return The Collection of the ReferenceItem
     */
    Collection<ReferenceItem> selectByDocumentIdAndDocumentType( int nDocumentId, String strCodeDocumentType,
        PortletOrder pOrder, PortletFilter pFilter );

    /**
     * Update the record in the table
     *
     *
     * @param portlet A portlet
     */
    void store( Portlet portlet );

    /**
     * Tests if is a portlet is portlet type alias
     *
     * @param nPortletId The identifier of the document
     * @return true if the portlet is alias, false otherwise
     */
    boolean checkIsAliasPortlet( int nPortletId );
}
