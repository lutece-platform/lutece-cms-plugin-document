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
package fr.paris.lutece.plugins.document.modules.metadatadublincore;

import fr.paris.lutece.plugins.document.modules.metadatadublincore.business.DublinCoreMetadata;
import fr.paris.lutece.plugins.document.service.metadata.MetadataHandler;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;


/**
 * Dublin Core Metadata Handler
 */
@ApplicationScoped
@Named("document.metadataDublinCore")
public class DublinCoreMetadataHandler implements MetadataHandler
{
	private static final String TEMPLATE_FORM = "admin/plugins/document/modules/metadatadublincore/dublin_core_form.html";

    // Parameters
    private static final String PARAMETER_TITLE = "dc_title";
    private static final String PARAMETER_CREATOR = "dc_creator";
    private static final String PARAMETER_SUBJECT = "dc_subject";
    private static final String PARAMETER_DESCRIPTION = "dc_description";
    private static final String PARAMETER_PUBLISHER = "dc_publisher";
    private static final String PARAMETER_CONTRIBUTOR = "dc_contributor";
    private static final String PARAMETER_DATE = "dc_date";
    private static final String PARAMETER_TYPE = "dc_type";
    private static final String PARAMETER_FORMAT = "dc_format";
    private static final String PARAMETER_IDENTIFIER = "dc_identifier";
    private static final String PARAMETER_SOURCE = "dc_source";
    private static final String PARAMETER_LANGUAGE = "dc_language";
    private static final String PARAMETER_RELATION = "dc_relation";
    private static final String PARAMETER_COVERAGE = "dc_coverage";
    private static final String PARAMETER_RIGTHS = "dc_rights";
    private static final String MARK_METADATA = "metadata_business";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreateForm( HttpServletRequest request )
    {
        DublinCoreMetadata dublinMetadata = new DublinCoreMetadata(  );
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_METADATA, dublinMetadata );

        Locale locale = AdminUserService.getLocale( request );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FORM, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModifyForm( HttpServletRequest request, String strMetadata )
    {
        DublinCoreMetadata metadata = DublinCoreMetadata.fromJson( strMetadata );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_METADATA, metadata );

        Locale locale = AdminUserService.getLocale( request );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FORM, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMetadata( Map<String, String[]> parameters )
    {
        DublinCoreMetadata meta = new DublinCoreMetadata(  );
        meta.setTitle( getParameter( parameters, PARAMETER_TITLE ) );
        meta.setCreator( getParameter( parameters, PARAMETER_CREATOR ) );
        meta.setSubject( getParameter( parameters, PARAMETER_SUBJECT ) );
        meta.setDescription( getParameter( parameters, PARAMETER_DESCRIPTION ) );
        meta.setPublisher( getParameter( parameters, PARAMETER_PUBLISHER ) );
        meta.setContributor( getParameter( parameters, PARAMETER_CONTRIBUTOR ) );
        meta.setDate( getParameter( parameters, PARAMETER_DATE ) );
        meta.setType( getParameter( parameters, PARAMETER_TYPE ) );
        meta.setFormat( getParameter( parameters, PARAMETER_FORMAT ) );
        meta.setIdentifier( getParameter( parameters, PARAMETER_IDENTIFIER ) );
        meta.setSource( getParameter( parameters, PARAMETER_SOURCE ) );
        meta.setLanguage( getParameter( parameters, PARAMETER_LANGUAGE ) );
        meta.setRelation( getParameter( parameters, PARAMETER_RELATION ) );
        meta.setCoverage( getParameter( parameters, PARAMETER_COVERAGE ) );
        meta.setRights( getParameter( parameters, PARAMETER_RIGTHS ) );

        return meta.toJson(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getMetaTags( String strMetadata )
    {
        if ( StringUtils.isBlank( strMetadata ) )
        {
            return new LinkedHashMap<>(  );
        }

        return DublinCoreMetadata.fromJson( strMetadata ).toMetaTags(  );
    }

    /**
     * Returns the first value of a parameter of the form
     *
     * @param parameters
     *            The parameters
     * @param strName
     *            The parameter name
     * @return The value, or an empty string if the parameter is missing
     */
    private static String getParameter( Map<String, String[]> parameters, String strName )
    {
        String [ ] values = parameters.get( strName );

        return ( ( values != null ) && ( values.length > 0 ) ) ? values [0] : StringUtils.EMPTY;
    }
}
