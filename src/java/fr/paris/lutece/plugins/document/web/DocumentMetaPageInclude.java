/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.util.AppPathService;

import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


/**
 * Document Meta PageInclude
 */
public class DocumentMetaPageInclude implements PageInclude
{
    private static final String MARK_DOCUMENT_META = "document_meta";
    private static final String PARAMETER_DOCUMENT_ID = "document_id";
    private static final String PATH_XSL = "/WEB-INF/plugins/document/xsl/";
    private static final String FILE_XSL = "document_include_meta.xsl";
    private static final String STYLE_PREFIX_ID = PATH_XSL + FILE_XSL;

    /**
     * Substitue specific Freemarker markers in the page template.
     * @param rootModel the HashMap containing markers to substitute
     * @param data A PageData object containing applications data
     * @param nMode The current mode
     * @param request The HTTP request
     */
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        String strMeta = StringUtils.EMPTY;

        if ( request != null )
        {
            String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );

            if ( IntegerUtils.isNumeric( strDocumentId ) )
            {
                Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( IntegerUtils.convert( strDocumentId ) );

                if ( document != null )
                {
                    String strMetadata = document.getXmlMetadata(  );

                    if ( StringUtils.isNotBlank( strMetadata ) )
                    {
                        Source xslSource = loadXsl(  );
                        XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
                        strMeta = xmlTransformerService.transformBySourceWithXslCache( strMetadata, xslSource,
                                STYLE_PREFIX_ID, null, null );
                    }
                }
            }

            rootModel.put( MARK_DOCUMENT_META, strMeta );
        }
    }

    /**
     * Load the XSL stylesheet used to convert document metadata into meta tags
     * @return The source stream
     */
    private Source loadXsl(  )
    {
        FileInputStream fis = AppPathService.getResourceAsStream( PATH_XSL, FILE_XSL );
        Source sourceXsl = new StreamSource( fis );

        return sourceXsl;
    }
}
