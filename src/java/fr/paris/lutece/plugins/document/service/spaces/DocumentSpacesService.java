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
package fr.paris.lutece.plugins.document.service.spaces;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Document Spaces management Service.
 */
@ApplicationScoped
@Named( "document.DocumentSpacesService" )
public class DocumentSpacesService
{
	public static final String PARAMETER_BROWSER_SELECTED_SPACE_ID = "browser_selected_space_id";
	private static final String REGEX_ID = "^[\\d]+$";

	// space forms
	private static final String TEMPLATE_CREATE_SPACE_FORM = "/admin/plugins/document/spaces/create_space_form.html";
	private static final String TEMPLATE_MODIFY_SPACE_FORM = "/admin/plugins/document/spaces/modify_space_form.html";
	private static final String TEMPLATE_MOVE_SPACE_FORM = "/admin/plugins/document/spaces/move_space_form.html";
	private static final String MARK_SPACES_TREE = "spaces_tree";
	private static final String MARK_PARENT_SPACE = "parent_space";
	private static final String MARK_VIEW_TYPE = "view_type";
	private static final String MARK_VIEW_TYPES_LIST = "view_types_list";
	private static final String MARK_ICONS_LIST = "icons_list";
	private static final String MARK_DOCUMENT_TYPES_LIST = "document_types_list";
	private static final String MARK_USER_WORKGROUP_LIST = "user_workgroup_list";
	private static final String MARK_WORKGROUP_SELECTED = "selected_workgroup";
	private static final String MARK_TOKEN = "token";
	private static final String MARK_MODAL = "in_modal";
	private static final String MARK_ID_PREFIX = "id_prefix";
	private static final String DEFAULT_VIEW_TYPE = "1";

	// browser
	private static final String TEMPLATE_BROWSE_SPACES = "/admin/plugins/document/spaces/browse_spaces.html";
	private static final String TEMPLATE_BROWSE_SPACES_FOR_FILE_SELECTION = "/admin/plugins/document/spaces/browse_spaces_for_file_selection.html";
	private static final String MARK_SPACE = "space";
	public static final String PARAMETER_BROWSER_SPACE_ID = "browser_id_space";
	private static final String MARK_ACTION = "action";
	private static final String MARK_SPACES_LIST = "spaces_list";
	private static final String MARK_URLS_LIST = "has_childs";
	private static final String MARK_SELECTED_SPACE = "selected_space";
	private static final String MARK_GO_UP = "go_up";

	/** Creates a new instance of DocumentSpacesService */
	public DocumentSpacesService( )
	{
	}

	/**
	 * Gets the tree of the spaces a given user is allowed to see
	 * 
	 * @param user The current user
	 * @return The root nodes of the tree
	 */
	public List < SpaceNode > getSpacesTree( AdminUser user )
	{
		List < SpaceNode > listNodes = new ArrayList<>( );
		Map < Integer, SpaceNode > mapBuiltNodes = new HashMap<>( );

		for( DocumentSpace space : getUserSpaces( user ) )
		{
			SpaceNode node = getSpaceNode( space.getId( ), user, mapBuiltNodes );

			if( node != null )
			{
				listNodes.add( node );
			}
		}

		return listNodes;
	}

	/**
	 * Gets the tree of the spaces a given user is allowed to see, each space being displayed once: {@link #getSpacesTree( AdminUser )} adds every
	 * authorized space as a root with its subtree, so a space that already appears in the subtree of another root is removed from the roots
	 * 
	 * @param user The current user
	 * @return The root nodes of the tree
	 */
	public List < SpaceNode > getSpacesTreeRoots( AdminUser user )
	{
		List < SpaceNode > listNodes = getSpacesTree( user );
		Set < Integer > setDescendantIds = new HashSet<>( );

		for( SpaceNode node : listNodes )
		{
			addDescendantIds( node, setDescendantIds );
		}

		List < SpaceNode > listRoots = new ArrayList<>( );

		for( SpaceNode node : listNodes )
		{
			if( ! setDescendantIds.contains( node.getId( ) ) )
			{
				listRoots.add( node );
			}
		}

		return listRoots;
	}

