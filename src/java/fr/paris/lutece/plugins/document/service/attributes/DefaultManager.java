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

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeHome;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeParameter;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttributeHome;
import fr.paris.lutece.plugins.document.service.AttributeManager;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Default implementation of an attribute manager
 */
public abstract class DefaultManager implements AttributeManager
{
    private static final String MARK_ATTRIBUTE = "attribute";
    private static final String MARK_ATTRIBUTE_PARAMETERS = "parameters";
    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_BASE_URL = "base_url";
    private String _strAttributeTypeCode;

    /**
     * Returns the Create HTML template correponding to this attribue
     * @return The Create HTML template correponding to this attribue
     */
    abstract String getCreateTemplate(  );

    /**
     * Returns the Modify HTML template correponding to this attribue
     * @return the Modify HTML template correponding to this attribue
     */
    abstract String getModifyTemplate(  );

    /**
     * Returns the Create HTML template for parameters correponding to this attribue
     * @return the Create HTML template for parameters correponding to this attribue
     */
    abstract String getCreateParametersTemplate(  );

    /**
     * Returns the Modify HTML template for parameters correponding to this attribue
     * @return the Modify HTML template for parameters correponding to this attribue
     */
    abstract String getModifyParametersTemplate(  );

    /**
     * Gets the part of an HTML form to enter attribute data
     * @param attribute The attribute
     * @param locale The current Locale
     * @param strBaseUrl The base url
     * @return A part of the HTML form
     */
    public String getCreateFormHtml( DocumentAttribute attribute, Locale locale, String strBaseUrl )
    {
        HashMap model = new HashMap(  );
        model.put( MARK_ATTRIBUTE, attribute );
        model.put( MARK_ATTRIBUTE_PARAMETERS, getParameters( attribute.getId(  ), locale ) );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_BASE_URL, strBaseUrl );

        HtmlTemplate template = AppTemplateService.getTemplate( getCreateTemplate(  ), locale, model );

