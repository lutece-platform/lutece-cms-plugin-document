/*
 * Copyright (c) 2002-2020, City of Paris
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

import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang3.StringUtils;


/**
 * PortletOrder
 */
public class PortletOrder
{
    // CONSTANTS
    public static final int DATE_UPDATE_PORTLET = AppPropertiesService.getPropertyInt( "document.order.date_update_portlet",
            0 );
    public static final int PAGE_NAME = AppPropertiesService.getPropertyInt( "document.order.page_name", 1 );
    public static final int PAGE_ID = AppPropertiesService.getPropertyInt( "document.order.page_id", 2 );
    public static final int PORTLET_NAME = AppPropertiesService.getPropertyInt( "document.order.portlet_name", 3 );
    public static final int SORT_ASC = AppPropertiesService.getPropertyInt( "document.order.asc", 1 );
    private static final String SQL_ORDER_BY_PAGE_NAME = " ORDER BY f.name ";
    private static final String SQL_ORDER_BY_PAGE_ID = " ORDER BY a.id_page ";
    private static final String SQL_ORDER_BY_PORTLET_NAME = " ORDER BY a.name ";
    private static final String SQL_ORDER_BY_DATE_UPDATE_PORTLET = " ORDER BY a.date_update ";
    private static final String SQL_ASC = " ASC ";
    private static final String SQL_DESC = " DESC ";

    // VARIABLES
    private boolean _bSortAsc;
    private int _nTypeOrder;

    /**
     * Constructor
     */
    public PortletOrder(  )
    {
        _bSortAsc = false;
        _nTypeOrder = DATE_UPDATE_PORTLET;
    }

    /**
     * Constructor
     * @param bSortAsc true if it must be sorted ascendingly, false otherwise
     * @param nTypeOrder the order type
     */
    public PortletOrder( boolean bSortAsc, int nTypeOrder )
    {
        _bSortAsc = bSortAsc;
        _nTypeOrder = nTypeOrder;
    }

    /**
     * Set the way of sorting the portlets
     * @param bSortAsc true if it must be sorted ascendingly, false otherwise
     */
    public void setSortAsc( boolean bSortAsc )
    {
        _bSortAsc = bSortAsc;
    }

    /**
     * Return true if it must be sorted ascendingly, false otherwise
     * @return true if it must be sorted ascendingly, false otherwise
     */
    public boolean isSortAsc(  )
    {
        return _bSortAsc;
    }

    /**
     * Set the order type
     * @param nTypeOrder the order type
     */
    public void setTypeOrder( int nTypeOrder )
    {
        _nTypeOrder = nTypeOrder;
    }

    /**
     * Get the order type
     * @return the order type
     */
    public int getTypeOrder(  )
    {
        return _nTypeOrder;
    }

    /**
     * Get the SQL query for ordering the portlet
     * @return the SQL query
     */
    public String getSQLOrderBy(  )
    {
        StringBuilder sbSQL = new StringBuilder(  );

        if ( _nTypeOrder == PAGE_NAME )
        {
            sbSQL.append( SQL_ORDER_BY_PAGE_NAME );
        }
        else if ( _nTypeOrder == PAGE_ID )
        {
            sbSQL.append( SQL_ORDER_BY_PAGE_ID );
        }
        else if ( _nTypeOrder == PORTLET_NAME )
        {
            sbSQL.append( SQL_ORDER_BY_PORTLET_NAME );
        }
        else if ( _nTypeOrder == DATE_UPDATE_PORTLET )
        {
            sbSQL.append( SQL_ORDER_BY_DATE_UPDATE_PORTLET );
        }

        if ( StringUtils.isNotBlank( sbSQL.toString(  ) ) )
        {
            sbSQL.append( _bSortAsc ? SQL_ASC : SQL_DESC );
        }

        return sbSQL.toString(  );
    }
}
