/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.document.business.attributes;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...) for DocumentAttributeType objects
 */
public final class AttributeTypeHome
{
    // Static variable pointed at the DAO instance
    private static IAttributeTypeDAO _dao = SpringContextService.getBean( "document.attributeTypeDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AttributeTypeHome(  )
    {
    }

    /**
     * Creation of an instance of documentAttributeType
     *
     * @param documentAttributeType The instance of the documentAttributeType which contains the informations to store
     * @return The  instance of documentAttributeType which has been created with its primary key.
     */
    public static AttributeType create( AttributeType documentAttributeType )
    {
        _dao.insert( documentAttributeType );

        return documentAttributeType;
    }

    /**
     * Update of the documentAttributeType which is specified in parameter
     *
     * @param documentAttributeType The instance of the documentAttributeType which contains the data to store
     * @return The instance of the  documentAttributeType which has been updated
     */
    public static AttributeType update( AttributeType documentAttributeType )
    {
        _dao.store( documentAttributeType );

        return documentAttributeType;
    }

    /**
     * Remove the DocumentAttributeType whose identifier is specified in parameter
     *
     * @param documentAttributeType The DocumentAttributeType object to remove
     */
    public static void remove( AttributeType documentAttributeType )
    {
        _dao.delete( documentAttributeType );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a documentAttributeType whose identifier is specified in parameter
     *
     * @param nKey The Primary key of the documentAttributeType
     * @return An instance of documentAttributeType
     */
    public static AttributeType findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Returns a collection of documentAttributeTypes objects
     * @return A collection of documentAttributeTypes
     */
    public static Collection<AttributeType> findAll(  )
    {
        return _dao.selectDocumentAttributeTypeList(  );
    }

    /**
     * Returns a ReferenceList of AttributeTypes objects
     * @return A ReferenceList of AttributeTypes
     * @param locale The locale
     */
    public static ReferenceList getAttributeTypesList( Locale locale )
    {
        return _dao.selectAttributeTypeList( locale );
    }

    /**
     * Gets managers list
     * @return A list of attribute managers
     */
    public static ReferenceList getAttributeManagersList(  )
    {
        return _dao.getAttributeManagersList(  );
    }

    /**
     * Get Attribute type parameters list
     * @param strAttributeTypeCode The attribute type code
     * @param locale The locale
     * @return A list of attribute parameters list
     */
    public static List<AttributeTypeParameter> getAttributeTypeParameterList( String strAttributeTypeCode, Locale locale )
    {
        List<AttributeTypeParameter> listParameters = _dao.selectAttributeTypeParameterList( strAttributeTypeCode );

        return (List<AttributeTypeParameter>) I18nService.localizeCollection( listParameters, locale );
    }
}
