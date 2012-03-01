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
package fr.paris.lutece.plugins.document.service.attributes;

import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeParameter;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Manager for Date Attribute
 */
public class DateManager extends DefaultManager
{
    private static final String TEMPLATE_CREATE_ATTRIBUTE = "admin/plugins/document/attributes/create_date.html";
    private static final String TEMPLATE_MODIFY_ATTRIBUTE = "admin/plugins/document/attributes/modify_date.html";
    private static final String TEMPLATE_CREATE_PARAMETERS_ATTRIBUTE = "admin/plugins/document/attributes/create_parameters_date.html";
    private static final String TEMPLATE_MODIFY_PARAMETERS_ATTRIBUTE = "admin/plugins/document/attributes/modify_parameters_date.html";
    private static final String PROPERTY_MESSAGE_ERROR_FORMAT_DATE = "document.message.errorDateFormat";
    private static final String[] DATE_FORMAT = { "yyyy", "MM", "MM/yyyy", "dd/MM/yyyy" };
    private static final String CHECK_ON = "on";

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
     * Validate the value for the attribute
     * @param nAttributeId The attribute identifier
     * @param strValue The value to check
     * @param locale The current Locale
     * @return null if valid, otherwise error message
     */
    public String validateValue( int nAttributeId, String strValue, Locale locale )
    {
        String strValidateValue = super.validateValue( nAttributeId, strValue, locale );

        if ( strValidateValue != null )
        {
            return strValidateValue;
        }
        else
        {
            String strValidateDate = validateDate( strValue, locale );

            if ( strValidateDate != null )
            {
                return I18nService.getLocalizedString( strValidateDate, locale );
            }
        }

        return null;
    }

    /**
     * Validate the value for the parameters
     * @param listParameters The list of parameters to check
     * @param locale The current Locale
     * @return null if valid, otherwise message property
     */
    public String validateValueParameters( List<AttributeTypeParameter> listParameters, Locale locale )
    {
        String strValue;
        String strReturn = null;

        for ( AttributeTypeParameter parameter : listParameters )
        {
            strValue = parameter.getValueList(  ).iterator(  ).hasNext(  )
                ? parameter.getValueList(  ).iterator(  ).next(  ) : "";

            // if current date is checked, return null
            if ( strValue.equals( CHECK_ON ) )
            {
                strReturn = null;
            }
            else
            {
                // if validation failed, fill strReturn
                String strValidate = validateDate( strValue, locale );

                if ( strValidate != null )
                {
                    strReturn = strValidate;
                }
            }
        }

        return strReturn;
    }

    /**
     * Gets parameters values for the attribute
     * @param nAttributeId The attribute Id
     * @param locale The current Locale
     * @return
     */
    public Collection<AttributeTypeParameter> getAttributeParametersValues( int nAttributeId, Locale locale )
    {
        Collection<AttributeTypeParameter> listParameters = super.getAttributeParametersValues( nAttributeId, locale );

        for ( AttributeTypeParameter parameter : listParameters )
        {
            if ( parameter.getValueList(  ).iterator(  ).hasNext(  ) &&
                    parameter.getValueList(  ).iterator(  ).next(  ).equals( CHECK_ON ) )
            {
                //replace CHECK_ON by current date
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat( DATE_FORMAT[3] );
                Date date = new Date(  );
                ArrayList<String> listValues = new ArrayList<String>(  );
                listValues.add( simpleDateFormat.format( date ) );
                parameter.setValueList( listValues );
            }
        }

        return listParameters;
    }

    /**
     * Validate the date for the attribute
     * @param strDate The date to check
     * @param locale The current Locale
     * @return null if valid, otherwise error message
     */
    private String validateDate( String strDate, Locale locale )
    {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(  );
        int i = 0;

        if ( ( strDate == null ) || strDate.equals( "" ) )
        {
            return null;
        }

        while ( ( date == null ) && ( i < DATE_FORMAT.length ) )
        {
            simpleDateFormat.applyPattern( DATE_FORMAT[i] );

            if ( strDate.length(  ) == DATE_FORMAT[i].length(  ) )
            {
                try
                {
                    date = simpleDateFormat.parse( strDate );
                }
                catch ( ParseException e )
                {
                    AppLogService.debug( "Bad date format" );
                }
            }

            i++;
        }

        if ( date == null )
        {
            date = DateUtil.formatDate( strDate, locale );
        }
        else if ( !simpleDateFormat.format( date ).equals( strDate ) )
        {
            return PROPERTY_MESSAGE_ERROR_FORMAT_DATE;
        }

        if ( date == null )
        {
            return PROPERTY_MESSAGE_ERROR_FORMAT_DATE;
        }

        return null;
    }
}
