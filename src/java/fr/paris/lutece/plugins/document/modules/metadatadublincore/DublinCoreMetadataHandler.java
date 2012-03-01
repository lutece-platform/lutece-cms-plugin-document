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
package fr.paris.lutece.plugins.document.modules.metadatadublincore;

import fr.paris.lutece.plugins.document.modules.metadatadublincore.business.DublinCoreMetadata;
import fr.paris.lutece.plugins.document.service.metadata.MetadataHandler;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Dublin Core Metadata Handler
 */
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
    private static final String MARK_METADATA = "metadata";

    public String getCreateForm( HttpServletRequest request )
    {
        DublinCoreMetadata metadata = new DublinCoreMetadata(  );
        HashMap model = new HashMap(  );
        model.put( MARK_METADATA, metadata );

        Locale locale = AdminUserService.getLocale( request );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FORM, locale, model );

        return template.getHtml(  );
    }

    public String getModifyForm( HttpServletRequest request, String strXmlData )
    {
        DublinCoreMetadata metadata = new DublinCoreMetadata(  );
        metadata.load( strXmlData );

        HashMap model = new HashMap(  );
        model.put( MARK_METADATA, metadata );

        Locale locale = AdminUserService.getLocale( request );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FORM, locale, model );

        return template.getHtml(  );
    }

    public String getXmlMetadata( Map<String, String[]> parameters )
    {
        DublinCoreMetadata meta = new DublinCoreMetadata(  );
        meta.setTitle( parameters.get( PARAMETER_TITLE )[0] );
        meta.setCreator( parameters.get( PARAMETER_CREATOR )[0] );
        meta.setSubject( parameters.get( PARAMETER_SUBJECT )[0] );
        meta.setDescription( parameters.get( PARAMETER_DESCRIPTION )[0] );
        meta.setPublisher( parameters.get( PARAMETER_PUBLISHER )[0] );
        meta.setContributor( parameters.get( PARAMETER_CONTRIBUTOR )[0] );
        meta.setDate( parameters.get( PARAMETER_DATE )[0] );
        meta.setType( parameters.get( PARAMETER_TYPE )[0] );
        meta.setFormat( parameters.get( PARAMETER_FORMAT )[0] );
        meta.setIdentifier( parameters.get( PARAMETER_IDENTIFIER )[0] );
        meta.setSource( parameters.get( PARAMETER_SOURCE )[0] );
        meta.setLanguage( parameters.get( PARAMETER_LANGUAGE )[0] );
        meta.setRelation( parameters.get( PARAMETER_RELATION )[0] );
        meta.setCoverage( parameters.get( PARAMETER_COVERAGE )[0] );
        meta.setRights( parameters.get( PARAMETER_RIGTHS )[0] );

        return meta.getXml(  );
    }
}
