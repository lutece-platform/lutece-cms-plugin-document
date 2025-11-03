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
package fr.paris.lutece.plugins.document.service.rules;

import fr.paris.lutece.plugins.document.business.rules.Rule;

import fr.paris.lutece.plugins.document.business.rules.RuleHome;
import fr.paris.lutece.plugins.document.service.DocumentEvent;
import fr.paris.lutece.plugins.document.service.DocumentException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;

import java.util.Locale;


/**
 * Rule engine. Executes rules on every document event.
 */
@ApplicationScoped
@Named("document.RuleEngine")
public class RuleEngine
{

    /**
     * Creates a new instance of RuleEngine
     */
    public RuleEngine(  )
    {
    }

    /**
     * Returns the unique instance of the {@link RuleEngine} service.
     * 
     * <p>This method is deprecated and is provided for backward compatibility only. 
     * For new code, use dependency injection with {@code @Inject} to obtain the 
     * {@link RuleEngine} instance instead.</p>
     * 
     * @return The unique instance of {@link RuleEngine}.
     * 
     * @deprecated Use {@code @Inject} to obtain the {@link RuleEngine} 
     * instance. This method will be removed in future versions.
     */
    @Deprecated( since = "8.0", forRemoval = true )
    public static RuleEngine getInstance(  )
    {
        return CDI.current( ).select( RuleEngine.class ).get( );
    }

    /**
     * Initialize
     */
    public void init(  )
    {
        // Initialize rules 
        for ( Rule ruleType : RuleHome.getRuleTypes(  ) )
        {
            ruleType.init(  );
        }
    }

    /**
     * Observe document events via CDI
     * @param event The document event
     * @throws DocumentException if error occurs
     */
    public void onDocumentEvent(@Observes DocumentEvent event) throws DocumentException
    {
        processDocumentEvent(event);
    }
    
    /**
     * Process a document event
     * @param event The event to process
     * @throws DocumentException raise when error occurs in event or rule
     */
    public void processDocumentEvent( DocumentEvent event )
        throws DocumentException
    {
        for ( Rule rule : RuleHome.findAll( Locale.getDefault(  ) ) )
        {
            rule.apply( event );
        }
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
    public void initializedService(@Observes @Initialized(ApplicationScoped.class) ServletContext context) 
    {
        // This method is intentionally left empty to trigger CDI bean instantiation
    }
}
