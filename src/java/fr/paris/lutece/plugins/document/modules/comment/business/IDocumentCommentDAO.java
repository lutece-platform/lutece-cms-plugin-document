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
package fr.paris.lutece.plugins.document.modules.comment.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


/**
 * DocumentCommentDAO Interface
 */
public interface IDocumentCommentDAO
{
    /**
     * Delete a record from the table
     * @param nCommentId the documentComment identifier
     */
    void delete( int nCommentId, Plugin plugin );

    /**
     * Insert a new record in the table.
     * @param documentComment The DocumentComment object
     */
    void insert( DocumentComment documentComment, Plugin plugin );

    /**
     * Load the data of DocumentComment from the table
     * @param nCommentId The identifier of DocumentComment
     * @return the instance of the DocumentComment
     */
    DocumentComment load( int nCommentId, Plugin plugin );

    /**
     * Gets all the comments for an document
     * @param nDocumentId the identifier of the Document
     * @param bPublishedOnly set to true to return published comments only
     * @return the list of comments, in chronological order for published
     * comments, in reverse chronological order otherwise
     */
    List<DocumentComment> selectByDocument( int nDocumentId, boolean bPublishedOnly, Plugin plugin );

    /**
     * Update the record in the table
     * @param documentComment The reference of DdocumentComment
     */
    void store( DocumentComment documentComment, Plugin plugin );

    /**
     * Update status from the comment
     * @param nCommentId The Comment identifier
     * @param nStatus The published Status
     */
    void updateCommentStatus( int nCommentId, int nStatus, Plugin plugin );

    /**
     * Return the nb of comment for a document
     * @param nDocumentId the document identifier
     * @return the nb of document
     **/
    int checkCommentNb( int nDocumentId, Plugin plugin );
    
    /**
     * Load the data of DocumentComment from the table
	 * @param plugin the plugin
     * @return the instance of the DocumentComment
     */
    DocumentComment loadLastComment( Plugin plugin );
}
