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
package fr.paris.lutece.plugins.document.service.attributes;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeHome;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeParameter;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttributeHome;
import fr.paris.lutece.plugins.document.business.attributes.IMapProvider;
import fr.paris.lutece.plugins.document.business.attributes.MapProviderManager;
import fr.paris.lutece.plugins.document.service.AttributeManager;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang3.StringUtils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


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
    private static final String GEOLOC_JSON_PATH_FEATURESS = "features";
    private static final String GEOLOC_JSON_PATH_PROPERTIES = "properties";
    private static final String GEOLOC_JSON_PATH_GEOMETRY = "geometry";
    private static final String GEOLOC_JSON_PATH_COORDINATES = "coordinates";
    private static final String GEOLOC_JSON_PATH_ADDRESS = "address";
    private static final String MARK_GISMAP_GEOMETRY = "gismap_geometry";
    private static final String MARK_GISMAP_X = "gismap_x";
    private static final String MARK_GISMAP_Y = "gismap_y";
    private static final String MARK_GISMAP_ADDRESS = "gismap_address";
    private String _strAttributeTypeCode;

    /**
     * Returns the Create HTML template corresponding to this attribute
     * @return The Create HTML template corresponding to this attribute
     */
    abstract protected String getCreateTemplate(  );

    /**
     * Returns the Modify HTML template corresponding to this attribute
     * @return the Modify HTML template corresponding to this attribute
     */
    abstract protected String getModifyTemplate(  );

    /**
     * Returns the Create HTML template for parameters corresponding to this
     * attribute
     * @return the Create HTML template for parameters corresponding to this
     *         attribute
     */
    abstract protected String getCreateParametersTemplate(  );

    /**
     * Returns the Modify HTML template for parameters corresponding to this
     * attribute
     * @return the Modify HTML template for parameters corresponding to this
     *         attribute
     */
    abstract protected String getModifyParametersTemplate(  );

    /**
     * {@inheritDoc}
     */
    public String getCreateFormHtml( DocumentAttribute attribute, Locale locale, String strBaseUrl )
    {
        if ( attribute.getCodeAttributeType(  ).equals( "geoloc" ) )
        {
            attribute.setMapProvider( MapProviderManager.getMapProvider( "gismap" ) );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ATTRIBUTE, attribute );
        model.put( MARK_ATTRIBUTE_PARAMETERS, getParameters( attribute.getId(  ), locale ) );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_BASE_URL, strBaseUrl );

        HtmlTemplate template = AppTemplateService.getTemplate( getCreateTemplate(  ), locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    public String getModifyFormHtml( DocumentAttribute attribute, Document document, Locale locale, String strBaseUrl )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ATTRIBUTE, attribute );
        model.put( MARK_ATTRIBUTE_PARAMETERS, getParameters( attribute.getId(  ), locale ) );
        model.put( MARK_DOCUMENT, document );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_BASE_URL, strBaseUrl );
        
        if ( attribute.getCodeAttributeType(  ).equals( "geoloc" ) )
        {
            attribute.setMapProvider( MapProviderManager.getMapProvider( "gismap" ) );
            if(attribute.getMapProvider()!=null)
            {
            	String strValue = attribute.getTextValue(  );

                JsonNode object = null;

                try
                {
                    object = new ObjectMapper(  ).readTree( strValue );
                }
                catch ( IOException e )
                {
                    AppLogService.error( "Erreur ", e );
                }

                String strGeometry = "";
                String strAddress = "";
                String strX = "";
                String strY = "";

                if ( object != null )
                {
                    strGeometry = object.path( GEOLOC_JSON_PATH_FEATURESS ).path( 1 ).toString(  );
                    strAddress = object.path( GEOLOC_JSON_PATH_FEATURESS ).path( 0 ).path( GEOLOC_JSON_PATH_PROPERTIES )
                                       .path( GEOLOC_JSON_PATH_ADDRESS ).toString(  );
                    strX = object.path( GEOLOC_JSON_PATH_FEATURESS ).path( 0 ).path( GEOLOC_JSON_PATH_GEOMETRY )
                            .path( GEOLOC_JSON_PATH_COORDINATES ).path(0).toString(  );
                    strY = object.path( GEOLOC_JSON_PATH_FEATURESS ).path( 0 ).path( GEOLOC_JSON_PATH_GEOMETRY )
                            .path( GEOLOC_JSON_PATH_COORDINATES ).path(1).toString(  );
                }
                model.put( MARK_GISMAP_GEOMETRY, strGeometry );
                model.put( MARK_GISMAP_ADDRESS, strAddress );
                model.put( MARK_GISMAP_X, strX );
                model.put( MARK_GISMAP_Y, strY );
            }
        }
        

        HtmlTemplate template = AppTemplateService.getTemplate( getModifyTemplate(  ), locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    public String getCreateParametersFormHtml( Locale locale )
    {
        return getCreateParametersFormHtml( null, locale );
    }

    /**
     * {@inheritDoc}
     */
    public String getCreateParametersFormHtml( List<AttributeTypeParameter> listParameters, Locale locale )
    {
        return getCreateParametersFormHtml( listParameters, locale, null );
    }

    public String getCreateParametersFormHtml( List<AttributeTypeParameter> listParameters, Locale locale,
        Map<String, Object> model )
    {
        if ( model == null )
        {
            model = new HashMap<String, Object>(  );
        }

        String strUrlTemplate = getCreateParametersTemplate(  );

        if ( strUrlTemplate == null )
        {
            return StringUtils.EMPTY;
        }

        if ( ( listParameters != null ) && !listParameters.isEmpty(  ) )
        {
            model.put( MARK_ATTRIBUTE_PARAMETERS, listParameters );
        }
        else
        {
            model.put( MARK_ATTRIBUTE_PARAMETERS, getExtraParameters( locale ) );
        }

        model.put( MARK_LOCALE, locale );

        HtmlTemplate template = AppTemplateService.getTemplate( getCreateParametersTemplate(  ), locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    public String getModifyParametersFormHtml( Locale locale, int nAttributeId )
    {
        return getModifyParametersFormHtml( locale, nAttributeId, null );
    }

    public String getModifyParametersFormHtml( Locale locale, int nAttributeId, Map<String, Object> model )
    {
        if ( model == null )
        {
            model = new HashMap<String, Object>(  );
        }

        String strUrlTemplate = getModifyParametersTemplate(  );

        if ( strUrlTemplate == null )
        {
            return StringUtils.EMPTY;
        }

        model.put( MARK_ATTRIBUTE_PARAMETERS, getExtraParametersValues( locale, nAttributeId ) );
        model.put( MARK_LOCALE, locale );

        HtmlTemplate template = AppTemplateService.getTemplate( strUrlTemplate, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    public List<AttributeTypeParameter> getExtraParameters( Locale locale )
    {
        return AttributeTypeHome.getAttributeTypeParameterList( getAttributeTypeCode(  ), locale );
    }

    /**
     * {@inheritDoc}
     */
    public List<AttributeTypeParameter> getExtraParametersValues( Locale locale, int nAttributeId )
    {
        List<AttributeTypeParameter> listParameters = getExtraParameters( locale );

        for ( AttributeTypeParameter parameter : listParameters )
        {
            parameter.setValueList( DocumentAttributeHome.getAttributeParameterValues( nAttributeId,
                    parameter.getName(  ) ) );
        }

        return listParameters;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributeTypeCode( String strCode )
    {
        _strAttributeTypeCode = strCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeTypeCode(  )
    {
        return _strAttributeTypeCode;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public String validateValueParameters( List<AttributeTypeParameter> listParameters, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeXmlValue( Document document, DocumentAttribute attribute )
    {
        return attribute.getTextValue(  );
    }

    /**
     * Get the attribute type parameters of an attribute
     * @param nAttributeId The id of the attribute to get the parameter of
     * @param locale The locale
     * @return The list of parameters
     */
    public Collection<AttributeTypeParameter> getAttributeParametersValues( int nAttributeId, Locale locale )
    {
        return DocumentAttributeHome.getAttributeParametersValues( nAttributeId, locale );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeUsedAsThumbnail(  )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public List<AttributeTypeParameter> getValueParameters( HttpServletRequest request, Locale locale )
    {
        return null;
    }

    /**
     * Get parameters map
     * @param nAttributeId the attribute identifier
     * @param locale The current locale
     * @return a map of parameters
     */
    protected Map<String, List<String>> getParameters( int nAttributeId, Locale locale )
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
}
