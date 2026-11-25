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
package fr.paris.lutece.plugins.document.web.portlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.category.Category;
import fr.paris.lutece.plugins.document.service.category.CategoryService.CategoryDisplay;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Renders the specific templates of the document portlet forms
 */
public class PortletFormTemplatesTest extends LuteceTestCase
{
    private static final String TEMPLATE_CATEGORIES = "admin/plugins/document/portlet/category_document_list.html";
    private static final String TEMPLATE_DOCUMENT_PORTLET_TYPE = "admin/plugins/document/portlet/document_portlet_type.html";
    private static final String TEMPLATE_DOCUMENT_LIST_PORTLET_TYPE = "admin/plugins/document/portlet/document_list_portlet_type.html";
    private static final String MARK_CATEGORY_LIST = "category_list";

    /**
     * The categories are checkboxes of a dropdown whose button names the checked ones
     */
    @Test
    public void testCategoriesDropdown( )
    {
        String strHtml = renderCategories( List.of( category( 1, "Culture", true ), category( 2, "Sport & <loisirs>", true ), category( 3, "Santé", false ) ) );

        assertTrue( strHtml.contains( "<details class=\"dropdown\" id=\"categories-dropdown\"" ) );
        assertTrue( strHtml.contains( "name=\"categories\" value=\"1\" checked" ) );
        assertTrue( strHtml.contains( "name=\"categories\" value=\"2\" checked" ) );
        assertTrue( strHtml.contains( "name=\"categories\" value=\"3\">" ) );
        assertTrue( strHtml.contains( "data-categories-label>Culture, Sport &amp; &lt;loisirs&gt;</span>" ) );
        assertFalse( strHtml.contains( "<loisirs>" ) );
    }

    /**
     * Without a checked category, the button says that every category is accepted
     */
    @Test
    public void testCategoriesDropdownNoneChecked( )
    {
        String strHtml = renderCategories( List.of( category( 1, "Culture", false ) ) );

        assertFalse( strHtml.contains( " checked" ) );
        assertTrue( strHtml.contains( "data-categories-label>Toutes les cat\u00e9gories</span>" ) );
        assertFalse( strHtml.contains( "#i18n" ) );
    }

    /**
     * The document type and the categories sit side by side in both portlet forms
     */
    @Test
    public void testTypeAndCategoriesSideBySide( )
    {
        for ( String strTemplate : List.of( TEMPLATE_DOCUMENT_PORTLET_TYPE, TEMPLATE_DOCUMENT_LIST_PORTLET_TYPE ) )
        {
            String strHtml = AppTemplateService.getTemplate( strTemplate, Locale.FRENCH, new HashMap<>( ) ).getHtml( );
            int nType = strHtml.indexOf( "@combo_document_type@" );
            int nCategories = strHtml.indexOf( "@combo_category_document@" );
            int nDocuments = strHtml.indexOf( "@combo_document@" );

            assertTrue( nType > 0 && nCategories > nType && nDocuments > nCategories, strTemplate );
            assertEquals( 2, strHtml.substring( 0, nDocuments ).split( "col-md-6", -1 ).length - 1, strTemplate );
        }
    }

    /**
     * Renders the categories template
     *
     * @param listCategories
     *            The categories
     * @return The html
     */
    private static String renderCategories( List<CategoryDisplay> listCategories )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CATEGORY_LIST, listCategories );

        return AppTemplateService.getTemplate( TEMPLATE_CATEGORIES, Locale.FRENCH, model ).getHtml( );
    }

    /**
     * Builds a category as shown in the portlet forms
     *
     * @param nId
     *            The identifier
     * @param strName
     *            The name
     * @param bAssigned
     *            true if the portlet has the category
     * @return The category
     */
    private static CategoryDisplay category( int nId, String strName, boolean bAssigned )
    {
        Category category = new Category( );
        category.setId( nId );
        category.setName( strName );

        CategoryDisplay categoryDisplay = new CategoryDisplay( );
        categoryDisplay.setCategory( category );
        categoryDisplay.setAssigned( bAssigned );

        return categoryDisplay;
    }
}
