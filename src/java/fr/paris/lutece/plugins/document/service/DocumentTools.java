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
package fr.paris.lutece.plugins.document.service;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;


/**
 * This Class provides tools to fix document data
 */
public class DocumentTools
{
    private static final String PARAMETER_TRACE = "trace";
    private static final String PARAMETER_REBUILD_VALIDATED_CONTENT = "rebuild_validated_content";

    /**
     * Rebuild all XML content according data found in the document_content table
     *
     * @param bTrace Add all XML to the output
     * @param bSetValidatedContent true = create the xml validated content
     * @return An output of the process
     */
    public static String rebuildXmlContent( HttpServletRequest request )
    {
        String strTrace = request.getParameter( PARAMETER_TRACE );
        boolean bTrace = ( strTrace != null ) ? Boolean.valueOf( strTrace ) : false;
        String strRebuildValidatedContent = request.getParameter( PARAMETER_REBUILD_VALIDATED_CONTENT );
        boolean bRebuildValidatedContent = ( strRebuildValidatedContent != null )
            ? Boolean.valueOf( strRebuildValidatedContent ) : false;
        StringBuffer sbOutput = new StringBuffer(  );
        Collection<Integer> listDocumentIds = DocumentHome.findAllPrimaryKeys(  );

        for ( Integer documentId : listDocumentIds )
        {
            Document document = DocumentHome.findByPrimaryKey( documentId );

            if ( document != null )
            {
                String strXml = DocumentService.getInstance(  ).buildXmlContent( document );
                document.setXmlWorkingContent( strXml );

                if ( bRebuildValidatedContent )
                {
                    document.setXmlValidatedContent( strXml );
                }

                DocumentHome.update( document, false );

                if ( bTrace )
                {
                    sbOutput.append( "\n-----------------------------" );
                    sbOutput.append( "\nDocument Title : " );
                    sbOutput.append( document.getTitle(  ) );
                    sbOutput.append( "\nDocument Type : " );
                    sbOutput.append( document.getCodeDocumentType(  ) );
                    sbOutput.append( "\nXML Content : \n" );
                    sbOutput.append( strXml );
                }
            }
        }

        sbOutput.append( "\n=============================\n" );
        sbOutput.append( listDocumentIds.size(  ) );
        sbOutput.append( " items processed" );

        return sbOutput.toString(  );
    }
}
