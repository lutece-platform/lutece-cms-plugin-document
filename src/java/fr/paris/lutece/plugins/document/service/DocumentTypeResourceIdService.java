/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.document.service;

import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;


/**
 * Resource Id service for RBAC features to control access to document depending their type
 */
public class DocumentTypeResourceIdService extends ResourceIdService
{
    /** Permission for creating a document */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Permission for viewing a document */
    public static final String PERMISSION_VIEW = "VIEW";

    /** Permission for deleting a document */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for modifying a document */
    public static final String PERMISSION_MODIFY = "MODIFY";

    /** Permission for modifying a document */
    public static final String PERMISSION_CHANGE = "CHANGE";

    /** Permission for moving a document */
    public static final String PERMISSION_MOVE = "MOVE";

    /** Permission for validating a document */
    public static final String PERMISSION_VALIDATE = "VALIDATE";

    /** Permission for submiting a document */
    public static final String PERMISSION_SUBMIT = "SUBMIT";

    /** Permission for archiving a document */
    public static final String PERMISSION_ARCHIVE = "ARCHIVE";

    /** Permission for publishing a document */
    public static final String PERMISSION_PUBLISH = "PUBLISH";

    /** Permission for assigning a document */
    public static final String PERMISSION_ASSIGN = "ASSIGN";

    /** Permission for approve comment a document */
    // public static final String PERMISSION_COMMENT = "COMMENT";

    /** Permission for viewing a space history */
    public static final String PERMISSION_VIEW_HISTORY = "VIEW_HISTORY";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "document.workflow.resourceType";
    private static final String PROPERTY_LABEL_CREATE = "document.workflow.permission.label.create";
    private static final String PROPERTY_LABEL_VIEW = "document.workflow.permission.label.view";
    private static final String PROPERTY_LABEL_DELETE = "document.workflow.permission.label.delete";
    private static final String PROPERTY_LABEL_MODIFY = "document.workflow.permission.label.modify";
    private static final String PROPERTY_LABEL_CHANGE = "document.workflow.permission.label.change";
    private static final String PROPERTY_LABEL_MOVE = "document.workflow.permission.label.move";
    private static final String PROPERTY_LABEL_VALIDATE = "document.workflow.permission.label.validate";
    private static final String PROPERTY_LABEL_SUBMIT = "document.workflow.permission.label.submit";
    private static final String PROPERTY_LABEL_ARCHIVE = "document.workflow.permission.label.archive";
    private static final String PROPERTY_LABEL_PUBLISH = "document.workflow.permission.label.publish";
    private static final String PROPERTY_LABEL_ASSIGN = "document.workflow.permission.label.assign";

    //private static final String PROPERTY_LABEL_COMMENT = "document.workflow.permission.label.comment";
    private static final String PROPERTY_LABEL_VIEW_HISTORY = "document.workflow.permission.label.viewHistory";
    private static final String PLUGIN_NAME = "document";

    /** Creates a new instance of DocumentTypeResourceIdService */
    public DocumentTypeResourceIdService(  )
    {
        setPluginName( PLUGIN_NAME );
    }

    /**
     * Initializes the service
     */
    public void register(  )
    {
        ResourceType rt = new ResourceType(  );
        rt.setResourceIdServiceClass( DocumentTypeResourceIdService.class.getName(  ) );
        rt.setPluginName( PLUGIN_NAME );
        rt.setResourceTypeKey( DocumentType.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission(  );
        p.setPermissionKey( PERMISSION_CREATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_VIEW );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MODIFY );
        p.setPermissionTitleKey( PROPERTY_LABEL_MODIFY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_CHANGE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CHANGE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MOVE );
        p.setPermissionTitleKey( PROPERTY_LABEL_MOVE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_VALIDATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_VALIDATE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_SUBMIT );
        p.setPermissionTitleKey( PROPERTY_LABEL_SUBMIT );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_ARCHIVE );
        p.setPermissionTitleKey( PROPERTY_LABEL_ARCHIVE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_PUBLISH );
        p.setPermissionTitleKey( PROPERTY_LABEL_PUBLISH );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_ASSIGN );
        p.setPermissionTitleKey( PROPERTY_LABEL_ASSIGN );
        rt.registerPermission( p );

        /*    p = new Permission(  );
            p.setPermissionKey( PERMISSION_COMMENT );
            p.setPermissionTitleKey( PROPERTY_LABEL_COMMENT );
            rt.registerPermission( p );*/
        p = new Permission(  );
        p.setPermissionKey( PERMISSION_VIEW_HISTORY );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW_HISTORY );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * Returns a list of resource ids
     * @param locale The current locale
     * @return A list of resource ids
     */
    public ReferenceList getResourceIdList( Locale locale )
    {
        return DocumentTypeHome.getDocumentTypesList(  );
    }

    /**
     * Returns the Title of a given resource
     * @param strId The Id of the resource
     * @param locale The current locale
     * @return The Title of a given resource
     */
    public String getTitle( String strId, Locale locale )
    {
        DocumentType type = DocumentTypeHome.findByPrimaryKey( strId );

        return type.getName(  );
    }
}
