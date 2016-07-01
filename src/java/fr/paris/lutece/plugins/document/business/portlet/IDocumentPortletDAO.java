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
package fr.paris.lutece.plugins.document.business.portlet;

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.util.ReferenceItem;

import java.util.Collection;
import java.util.List;


/**
 * Interface for document portlet DAO
 */
public interface IDocumentPortletDAO extends IPortletInterfaceDAO
{
    /**
     * Returns a list of couple id_portlet/name filtered by documentType and
     * category
     * @param nDocumentId the Document ID
     * @param strCodeDocumentType the code
     * @param pOrder the order of the portlets
     * @param pFilter The portlet filter
     * @return A collection of referenceItem
     */
    Collection<ReferenceItem> selectByDocumentOdAndDocumentType( int nDocumentId, String strCodeDocumentType,
        PortletOrder pOrder, PortletFilter pFilter );

    /**
     * Tests if is a portlet is portlet type alias
     *
     * @param nPortletId The identifier of the document
     * @return true if the portlet is alias, false otherwise
     */
    boolean checkIsAliasPortlet( int nPortletId );

    /**
     * Find all portlets that contain the document
     * @param nDocumentId the document id
     * @return all portlets id that contain the document
     */
    List<Integer> selectPortletsByDocumentId( int nDocumentId );
}