	/**
	 * Adds recursively the identifiers of the descendants of a node
	 * 
	 * @param node   The node
	 * @param setIds The set of identifiers to fill
	 */
	private static void addDescendantIds( SpaceNode node, Set < Integer > setIds )
	{
		for( SpaceNode child : node.getChildren( ) )
		{
			setIds.add( child.getId( ) );
			addDescendantIds( child, setIds );
		}
	}

	/**
	 * Gets the HTML form to create a child space
	 * 
	 * @param nIdParentSpace The identifier of the parent space
	 * @param user           The current user
	 * @param locale         The locale
	 * @param strToken       The security token of the spaces actions
	 * @param bModal         true if the form is displayed in a modal dialog
	 * @param strIdPrefix    The prefix of the identifiers of the fields, to keep them unique in the page
	 * @return The form, or an empty string if the user is not allowed to create a space in the parent space
	 */
	public String getCreateSpaceForm( int nIdParentSpace, AdminUser user, Locale locale, String strToken, boolean bModal,
			String strIdPrefix )
	{
		String strIdParentSpace = Integer.toString( nIdParentSpace );
		DocumentSpace spaceParent = DocumentSpaceHome.findByPrimaryKey( nIdParentSpace );

		if( ( spaceParent == null ) ||
				! RBACService.isAuthorized( DocumentSpace.RESOURCE_TYPE, strIdParentSpace, SpaceResourceIdService.PERMISSION_CREATE, ( User ) user ) ||
				! isAuthorizedViewByWorkgroup( nIdParentSpace, user ) )
		{
			return StringUtils.EMPTY;
		}

		ReferenceList refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( user, locale );
		Map < String, Object > model = getSpaceFormModel( refListWorkGroups, locale, strToken, bModal, strIdPrefix );
		model.put( MARK_DOCUMENT_TYPES_LIST, DocumentTypeHome.getDocumentTypesList( ) );
		model.put( MARK_PARENT_SPACE, spaceParent );

		// LUTECE-890 : the first workgroup will be selected by default
		if( ! refListWorkGroups.isEmpty( ) )
		{
			model.put( MARK_WORKGROUP_SELECTED, refListWorkGroups.get( 0 ).getCode( ) );
		}

		return AppTemplateService.getTemplate( TEMPLATE_CREATE_SPACE_FORM, locale, model ).getHtml( );
	}

	/**
	 * Gets the HTML form to modify a space
	 * 
	 * @param nIdSpace    The identifier of the space
	 * @param user        The current user
	 * @param locale      The locale
	 * @param strToken    The security token of the spaces actions
	 * @param bModal      true if the form is displayed in a modal dialog
	 * @param strIdPrefix The prefix of the identifiers of the fields, to keep them unique in the page
	 * @return The form, or an empty string if the user is not allowed to modify the space
	 */
	public String getModifySpaceForm( int nIdSpace, AdminUser user, Locale locale, String strToken, boolean bModal, String strIdPrefix )
	{
		String strIdSpace = Integer.toString( nIdSpace );
		DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nIdSpace );

		if( ( space == null ) ||
				! RBACService.isAuthorized( DocumentSpace.RESOURCE_TYPE, strIdSpace, SpaceResourceIdService.PERMISSION_MODIFY, ( User ) user ) ||
				! isAuthorizedViewByWorkgroup( nIdSpace, user ) )
		{
			return StringUtils.EMPTY;
		}

		ReferenceList listDocumentTypes = DocumentTypeHome.getDocumentTypesList( );
		listDocumentTypes.checkItems( space.getAllowedDocumentTypes( ) );

		Map < String, Object > model = getSpaceFormModel( AdminWorkgroupService.getUserWorkgroups( user, locale ), locale, strToken, bModal,
				strIdPrefix );
		model.put( MARK_DOCUMENT_TYPES_LIST, listDocumentTypes );
		model.put( MARK_SPACE, space );

