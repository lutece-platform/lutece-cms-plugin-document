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
package fr.paris.lutece.plugins.document.service.view;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.resource.loader.ResourceNotFoundException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.exception.LuteceFreemarkerException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Renders the documents with the FreeMarker templates of their document type. Each type has a back office template (preview of the working
 * content) and a front office template (validated content). A type without template uses the default templates of the plugin.
 */
@ApplicationScoped
public class DocumentTemplateService
{
    /** Template used by the types without front office template */
    public static final String TEMPLATE_DEFAULT_CONTENT = "skin/plugins/document/document_content_default.html";

    /** Template used by the types without back office template */
    public static final String TEMPLATE_DEFAULT_ADMIN = "admin/plugins/document/document_admin_default.html";

    /** Model key of the {@link DocumentView} */
    public static final String MARK_DOCUMENT = "document";

    /** Model key of the {@link DocumentType} */
    public static final String MARK_DOCUMENT_TYPE = "document_type";

    /** Front office model key of the formatted publication date (null if the document is not published in the portlet) */
    public static final String MARK_PUBLICATION_DATE = "publication_date";

    /** Front office model key of the site locale code */
    public static final String MARK_SITE_LOCALE = "site_locale";

    /** Front office model key of the request parameters, as a map of strings */
    public static final String MARK_REQUEST_PARAMETERS = "request_parameters";

    private static final String PATH_TEMPLATES = "/WEB-INF/templates/";
    private static final String SAMPLE_TEXT = "Lorem ipsum";
    private static final String SAMPLE_CONTENT_TYPE = "image/png";
    private static final int SAMPLE_DOCUMENT_ID = 0;

    private DocumentViewService _documentViewService;

    /**
     * Constructor used by CDI for the client proxy
     */
    protected DocumentTemplateService( )
    {
    }

    /**
     * Constructor
     *
     * @param documentViewService
     *            The service building the document views
     */
    @Inject
    public DocumentTemplateService( DocumentViewService documentViewService )
    {
        _documentViewService = documentViewService;
    }

    /**
     * Renders a document in the front office
     *
     * @param type
     *            The document type
     * @param view
     *            The validated view of the document
     * @param model
     *            Additional model entries (publication, request parameters...), may be null
     * @param locale
     *            The locale
     * @return The HTML of the document
     */
    public String renderContent( DocumentType type, DocumentView view, Map<String, Object> model, Locale locale )
    {
        return render( type.getContentTemplate( ), TEMPLATE_DEFAULT_CONTENT, type, buildModel( type, view, model, false, locale ), locale );
    }

    /**
     * Renders the preview of a document in the back office
     *
     * @param type
     *            The document type
     * @param view
     *            The working view of the document
     * @param locale
     *            The locale
     * @return The HTML of the preview
     */
    public String renderAdmin( DocumentType type, DocumentView view, Locale locale )
    {
        return render( type.getAdminTemplate( ), TEMPLATE_DEFAULT_ADMIN, type, buildModel( type, view, null, true, locale ), locale );
    }

    /**
     * Checks that a template can be rendered, by rendering it with a sample document of the given type. The publication date of the sample
     * is null, as for a document that is not published in the current portlet.
     *
     * @param type
     *            The document type
     * @param strTemplate
     *            The template to check
     * @param bAdmin
     *            true for a back office template, false for a front office template
     * @param locale
     *            The locale
     * @return null if the template is valid, otherwise the error message
     */
    public String validateTemplate( DocumentType type, String strTemplate, boolean bAdmin, Locale locale )
    {
        if ( StringUtils.isBlank( strTemplate ) )
        {
            return null;
        }

        try
        {
            AppTemplateService.getTemplateFromStringFtl( strTemplate, locale, buildModel( type, buildSampleView( type, !bAdmin ), null, bAdmin, locale ) );

            return null;
        }
        catch( LuteceFreemarkerException e )
        {
            return e.getMessage( );
        }
    }

