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

import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentResource;
import fr.paris.lutece.plugins.document.utils.IntegerUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JSP Bean for document file resource management
 */
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@SessionScoped
@Named
public class ResourceJspBean implements java.io.Serializable
{
    //////////////////////////////////////////////////////////////////////////////
    // Constants

    // Parameters
    private static final String PARAMETER_DOCUMENT_ID = "id_document";
    private static final String PARAMETER_ATTRIBUTE_ID = "id_attribute";
    private DocumentResource _resource;


    /**
     * Initialise la ressource à partir de la requête (utilisable en EL)
     */
    public void loadResource( HttpServletRequest request )
    {
        String strDocumentId = request.getParameter( PARAMETER_DOCUMENT_ID );
        int nDocumentId = IntegerUtils.convert( strDocumentId );

        String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
        int nAttributeId = IntegerUtils.convert( strAttributeId );

        _resource = DocumentHome.getValidatedResource( nDocumentId, nAttributeId );
    }
    
    /**
     * Returns the content of the file resource
     * @return The content of the file resource
     */
    public byte[] getContent(  )
    {
        return _resource.getContent(  );
    }

    /**
     * Returns the content type of the file resource
     * @return The content type of the file resource
     */
    public String getContentType(  )
    {
        return _resource.getContentType(  );
    }
}
