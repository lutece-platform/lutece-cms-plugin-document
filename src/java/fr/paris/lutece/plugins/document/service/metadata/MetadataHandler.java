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
package fr.paris.lutece.plugins.document.service.metadata;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;


/**
 * Handler of the metadata of a document type. The metadata of a document are stored as a JSON object whose properties are defined by the
 * handler.
 */
public interface MetadataHandler
{
    /**
     * Get the create form
     * @param request The request
     * @return The HTML form
     */
    String getCreateForm( HttpServletRequest request );

    /**
     * Get the modify form
     * @param request The request
     * @param strMetadata The current metadata of the document, as a JSON object, may be null
     * @return The HTML form
     */
    String getModifyForm( HttpServletRequest request, String strMetadata );

    /**
     * Build the metadata from the parameters of the form
     * @param parameters The parameters
     * @return The metadata, as a JSON object
     */
    String getMetadata( Map<String, String[]> parameters );

    /**
     * Get the HTML meta tags of the metadata, displayed in the header of the page of the document
     * @param strMetadata The metadata of the document, as a JSON object, may be null
     * @return The contents of the meta tags by meta name, in display order, without the empty values
     */
    Map<String, String> getMetaTags( String strMetadata );
}
