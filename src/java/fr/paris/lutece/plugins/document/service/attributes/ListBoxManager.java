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
package fr.paris.lutece.plugins.document.service.attributes;

import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeParameter;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttributeHome;
import fr.paris.lutece.portal.web.constants.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * Manager for ListBox Attribute
 */
public class ListBoxManager extends DefaultManager
{
    // TEMPLATES
    private static final String TEMPLATE_CREATE_ATTRIBUTE = "admin/plugins/document/attributes/create_listbox.html";
    private static final String TEMPLATE_MODIFY_ATTRIBUTE = "admin/plugins/document/attributes/modify_listbox.html";
    private static final String TEMPLATE_CREATE_PARAMETERS_ATTRIBUTE = "admin/plugins/document/attributes/create_parameters_listbox.html";
    private static final String TEMPLATE_MODIFY_PARAMETERS_ATTRIBUTE = "admin/plugins/document/attributes/modify_parameters_listbox.html";

    // PARAMETERS
    private static final String PARAMETER_ITEMS_VALUE = "items_value";
    private static final String PARAMETER_ITEMS_SELECT = "items_select";
    private static final String PARAMETER_ADD = "add";
    private static final String PARAMETER_DELETE = "delete";
    private static final String PARAMETER_BY_DEFAULT = "bydefault";

    // NAMES
    private static final String NAME_ITEMS = "items";
    private static final String NAME_VALUE = "value";

    /**
     * Gets the template to enter the attribute value
     * @return The template to enter the attribute value
     */
    String getCreateTemplate(  )
    {
        return TEMPLATE_CREATE_ATTRIBUTE;
    }

    /**
     * Gets the template to modify the attribute value
     * @return The template to modify the attribute value
     */
    String getModifyTemplate(  )
    {
        return TEMPLATE_MODIFY_ATTRIBUTE;
    }

    /**
     * Gets the template to enter the parameters of the attribute value
     * @return The template to enter the parameters of the attribute value
     */
    String getCreateParametersTemplate(  )
    {
        return TEMPLATE_CREATE_PARAMETERS_ATTRIBUTE;
    }

    /**
     * Gets the template to modify the parameters of the attribute value
     * @return The template to modify the parameters of the attribute value
     */
    String getModifyParametersTemplate(  )
    {
        return TEMPLATE_MODIFY_PARAMETERS_ATTRIBUTE;
    }

    /**
     * Validate the value for the parameters
     * @param listParameters The list of parameters to check
     * @param locale The current Locale
     * @return null if valid, otherwise message property
     */
    public String validateValueParameters( List<AttributeTypeParameter> listParameters, Locale locale )
    {
        for ( AttributeTypeParameter parameter : listParameters )
        {
            if ( !parameter.getValueList(  ).iterator(  ).hasNext(  ) )
            {
                return Messages.MANDATORY_FIELDS;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreateParametersFormHtml( List<AttributeTypeParameter> listParameters, Locale locale )
    {
        // We sort parameters values alphabetically
        if ( listParameters != null )
        {
            for ( AttributeTypeParameter attributeParameter : listParameters )
            {
                List<String> listValues = attributeParameter.getValueList( );
                if ( listValues != null && listValues.size( ) > 0 )
                {
                    Collections.sort( listValues );
                    attributeParameter.setValueList( listValues );
                }
            }
        }
        return super.getCreateParametersFormHtml( listParameters, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AttributeTypeParameter> getExtraParametersValues( Locale locale, int nAttributeId )
    {
        Collection<AttributeTypeParameter> listParameters = getExtraParameters( locale );

        for ( AttributeTypeParameter parameter : listParameters )
        {
            List<String> listValues = DocumentAttributeHome.getAttributeParameterValues( nAttributeId,
                    parameter.getName( ) );
            Collections.sort( listValues );
            parameter.setValueList( listValues );
        }

        return listParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AttributeTypeParameter> getValueParameters( HttpServletRequest request, Locale locale )
    {
        // Button "Add" new parameter
        boolean bAddNewValue = StringUtils.isNotBlank( request.getParameter( PARAMETER_ADD ) );

        // Button "Delete" parameters
        boolean bDeleteValue = StringUtils.isNotBlank( request.getParameter( PARAMETER_DELETE ) );
        boolean bByDefault = StringUtils.isNotBlank( request.getParameter( PARAMETER_BY_DEFAULT ) );
        List<AttributeTypeParameter> listParameters = super.getExtraParameters( locale );

        for ( AttributeTypeParameter parameter : listParameters )
        {
            List<String> listValues = new ArrayList<String>(  );
            String[] arrayStrValues = request.getParameterValues( parameter.getName(  ) );

            if ( NAME_VALUE.equals( parameter.getName(  ) ) )
            {
                // Define default value
                if ( bByDefault )
                {
                    String strValue = request.getParameter( PARAMETER_ITEMS_SELECT );

                    if ( StringUtils.isNotBlank( strValue ) )
                    {
                        listValues.add( strValue );
                    }
                }
                else
                {
                    if ( ( arrayStrValues != null ) && ( arrayStrValues.length > 0 ) )
                    {
                        listValues.addAll( Arrays.asList( arrayStrValues ) );
                    }
                }
            }
            else if ( NAME_ITEMS.equals( parameter.getName(  ) ) )
            {
                if ( ( arrayStrValues != null ) && ( arrayStrValues.length > 0 ) )
                {
                    listValues.addAll( Arrays.asList( arrayStrValues ) );
                }

                // Add new value
                if ( bAddNewValue )
                {
                    String strNewValue = request.getParameter( PARAMETER_ITEMS_VALUE );

                    if ( StringUtils.isNotBlank( strNewValue ) )
                    {
                        listValues.add( strNewValue );
                    }
                }

                // Remove value
                if ( bDeleteValue )
                {
                    String strValue = request.getParameter( PARAMETER_ITEMS_SELECT );

                    if ( StringUtils.isNotBlank( strValue ) )
                    {
                        listValues.remove( strValue );
                        removeParameterValue( listParameters, strValue );
                    }
                }
            }

            parameter.setValueList( listValues );
            listValues.clear(  );
        }
        return listParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, List<String>> getParameters( int nAttributeId, Locale locale )
    {
        HashMap<String, List<String>> mapParameters = new HashMap<String, List<String>>( );
        Collection<AttributeTypeParameter> listParameters = getAttributeParametersValues( nAttributeId, locale );

        for ( AttributeTypeParameter parameter : listParameters )
        {
            // We sort attributes alphabetically
            List<String> listValues = parameter.getValueList( );
            Collections.sort( listValues );
            mapParameters.put( parameter.getName( ), listValues );
        }

        // Set all missing parameters with their default values
        for ( AttributeTypeParameter parameter : getExtraParameters( locale ) )
        {
            if ( !mapParameters.containsKey( parameter.getName( ) ) )
            {
                mapParameters.put( parameter.getName( ), parameter.getDefaultValue( ) );
            }
        }

        return mapParameters;
    }

    /**
     * Remove a parameter value from a list
     * @param listParameters the list of parameters
     * @param strValueToRemove the value to remove
     */
    private void removeParameterValue( List<AttributeTypeParameter> listParameters, String strValueToRemove )
    {
        for ( AttributeTypeParameter parameter : listParameters )
        {
            if ( NAME_VALUE.equals( parameter.getName(  ) ) )
            {
                if ( StringUtils.isNotBlank( strValueToRemove ) )
                {
                    List<String> listValues = parameter.getValueList(  );
                    listValues.remove( strValueToRemove );
                    parameter.setValueList( listValues );

                    break;
                }
            }
        }
    }
}
