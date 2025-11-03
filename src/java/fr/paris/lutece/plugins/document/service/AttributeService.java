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

import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;

import java.util.HashMap;
import java.util.Map;


/**
 * Attribute Service
 */
@ApplicationScoped
@Named( "document.AttributeService" )
public class AttributeService
{

    private static Map<String, AttributeManager> _mapManagers = new HashMap<String, AttributeManager>(  );

    /** Creates a new instance of AttributeService */
    public AttributeService(  )
    {
    }
    
    /**
     * Returns the unique instance of the {@link AttributeService} service.
     * 
     * <p>This method is deprecated and is provided for backward compatibility only. 
     * For new code, use dependency injection with {@code @Inject} to obtain the 
     * {@link AttributeService} instance instead.</p>
     * 
     * @return The unique instance of {@link AttributeService}.
     * 
     * @deprecated Use {@code @Inject} to obtain the {@link AttributeService} 
     * instance. This method will be removed in future versions.
     */
    @Deprecated( since = "8.0", forRemoval = true )
    public static synchronized AttributeService getInstance(  )
    {
    	return CDI.current( ).select( AttributeService.class ).get( );
    }

    /**
     * Initializes the service
     */
    @PostConstruct
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
    
	/**
     * This method observes the initialization of the {@link ApplicationScoped} context.
     * It ensures that this CDI beans are instantiated at the application startup.
     *
     * <p>This method is triggered automatically by CDI when the {@link ApplicationScoped} context is initialized,
     * which typically occurs during the startup of the application server.</p>
     *
     * @param context the {@link ServletContext} that is initialized. This parameter is observed
     *                and injected automatically by CDI when the {@link ApplicationScoped} context is initialized.
     */
    public void initializedService( @Observes @Initialized( ApplicationScoped.class ) ServletContext context ) 
    {
        // This method is intentionally left empty to trigger CDI bean instantiation
    }
}
