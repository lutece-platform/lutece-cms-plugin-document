/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.document.business.category;

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;


/**
 * This class provides instances management methods (create, find, ...) for Category objects
 */
public final class CategoryHome
{
    // Static variable pointed at the DAO instance
    private static ICategoryDAO _dao = (ICategoryDAO) SpringContextService.getPluginBean( "document",
            "document.categoryDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private CategoryHome(  )
    {
    }

    /**
     * Returns the category list
     *
     * @return Collection of Category (empty collection is no result)
     */
    public static Collection<Category> findAll(  )
    {
        Collection<Category> categoryList = _dao.selectAll(  );

        return categoryList;
    }

    /**
     * Create a new Category
     * @param category The new Category
     *
     */
    public static void create( Category category )
    {
        _dao.insert( category );
    }

    /**
     * Find the data of Category from the table
     * @param nIdCategory The id of the category
     * @return The Instance of the object Category of null if no category match
     */
    public static Category find( int nIdCategory )
    {
        return _dao.load( nIdCategory );
    }

    /**
     * Find the data of Category from the table
     * @param strCategoryName The id of the category
     * @return The Collection of Category (empty collection is no result)
     */
    public static Collection<Category> findByName( String strCategoryName )
    {
        return _dao.selectByName( strCategoryName );
    }

    /**
     * Remove a record from the table
     * @param nIdCategory The identifier of the object Category
     */
    public static void remove( int nIdCategory )
    {
        _dao.delete( nIdCategory );
    }

    /**
     * Update the record in the table
     * @param category The instance of the Category to update
     */
    public static void update( Category category )
    {
        _dao.store( category );
    }

    /**
     * Find the number of documents linked to a category
     * @param nIdCategory The category id
     * @return count of id document
     */
    public static int findCountIdDocuments( int nIdCategory )
    {
        return _dao.selectCountIdDocuments( nIdCategory );
    }

    /**
    * Return the image resource for the specified category id
    * @param nCategoryId The identifier of Category object
    * @return ImageResource
    */
    public static ImageResource getImageResource( int nCategoryId )
    {
        return _dao.loadImageResource( nCategoryId );
    }
}
