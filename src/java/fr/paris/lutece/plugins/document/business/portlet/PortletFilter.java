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

import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * PortletFilter class
 * @author merlinfe
 *
 */
public class PortletFilter
{
    public static final String CONSTANTE_SPACE_STRING = " ";
    public static final String PAGE_NAME = AppPropertiesService.getProperty( "document.filter.page_name", "page_name" );
    public static final String PORTLET_NAME = AppPropertiesService.getProperty( "document.filter.portlet_name",
            "portlet_name" );
    public static final String PAGE_ID = AppPropertiesService.getProperty( "document.filter.page_id", "page_id" );
    public static final int PROPERTY_NUMBER_OF_MAX_LATEST_PORTLETS_DISPLAY = AppPropertiesService.getPropertyInt( "document.filter.number_of_max_latest_portlets_display",
            10 );
    private static final String SQL_FILTER_PAGE_NAME = "  f.name like ? ";
    private static final String SQL_FILTER_BY_PAGE_ID = "  a.id_page=? ";
    private static final String SQL_FILTER_BY_PORTLET_NAME = " a.name like ? ";
    private boolean _bIsDisplayLatestPortlets;
    private String[] _tabPageName;
    private String[] _tabPortletName;
    private Integer _nIdPage;
    private String _portletFilterType;
    private String _strSearchValue;

    /**
     * Set the page name list
     * @param strPageTitle The list of titles of pages
     */
    public void setPageName( String[] strPageTitle )
    {
        this._tabPageName = strPageTitle;
    }

    /**
     * Get the page name list
     * @return The page name list
     */
    public String[] getPageName(  )
    {
        return _tabPageName;
    }

    /**
     * Set the portlet title filter
     * @param strPortletTitle The portlet title filter
     */
    public void setPortletName( String[] strPortletTitle )
    {
        this._tabPortletName = strPortletTitle;
    }

    /**
     * Get the portlet title filter
     * @return The portlet title filter
     */
    public String[] getPortletName(  )
    {
        return _tabPortletName;
    }

    /**
     * Set the id of the page
     * @param nIdPage The id of the page
     */
    public void setIdPage( Integer nIdPage )
    {
        this._nIdPage = nIdPage;
    }

    /**
     * Get the id of the page
     * @return The id of the page
     */
    public Integer getIdPage(  )
    {
        return _nIdPage;
    }

    /**
     * Set the display latest portlets filter attribute
     * @param bIsDisplayLatestPortlets the display latest portlets filter
     *            attribute
     */
    public void setDisplayLatestPortlets( boolean bIsDisplayLatestPortlets )
    {
        this._bIsDisplayLatestPortlets = bIsDisplayLatestPortlets;
    }

    /**
     * Get the display latest portlets filter attribute
     * @return The display latest portlets filter attribute
     */
    public boolean isDisplayLatestPortlets(  )
    {
        return _bIsDisplayLatestPortlets;
    }

    /**
     * Set the portlet type
     * @param portletFilterType The portlet type
     */
    public void setPortletFilterType( String portletFilterType )
    {
        this._portletFilterType = portletFilterType;
    }

    /**
     * Get the portlet type
     * @return The portlet type
     */
    public String getPortletFilterType(  )
    {
        return _portletFilterType;
    }

    /**
     * Get the SQL query for searching
     *
     * @return the SQL query
     */
    public String getSQLFilter(  )
    {
        if ( ( _portletFilterType != null ) && !_bIsDisplayLatestPortlets )
        {
            StringBuilder sbSQL = new StringBuilder(  );

            if ( _portletFilterType.equals( PAGE_NAME ) && ( _tabPageName != null ) )
            {
                sbSQL.append( " ( " );

                for ( int i = 0; i < _tabPageName.length; i++ )
                {
                    sbSQL.append( SQL_FILTER_PAGE_NAME );

                    if ( i != ( _tabPageName.length - 1 ) )
                    {
                        sbSQL.append( " OR " );
                    }
                }

                sbSQL.append( " ) " );
            }
            else if ( _portletFilterType.equals( PORTLET_NAME ) && ( _tabPortletName != null ) )
            {
                sbSQL.append( " ( " );

                for ( int i = 0; i < _tabPortletName.length; i++ )
                {
                    sbSQL.append( SQL_FILTER_BY_PORTLET_NAME );

                    if ( i != ( _tabPortletName.length - 1 ) )
                    {
                        sbSQL.append( " OR " );
                    }
                }

                sbSQL.append( " ) " );
            }
            else if ( _portletFilterType.equals( PAGE_ID ) && ( _nIdPage != null ) )
            {
                sbSQL.append( SQL_FILTER_BY_PAGE_ID );
            }

            return sbSQL.toString(  );
        }

        return null;
    }

    /**
     * Set the search value
     * @param strSearchValue the search value
     */
    public void setSearchValue( String strSearchValue )
    {
        this._strSearchValue = strSearchValue;
    }

    /**
     * Get the search value
     * @return the search value
     */
    public String getSearchValue(  )
    {
        return _strSearchValue;
    }
}
