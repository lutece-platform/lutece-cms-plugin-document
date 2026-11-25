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
package fr.paris.lutece.plugins.document.web;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.service.metadata.MetadataHandler;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.service.template.AppTemplateService;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Document Meta PageInclude: the meta tags of the metadata of the document displayed in the page
 */
public class DocumentMetaPageInclude implements PageInclude
{
    /** Template of the meta tags */
    public static final String TEMPLATE_META = "skin/plugins/document/document_include_meta.html";

    /** Model key of the meta tags, as a map of the contents by meta name */
    public static final String MARK_META_TAGS = "meta_tags";

    private static final String MARK_DOCUMENT_META = "document_meta";
    private static final String PARAMETER_DOCUMENT_ID = "document_id";

    /**
     * Substitue specific Freemarker markers in the page template.
     * @param rootModel the HashMap containing markers to substitute
     * @param data A PageData object containing applications data
     * @param nMode The current mode
     * @param request The HTTP request
     */
    @Override
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        if ( request != null )
        {
            String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
            String strMeta = IntegerUtils.isNumeric( strDocumentId ) ? getMetaTags( IntegerUtils.convert( strDocumentId ), request ) : StringUtils.EMPTY;

            rootModel.put( MARK_DOCUMENT_META, HtmlMarkup.of( strMeta ) );
        }
    }

    /**
     * Renders the meta tags of the metadata of a document, with the metadata handler of its document type
     * @param nDocumentId The document identifier
     * @param request The HTTP request
     * @return The HTML meta tags, or an empty string if the document has no metadata
     */
    private String getMetaTags( int nDocumentId, HttpServletRequest request )
    {
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( ( document == null ) || StringUtils.isBlank( document.getMetadata(  ) ) )
        {
            return StringUtils.EMPTY;
        }

        DocumentType type = DocumentTypeHome.findByPrimaryKey( document.getCodeDocumentType(  ) );
        MetadataHandler handler = ( type != null ) ? type.metadataHandler(  ) : null;

        if ( handler == null )
        {
            return StringUtils.EMPTY;
        }

        Map<String, String> mapMetaTags = handler.getMetaTags( document.getMetadata(  ) );

        if ( mapMetaTags.isEmpty(  ) )
        {
            return StringUtils.EMPTY;
        }

        Map<String, Object> model = new HashMap<>(  );
        model.put( MARK_META_TAGS, mapMetaTags );

        return AppTemplateService.getTemplate( TEMPLATE_META, request.getLocale(  ), model ).getHtml(  );
    }
}
