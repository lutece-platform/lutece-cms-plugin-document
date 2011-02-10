/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
import fr.paris.lutece.portal.service.i18n.Localizable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * This class represents the business object AttributeTypeParameter
 */
public class AttributeTypeParameter implements Localizable
{
    // Variables declarations
    private String _strName;
    private String _strLabelKey;
    private String _strDescriptionKey;
    private List<String> _listDefaultValue = new ArrayList<String>(  );
    private List<String> _listValues = new ArrayList<String>(  );
    private Locale _locale;

    /**
     * Returns the Name
     *
     * @return The Name
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * Returns the Locale
     *
     * @return The Locale
     */
    public Locale getLocale(  )
    {
        return _locale;
    }

    /**
     * Sets the Locale
     *
     * @param locale The Locale
     */
    public void setLocale( Locale locale )
    {
        _locale = locale;
    }

    /**
     * Sets the Name
     *
     * @param strName The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the Label
     *
     * @return The Label
     */
    public String getLabelKey(  )
    {
        return _strLabelKey;
    }

    /**
     * Sets the Label
     *
     * @param strLabelKey The Label
     */
    public void setLabelKey( String strLabelKey )
    {
        _strLabelKey = strLabelKey;
    }

    /**
     * Returns the Description
     *
     * @return The Description
     */
    public String getDescriptionKey(  )
    {
        return _strDescriptionKey;
    }

    /**
     * Sets the Description
     *
     * @param strDescriptionKey The Description
     */
    public void setDescriptionKey( String strDescriptionKey )
    {
        _strDescriptionKey = strDescriptionKey;
    }

    /**
     *
     * @param listValues
     */
    public void setValueList( List<String> listValues )
    {
        _listValues.clear(  );
        _listValues.addAll( listValues );
    }

    /**
     *
     * @return listValues
     */
    public List<String> getValueList(  )
    {
        List<String> returnList = new ArrayList<String>(  );
        returnList.addAll( _listValues );

        return returnList;
    }

    /**
     * Returns the Label
     *
     * @return The Label
     */
    public String getLabel(  )
    {
        return I18nService.getLocalizedString( _strLabelKey, _locale );
    }

    /**
     * Returns the Description
     *
     * @return The Description
     */
    public String getDescription(  )
    {
        return I18nService.getLocalizedString( _strDescriptionKey, _locale );
    }

    /**
     * Returns the default value
     *
     * @return The default value
     */
    public List<String> getDefaultValue(  )
    {
        List<String> returnList = new ArrayList<String>(  );
        returnList.addAll( _listDefaultValue );

        return returnList;
    }

    /**
     * Returns the default value
     *
     * @return The default value
     */
    public void setDefaultValue( List<String> listDefaultValue )
    {
        _listDefaultValue.clear(  );
        _listDefaultValue.addAll( listDefaultValue );
    }
}
