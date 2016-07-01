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
package fr.paris.lutece.plugins.document.service.spaces;

import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.plugins.document.service.DocumentTypeResourceIdService;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


/**
 * Document Spaces management Service.
 */
public class DocumentSpacesService
{
    public static final String PARAMETER_BROWSER_SELECTED_SPACE_ID = "browser_selected_space_id";
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String TAG_SPACES = "spaces";
    private static final String TAG_SPACE = "space";
    private static final String TAG_SPACE_ID = "space-id";
    private static final String TAG_SPACE_IS_VALID = "space-is-valid";
    private static final String TAG_SPACE_NAME = "name";
    private static final String TAG_SPACE_DESCRIPTION = "description";
    private static final String TAG_SPACE_CHILDS = "child-spaces";
    private static final String TAG_SPACE_ICON_URL = "space-icon-url";

    //browser
    private static final String TEMPLATE_BROWSE_SPACES = "/admin/plugins/document/spaces/browse_spaces.html";
    private static final String MARK_SPACE = "space";
    private static final String PARAMETER_BROWSER_SPACE_ID = "browser_id_space";
    private static final String MARK_ACTION = "action";
    private static final String MARK_SPACES_LIST = "spaces_list";
    private static final String MARK_URLS_LIST = "has_childs";
    private static final String MARK_SELECTED_SPACE = "selected_space";
    private static final String MARK_GO_UP = "go_up";
    private static final String PATH_XSL = "/WEB-INF/plugins/document/xsl/";
    private static final String FILE_TREE_XSL = "document_spaces_tree.xsl";

    //CONSTANTS
    private static final String CONSTANT_TRUE = "true";
    private static DocumentSpacesService _singleton = new DocumentSpacesService(  );

    /** Creates a new instance of DocumentSpacesService */
    private DocumentSpacesService(  )
    {
    }

    /**
     * Returns the unique instance of the service
     * @return the unique instance of the service
     */
    public static DocumentSpacesService getInstance(  )
    {
        return _singleton;
    }

    /**
     * Gets allowed Spaces for a given user as an XML document
     * @param user The current user
     * @return An XML document containing allowed spaces
     */
    public String getXmlSpacesList( AdminUser user )
    {
        StringBuffer sbXML = new StringBuffer(  );
        XmlUtil.beginElement( sbXML, TAG_SPACES );

        for ( DocumentSpace space : getUserSpaces( user ) )
        {
            findSpaces( sbXML, space.getId(  ), user );
        }

        XmlUtil.endElement( sbXML, TAG_SPACES );

        return sbXML.toString(  );
    }

    /**
     * Gets allowed Spaces for a given user and a type of document as an XML
     * document
     * @param user The current user
     * @param strCodeType The code for the document type
     * @return An XML document containing allowed spaces
     */
    public String getXmlSpacesList( AdminUser user, String strCodeType )
    {
        StringBuffer sbXML = new StringBuffer(  );
        XmlUtil.beginElement( sbXML, TAG_SPACES );

        for ( DocumentSpace space : getUserSpaces( user ) )
        {
            findSpacesByCodeType( sbXML, space.getId(  ), user, strCodeType );
        }

        XmlUtil.endElement( sbXML, TAG_SPACES );

        return sbXML.toString(  );
    }

    /**
     * Gets user default space
     * @param user The current user
     * @return The user default space
     */
    public int getUserDefaultSpace( AdminUser user )
    {
        int nIdDefaultSpace = -1;

        for ( DocumentSpace space : getUserSpaces( user ) )
        {
            nIdDefaultSpace = space.getId(  );
        }

        return nIdDefaultSpace;
    }

    private Collection<DocumentSpace> getUserSpaces( AdminUser user )
    {
        Collection<DocumentSpace> listSpaces = DocumentSpaceHome.findAll(  );
        listSpaces = RBACService.getAuthorizedCollection( listSpaces, SpaceResourceIdService.PERMISSION_VIEW, user );

        return listSpaces;
    }

    /**
     * Gets the XSL to display user spaces tree
     * @return The XSL to display user spaces tree
     */
    public Source getTreeXsl(  )
    {
        FileInputStream fis = AppPathService.getResourceAsStream( PATH_XSL, FILE_TREE_XSL );
        Source xslSource = new StreamSource( fis );

        return xslSource;
    }

