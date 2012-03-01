/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import java.util.HashMap;
import java.util.Map;


/**
 * Attribute Service
 */
public class AttributeService
{
    private static AttributeService _singleton;
    private static Map<String, AttributeManager> _mapManagers = new HashMap<String, AttributeManager>(  );

    /** Creates a new instance of AttributeService */
    private AttributeService(  )
    {
    }

    /**
     * Get the unique instance of the service
     * @return
     */
    public static synchronized AttributeService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new AttributeService(  );
            _singleton.init(  );
        }

        return _singleton;
    }

    /**
     * Initializes the service
     */
    private void init(  )
    {
        ReferenceList listManagers = AttributeTypeHome.getAttributeManagersList(  );

        for ( ReferenceItem item : listManagers )
        {
            String strManagerKey = item.getCode(  );
            String strManagerClass = item.getName(  );

            try
            {
                AttributeManager manager = (AttributeManager) Class.forName( strManagerClass ).newInstance(  );
                manager.setAttributeTypeCode( item.getCode(  ) );
                _mapManagers.put( strManagerKey, manager );
            }
            catch ( IllegalAccessException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( ClassNotFoundException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( InstantiationException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }
    }

    /**
     * Returns the manager of an attribute
     * @param strAttributeType The attribute type code
     * @return An manager object
     */
    public AttributeManager getManager( String strAttributeType )
    {
        return _mapManagers.get( strAttributeType );
    }
}
