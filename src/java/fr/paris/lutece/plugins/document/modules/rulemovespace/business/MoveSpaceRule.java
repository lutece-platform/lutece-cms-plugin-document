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
package fr.paris.lutece.plugins.document.modules.rulemovespace.business;

import fr.paris.lutece.plugins.document.business.rules.AbstractRule;
import fr.paris.lutece.plugins.document.business.rules.Rule;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceHome;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.plugins.document.business.workflow.DocumentStateHome;
import fr.paris.lutece.plugins.document.service.DocumentEvent;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.plugins.document.service.spaces.SpaceRemovalListenerService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;


/**
 * This rule lets move a document from a space to another when the document's state is changing
 */
public class MoveSpaceRule extends AbstractRule
{
    private static final String TEMPLATE_CREATE_RULE = "/admin/plugins/document/modules/rulemovespace/create_rule_move_space.html";
    private static final String MARK_STATES_LIST = "states_list";
    private static final String MARK_STATE_ID = "id_state";
    private static final String MARK_SPACE_SOURCE_PATH = "path_space_source";
    private static final String MARK_SPACE_SOURCE_ID = "id_space_source";
    private static final String MARK_SPACE_DESTINATION_PATH = "path_space_destination";
    private static final String MARK_SPACE_DESTINATION_ID = "id_space_destination";
    private static final String PARAMETER_SPACE_SOURCE_ID = "id_space_source";
    private static final String PARAMETER_SPACE_DESTINATION_ID = "id_space_destination";
    private static final String PARAMETER_STATE_ID = "id_state";
    private static final String PROPERTY_RULE_DESCRIPTION = "module.document.rulemovespace.ruleLiteral";
    private static final String PROPERTY_RULE_ERROR_NOT_SELECT_SPACE_SOURCE = "module.document.rulemovespace.message.create_rule_move_space.errorNotSelectSpaceSource";
    private static final String PROPERTY_RULE_ERROR_NOT_SELECT_SPACE_DESTINATION = "module.document.rulemovespace.message.create_rule_move_space.errorNotSelectSpaceDestination";
    private static final String PROPERTY_RULE_ERROR_SAME_SPACE = "module.document.rulemovespace.message.create_rule_move_space.errorSameSpace";
    private static final String PROPERTY_RULE_UNKNOWN_ERROR = "module.document.rulemovespace.message.create_rule_move_space.unknownError";
    private static final String PROPERTY_RULE_NAME = "module.document.rulemovespace.ruleName";
    private static String[] _attributes = { PARAMETER_SPACE_SOURCE_ID, PARAMETER_SPACE_DESTINATION_ID, PARAMETER_STATE_ID };
    private static MoveSpaceSpaceRemovalListener _listenerSpaces;

