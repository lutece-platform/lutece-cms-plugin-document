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
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.category.Category;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.plugins.document.service.AttributeManager;
import fr.paris.lutece.plugins.document.service.AttributeService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds the {@link DocumentView} of a document, the model used by the document templates.
 * It reads the attribute values from the document_content table instead of the XML content.
 */
@ApplicationScoped
public class DocumentViewService
{
    private AttributeService _attributeService;

    /**
     * Constructor used by CDI for the client proxy
     */
    protected DocumentViewService( )
    {
    }

    /**
     * Constructor
     *
     * @param attributeService
     *            The service providing the attribute managers
     */
    @Inject
    public DocumentViewService( AttributeService attributeService )
    {
        _attributeService = attributeService;
    }

    /**
     * Loads a document and builds the view of its working or validated content. The attributes loaded with the document depend on its state:
     * the requested version is reloaded.
     *
     * @param nDocumentId
     *            The document identifier
     * @param bValidated
     *            true for the validated content (front office), false for the working content (back office preview)
     * @return The view, or null if the document does not exist
     */
    public DocumentView getView( int nDocumentId, boolean bValidated )
    {
        Document document = DocumentHome.findByPrimaryKeyWithoutBinaries( nDocumentId );

        if ( document == null )
        {
            return null;
        }

        DocumentHome.loadAttributesWithoutBinaries( document, bValidated );

        return buildView( document, bValidated );
    }

    /**
     * Builds the validated view of a document loaded with {@link DocumentHome#findByPrimaryKeyWithoutBinaries(int)}: the validated attributes
     * are reloaded only when the document state is not the validated state (the working attributes were loaded).
     *
     * @param document
     *            The document
     * @return The validated view
     */
    public DocumentView buildValidatedView( Document document )
    {
        if ( document.getStateId( ) != DocumentState.STATE_VALIDATE )
        {
            DocumentHome.loadAttributesWithoutBinaries( document, true );
        }

        return buildView( document, true );
    }

    /**
     * Builds the view of a document from its loaded attributes. The attributes of the document must match the requested version. The
     * validated view uses the title and summary saved at validation time, or the current ones for a document validated before they were saved.
     *
     * @param document
     *            The document
     * @param bValidated
     *            true for the validated content, false for the working content
     * @return The view
     */
    public DocumentView buildView( Document document, boolean bValidated )
    {
        DocumentView view = new DocumentView( );
        view.setId( document.getId( ) );
        view.setCodeDocumentType( document.getCodeDocumentType( ) );
        view.setValidated( bValidated );
        view.setDateValidityBegin( document.getDateValidityBegin( ) );
        view.setDateValidityEnd( document.getDateValidityEnd( ) );
        view.setSkipPortlet( document.isSkipPortlet( ) );
        view.setSkipCategories( document.isSkipCategories( ) );

        if ( bValidated )
        {
            view.setTitle( StringUtils.defaultIfEmpty( document.getValidatedTitle( ), document.getTitle( ) ) );
            view.setSummary( StringUtils.defaultIfEmpty( document.getValidatedSummary( ), document.getSummary( ) ) );
        }
        else
        {
            view.setTitle( document.getTitle( ) );
            view.setSummary( document.getSummary( ) );
        }

        if ( document.getCategories( ) != null )
        {
            view.setCategories( document.getCategories( ).stream( ).map( Category::getName ).collect( Collectors.toList( ) ) );
        }

        view.setAttributes( buildAttributes( document, bValidated ) );

        return view;
    }

    /**
     * Builds the attribute values, keyed by attribute code, in the attribute order
     *
     * @param document
     *            The document
     * @param bValidated
     *            true for the validated content, false for the working content
     * @return The attribute values
     */
    private Map<String, Object> buildAttributes( Document document, boolean bValidated )
    {
        Map<String, Object> mapAttributes = new LinkedHashMap<>( );
        List<DocumentAttribute> listAttributes = document.getAttributes( );

        if ( listAttributes == null )
        {
            return mapAttributes;
        }

        listAttributes.stream( ).sorted( Comparator.comparingInt( DocumentAttribute::getAttributeOrder ) ).forEach( attribute -> {
            AttributeManager manager = _attributeService.getManager( attribute.getCodeAttributeType( ) );
            Object value = ( manager != null ) ? manager.getAttributeValue( document, attribute, bValidated ) : attribute.getTextValue( );

            if ( value != null )
            {
                mapAttributes.put( attribute.getCode( ), value );
            }
        } );

        return mapAttributes;
    }
}