    /**
     * Returns the source of a default template, used as a starting point to write a specific template
     *
     * @param bAdmin
     *            true for the back office template, false for the front office template
     * @return The source of the template, or an empty string if it can not be read
     */
    public String getDefaultTemplateSource( boolean bAdmin )
    {
        String strTemplate = bAdmin ? TEMPLATE_DEFAULT_ADMIN : TEMPLATE_DEFAULT_CONTENT;

        try ( InputStream stream = AppPathService.getResourceStream( PATH_TEMPLATES, strTemplate ) )
        {
            return new String( stream.readAllBytes( ), StandardCharsets.UTF_8 );
        }
        catch( ResourceNotFoundException | IOException e )
        {
            AppLogService.error( "Unable to read the default document template {}", strTemplate, e );

            return StringUtils.EMPTY;
        }
    }

    /**
     * Renders a document with its specific template, or the default one. A specific template that fails is logged and the default template is
     * used, so that a broken template does not break the page.
     *
     * @param strTemplate
     *            The specific template of the type, may be blank
     * @param strDefaultTemplate
     *            The path of the default template
     * @param type
     *            The document type
     * @param mapModel
     *            The model
     * @param locale
     *            The locale
     * @return The HTML
     */
    private String render( String strTemplate, String strDefaultTemplate, DocumentType type, Map<String, Object> mapModel, Locale locale )
    {
        if ( StringUtils.isNotBlank( strTemplate ) )
        {
            try
            {
                return AppTemplateService.getTemplateFromStringFtl( strTemplate, locale, mapModel ).getHtml( );
            }
            catch( LuteceFreemarkerException e )
            {
                AppLogService.error( "Error rendering the template of the document type {} for the document {}", type.getCode( ),
                        ( (DocumentView) mapModel.get( MARK_DOCUMENT ) ).getId( ), e );
            }
        }

        return AppTemplateService.getTemplate( strDefaultTemplate, locale, mapModel ).getHtml( );
    }

    /**
     * Builds the template model. The front office model always contains the site locale and the request parameters.
     *
     * @param type
     *            The document type
     * @param view
     *            The document view
     * @param model
     *            Additional model entries, may be null
     * @param bAdmin
     *            true for the back office model, false for the front office model
     * @param locale
     *            The locale
     * @return The model
     */
    private Map<String, Object> buildModel( DocumentType type, DocumentView view, Map<String, Object> model, boolean bAdmin, Locale locale )
    {
        Map<String, Object> mapModel = new HashMap<>( );

        if ( model != null )
        {
            mapModel.putAll( model );
        }

        mapModel.put( MARK_DOCUMENT, view );
        mapModel.put( MARK_DOCUMENT_TYPE, type );

        if ( !bAdmin )
        {
            mapModel.putIfAbsent( MARK_SITE_LOCALE, locale.getLanguage( ) );
            mapModel.putIfAbsent( MARK_REQUEST_PARAMETERS, new HashMap<String, String>( ) );
        }

        return mapModel;
    }

    /**
     * Builds a view of a sample document of the given type, where every attribute has a value. Every sample attribute has a content type: the
     * file managers expose it as a file, the other managers ignore it and expose the text.
     *
     * @param type
     *            The document type
     * @param bValidated
     *            true for a validated view, false for a working view
     * @return The sample view
     */
    DocumentView buildSampleView( DocumentType type, boolean bValidated )
    {
        Document document = new Document( );
        document.setId( SAMPLE_DOCUMENT_ID );
        document.setCodeDocumentType( type.getCode( ) );
        document.setTitle( SAMPLE_TEXT );
        document.setSummary( SAMPLE_TEXT );
        document.setDateValidityBegin( new Timestamp( System.currentTimeMillis( ) ) );
        document.setDateValidityEnd( new Timestamp( System.currentTimeMillis( ) ) );
        document.setCategories( new ArrayList<>( ) );

        List<DocumentAttribute> listAttributes = new ArrayList<>( );

        for ( DocumentAttribute typeAttribute : type.getAttributes( ) )
        {
            DocumentAttribute attribute = new DocumentAttribute( );
            attribute.setId( typeAttribute.getId( ) );
            attribute.setCode( typeAttribute.getCode( ) );
            attribute.setCodeAttributeType( typeAttribute.getCodeAttributeType( ) );
            attribute.setAttributeOrder( typeAttribute.getAttributeOrder( ) );
            attribute.setTextValue( SAMPLE_TEXT );
            attribute.setBinary( true );
            attribute.setValueContentType( SAMPLE_CONTENT_TYPE );
            listAttributes.add( attribute );
        }

        document.setAttributes( listAttributes );

        return _documentViewService.buildView( document, bValidated );
    }
}
