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
package fr.paris.lutece.plugins.document.service;

import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Gives access to the advanced parameters of the document plugin, managed from the admin dashboard. The values are stored in the datastore; until
 * they are saved once, the values of document.properties apply.
 */
@ApplicationScoped
public class DocumentParameterService
{
    /** Datastore key of the automatic validation of the documents, bypassing the validation workflow */
    public static final String DATASTORE_KEY_WORKFLOW_AUTO_VALIDATION = "document.advanced_parameters.workflow_auto_validation";

    private static final String PROPERTY_WORKFLOW_AUTO_VALIDATION = "document.workflow.auto_validation";

    /**
     * Tells if the documents are validated automatically when they are created or modified, bypassing the validation workflow
     *
     * @return true if the documents are validated automatically
     */
    public boolean isWorkflowAutoValidation( )
    {
        String strDefault = Boolean.toString( AppPropertiesService.getPropertyBoolean( PROPERTY_WORKFLOW_AUTO_VALIDATION, false ) );

        return Boolean.parseBoolean( DatastoreService.getDataValue( DATASTORE_KEY_WORKFLOW_AUTO_VALIDATION, strDefault ) );
    }

    /**
     * Sets the automatic validation of the documents
     *
     * @param bAutoValidation
     *            true to validate the documents automatically, false to use the validation workflow
     */
    public void setWorkflowAutoValidation( boolean bAutoValidation )
    {
        DatastoreService.setDataValue( DATASTORE_KEY_WORKFLOW_AUTO_VALIDATION, Boolean.toString( bAutoValidation ) );
    }
}