    /**
     * Initialize the rule
     */
    public void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerSpaces == null )
        {
            _listenerSpaces = new MoveSpaceSpaceRemovalListener(  );
            SpaceRemovalListenerService.getService(  ).registerListener( _listenerSpaces );
        }
    }

    /**
     * Gets the Rule name key
     * @return The Rule name key
     */
    public String getNameKey(  )
    {
        return PROPERTY_RULE_NAME;
    }

    /**
     * Execute the rule
     * @param event The document event
     * @throws DocumentException raise when error occurs in event or rule
     */
    public void apply( DocumentEvent event ) throws DocumentException
    {
        try
        {
            int nSourceSpace = Integer.parseInt( getAttribute( PARAMETER_SPACE_SOURCE_ID ) );
            int nDestinationSpace = Integer.parseInt( getAttribute( PARAMETER_SPACE_DESTINATION_ID ) );
            int nState = Integer.parseInt( getAttribute( PARAMETER_STATE_ID ) );

            if ( ( event.getStateId(  ) == nState ) && ( nSourceSpace != nDestinationSpace ) )
            {
                if ( event.getSpaceId(  ) == nSourceSpace )
                {
                    DocumentService.getInstance(  )
                                   .moveDocument( event.getDocument(  ), event.getUser(  ), nDestinationSpace );
                }
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( "Error in MoveSpaceRule event : " + e.getMessage(  ), e );
            throw new DocumentException( PROPERTY_RULE_UNKNOWN_ERROR );
        }
    }

    /**
     * Gets a specific form to enter rule's attributes
     * @param user The current user
     * @param locale The current Locale
     * @return The HTML code of the form
     */
    public String getCreateForm( AdminUser user, Locale locale )
    {
        HashMap model = new HashMap(  );

        //Collection listSpaces = DocumentSpaceHome.getDocumentSpaceList(  );
        Collection listStates = DocumentStateHome.getDocumentStatesList( locale );
        //model.put( MARK_SPACES_LIST, listSpaces );
        model.put( MARK_STATES_LIST, listStates );

        if ( this.getAttribute( PARAMETER_STATE_ID ) != null )
        {
            model.put( MARK_STATE_ID, this.getAttribute( PARAMETER_STATE_ID ) );
        }

        if ( this.getAttribute( PARAMETER_SPACE_SOURCE_ID ) != null )
        {
            int nIdSpaceSource = -1;
            String strPathSpaceSource;

            try
            {
                nIdSpaceSource = Integer.parseInt( this.getAttribute( PARAMETER_SPACE_SOURCE_ID ) );
                model.put( MARK_SPACE_SOURCE_ID, nIdSpaceSource );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }

            strPathSpaceSource = DocumentSpacesService.getInstance(  ).getLabelSpacePath( nIdSpaceSource, user );
            model.put( MARK_SPACE_SOURCE_PATH, strPathSpaceSource );
        }

        if ( this.getAttribute( PARAMETER_SPACE_DESTINATION_ID ) != null )
        {
            int nIdSpaceDestination = -1;
            String strPathSpaceDestination;

            try
            {
                nIdSpaceDestination = Integer.parseInt( this.getAttribute( PARAMETER_SPACE_DESTINATION_ID ) );
                model.put( MARK_SPACE_DESTINATION_ID, nIdSpaceDestination );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }

            strPathSpaceDestination = DocumentSpacesService.getInstance(  ).getLabelSpacePath( nIdSpaceDestination, user );
            model.put( MARK_SPACE_DESTINATION_PATH, strPathSpaceDestination );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_RULE, locale, model );

        return template.getHtml(  );
    }

    /**
     * true if the user is authorized to view the rule
     * @param user the current user
     * @return true if the user is authorized to view the rule
     */
    public boolean isAuthorized( AdminUser user )
    {
        int nSourceSpaceId = Integer.parseInt( getAttribute( PARAMETER_SPACE_SOURCE_ID ) );
        int nDestinationSpaceId = Integer.parseInt( getAttribute( PARAMETER_SPACE_DESTINATION_ID ) );

        if ( !DocumentSpacesService.getInstance(  ).isAuthorizedViewByWorkgroup( nSourceSpaceId, user ) )
        {
            return false;
        }

        if ( !DocumentSpacesService.getInstance(  ).isAuthorizedViewByWorkgroup( nDestinationSpaceId, user ) )
        {
            return false;
        }

        return true;
    }

    /**
     * Check the rule
     *
     * @return null if rule is valid, message if rule not valid
     */
    public String validateRule(  )
    {
        String strSourceSpaceId = getAttribute( PARAMETER_SPACE_SOURCE_ID );
        String strDestinationSpaceId = getAttribute( PARAMETER_SPACE_DESTINATION_ID );

        if ( strSourceSpaceId == null )
        {
            return PROPERTY_RULE_ERROR_NOT_SELECT_SPACE_SOURCE;
        }

        if ( strDestinationSpaceId == null )
        {
            return PROPERTY_RULE_ERROR_NOT_SELECT_SPACE_DESTINATION;
        }

        int nSourceSpace = Integer.parseInt( getAttribute( PARAMETER_SPACE_SOURCE_ID ) );
        int nDestinationSpace = Integer.parseInt( getAttribute( PARAMETER_SPACE_DESTINATION_ID ) );

        if ( nSourceSpace == nDestinationSpace )
        {
            return PROPERTY_RULE_ERROR_SAME_SPACE;
        }

        return null;
    }

    /**
     * Gets all attributes of the rule
     * @return attributes of the rule
     */
    public String[] getAttributesList(  )
    {
        return _attributes;
    }

    /**
     * Gets the explicit text of the rule
     * @return The text of the rule
     */
    public String getRule(  )
    {
        int nSourceSpaceId = Integer.parseInt( getAttribute( PARAMETER_SPACE_SOURCE_ID ) );
        String strSourceSpace = DocumentSpacesService.getInstance(  ).getLabelSpacePath( nSourceSpaceId, getUser(  ) );
        int nDestinationSpaceId = Integer.parseInt( getAttribute( PARAMETER_SPACE_DESTINATION_ID ) );
        String strDestinationSpace = DocumentSpacesService.getInstance(  )
                                                          .getLabelSpacePath( nDestinationSpaceId, getUser(  ) );
        int nStateId = Integer.parseInt( getAttribute( PARAMETER_STATE_ID ) );
        DocumentState state = DocumentStateHome.findByPrimaryKey( nStateId );
        state.setLocale( getLocale(  ) );

        String strState = state.getName(  );
        String[] ruleArgs = { strSourceSpace, strState, strDestinationSpace };

        return I18nService.getLocalizedString( PROPERTY_RULE_DESCRIPTION, ruleArgs, getLocale(  ) );
    }

    /**
     * Get the parameter key for the source space Id
     * @return The parameter key
     */
    public static String getParameterKeySourceSpaceId(  )
    {
        return PARAMETER_SPACE_SOURCE_ID;
    }

    /**
     * Get the parameter key for the destination space Id
     * @return The parameter key
     */
    public static String getParameterKeyDestinationSpaceId(  )
    {
        return PARAMETER_SPACE_DESTINATION_ID;
    }

    /**
     * Get the parameter key for the state Id
     * @return The parameter key
     */
    public static String getParameterKeyStateId(  )
    {
        return PARAMETER_STATE_ID;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }

        if ( obj instanceof MoveSpaceRule )
        {
            MoveSpaceRule rule = (MoveSpaceRule) obj;

            if ( ( this.getAttribute( PARAMETER_SPACE_SOURCE_ID ) == null ) ||
                    ( this.getAttribute( PARAMETER_SPACE_DESTINATION_ID ) == null ) ||
                    ( this.getAttribute( PARAMETER_STATE_ID ) == null ) ||
                    ( rule.getAttribute( PARAMETER_SPACE_SOURCE_ID ) == null ) ||
                    ( rule.getAttribute( PARAMETER_SPACE_DESTINATION_ID ) == null ) ||
                    ( rule.getAttribute( PARAMETER_STATE_ID ) == null ) )
            {
                return false;
            }

            if ( this.getAttribute( PARAMETER_SPACE_SOURCE_ID ).equals( rule.getAttribute( PARAMETER_SPACE_SOURCE_ID ) ) &&
                    this.getAttribute( PARAMETER_SPACE_DESTINATION_ID )
                            .equals( rule.getAttribute( PARAMETER_SPACE_DESTINATION_ID ) ) &&
                    this.getAttribute( PARAMETER_STATE_ID ).equals( rule.getAttribute( PARAMETER_STATE_ID ) ) )
            {
                return true;
            }
        }

        return false;
    }
}