    /**
     * Build recursively the XML document containing the arborescence of spaces
     *
     * @param sbXML The buffer in which adding the current space of the
     *            arborescence
     * @param nSpaceId The current space of the recursive course
     * @param user AdminUser
     */
    private void findSpaces( StringBuffer sbXML, int nSpaceId, AdminUser user )
    {
        DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nSpaceId );

        if ( AdminWorkgroupService.isAuthorized( space, user ) )
        {
            XmlUtil.beginElement( sbXML, TAG_SPACE );
            XmlUtil.addElement( sbXML, TAG_SPACE_ID, space.getId(  ) );
            XmlUtil.addElement( sbXML, TAG_SPACE_NAME, StringEscapeUtils.escapeXml( space.getName(  ) ) );
            XmlUtil.addElement( sbXML, TAG_SPACE_DESCRIPTION, space.getDescription(  ) );
            XmlUtil.addElement( sbXML, TAG_SPACE_ICON_URL, space.getIconUrl(  ) );

            List<DocumentSpace> listChilds = DocumentSpaceHome.findChilds( nSpaceId );

            if ( listChilds.size(  ) > 0 )
            {
                XmlUtil.beginElement( sbXML, TAG_SPACE_CHILDS );

                for ( DocumentSpace child : listChilds )
                {
                    findSpaces( sbXML, child.getId(  ), user );
                }

                XmlUtil.endElement( sbXML, TAG_SPACE_CHILDS );
            }

            XmlUtil.endElement( sbXML, TAG_SPACE );
        }
    }

    /**
     * Build recursively the XML document containing the arborescence of spaces
     *
     * @param sbXML The buffer in which adding the current space of the
     *            arborescence
     * @param nSpaceId The current space of the recursive course
     * @param user AdminUser
     * @param strCodeType The code type
     * @return True if the space is valid, false otherwise
     */
    private boolean findSpacesByCodeType( StringBuffer sbXML, int nSpaceId, AdminUser user, String strCodeType )
    {
        DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nSpaceId );
        DocumentType documentType = DocumentTypeHome.findByPrimaryKey( strCodeType );
        boolean bValidSpace = false;
        boolean bChildValidity = false;

        if ( AdminWorkgroupService.isAuthorized( space, user ) )
        {
            Collection<DocumentSpace> listChilds = DocumentSpaceHome.findChilds( nSpaceId );

            boolean bContainCodeType = false;

            for ( String strCodeTypeDocument : space.getAllowedDocumentTypes(  ) )
            {
                if ( strCodeTypeDocument.equals( strCodeType ) )
                {
                    bContainCodeType = true;
                }
            }

            if ( listChilds.size(  ) > 0 )
            {
                StringBuffer sbChildrenXML = new StringBuffer(  );

                for ( DocumentSpace child : listChilds )
                {
                    bChildValidity = findSpacesByCodeType( sbChildrenXML, child.getId(  ), user, strCodeType );

                    if ( bChildValidity )
                    {
                        //if only one child is valid the current space must be displayed
                        bValidSpace = true;
                    }
                }

                if ( bValidSpace )
                {
                    //the space has children which are valid or have valid children
                    XmlUtil.beginElement( sbXML, TAG_SPACE );
                    XmlUtil.addElement( sbXML, TAG_SPACE_ID, space.getId(  ) );

                    if ( bContainCodeType && space.isDocumentCreationAllowed(  ) &&
                            ( RBACService.isAuthorized( documentType, DocumentTypeResourceIdService.PERMISSION_CREATE,
                                user ) ) )
                    {
                        //the user can create the selected type of document into the space
                        XmlUtil.addElement( sbXML, TAG_SPACE_IS_VALID, CONSTANT_TRUE );
                    }

                    XmlUtil.addElement( sbXML, TAG_SPACE_NAME, space.getName(  ) );
                    XmlUtil.addElement( sbXML, TAG_SPACE_DESCRIPTION, space.getDescription(  ) );
                    XmlUtil.addElement( sbXML, TAG_SPACE_ICON_URL, space.getIconUrl(  ) );
                    XmlUtil.beginElement( sbXML, TAG_SPACE_CHILDS );
                    sbXML.append( sbChildrenXML );
                    XmlUtil.endElement( sbXML, TAG_SPACE_CHILDS );
                    XmlUtil.endElement( sbXML, TAG_SPACE );
                }
            }

            //the space has no children
            else
            {
                if ( ( bContainCodeType ) &&
                        RBACService.isAuthorized( documentType, DocumentTypeResourceIdService.PERMISSION_CREATE, user ) )
                {
                    //the space can contain the type of document
                    XmlUtil.beginElement( sbXML, TAG_SPACE );
                    XmlUtil.addElement( sbXML, TAG_SPACE_ID, space.getId(  ) );
                    XmlUtil.addElement( sbXML, TAG_SPACE_IS_VALID, CONSTANT_TRUE );
                    XmlUtil.addElement( sbXML, TAG_SPACE_NAME, space.getName(  ) );
                    XmlUtil.addElement( sbXML, TAG_SPACE_DESCRIPTION, space.getDescription(  ) );
                    XmlUtil.addElement( sbXML, TAG_SPACE_ICON_URL, space.getIconUrl(  ) );
                    XmlUtil.endElement( sbXML, TAG_SPACE );
                    bValidSpace = true;
                }
                else
                {
                    //the space can not contain the type of document or the user is not authorized
                    bValidSpace = false;
                }
            }
        }

        return bValidSpace;
    }

    /**
     * Check if the user can view a space according its role
     * @param nIdSpace The Space Id
     * @param user The current user
     * @return True if the user has the permission to view document Space.
     */
    public boolean isAuthorizedViewByRole( int nIdSpace, AdminUser user )
    {
        DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nIdSpace );
        boolean bAuthorized;

        while ( space != null )
        {
            bAuthorized = RBACService.isAuthorized( space, SpaceResourceIdService.PERMISSION_VIEW, user );

            if ( bAuthorized )
            {
                return true;
            }

            space = DocumentSpaceHome.findByPrimaryKey( space.getIdParent(  ) );
        }

        return false;
    }

    /**
     * Check if a space should be visible to the user according its workgroup
     * @param nIdSpace the id of the space to check
     * @param user The current user
     * @return true if authorized, otherwise false
     */
    public boolean isAuthorizedViewByWorkgroup( int nIdSpace, AdminUser user )
    {
        if ( nIdSpace != -1 )
        {
            DocumentSpace space = DocumentSpaceHome.findByPrimaryKey( nIdSpace );

            if ( ( space != null ) && AdminWorkgroupService.isAuthorized( space, user ) )
            {
                return isAuthorizedViewByWorkgroup( space.getIdParent(  ), user );
            }

            return false;
        }

        return true;
    }

    /**
     * Get the list of spaces allowed for a given user
     * @param user The admin user
     * @return The list of spaces allowed for the given user
     */
    public List<DocumentSpace> getUserAllowedSpaces( AdminUser user )
    {
        List<DocumentSpace> listSpaces = new ArrayList<DocumentSpace>(  );
        List<DocumentSpace> listSpacesAllowed = new ArrayList<DocumentSpace>(  );

        for ( DocumentSpace space : getUserSpaces( user ) )
        {
            addChildSpaces( space, listSpaces );
        }

        //verify authorization workgroup
        for ( DocumentSpace spaceAllowed : listSpaces )
        {
            if ( isAuthorizedViewByWorkgroup( spaceAllowed.getId(  ), user ) )
            {
                listSpacesAllowed.add( spaceAllowed );
            }
        }

        return listSpacesAllowed;
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
     * @param locale The Locale
     * @return The HTML form
     */
    public String getSpacesBrowser( HttpServletRequest request, AdminUser user, Locale locale,
        boolean bFilterViewRollUser, boolean bFilterWorkspaceUser )
    {
        String strIdSpaceFilter = request.getParameter( PARAMETER_BROWSER_SELECTED_SPACE_ID );
        String strIdSpace = request.getParameter( PARAMETER_BROWSER_SPACE_ID );
        Map<String, Object> model = new HashMap<String, Object>(  );
        DocumentSpace selectedSpace = null;
        DocumentSpace space;
        Collection<DocumentSpace> spacesList;
        int nIdSpace = -1;
        int i = 0;
        boolean bIsAuthorized = true;
        boolean bGoUp = true;

        // Selected space
        if ( StringUtils.isNotBlank( strIdSpaceFilter ) && strIdSpaceFilter.matches( REGEX_ID ) )
        {
            selectedSpace = DocumentSpaceHome.findByPrimaryKey( IntegerUtils.convert( strIdSpaceFilter ) );
        }

        // if current space doesn't exists then set it up
        if ( IntegerUtils.isNotNumeric( strIdSpace ) )
        {
            nIdSpace = DocumentSpacesService.getInstance(  ).getUserDefaultSpace( user );
        }
        else
        {
            nIdSpace = IntegerUtils.convert( strIdSpace );
        }

        // set space list
        if ( nIdSpace == -1 )
        {
            space = new DocumentSpace(  );
            space.setId( -1 );
            space.setIdParent( -1 );
        }
        else
        {
            space = DocumentSpaceHome.findByPrimaryKey( nIdSpace );
        }

        spacesList = DocumentSpaceHome.findChilds( space.getId(  ) );

        if ( bFilterViewRollUser )
        {
            bIsAuthorized = isAuthorizedViewByRole( space.getId(  ), user );
        }

        if ( bIsAuthorized && bFilterWorkspaceUser )
        {
            bIsAuthorized = AdminWorkgroupService.isAuthorized( space, user );

            if ( bIsAuthorized )
            {
                spacesList = AdminWorkgroupService.getAuthorizedCollection( spacesList, user );
            }
        }

        // set links for childs spaces
        int[] arrayHasChilds = new int[spacesList.size(  )];

        for ( DocumentSpace documentSpace : spacesList )
        {
            // Check Childs spaces
            List<DocumentSpace> childsSpaces = DocumentSpaceHome.findChilds( documentSpace.getId(  ) );

            //If childs spaces list is not empty, then add into array
            if ( childsSpaces.size(  ) != 0 )
            {
                arrayHasChilds[i] = documentSpace.getId(  );
            }

            i++;
        }

        if ( !bIsAuthorized )
        {
            //display "go up" link
            bGoUp = false;
        }

        model.put( MARK_GO_UP, bGoUp );
        model.put( MARK_SELECTED_SPACE, selectedSpace );
        model.put( MARK_SPACE, space );
        model.put( MARK_SPACES_LIST, spacesList );
        model.put( MARK_URLS_LIST, arrayHasChilds );
        model.put( MARK_ACTION, request.getRequestURI(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_BROWSE_SPACES, locale, model );

        return template.getHtml(  );
    }

    private void addChildSpaces( DocumentSpace spaceParent, List<DocumentSpace> listSpaces )
    {
        listSpaces.add( spaceParent );

        List<DocumentSpace> listChilds = DocumentSpaceHome.findChilds( spaceParent.getId(  ) );

        for ( DocumentSpace space : listChilds )
        {
            addChildSpaces( space, listSpaces );
        }
    }

    /**
     * the list of parents Document space of the document space specified in
     * parameter
     * @param documentSpace the document space
     * @param user the user
     * @return the list of parents Document space of the document space
     *         specified in parameter
     */
    private List<DocumentSpace> getSpaceParents( DocumentSpace documentSpace, AdminUser user )
    {
        List<DocumentSpace> documentSpaceParents = new ArrayList<DocumentSpace>(  );
        getSpaceParents( documentSpace.getIdParent(  ), documentSpaceParents, user );

        return documentSpaceParents;
    }

    /**
     * add in the list of document space the list of parents document space
     * specified in parameter
     * @param nSpaceId the id of the document space
     * @param documentSpaceParents the list of document space
     * @param user the user
     */
    private void getSpaceParents( int nSpaceId, List<DocumentSpace> documentSpaceParents, AdminUser user )
    {
        DocumentSpace documentSpace = DocumentSpaceHome.findByPrimaryKey( nSpaceId );

        if ( AdminWorkgroupService.isAuthorized( documentSpace, user ) )
        {
            if ( documentSpace.getIdParent(  ) != -1 )
            {
                getSpaceParents( documentSpace.getIdParent(  ), documentSpaceParents, user );
            }

            documentSpaceParents.add( documentSpace );
        }
    }

    /**
     * the path of the document space
     * @param nIdDocumentSpace the id of the document space
     * @param user the user
     * @return the path of the document space
     */
    public String getLabelSpacePath( int nIdDocumentSpace, AdminUser user )
    {
        DocumentSpace documentSpace = DocumentSpaceHome.findByPrimaryKey( nIdDocumentSpace );
        StringBuffer sbLabelPath = new StringBuffer(  );

        if ( documentSpace != null )
        {
            if ( documentSpace.getIdParent(  ) != -1 )
            {
                List<DocumentSpace> documentSpaceParents = getSpaceParents( documentSpace, user );

                for ( DocumentSpace documentSpaceParent : documentSpaceParents )
                {
                    sbLabelPath.append( documentSpaceParent.getName(  ) );
                    sbLabelPath.append( "/" );
                }
            }

            sbLabelPath.append( documentSpace.getName(  ) );
        }

        return sbLabelPath.toString(  );
    }
}
