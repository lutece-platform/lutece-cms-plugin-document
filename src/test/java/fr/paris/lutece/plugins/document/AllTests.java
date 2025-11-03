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
package fr.paris.lutece.plugins.document;

import fr.paris.lutece.plugins.document.business.DocumentTest;
import fr.paris.lutece.plugins.document.business.DocumentTypeTest;
import fr.paris.lutece.plugins.document.business.history.HistoryTest;
import fr.paris.lutece.plugins.document.business.rules.DocumentRulesTest;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpaceTest;
import fr.paris.lutece.plugins.document.modules.metadatadublincore.business.DublinCoreMetadataTest;
import fr.paris.lutece.plugins.document.service.docsearch.DocSearchServiceTest;
import fr.paris.lutece.plugins.document.web.DocumentJspBeanTest;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Main test suite for the Document plugin
 */
@Suite
@SelectClasses({
    DocumentTest.class,
    DocumentTypeTest.class,
    HistoryTest.class,
    DocumentSpaceTest.class,
    DocumentRulesTest.class,
    DublinCoreMetadataTest.class,
    DocumentJspBeanTest.class,
    DocSearchServiceTest.class
})
public class AllTests
{

}