        return template.getHtml(  );
    }

    /**
     * Gets the part of an HTML form to modify attribute data
     * @param attribute The attribute
     * @param document The Document Object
     * @param locale The current Locale
     * @param strBaseUrl The base url
     * @return A part of the HTML form
     */
    public String getModifyFormHtml( DocumentAttribute attribute, Document document, Locale locale, String strBaseUrl )
    {
        HashMap model = new HashMap(  );
        model.put( MARK_ATTRIBUTE, attribute );
        model.put( MARK_ATTRIBUTE_PARAMETERS, getParameters( attribute.getId(  ), locale ) );
        model.put( MARK_DOCUMENT, document );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_BASE_URL, strBaseUrl );

        HtmlTemplate template = AppTemplateService.getTemplate( getModifyTemplate(  ), locale, model );

        return template.getHtml(  );
    }

    /**
     * Gets the part of an HTML form to enter parameters data
     * @param locale The current Locale
     * @return A part of the HTML form
     */
    public String getCreateParametersFormHtml( Locale locale )
    {
        HashMap model = new HashMap(  );
        String strUrlTemplate = getCreateParametersTemplate(  );

        if ( strUrlTemplate == null )
        {
            return "";
        }

        model.put( MARK_ATTRIBUTE_PARAMETERS, getExtraParameters( locale ) );
        model.put( MARK_LOCALE, locale );

        HtmlTemplate template = AppTemplateService.getTemplate( getCreateParametersTemplate(  ), locale, model );

        return template.getHtml(  );
    }

    /**
     * Gets the part of an HTML form to modify parameters data
     * @param locale The current Locale
     * @param nAttributeId Attribute Id
     * @return A part of the HTML form
     */
    public String getModifyParametersFormHtml( Locale locale, int nAttributeId )
    {
        HashMap model = new HashMap(  );
        String strUrlTemplate = getModifyParametersTemplate(  );

        if ( strUrlTemplate == null )
        {
            return "";
        }

        model.put( MARK_ATTRIBUTE_PARAMETERS, getExtraParametersValues( locale, nAttributeId ) );
        model.put( MARK_LOCALE, locale );

        HtmlTemplate template = AppTemplateService.getTemplate( strUrlTemplate, locale, model );

        return template.getHtml(  );
    }

    /**
     * Gets extra parameters for the attribute
     * @param locale The current Locale
     * @return A List of attribute parameters
     */
    public List<AttributeTypeParameter> getExtraParameters( Locale locale )
    {
        return AttributeTypeHome.getAttributeTypeParameterList( getAttributeTypeCode(  ), locale );
    }

    /**
     * Gets extra parameters values for the attribute
     * @param locale The current Locale
     * @param nAttributeId The attribute identifier
     * @return a list of parameters
     */
    public Collection<AttributeTypeParameter> getExtraParametersValues( Locale locale, int nAttributeId )
    {
        Collection<AttributeTypeParameter> listParameters = getExtraParameters( locale );

        for ( AttributeTypeParameter parameter : listParameters )
        {
            parameter.setValueList( DocumentAttributeHome.getAttributeParameterValues( nAttributeId,
                    parameter.getName(  ) ) );
        }

        return listParameters;
    }

    /**
     * Define the attribute Type code
     * @param strCode The Attribute Type Code
     */
    public void setAttributeTypeCode( String strCode )
    {
        _strAttributeTypeCode = strCode;
    }

    /**
     * Return the attribute Type code
     * @return The Attribute Type Code
     */
    public String getAttributeTypeCode(  )
    {
        return _strAttributeTypeCode;
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
        // Regular expression validation
        if ( RegularExpressionService.getInstance(  ).isAvailable(  ) && ( strValue != null ) &&
                !strValue.equals( "" ) )
        {
            Collection<Integer> colRegularExpression = DocumentAttributeHome.getListRegularExpressionKeyByIdAttribute( nAttributeId );

            if ( ( colRegularExpression != null ) && ( colRegularExpression.size(  ) != 0 ) )
            {
                for ( Integer nExpressionId : colRegularExpression )
                {
                    RegularExpression regularExpression = RegularExpressionService.getInstance(  )
                                                                                  .getRegularExpressionByKey( nExpressionId );

                    if ( !RegularExpressionService.getInstance(  ).isMatches( strValue, regularExpression ) )
                    {
                        return regularExpression.getErrorMessage(  );
                    }
                }
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
        return null;
    }

    /**
     * Get the XML data corresponding to the attribute to build the document XML content
     * @param document The document
     * @param attribute  The attribute
     * @return The XML value of the attribute
     */
    public String getAttributeXmlValue( Document document, DocumentAttribute attribute )
    {
        return attribute.getTextValue(  );
    }

    /**
     * Gets parameters values for the attribute
     * @param nAttributeId The attribute Id
     * @param locale The current Locale
     * @return Collection<AttributeTypeParameter> list of attribute type parameters
     */
    public Collection<AttributeTypeParameter> getAttributeParametersValues( int nAttributeId, Locale locale )
    {
        return DocumentAttributeHome.getAttributeParametersValues( nAttributeId, locale );
    }

    /**
     * Get parameters map
     * @param nAttributeId the attribute identifier
     * @parma locale The current locale
     * return a map of parameters
     */
    private Map<String, List<String>> getParameters( int nAttributeId, Locale locale )
    {
        HashMap<String, List<String>> mapParameters = new HashMap<String, List<String>>(  );
        Collection<AttributeTypeParameter> listParameters = getAttributeParametersValues( nAttributeId, locale );

        for ( AttributeTypeParameter parameter : listParameters )
        {
            mapParameters.put( parameter.getName(  ), parameter.getValueList(  ) );
        }

        // Set all missing parameters with their default values
        for ( AttributeTypeParameter parameter : getExtraParameters( locale ) )
        {
            if ( !mapParameters.containsKey( parameter.getName(  ) ) )
            {
                mapParameters.put( parameter.getName(  ), parameter.getDefaultValue(  ) );
            }
        }

        return mapParameters;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeUsedAsThumbnail(  )
    {
        return false;
    }
}
