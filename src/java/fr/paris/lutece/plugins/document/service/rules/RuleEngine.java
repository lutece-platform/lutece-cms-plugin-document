/*
 * Copyright (c) 2002-2023, City of Paris
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
import fr.paris.lutece.plugins.document.service.DocumentEventListener;
import fr.paris.lutece.plugins.document.service.DocumentException;

import java.util.Locale;


/**
 * Rule engine. Executes rules on every document event.
 */
public class RuleEngine implements DocumentEventListener
{
    private static RuleEngine _singleton = new RuleEngine(  );

    /**
     * Creates a new instance of RuleEngine
     */
    private RuleEngine(  )
    {
    }

    /**
     * Gets the unique instance
     * @return The unique instance
     */
    public static RuleEngine getInstance(  )
    {
        return _singleton;
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
}