		return AppTemplateService.getTemplate( TEMPLATE_MODIFY_SPACE_FORM, locale, model ).getHtml( );
	}

	/**
	 * Gets the HTML form to move a space under another space
	 * 
	 * @param nIdSpace    The identifier of the space to move
	 * @param user        The current user
	 * @param locale      The locale
	 * @param strToken    The security token of the spaces actions
	 * @param bModal      true if the form is displayed in a modal dialog
	 * @param strIdPrefix The prefix of the identifiers of the form, to keep them unique in the page
	 * @return The form, or an empty string if the user is not allowed to move the space
	 */
	public String getMoveSpaceForm( int nIdSpace, AdminUser user, Locale locale, String strToken, boolean bModal, String strIdPrefix )
	{
		DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nIdSpace );

		if( ( space == null ) ||
				! RBACService.isAuthorized( DocumentSpace.RESOURCE_TYPE, Integer.toString( nIdSpace ), SpaceResourceIdService.PERMISSION_MOVE, ( User ) user ) ||
				! isAuthorizedViewByWorkgroup( nIdSpace, user ) )
		{
			return StringUtils.EMPTY;
		}

		Map < String, Object > model = new HashMap<>( );
		model.put( MARK_SPACE, space );
		model.put( MARK_SPACES_TREE, getSpacesTreeRoots( user ) );
		model.put( MARK_TOKEN, strToken );
		model.put( MARK_MODAL, bModal );
		model.put( MARK_ID_PREFIX, StringUtils.defaultString( strIdPrefix ) );

		return AppTemplateService.getTemplate( TEMPLATE_MOVE_SPACE_FORM, locale, model ).getHtml( );
	}

	/**
	 * Gets the model shared by the space forms
	 */
	private static Map < String, Object > getSpaceFormModel( ReferenceList refListWorkGroups, Locale locale, String strToken, boolean bModal,
			String strIdPrefix )
	{
		Map < String, Object > model = new HashMap<>( );
		model.put( MARK_VIEW_TYPE, DEFAULT_VIEW_TYPE );
		model.put( MARK_VIEW_TYPES_LIST, DocumentSpaceHome.getViewTypeList( locale ) );
		model.put( MARK_ICONS_LIST, DocumentSpaceHome.getIconsList( ) );
		model.put( MARK_USER_WORKGROUP_LIST, refListWorkGroups );
		model.put( MARK_TOKEN, strToken );
		model.put( MARK_MODAL, bModal );
		model.put( MARK_ID_PREFIX, StringUtils.defaultString( strIdPrefix ) );

		return model;
	}

	/**
	 * Gets user default space
	 * 
	 * @param user The current user
	 * @return The user default space
	 */
	public int getUserDefaultSpace( AdminUser user )
	{
		int nIdDefaultSpace = - 1;

		for( DocumentSpace space : getUserSpaces( user ) )
		{
			nIdDefaultSpace = space.getId( );
		}

		return nIdDefaultSpace;
	}

	private Collection < DocumentSpace > getUserSpaces( AdminUser user )
	{
		Collection < DocumentSpace > listSpaces = DocumentSpaceHome.findAll( );
		listSpaces = RBACService.getAuthorizedCollection( listSpaces, SpaceResourceIdService.PERMISSION_VIEW, ( User ) user );

		return listSpaces;
	}

	/**
	 * Gets the node of a space with the nodes of its children, if the user is allowed to see the space. Every authorized space is the root of its
	 * own subtree in {@link #getSpacesTree( AdminUser )}: a node already built, as the descendant of another space, is reused instead of being
	 * loaded again, so that each space is read once from the database
	 *
	 * @param nSpaceId      The current space of the recursive course
	 * @param user          AdminUser
	 * @param mapBuiltNodes The nodes already built, by space identifier, null for a space the user is not allowed to see
	 * @return The node of the space, or null if the user is not allowed to see the space
	 */
	private SpaceNode getSpaceNode( int nSpaceId, AdminUser user, Map < Integer, SpaceNode > mapBuiltNodes )
	{
		if( mapBuiltNodes.containsKey( nSpaceId ) )
		{
			return mapBuiltNodes.get( nSpaceId );
		}

		DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nSpaceId );
		SpaceNode node = null;

		// the space may have been removed since its id was read
		if( ( space != null ) && AdminWorkgroupService.isAuthorized( space, ( User ) user ) )
		{
			node = new SpaceNode( space.getId( ), space.getName( ), space.getDescription( ), space.getIconUrl( ) );
			node.getAllowedDocumentTypes( ).addAll( Arrays.asList( space.getAllowedDocumentTypes( ) ) );
			node.setDocumentCreationAllowed( space.isDocumentCreationAllowed( ) );

			for( DocumentSpace child : DocumentSpaceHome.findChilds( nSpaceId ) )
			{
				SpaceNode childNode = getSpaceNode( child.getId( ), user, mapBuiltNodes );

				if( childNode != null )
				{
					node.getChildren( ).add( childNode );
				}
			}
		}

		mapBuiltNodes.put( nSpaceId, node );

		return node;
	}

	/**
	 * Check if the user can view a space according its role
	 * 
	 * @param nIdSpace The Space Id
	 * @param user     The current user
	 * @return True if the user has the permission to view document Space.
	 */
	public boolean isAuthorizedViewByRole( int nIdSpace, AdminUser user )
	{
		DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nIdSpace );
		boolean bAuthorized;

		while( space != null )
		{
			bAuthorized = RBACService.isAuthorized( space, SpaceResourceIdService.PERMISSION_VIEW, ( User ) user );

			if( bAuthorized )
			{
				return true;
			}

			space = DocumentSpaceHome.findByPrimaryKey( space.getIdParent( ) );
		}

		return false;
	}

	/**
	 * Check if a space should be visible to the user according its workgroup
	 * 
	 * @param nIdSpace the id of the space to check
	 * @param user     The current user
	 * @return true if authorized, otherwise false
	 */
	public boolean isAuthorizedViewByWorkgroup( int nIdSpace, AdminUser user )
	{
		if( nIdSpace != - 1 )
		{
			DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nIdSpace );

			if( ( space != null ) && AdminWorkgroupService.isAuthorized( space, ( User ) user ) )
			{
				return isAuthorizedViewByWorkgroup( space.getIdParent( ), user );
			}

			return false;
		}

		return true;
	}

	/**
	 * Get the list of spaces allowed for a given user
	 * 
	 * @param user The admin user
	 * @return The list of spaces allowed for the given user
	 */
	public List < DocumentSpace > getUserAllowedSpaces( AdminUser user )
	{
		List < DocumentSpace > listSpaces = new ArrayList < DocumentSpace >( );
		List < DocumentSpace > listSpacesAllowed = new ArrayList < DocumentSpace >( );

		for( DocumentSpace space : getUserSpaces( user ) )
		{
			addChildSpaces( space, listSpaces );
		}

		// verify authorization workgroup
		for( DocumentSpace spaceAllowed : listSpaces )
		{
			if( isAuthorizedViewByWorkgroup( spaceAllowed.getId( ), user ) )
			{
				listSpacesAllowed.add( spaceAllowed );
			}
		}

		return listSpacesAllowed;
	}

	/**
	 * get the HTML code to display a space browser.
	 *
	 * @param request              The HTTP request
	 * @param user                 The current user
	 * @param bFilterViewRollUser  true if the list of childs space must be
	 *                             filter by RBAC view permission
	 * @param bFilterWorkspaceUser true if the list of childs space must be
	 *                             filter by workgroup
	 * @param locale               The Locale
	 * @return The HTML form
	 */
	public String getSpacesBrowser( HttpServletRequest request, AdminUser user, Locale locale,
			boolean bFilterViewRollUser, boolean bFilterWorkspaceUser )
	{
		return getSpacesBrowser( request, user, locale, bFilterViewRollUser, bFilterWorkspaceUser, false );
	}

	/**
	 * get the HTML code to display a space browser.
	 *
	 * @param request              The HTTP request
	 * @param user                 The current user
	 * @param bFilterViewRollUser  true if the list of childs space must be
	 *                             filter by RBAC view permission
	 * @param bFilterWorkspaceUser true if the list of childs space must be
	 *                             filter by workgroup
	 * @param bSelectFilesMode     true if the list spaces is used to select files
	 *                             only
	 * @param locale               The Locale
	 * @return The HTML form
	 */
	public String getSpacesBrowser( HttpServletRequest request, AdminUser user, Locale locale,
			boolean bFilterViewRollUser, boolean bFilterWorkspaceUser, boolean bSelectFilesMode )
	{
		return getSpacesBrowser(request, user, locale, bFilterViewRollUser, bFilterWorkspaceUser, bSelectFilesMode, null, null);		
	}

	/**
     * get the HTML code to display a space browser.
     *
     * @param request The HTTP request
     * @param user The current user
     * @param bFilterViewRollUser true if the list of childs space must be
     *            filter by RBAC view permission
     * @param bFilterWorkspaceUser true if the list of childs space must be
     *            filter by workgroup
     * @param bSelectFilesMode true if the list spaces is used to select files only
     * @param locale The Locale
     * @param idSpaceOverwrite idSpace to browse
     * @param actionUrl base url to use on action
     * @return The HTML form
     */
    public String getSpacesBrowser( HttpServletRequest request, AdminUser user, Locale locale,
        boolean bFilterViewRollUser, boolean bFilterWorkspaceUser, boolean bSelectFilesMode, String idSpaceOverwrite, String actionUrl )
    {

        String strIdSpaceFilter = idSpaceOverwrite;
        String strIdSpace = idSpaceOverwrite;

        if( idSpaceOverwrite == null)
        {
            strIdSpaceFilter = request.getParameter( PARAMETER_BROWSER_SELECTED_SPACE_ID );
            strIdSpace = request.getParameter( PARAMETER_BROWSER_SPACE_ID );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
		DocumentSpace selectedSpace = null;
		DocumentSpace space;
		Collection < DocumentSpace > spacesList;
		int nIdSpace = - 1;
		int i = 0;
		boolean bIsAuthorized = true;
		boolean bGoUp = true;

		// Selected space
		if( StringUtils.isNotBlank( strIdSpaceFilter ) && strIdSpaceFilter.matches( REGEX_ID ) )
		{
			selectedSpace = DocumentSpaceHome.findByPrimaryKey( IntegerUtils.convert( strIdSpaceFilter ) );
		}

		// if current space doesn't exists then set it up
		if( IntegerUtils.isNotNumeric( strIdSpace ) )
		{
			nIdSpace = getUserDefaultSpace( user );
		}
		else
		{
			nIdSpace = IntegerUtils.convert( strIdSpace );

			// the requested space no longer exists: use the default space
			if( nIdSpace != - 1 && DocumentSpaceHome.findByPrimaryKey( nIdSpace ) == null )
			{
				nIdSpace = getUserDefaultSpace( user );
			}
		}

		// set space list
		if( nIdSpace == - 1 )
		{
			space = new DocumentSpace( );
			space.setId( - 1 );
			space.setIdParent( - 1 );
		}
		else
		{
			space = DocumentSpaceHome.findByPrimaryKey( nIdSpace );
		}

		spacesList = DocumentSpaceHome.findChilds( space.getId( ) );

		if( bFilterViewRollUser )
		{
			bIsAuthorized = isAuthorizedViewByRole( space.getId( ), user );
		}

		if( bIsAuthorized && bFilterWorkspaceUser )
		{
			bIsAuthorized = AdminWorkgroupService.isAuthorized( space, ( User ) user );

			if( bIsAuthorized )
			{
				spacesList = AdminWorkgroupService.getAuthorizedCollection( spacesList, ( User ) user );
			}
		}

		// set links for childs spaces
		int [ ] arrayHasChilds = new int [ spacesList.size( ) ];

		for( DocumentSpace documentSpace : spacesList )
		{
			// Check Childs spaces
			List < DocumentSpace > childsSpaces = DocumentSpaceHome.findChilds( documentSpace.getId( ) );

			// If childs spaces list is not empty, then add into array
			if( childsSpaces.size( ) != 0 )
			{
				arrayHasChilds [i] = documentSpace.getId( );
			}

			i ++ ;
		}

		if( ! bIsAuthorized )
		{
			// display "go up" link
			bGoUp = false;
		}

		model.put( MARK_GO_UP, bGoUp );
		model.put( MARK_SELECTED_SPACE, selectedSpace );
		model.put( MARK_SPACE, space );
		model.put( MARK_SPACES_LIST, spacesList );
		model.put( MARK_URLS_LIST, arrayHasChilds );
		if( actionUrl !=null)
        {
            model.put( MARK_ACTION, actionUrl );
        }
        else
        {
            model.put( MARK_ACTION, request.getRequestURI(  ) );
        }

		String _template = TEMPLATE_BROWSE_SPACES;
		if( bSelectFilesMode )
			_template = TEMPLATE_BROWSE_SPACES_FOR_FILE_SELECTION;

		HtmlTemplate template = AppTemplateService.getTemplate( _template, locale, model );

		return template.getHtml( );
	}

	private void addChildSpaces( DocumentSpace spaceParent, List < DocumentSpace > listSpaces )
	{
		listSpaces.add( spaceParent );

		List < DocumentSpace > listChilds = DocumentSpaceHome.findChilds( spaceParent.getId( ) );

		for( DocumentSpace space : listChilds )
		{
			addChildSpaces( space, listSpaces );
		}
	}

	/**
	 * the list of parents Document space of the document space specified in
	 * parameter
	 * 
	 * @param documentSpace the document space
	 * @param user          the user
	 * @return the list of parents Document space of the document space
	 *         specified in parameter
	 */
	private List < DocumentSpace > getSpaceParents( DocumentSpace documentSpace, AdminUser user )
	{
		List < DocumentSpace > documentSpaceParents = new ArrayList < DocumentSpace >( );
		getSpaceParents( documentSpace.getIdParent( ), documentSpaceParents, user );

		return documentSpaceParents;
	}

	/**
	 * add in the list of document space the list of parents document space
	 * specified in parameter
	 * 
	 * @param nSpaceId             the id of the document space
	 * @param documentSpaceParents the list of document space
	 * @param user                 the user
	 */
	private void getSpaceParents( int nSpaceId, List < DocumentSpace > documentSpaceParents, AdminUser user )
	{
		DocumentSpace documentSpace = DocumentSpaceHome.findByPrimaryKey( nSpaceId );

		// the parent space may have been removed
		if( ( documentSpace != null ) && AdminWorkgroupService.isAuthorized( documentSpace, ( User ) user ) )
		{
			if( documentSpace.getIdParent( ) != - 1 )
			{
				getSpaceParents( documentSpace.getIdParent( ), documentSpaceParents, user );
			}

			documentSpaceParents.add( documentSpace );
		}
	}

	/**
	 * the path of the document space
	 * 
	 * @param nIdDocumentSpace the id of the document space
	 * @param user             the user
	 * @return the path of the document space
	 */
	public String getLabelSpacePath( int nIdDocumentSpace, AdminUser user )
	{
		DocumentSpace documentSpace = DocumentSpaceHome.findByPrimaryKey( nIdDocumentSpace );
		StringBuffer sbLabelPath = new StringBuffer( );

		if( documentSpace != null )
		{
			if( documentSpace.getIdParent( ) != - 1 )
			{
				List < DocumentSpace > documentSpaceParents = getSpaceParents( documentSpace, user );

				for( DocumentSpace documentSpaceParent : documentSpaceParents )
				{
					sbLabelPath.append( documentSpaceParent.getName( ) );
					sbLabelPath.append( "/" );
				}
			}

			sbLabelPath.append( documentSpace.getName( ) );
		}

		return sbLabelPath.toString( );
	}

	/**
	 * The path of a document space, from the root to the space itself, for a
	 * breadcrumb
	 *
	 * @param nIdDocumentSpace the id of the document space
	 * @param user             the user
	 * @return the parents of the space the user is allowed to see followed by the
	 *         space, or an empty list if the space does not exist
	 */
	public List < DocumentSpace > getSpacePath( int nIdDocumentSpace, AdminUser user )
	{
		DocumentSpace documentSpace = DocumentSpaceHome.findByPrimaryKey( nIdDocumentSpace );

		if( documentSpace == null )
		{
			return new ArrayList<>( );
		}

		List < DocumentSpace > listPath = ( documentSpace.getIdParent( ) != - 1 ) ? getSpaceParents( documentSpace, user )
				: new ArrayList<>( );
		listPath.add( documentSpace );

		return listPath;
	}

	/**
	 * This method observes the initialization of the {@link ApplicationScoped}
	 * context.
	 * It ensures that this CDI beans are instantiated at the application startup.
	 *
	 * <p>
	 * This method is triggered automatically by CDI when the
	 * {@link ApplicationScoped} context is initialized,
	 * which typically occurs during the startup of the application server.
	 * </p>
	 *
	 * @param context the {@link ServletContext} that is initialized. This parameter
	 *                is observed
	 *                and injected automatically by CDI when the
	 *                {@link ApplicationScoped} context is initialized.
	 */
	public void initializedService( @Observes @Initialized( ApplicationScoped.class ) ServletContext context )
	{
		// This method is intentionally left empty to trigger CDI bean instantiation
	}
}
