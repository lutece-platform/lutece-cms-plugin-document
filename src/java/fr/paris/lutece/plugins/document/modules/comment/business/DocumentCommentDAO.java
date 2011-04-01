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
package fr.paris.lutece.plugins.document.modules.comment.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for DocumentComment objects
 */
public final class DocumentCommentDAO implements IDocumentCommentDAO
{
    // DocumentComments queries
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_comment ) FROM document_comment ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO document_comment ( id_comment, id_document, date_comment, " +
        " document_comment_name, email, ip_address, document_comment_comment, status ) " +
        " VALUES ( ? , ? , ? , ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_SELECT = " SELECT id_document, date_comment, document_comment_name, email, ip_address, document_comment_comment, status " +
        " FROM document_comment WHERE id_comment = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM document_comment WHERE id_comment = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE document_comment SET id_document = ?, document_comment_name = ?, email = ?, " +
        " ip_address = ?, document_comment_comment = ?, status = ?, date_comment = ?  WHERE id_comment = ?  ";
    private static final String SQL_QUERY_SELECT_BY_DOCUMENT = " SELECT id_comment, date_comment, document_comment_name, email, ip_address, document_comment_comment, status " +
        " FROM document_comment WHERE id_document = ? ";
    private static final String SQL_QUERY_UPDATE_COMMENT_STATUS = "UPDATE document_comment SET status = ?, date_comment = ? WHERE id_comment = ?  ";
    private static final String SQL_QUERY_SELECT_NB_COMMENT_BY_DOCUMENT = " SELECT count(*) FROM document_comment WHERE id_document = ? ";
    private static final String SQL_QUERY_SELECT_LAST_COMMENT = " SELECT id_comment, id_document, date_comment, document_comment_name, email, ip_address, document_comment_comment, status " +
        " FROM document_comment ORDER BY date_comment DESC LIMIT 1 ";

    /**
     * Generates a new primary key
     * @return The new primary key
     */
    int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     * @param documentComment The DocumentComment object
     */
    public synchronized void insert( DocumentComment documentComment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nNewPrimaryKey = newPrimaryKey( plugin );
        documentComment.setCommentId( nNewPrimaryKey );

        daoUtil.setInt( 1, documentComment.getCommentId(  ) );
        daoUtil.setInt( 2, documentComment.getDocumentId(  ) );
        daoUtil.setTimestamp( 3, documentComment.getDateComment(  ) );
        daoUtil.setString( 4, documentComment.getName(  ) );
        daoUtil.setString( 5, documentComment.getEmail(  ) );
        daoUtil.setString( 6, documentComment.getIpAddress(  ) );
        daoUtil.setString( 7, documentComment.getComment(  ) );
        daoUtil.setInt( 8, documentComment.getStatus(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of DocumentComment from the table
     * @param nCommentId The identifier of DocumentComment
     * @return the instance of the DocumentComment
     */
    public DocumentComment load( int nCommentId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nCommentId );
        daoUtil.executeQuery(  );

        DocumentComment documentComment = null;

        if ( daoUtil.next(  ) )
        {
            documentComment = new DocumentComment(  );
            documentComment.setCommentId( daoUtil.getInt( 1 ) );
            documentComment.setDocumentId( daoUtil.getInt( 2 ) );
            documentComment.setDateComment( daoUtil.getTimestamp( 3 ) );
            documentComment.setName( daoUtil.getString( 4 ) );
            documentComment.setEmail( daoUtil.getString( 5 ) );
            documentComment.setIpAddress( daoUtil.getString( 5 ) );
            documentComment.setComment( daoUtil.getString( 6 ) );
            documentComment.setStatus( daoUtil.getInt( 7 ) );
        }

        daoUtil.free(  );

        return documentComment;
    }

    /**
     * Delete a record from the table
     * @param nCommentId the documentComment identifier
     */
    public void delete( int nCommentId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nCommentId );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
      * Update the record in the table
      * @param documentComment The reference of DdocumentComment
      */
    public void store( DocumentComment documentComment, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, documentComment.getCommentId(  ) );
        daoUtil.setInt( 2, documentComment.getDocumentId(  ) );
        daoUtil.setString( 3, documentComment.getName(  ) );
        daoUtil.setString( 4, documentComment.getEmail(  ) );
        daoUtil.setString( 5, documentComment.getIpAddress(  ) );
        daoUtil.setString( 6, documentComment.getComment(  ) );
        //FIXME The date shoud be documentComment.getDateComment(  )
        daoUtil.setTimestamp( 7, new Timestamp( new java.util.Date(  ).getTime(  ) ) );
        daoUtil.setInt( 8, documentComment.getStatus(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
       * Gets all the comments for an document
       * @param nDocumentId the identifier of the Document
       * @param bPublishedOnly set to true to return published comments only
       * @return the list of comments, in chronological order for published
       * comments, in reverse chronological order otherwise
       */
    public List<DocumentComment> selectByDocument( int nDocumentId, boolean bPublishedOnly, Plugin plugin )
    {
        List<DocumentComment> listDocumentComments = new ArrayList<DocumentComment>(  );
        String strSQL = SQL_QUERY_SELECT_BY_DOCUMENT;

        if ( bPublishedOnly )
        {
            strSQL += " AND status = 1 ORDER BY date_comment ASC";
        }
        else
        {
            strSQL += "ORDER BY date_comment DESC";
        }

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.setInt( 1, nDocumentId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DocumentComment documentComment = new DocumentComment(  );
            documentComment.setCommentId( daoUtil.getInt( 1 ) );
            documentComment.setDateComment( daoUtil.getTimestamp( 2 ) );
            documentComment.setName( daoUtil.getString( 3 ) );
            documentComment.setEmail( daoUtil.getString( 4 ) );
            documentComment.setIpAddress( daoUtil.getString( 5 ) );
            documentComment.setComment( daoUtil.getString( 6 ) );
            documentComment.setStatus( daoUtil.getInt( 7 ) );

            listDocumentComments.add( documentComment );
        }

        daoUtil.free(  );

        return listDocumentComments;
    }

    /**
     * Update status from the comment
     * @param nCommentId The Comment identifier
     * @param nStatus The published Status
     */
    public void updateCommentStatus( int nCommentId, int nStatus, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_COMMENT_STATUS, plugin );
        daoUtil.setInt( 1, nStatus );
        daoUtil.setTimestamp( 2, new Timestamp( new java.util.Date(  ).getTime(  ) ) );
        daoUtil.setInt( 3, nCommentId );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Return the nb of comment for a document
     * @param nDocumentId the document identifier
     * @return the nb of document
     **/
    public int checkCommentNb( int nDocumentId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_NB_COMMENT_BY_DOCUMENT, plugin );
        daoUtil.setInt( 1, nDocumentId );
        daoUtil.executeQuery(  );

        int nCount = 0;

        if ( daoUtil.next(  ) )
        {
            nCount = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nCount;
    }
    
    /**
     * Load the data of DocumentComment from the table
	 * @param plugin the plugin
     * @return the instance of the DocumentComment
     */
    public DocumentComment loadLastComment( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LAST_COMMENT, plugin );
        daoUtil.executeQuery(  );

        DocumentComment documentComment = null;

        if ( daoUtil.next(  ) )
        {
        	int nIndex = 1;
            documentComment = new DocumentComment(  );
            documentComment.setCommentId( daoUtil.getInt( nIndex++ ) );
            documentComment.setDocumentId( daoUtil.getInt( nIndex++ ) );
            documentComment.setDateComment( daoUtil.getTimestamp( nIndex++ ) );
            documentComment.setName( daoUtil.getString( nIndex++ ) );
            documentComment.setEmail( daoUtil.getString( nIndex++ ) );
            documentComment.setIpAddress( daoUtil.getString( nIndex++ ) );
            documentComment.setComment( daoUtil.getString( nIndex++ ) );
            documentComment.setStatus( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free(  );

        return documentComment;
    }
}
