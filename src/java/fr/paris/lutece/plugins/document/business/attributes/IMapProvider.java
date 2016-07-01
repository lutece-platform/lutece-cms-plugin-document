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
package fr.paris.lutece.plugins.document.business.attributes;

import fr.paris.lutece.util.ReferenceItem;


/**
 *
 * IMapProvider : map provider for Geolocation
 *
 */
public interface IMapProvider
{
    /**
     * Gets the key. This key <b>must be unique</b>.
     * @return the key;
     */
    String getKey(  );

    /**
     * Gets the displayed name
     * @return the displayed name
     */
    String getDisplayedName(  );

    /**
     * Gets the html code
     * @return the html code
     */
    String getHtmlCode(  );

    /**
     * Gets the html code for list result in Back Office
     * @return the html code
     */
    String getBackListHtmlCode(  );

    /**
     * Gets the html code for list result front office
     * @return the html code
     */
    String getFrontListHtmlCode(  );

    /**
     * Gets the html code for front office
     * @return the html code
     */
    String getFrontHtmlCode(  );

    /**
     * Builds a new {@link ReferenceItem} for the map provider
     * @return the item created.
     */
    ReferenceItem toRefItem(  );

    /**
     * Defines if map in bo and fo lists is supported
     * @return <code>true</code> if a map can be generated (show on map button), <code>false</code> otherwise.
     */
    boolean isMapListSupported(  );

    /**
     * returns the Parameter class contains all the parameters of the map
     * @return the Parameter
     */
    Object getParameter( int nKey );
}
