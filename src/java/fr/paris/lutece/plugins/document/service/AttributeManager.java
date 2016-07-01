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
package fr.paris.lutece.plugins.document.service;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeParameter;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * Define the interface for attribute's specific behavior
 */
public interface AttributeManager
{
    /**
     * Define the attribute Type code
     * @param strCode The Attribute Type Code
     */
    void setAttributeTypeCode( String strCode );

    /**
     * Return the attribute Type code
     * @return The Attribute Type Code
     */
    String getAttributeTypeCode(  );

    /**
     * Gets the part of an HTML form to enter attribute data
     * @param attribute The attribute
     * @param locale The current Locale
     * @param strBaseUrl the base url
     * @return A part of the HTML form
     */
    String getCreateFormHtml( DocumentAttribute attribute, Locale locale, String strBaseUrl );

    /**
     * Gets the part of an HTML form to modify attribute data
     * @param attribute The attribute
     * @param document The attribute document
     * @param locale The current Locale
     * @param strBaseUrl The base url
     * @return A part of the HTML form
     */
    String getModifyFormHtml( DocumentAttribute attribute, Document document, Locale locale, String strBaseUrl );

    /**
     * Gets the part of an HTML form to enter parameters data
     * @param locale The current Locale
     * @return A part of the HTML form
     */
    String getCreateParametersFormHtml( Locale locale );

    /**
     * Gets the part of an HTML form to enter parameters data
     * @param listParameters list attribute type parameters
     * @param locale The current Locale
     * @return A part of the HTML form
     */
    String getCreateParametersFormHtml( List<AttributeTypeParameter> listParameters, Locale locale );

    /**
     * Gets the part of an HTML form to modify parameters data
     * @param locale The current Locale
     * @param nAttributeId Attribute Id
     * @return A part of the HTML form
     */
    String getModifyParametersFormHtml( Locale locale, int nAttributeId );

    /**
     * Gets extra parameters for the attribute
     * @param locale The current Locale
     * @return A collection of attribute parameters
     */
    List<AttributeTypeParameter> getExtraParameters( Locale locale );

    /**
     * Gets extra parameters values for the attribute
     * @param locale The current Locale
     * @param nAttributeId
     * @return The extra parameters values for the attribute
     */
    List<AttributeTypeParameter> getExtraParametersValues( Locale locale, int nAttributeId );

    /**
     * Validate the value for the attribute
     * @param nAttributeId The attribute identifier
     * @param strValue The value to check
     * @param locale The current Locale
     * @return null if valid, otherwise error message
     */
    String validateValue( int nAttributeId, String strValue, Locale locale );

    /**
     * Validate the value for the parameters
     * @param listParameters The list of parameters to check
     * @param locale The locale
     * @return null if valid, otherwise message property
     */
    String validateValueParameters( List<AttributeTypeParameter> listParameters, Locale locale );

    /**
     * Get the XML data corresponding to the attribute to build the docuemnt XML
     * content
     * @param document The document
     * @param attribute The attribute
     * @return The XML value of the attribute
     */
    String getAttributeXmlValue( Document document, DocumentAttribute attribute );

    /**
     * Tells if the attrubute can be used as Thumbnail (image)
     * @return true if the attrubute can be used as Thumbnail, otherwise false
     */
    boolean canBeUsedAsThumbnail(  );

    /**
     * Get the value parameters
     * @param request the HTTP request
     * @param locale the locale
     * @return a list of {@link AttributeTypeParameter}
     */
    List<AttributeTypeParameter> getValueParameters( HttpServletRequest request, Locale locale );
}
