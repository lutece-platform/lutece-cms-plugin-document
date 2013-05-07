/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.document.web.category;

import fr.paris.lutece.plugins.document.business.category.Category;
import fr.paris.lutece.plugins.document.business.category.CategoryHome;
import fr.paris.lutece.plugins.document.service.category.CategoryService;
import fr.paris.lutece.plugins.document.service.category.CategoryService.CategoryDisplay;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.AdminFeaturesPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage Lutece group features ( manage, create, modify, remove )
 */
public class CategoryJspBean extends AdminFeaturesPageJspBean
{
    // Right
    public static final String RIGHT_CATEGORY_MANAGEMENT = "DOCUMENT_CATEGORY_MANAGEMENT";

    //Constants
    private static final String REGEX_ID = "^[\\d]+$";
    private static final int ERROR_ID_CATEGORY = -1;

    // JSP
    private static final String JSP_URL_REMOVE_CATEGORY = "jsp/admin/plugins/document/DoRemoveCategory.jsp";

    //Markers
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_CATEGORY_DISPLAY = "categoryDisplay";
    private static final String MARK_USER_WORKGROUP_LIST = "user_workgroup_list";
    private static final String MARK_WORKGROUP_SELECTED = "selected_workgroup";

    // Parameters
    private static final String PARAMETER_CATEGORY_ID = "category_id";
    private static final String PARAMETER_CATEGORY_NAME = "category_name";
    private static final String PARAMETER_CATEGORY_DESCRIPTION = "category_description";
    private static final String PARAMETER_CATEGORY_UPDATE_ICON = "update_icon";
    private static final String PARAMETER_IMAGE_CONTENT = "category_icon";
    private static final String PARAMETER_WORKGROUP_KEY = "workgroup_key";

    // Templates
    private static final String TEMPLATE_MANAGE_CATEGORY = "admin/plugins/document/category/manage_category.html";
    private static final String TEMPLATE_CREATE_CATEGORY = "admin/plugins/document/category/create_category.html";
    private static final String TEMPLATE_MODIFY_CATEGORY = "admin/plugins/document/category/modify_category.html";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_CREATE_CATEGORY = "document.create_category.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CATEGORY = "document.modify_category.pageTitle";

    // Message
    private static final String MESSAGE_CATEGORY_EXIST = "document.message.categoryExist";
    private static final String MESSAGE_CATEGORY_IS_LINKED = "document.message.categoryIsLinked";
    private static final String MESSAGE_CATEGORY_ERROR = "document.message.categoryError";
    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORY = "document.message.confirmRemoveCategory";

    /**
     * Creates a new CategoryJspBean object.
     */
    public CategoryJspBean(  )
    {
    }

