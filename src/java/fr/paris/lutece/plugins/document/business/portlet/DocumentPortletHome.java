/*
 * Copyright (c) 2002-2025, City of Paris
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
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.util.ReferenceItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;

import java.util.Collection;
import java.util.List;

@ApplicationScoped
@Named("document.DocumentPortletHome")
public class DocumentPortletHome extends PortletHome
{
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    // Static variable pointed at the DAO instance
    private static IDocumentPortletDAO _dao =  CDI.current( ).select( IDocumentPortletDAO.class ).get( );

    /**
     * Constructor
     */
    public DocumentPortletHome( )
    {
    }

    /**
     * Returns the instance of DocumentPortletHome
     *
     * @return the DocumentPortletHome instance
     */
    @Deprecated( since = "8.0", forRemoval = true )
    public static PortletHome getInstance( )
    {
        return CDI.current( ).select( DocumentPortletHome.class ).get( );
    }

    /**
     * Returns the identifier of the portlet type
     *
     * @return the portlet type identifier
     */
    @Override
    public String getPortletTypeId( )
    {
        String strCurrentClassName = this.getClass( ).getName( );
        String strPortletTypeId = PortletTypeHome.getPortletTypeId( strCurrentClassName );

        return strPortletTypeId;
    }

    /**
     * Returns the instance of the portlet DAO singleton
     *
     * @return the instance of the DAO singleton
     */
    @Override
    public IPortletInterfaceDAO getDAO( )
    {
        return _dao;
    }

    /**
     * Returns a list of couple id_portlet/name filtered by documentType and category
     * 
     * @param nDocumentId
     *            the Document ID
     * @param strCodeDocumentType
     *            the code
     * @param pOrder
     *            the order of the portlets
     * @param pFilter
     *            The portlet filter
     * @return A collection of referenceItem
     */
    public static Collection<ReferenceItem> findByCodeDocumentTypeAndCategory( int nDocumentId, String strCodeDocumentType, PortletOrder pOrder,
            PortletFilter pFilter )
    {
        // FIXME : method should access to different home business methods
        return _dao.selectByDocumentOdAndDocumentType( nDocumentId, strCodeDocumentType, pOrder, pFilter );
    }

    /**
     * Check whether a portlet is an alias portlet
     * 
     * @param nPortletId
     *            The id of the portlet
     * @return True if the portlet is an alias portlet, false otherwise
     */
    public static boolean checkIsAliasPortlet( int nPortletId )
    {
        return _dao.checkIsAliasPortlet( nPortletId );
    }

    /**
     * Finds all portlets that contains the document
     * 
     * @param nDocumentId
     *            document id
     * @return all portlets id.
     */
    public static List<Integer> findPortletForDocument( int nDocumentId )
    {
        return _dao.selectPortletsByDocumentId( nDocumentId );
    }
}
