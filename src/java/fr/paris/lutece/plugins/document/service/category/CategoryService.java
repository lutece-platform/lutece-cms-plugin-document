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
package fr.paris.lutece.plugins.document.service.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.paris.lutece.plugins.document.business.category.Category;
import fr.paris.lutece.plugins.document.business.category.CategoryHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.image.ImageResourceManager;
import fr.paris.lutece.portal.service.image.ImageResourceProvider;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.url.UrlItem;


/**
 * 
 * This classe provide services for Category
 * 
 */
public class CategoryService implements ImageResourceProvider
{
    private static CategoryService _singleton = new CategoryService( );
    private static final String IMAGE_RESOURCE_TYPE_ID = "icon_category";

    /**
     * Creates a new instance of CategoryService
     */
    CategoryService( )
    {
        ImageResourceManager.registerProvider( this );
    }

    /**
     * Get the unique instance of the service
     * 
     * @return The unique instance
     */
    public static CategoryService getInstance( )
    {
        return _singleton;
    }

    /**
     * Get the resource for image
     * @param nIdCategory The identifier of Category object
     * @return The ImageResource
     */
    public ImageResource getImageResource( int nIdCategory )
    {
        return CategoryHome.getImageResource( nIdCategory );
    }

    /**
     * Get the type of resource
     * @return The type of resource
     */
    public String getResourceTypeId( )
    {
        return IMAGE_RESOURCE_TYPE_ID;
    }

    /**
     * Get all Category converted to CategoryDisplay objects
     * @param user The current user
     * @return The Collection of CategoryDisplay
     */
    public static Collection<CategoryDisplay> getAllCategoriesDisplay( AdminUser user )
    {
        Collection<Category> listCategory = CategoryHome.findAll( );
        listCategory = AdminWorkgroupService.getAuthorizedCollection( listCategory, user );

        Collection<CategoryDisplay> listCategoryDisplay = new ArrayList<CategoryDisplay>( );

        for ( Category category : listCategory )
        {
            CategoryDisplay categoryDisplay = _singleton.new CategoryDisplay( );
            categoryDisplay.setCategory( category );
            categoryDisplay.setIconUrl( getResourceImageCategory( category.getId( ) ) );
            categoryDisplay.setCountLinkedDocuments( CategoryHome.findCountIdDocuments( category.getId( ) ) );
            categoryDisplay.setAssigned( false );
            listCategoryDisplay.add( categoryDisplay );
        }

        return listCategoryDisplay;
    }

    /**
     * Get all Category converted to CategoryDisplay objects and tagged with the
     * assigned value when lists of categories matched
     * @param arrayIdCategory The array of Id categories
     * @param user The current user
     * @return The Collection of CategoryDisplay
     */
    public static Collection<CategoryDisplay> getAllCategoriesDisplay( int[] arrayIdCategory, AdminUser user )
    {
        Collection<Category> listCategory = CategoryHome.findAll( );
        listCategory = AdminWorkgroupService.getAuthorizedCollection( listCategory, user );

        Collection<CategoryDisplay> listCategoryDisplay = new ArrayList<CategoryDisplay>( );

        for ( Category category : listCategory )
        {
            CategoryDisplay categoryDisplay = _singleton.new CategoryDisplay( );
            categoryDisplay.setCategory( category );
            categoryDisplay.setIconUrl( getResourceImageCategory( category.getId( ) ) );
            categoryDisplay.setCountLinkedDocuments( CategoryHome.findCountIdDocuments( category.getId( ) ) );
            categoryDisplay.setAssigned( false );

            for ( int nIdCategory : arrayIdCategory )
            {
                if ( nIdCategory == category.getId( ) )
                {
                    categoryDisplay.setAssigned( true );
                }
            }

            listCategoryDisplay.add( categoryDisplay );
        }

        return listCategoryDisplay;
    }

    /**
     * Get all Category converted to CategoryDisplay objects and tagged with the
     * assigned value when lists of categories matched
     * @param listCategory The list of ca t
     * @param user The current user
     * @return A Collection of CategoryDisplay object
     */
    public static Collection<CategoryDisplay> getAllCategoriesDisplay( List<Category> listCategory, AdminUser user )
    {
        int[] arrayCategory = new int[listCategory.size( )];
        int i = 0;

        for ( Category category : listCategory )
        {
            arrayCategory[i++] = category.getId( );
        }

        return getAllCategoriesDisplay( arrayCategory, user );
    }

    /**
     * Return a CategoryDisplay object for a specified Category
     * @param nIdCategory The id of Category
     * @return The CategoryDisplay object
     */
    public static CategoryDisplay getCategoryDisplay( int nIdCategory )
    {
        CategoryDisplay categoryDisplay = _singleton.new CategoryDisplay( );

        Category category = CategoryHome.find( nIdCategory );

        if ( category == null )
        {
            return null;
        }

        categoryDisplay.setCategory( category );
        categoryDisplay.setIconUrl( getResourceImageCategory( categoryDisplay.getCategory( ).getId( ) ) );
        categoryDisplay.setCountLinkedDocuments( CategoryHome.findCountIdDocuments( categoryDisplay.getCategory( )
                .getId( ) ) );
        categoryDisplay.setAssigned( false );

        return categoryDisplay;
    }

    /**
     * Management of the image associated to the Category
     * @param nCategoryId The Category identifier
     * @return The url of the resource
     */
    public static String getResourceImageCategory( int nCategoryId )
    {
        String strResourceType = CategoryService.getInstance( ).getResourceTypeId( );
        UrlItem url = new UrlItem( Parameters.IMAGE_SERVLET );
        url.addParameter( Parameters.RESOURCE_TYPE, strResourceType );
        url.addParameter( Parameters.RESOURCE_ID, Integer.toString( nCategoryId ) );

        return url.getUrlWithEntity( );
    }

    /**
     * 
     * This class defines a CategoryDisplay object intended to be used in
     * freemarker templates
     * It provide the Category object and other informations.
     * 
     */
    public class CategoryDisplay
    {
        private Category _category;
        private String _strIconUrl;
        private int _nCountLinkedDocuments;
        private boolean _bAssigned;

        /**
         * Get the Category object
         * @return The Category object
         */
        public Category getCategory( )
        {
            return _category;
        }

        /**
         * Set the Category object
         * @param category The Category object to set
         */
        public void setCategory( Category category )
        {
            this._category = category;
        }

        /**
         * Get the number of linked documents
         * @return The number of linked documents
         */
        public int getCountLinkedDocuments( )
        {
            return _nCountLinkedDocuments;
        }

        /**
         * Set the number of linked documents
         * @param nCountLinkedDocuments The number of linked documents
         */
        public void setCountLinkedDocuments( int nCountLinkedDocuments )
        {
            _nCountLinkedDocuments = nCountLinkedDocuments;
        }

        /**
         * Get the icon url
         * @return The icon url
         */
        public String getIconUrl( )
        {
            return _strIconUrl;
        }

        /**
         * Set the icon url
         * @param strIconUrl The url to set
         */
        public void setIconUrl( String strIconUrl )
        {
            _strIconUrl = strIconUrl;
        }

        /**
         * Return true if Document is linked to this Category
         * @return true if Document is linked, false else
         */
        public boolean getAssigned( )
        {
            return _bAssigned;
        }

        /**
         * Set the assigned value (true if document is linked to this Category)
         * @param bAssigned true if document if assigned
         */
        public void setAssigned( boolean bAssigned )
        {
            _bAssigned = bAssigned;
        }
    }
}