    /**
     * Returns Category management form
     * @param request The Http request
     * @return Html form
     */
    public String getManageCategory( HttpServletRequest request )
    {
        setPageTitleProperty( null );

        AdminUser user = getUser(  );

        HashMap<String, Collection<CategoryDisplay>> model = new HashMap<String, Collection<CategoryDisplay>>(  );
        model.put( MARK_CATEGORY_LIST, CategoryService.getAllCategoriesDisplay( user ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_CATEGORY, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Insert a new Category
     * @param request The HTTP request
     * @return String The html code page
     */
    public String getCreateCategory( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_CATEGORY );

        AdminUser user = getUser(  );
        ReferenceList refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( user, getLocale(  ) );
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_USER_WORKGROUP_LIST, refListWorkGroups );

        //LUTECE-890 : the first workgroup will be selected by default
        if ( !refListWorkGroups.isEmpty(  ) )
        {
            model.put( MARK_WORKGROUP_SELECTED, refListWorkGroups.get( 0 ).getCode(  ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_CATEGORY, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Create Category
     * @param request The HTTP request
     * @return String The url page
     */
    public String doCreateCategory( HttpServletRequest request )
    {
        Category category = new Category(  );
        String strCategoryName = request.getParameter( PARAMETER_CATEGORY_NAME ).trim(  );
        String strCategoryDescription = request.getParameter( PARAMETER_CATEGORY_DESCRIPTION ).trim(  );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP_KEY );

        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        FileItem item = mRequest.getFile( PARAMETER_IMAGE_CONTENT );

        // Mandatory field
        if ( ( strCategoryName.length(  ) == 0 ) || ( strCategoryDescription.length(  ) == 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        // check if category exist
        if ( CategoryHome.findByName( strCategoryName ).size(  ) > 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CATEGORY_EXIST, AdminMessage.TYPE_STOP );
        }

        category.setName( strCategoryName );
        category.setDescription( strCategoryDescription );

        byte[] bytes = item.get(  );

        category.setIconContent( bytes );
        category.setIconMimeType( item.getContentType(  ) );
        category.setWorkgroup( strWorkgroup );
        CategoryHome.create( category );

        return getHomeUrl( request );
    }

    /**
     * Returns Category modification form
     * @param request The HTTP request
     * @return String The html code page
     */
    public String getModifyCategory( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_CATEGORY );

        AdminUser user = getUser(  );
        ReferenceList refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( user, getLocale(  ) );
        int nIdCategory = checkCategoryId( request );

        if ( nIdCategory == ERROR_ID_CATEGORY )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CATEGORY_ERROR, AdminMessage.TYPE_ERROR );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_CATEGORY_DISPLAY, CategoryService.getCategoryDisplay( nIdCategory ) );
        model.put( MARK_USER_WORKGROUP_LIST, refListWorkGroups );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_CATEGORY, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Modify Category
     * @param request The HTTP request
     * @return String The url page
     */
    public String doModifyCategory( HttpServletRequest request )
    {
        Category category = null;
        String strCategoryName = request.getParameter( PARAMETER_CATEGORY_NAME ).trim(  );
        String strCategoryDescription = request.getParameter( PARAMETER_CATEGORY_DESCRIPTION ).trim(  );
        String strCategoryUpdateIcon = request.getParameter( PARAMETER_CATEGORY_UPDATE_ICON );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP_KEY );

        int nIdCategory = checkCategoryId( request );

        if ( nIdCategory == ERROR_ID_CATEGORY )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CATEGORY_ERROR, AdminMessage.TYPE_ERROR );
        }

        // Mandatory field
        if ( ( strCategoryName.length(  ) == 0 ) || ( strCategoryDescription.length(  ) == 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        // check if category exist
        Collection<Category> categoriesList = CategoryHome.findByName( strCategoryName );

        if ( !categoriesList.isEmpty(  ) && ( categoriesList.iterator(  ).next(  ).getId(  ) != nIdCategory ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CATEGORY_EXIST, AdminMessage.TYPE_STOP );
        }

        category = CategoryHome.find( nIdCategory );
        category.setName( strCategoryName );
        category.setDescription( strCategoryDescription );

        if ( strCategoryUpdateIcon != null )
        {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            FileItem item = mRequest.getFile( PARAMETER_IMAGE_CONTENT );

            byte[] bytes = item.get(  );
            category.setIconContent( bytes );
            category.setIconMimeType( item.getContentType(  ) );
        }

        category.setWorkgroup( strWorkgroup );

        CategoryHome.update( category );

        return getHomeUrl( request );
    }

    /**
     * Returns the page of confirmation for deleting a workgroup
     *
     * @param request The Http Request
     * @return the confirmation url
     */
    public String getConfirmRemoveCategory( HttpServletRequest request )
    {
        int nIdCategory = checkCategoryId( request );

        if ( nIdCategory == ERROR_ID_CATEGORY )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CATEGORY_ERROR, AdminMessage.TYPE_ERROR );
        }

        // Test if the category is assigned
        if ( CategoryHome.findCountIdDocuments( nIdCategory ) > 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CATEGORY_IS_LINKED, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_URL_REMOVE_CATEGORY );
        url.addParameter( PARAMETER_CATEGORY_ID, Integer.toString( nIdCategory ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CATEGORY, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the deletion
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveCategory( HttpServletRequest request )
    {
        int nIdCategory = checkCategoryId( request );

        if ( nIdCategory == ERROR_ID_CATEGORY )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CATEGORY_ERROR, AdminMessage.TYPE_ERROR );
        }

        // Test if the category is assigned
        if ( CategoryHome.findCountIdDocuments( nIdCategory ) > 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CATEGORY_IS_LINKED, AdminMessage.TYPE_STOP );
        }

        CategoryHome.remove( nIdCategory );

        return getHomeUrl( request );
    }

    /**
     *
     * @param request The http request
     * @return id of category, ERROR_ID_CATEGORY else
     */
    private int checkCategoryId( HttpServletRequest request )
    {
        String strCategoryId = request.getParameter( PARAMETER_CATEGORY_ID );

        if ( StringUtils.isBlank( strCategoryId ) || !strCategoryId.matches( REGEX_ID ) )
        {
            return ERROR_ID_CATEGORY;
        }

        return IntegerUtils.convert( strCategoryId );
    }
}
