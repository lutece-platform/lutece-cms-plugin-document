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
package fr.paris.lutece.plugins.document.service.docsearch;

import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.IndexerAction;
import fr.paris.lutece.plugins.document.business.IndexerActionFilter;
import fr.paris.lutece.plugins.document.business.IndexerActionHome;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


/**
 * DocumentSearchService
 */
public class DocSearchService
{
    // Constants corresponding to the variables defined in the lutece.properties file
    public static final String PATH_INDEX = "document.docsearch.lucene.indexPath";
    public static final String PARAM_FORCING = "forcing";
    public static final String PATTERN_DATE = "dd/MM/yy";
    private static final String PROPERTY_ANALYSER_CLASS_NAME = "document.docsearch.lucene.analyser.className";
    private static final int MAX_RESPONSES = 1000000;
    private static String _strIndex;
    private static Analyzer _analyzer;
    private static IndexSearcher _searcher;
    private static DocSearchService _singleton;
    private static IDocSearchIndexer _indexer;

    /** Creates a new instance of DocumentSearchService */
    private DocSearchService(  )
    {
        // Read configuration properties
        _strIndex = AppPathService.getPath( PATH_INDEX );

        if ( ( _strIndex == null ) || ( _strIndex.equals( StringUtils.EMPTY ) ) )
        {
            throw new AppException( "Lucene index path not found in document.properties", null );
        }

        String strAnalyserClassName = AppPropertiesService.getProperty( PROPERTY_ANALYSER_CLASS_NAME );

        if ( ( strAnalyserClassName == null ) || ( strAnalyserClassName.equals( StringUtils.EMPTY ) ) )
        {
            throw new AppException( "Analyser class name not found in lucene.properties", null );
        }

        _indexer = SpringContextService.getBean( "document.docSearchIndexer" );

        try
        {
            _analyzer = (Analyzer) Class.forName( strAnalyserClassName ).newInstance(  );
        }
        catch ( Exception e )
        {
            throw new AppException( "Failed to load Lucene Analyzer class", e );
        }
    }

    /**
     * The singleton
     * @return instance of DocSearchService
     */
    public static DocSearchService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new DocSearchService(  );
        }

        return _singleton;
    }

    /**
     * Indexing documents for searching
     * @param bCreate tell if it's total indexing or total (total = true)
     * @return indexing logs
     */
    public String processIndexing( boolean bCreate )
    {
        StringBuilder sbLogs = new StringBuilder(  );

        IndexWriter writer = null;
        boolean bCreateIndex = bCreate;

        try
        {
            sbLogs.append( "\r\nIndexing all contents ...\r\n" );

            Directory dir = NIOFSDirectory.open( Paths.get( _strIndex ) );

            if ( !DirectoryReader.indexExists( dir ) )
            { //init index
                bCreateIndex = true;
            }

            Date start = new Date(  );
            IndexWriterConfig conf = new IndexWriterConfig( _analyzer );

            if ( bCreateIndex )
            {
                conf.setOpenMode( OpenMode.CREATE );
            }
            else
            {
                conf.setOpenMode( OpenMode.APPEND );
            }

            writer = new IndexWriter( dir, conf );

            if ( !bCreateIndex )
            {
                //incremental indexing

                //add all document which must be add
                for ( IndexerAction action : getAllIndexerActionByTask( IndexerAction.TASK_CREATE ) )
                {
                    ArrayList<Integer> luceneDocumentId = new ArrayList<Integer>(  );
                    try
                    {
                        luceneDocumentId.add( action.getIdDocument(  ) );

                        List<org.apache.lucene.document.Document> luceneDocument = _indexer.getDocuments( luceneDocumentId );

                        if ( ( luceneDocument != null ) && ( luceneDocument.size(  ) > 0 ) )
                        {
                            Iterator<org.apache.lucene.document.Document> it = luceneDocument.iterator(  );

                            while ( it.hasNext(  ) )
                            {
                                org.apache.lucene.document.Document doc = it.next(  );
                                writer.addDocument( doc );
                                sbLogs.append( "Adding " );
                                sbLogs.append( doc.get( DocSearchItem.FIELD_TYPE ) );
                                sbLogs.append( " #" );
                                sbLogs.append( doc.get( DocSearchItem.FIELD_UID ) );
                                sbLogs.append( " - " );
                                sbLogs.append( doc.get( DocSearchItem.FIELD_TITLE ) );
                                sbLogs.append( "\r\n" );
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                        sbLogs.append( "Indexing DocId " + luceneDocumentId + " Error durign document indexation parsing.\r\n" );
                        sbLogs.append( "Caught a " );
                        sbLogs.append( e.getClass(  ) );
                        sbLogs.append( "\r\n with message: " );
                        sbLogs.append( e.getMessage(  ) );
                        AppLogService.error( "Indexing error : ", e );
                    }

                    removeIndexerAction( action.getIdAction(  ) );
                }

                //Update all document which must be update
                for ( IndexerAction action : getAllIndexerActionByTask( IndexerAction.TASK_MODIFY ) )
                {
                    ArrayList<Integer> luceneDocumentId = new ArrayList<Integer>(  );
                    try
                    {
                        luceneDocumentId.add( action.getIdDocument(  ) );

                        List<org.apache.lucene.document.Document> luceneDocument = _indexer.getDocuments( luceneDocumentId );

                        if ( ( luceneDocument != null ) && ( luceneDocument.size(  ) > 0 ) )
                        {
                            Iterator<org.apache.lucene.document.Document> it = luceneDocument.iterator(  );

                            while ( it.hasNext(  ) )
                            {
                                org.apache.lucene.document.Document doc = it.next(  );
                                writer.updateDocument( new Term( DocSearchItem.FIELD_UID,
                                        Integer.toString( action.getIdDocument(  ) ) ), doc );
                                sbLogs.append( "Updating " );
                                sbLogs.append( doc.get( DocSearchItem.FIELD_TYPE ) );
                                sbLogs.append( " #" );
                                sbLogs.append( doc.get( DocSearchItem.FIELD_UID ) );
                                sbLogs.append( " - " );
                                sbLogs.append( doc.get( DocSearchItem.FIELD_TITLE ) );
                                sbLogs.append( "\r\n" );
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                        sbLogs.append( "Indexing DocId " + luceneDocumentId + " Error durign document indexation parsing.\r\n" );
                        sbLogs.append( "Caught a " );
                        sbLogs.append( e.getClass(  ) );
                        sbLogs.append( "\r\n with message: " );
                        sbLogs.append( e.getMessage(  ) );
                        AppLogService.error( "Indexing error : ", e );
                    }

                    removeIndexerAction( action.getIdAction(  ) );
                }

                //delete all document which must be delete
                for ( IndexerAction action : getAllIndexerActionByTask( IndexerAction.TASK_DELETE ) )
                {
                    writer.deleteDocuments( new Term( DocSearchItem.FIELD_UID,
                            Integer.toString( action.getIdDocument(  ) ) ) );
                    sbLogs.append( "Deleting " );
                    sbLogs.append( " #" );
                    sbLogs.append( action.getIdDocument(  ) );
                    sbLogs.append( "\r\n" );

                    removeIndexerAction( action.getIdAction(  ) );
                }
            }
            else
            {
                //delete all incremental action
                removeAllIndexerAction(  );

                Collection<Integer> listIdDocuments = DocumentHome.findAllPrimaryKeys(  );

                for ( Integer nIdDocument : listIdDocuments )
                {

                    ArrayList<Integer> luceneDocumentId = new ArrayList<Integer>(  );
                    try
                    {
                        luceneDocumentId.add( nIdDocument );

                        List<Document> listDocuments = _indexer.getDocuments( luceneDocumentId );

                        for ( Document doc : listDocuments )
                        {
                            writer.addDocument( doc );
                            sbLogs.append( "Indexing " );
                            sbLogs.append( doc.get( DocSearchItem.FIELD_TYPE ) );
                            sbLogs.append( " #" );
                            sbLogs.append( doc.get( DocSearchItem.FIELD_UID ) );
                            sbLogs.append( " - " );
                            sbLogs.append( doc.get( DocSearchItem.FIELD_TITLE ) );
                            sbLogs.append( "\r\n" );
                        }
                    }
                    catch ( Exception e )
                    {
                        sbLogs.append( "Indexing DocId " + luceneDocumentId + " Error durign document indexation parsing.\r\n" );
                        sbLogs.append( "Caught a " );
                        sbLogs.append( e.getClass(  ) );
                        sbLogs.append( "\r\n with message: " );
                        sbLogs.append( e.getMessage(  ) );
                        AppLogService.error( "Indexing error : ", e );
                    }
                }
            }

            Date end = new Date(  );
            sbLogs.append( "Duration of the treatment : " );
            sbLogs.append( end.getTime(  ) - start.getTime(  ) );
            sbLogs.append( " milliseconds\r\n" );
        }
        catch ( Exception e )
        {
            sbLogs.append( " caught a " );
            sbLogs.append( e.getClass(  ) );
            sbLogs.append( "\n with message: " );
            sbLogs.append( e.getMessage(  ) );
            sbLogs.append( "\r\n" );
            AppLogService.error( "Indexing error : " + e.getMessage(  ), e );
        }
        finally
        {
            try
            {
                if ( writer != null )
                {
                    writer.close(  );
                }
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        return sbLogs.toString(  );
    }

    /**
     * Return search results
     * @param strQuery The search query
     * @param nStartIndex The start index
     * @param user The user
     * @return Results as a collection of SarchItem
     */
    public List<DocSearchItem> getSearchResults( String strQuery, int nStartIndex, AdminUser user )
    {
        ArrayList<DocSearchItem> listResults = new ArrayList<DocSearchItem>(  );

        try( Directory directory = NIOFSDirectory.open( Paths.get( _strIndex ) ) ; IndexReader ir = DirectoryReader.open( directory ) ; )
        {
            _searcher = new IndexSearcher( ir );

            QueryParser parser = new QueryParser( DocSearchItem.FIELD_CONTENTS,
                    _analyzer );
            Query query = parser.parse( ( StringUtils.isNotBlank( strQuery ) ) ? strQuery : "*:*" );

            List<DocumentSpace> listSpaces = DocumentSpacesService.getInstance(  ).getUserAllowedSpaces( user );
            Query[] filters = new Query[listSpaces.size(  )];
            int nIndex = 0;

            for ( DocumentSpace space : listSpaces )
            {
                Query querySpace = new TermQuery( new Term( DocSearchItem.FIELD_SPACE, "s" + space.getId(  ) ) );
                filters[nIndex++] = querySpace;
            }

            BooleanQuery.Builder booleanQueryBuilderFilters  = new BooleanQuery.Builder( );
            for (Query filter: filters) {
                booleanQueryBuilderFilters.add( filter , BooleanClause.Occur.SHOULD );
            }
            Query allFilters = booleanQueryBuilderFilters.build( );

            BooleanQuery.Builder booleanQueryBuilder  = new BooleanQuery.Builder( );
            booleanQueryBuilder.add( allFilters, BooleanClause.Occur.FILTER );
            booleanQueryBuilder.add( query, BooleanClause.Occur.MUST );

            // Get results documents
            TopDocs topDocs = _searcher.search( booleanQueryBuilder.build( ) , MAX_RESPONSES );
            ScoreDoc[] hits = topDocs.scoreDocs;

            for ( ScoreDoc hit : hits )
            {
                int docId = hit.doc;
                Document document = _searcher.doc( docId );
                DocSearchItem si = new DocSearchItem( document );
                listResults.add( si );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return listResults;
    }

    /**
     * Return advanced search results
     * @param strQuery The search query
     * @param bTitle true for query in title
     * @param bSummary true for query in summary
     * @param date for filtering the result by date
     * @param documentType for filtering the result by type
     * @return Results as a collection of SarchItem
     */
    public List<DocSearchItem> getSearchResults( String strQuery, boolean bTitle, boolean bSummary, String date,
        DocumentType documentType )
    {
        ArrayList<DocSearchItem> listResults = new ArrayList<DocSearchItem>(  );

        try( Directory directory = NIOFSDirectory.open( Paths.get( _strIndex ) ) ; IndexReader ir = DirectoryReader.open( directory ) ; )
        {
            _searcher = new IndexSearcher( ir );

            Collection<String> queries = new ArrayList<String>(  );
            Collection<String> fields = new ArrayList<String>(  );
            Collection<BooleanClause.Occur> flags = new ArrayList<BooleanClause.Occur>(  );

            if ( bTitle )
            {
                Query queryTitle = new TermQuery( new Term( DocSearchItem.FIELD_TITLE, strQuery ) );
                queries.add( queryTitle.toString(  ) );
                fields.add( DocSearchItem.FIELD_TITLE );
                flags.add( BooleanClause.Occur.SHOULD );
            }

            if ( bSummary )
            {
                Query querySummary = new TermQuery( new Term( DocSearchItem.FIELD_SUMMARY, strQuery ) );
                queries.add( querySummary.toString(  ) );
                fields.add( DocSearchItem.FIELD_SUMMARY );
                flags.add( BooleanClause.Occur.SHOULD );
            }

            if ( !( bTitle ) && !( bSummary ) && !( strQuery.equals( StringUtils.EMPTY ) ) )
            {
                Query queryContents = new TermQuery( new Term( DocSearchItem.FIELD_CONTENTS, strQuery ) );
                queries.add( queryContents.toString(  ) );
                fields.add( DocSearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.SHOULD );
            }

            Query queryMulti = null;

            if ( strQuery.equals( StringUtils.EMPTY ) )
            {
                if ( documentType != null )
                {
                    Query queryType = new TermQuery( new Term( DocSearchItem.FIELD_TYPE,
                                "\"" + documentType.getName(  ) + "\"" ) );
                    queries.add( queryType.toString(  ) );
                    fields.add( DocSearchItem.FIELD_TYPE );
                    flags.add( BooleanClause.Occur.SHOULD );
                }

                if ( ( date != null ) && ( !date.equals( StringUtils.EMPTY ) ) )
                {
                    String formatedDate = formatDate( date );

                    Query queryDate = new TermQuery( new Term( DocSearchItem.FIELD_DATE, formatedDate ) );
                    queries.add( queryDate.toString(  ) );
                    fields.add( DocSearchItem.FIELD_DATE );
                    flags.add( BooleanClause.Occur.SHOULD );
                }

                KeywordAnalyzer analyzer = new KeywordAnalyzer(  );

                queryMulti = MultiFieldQueryParser.parse(
                        queries.toArray( new String[queries.size(  )] ), fields.toArray( new String[fields.size(  )] ),
                        flags.toArray( new BooleanClause.Occur[flags.size(  )] ), analyzer );
            }
            else
            {
                queryMulti = MultiFieldQueryParser.parse(
                        queries.toArray( new String[queries.size(  )] ), fields.toArray( new String[fields.size(  )] ),
                        flags.toArray( new BooleanClause.Occur[flags.size(  )] ), IndexationService.getAnalyser(  ) );
            }

            List<Query> filterList = new ArrayList<Query>(  );

            if ( documentType != null )
            {
                Query queryType = new TermQuery( new Term( DocSearchItem.FIELD_TYPE, documentType.getName(  ) ) );
                filterList.add( queryType );
            }

            if ( ( date != null ) && ( !date.equals( StringUtils.EMPTY ) ) )
            {
                String formatedDate = formatDate( date );
                Query queryDate = new TermQuery( new Term( DocSearchItem.FIELD_DATE, formatedDate ) );
                filterList.add( queryDate );
            }

            TopDocs topDocs = null;

            if ( filterList.size(  ) > 0 )
            {
                BooleanQuery.Builder booleanQueryBuilderFilters  = new BooleanQuery.Builder( );
                for (Query filter: filterList) {
                    booleanQueryBuilderFilters.add( filter , BooleanClause.Occur.MUST );
                }
                Query allFilters = booleanQueryBuilderFilters.build( );

                BooleanQuery.Builder booleanQueryBuilder  = new BooleanQuery.Builder( );
                booleanQueryBuilder.add( allFilters , BooleanClause.Occur.FILTER );
                booleanQueryBuilder.add( queryMulti , BooleanClause.Occur.MUST );
                topDocs = _searcher.search( booleanQueryBuilder.build( ), MAX_RESPONSES );
            }
            else
            {
                topDocs = _searcher.search( queryMulti, MAX_RESPONSES );
            }

            ScoreDoc[] hits = topDocs.scoreDocs;

            for ( ScoreDoc hit : hits )
            {
                int docId = hit.doc;
                Document document = _searcher.doc( docId );
                listResults.add( new DocSearchItem( document ) );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return listResults;
    }

    /**
     * return a list of IndexerAction by task key
     * @param nIdTask the task key
     * @return a list of IndexerAction
     */
    public List<IndexerAction> getAllIndexerActionByTask( int nIdTask )
    {
        IndexerActionFilter filter = new IndexerActionFilter(  );
        filter.setIdTask( nIdTask );

        return IndexerActionHome.getList( filter );
    }

    /**
     * Remove a Indexer Action
     * @param nIdAction the key of the action to remove
     */
    public void removeIndexerAction( int nIdAction )
    {
        IndexerActionHome.remove( nIdAction );
    }

    /**
     * Remove all Indexer Action
     *
     */
    public static void removeAllIndexerAction(  )
    {
        IndexerActionHome.removeAll(  );
    }

    /**
     * Add Indexer Action to perform on a record
     * @param nIdDocument the document id
     * @param nIdTask the key of the action to do
     */
    public void addIndexerAction( int nIdDocument, int nIdTask )
    {
        IndexerAction indexerAction = new IndexerAction(  );
        indexerAction.setIdDocument( nIdDocument );
        indexerAction.setIdTask( nIdTask );
        IndexerActionHome.create( indexerAction );
    }

    /**
     * Format the date
     * @param date the date
     * @return formatedDate the formated date
     */
    private String formatDate( String date )
    {
        DateFormat dateFormat = new SimpleDateFormat( PATTERN_DATE, Locale.FRENCH );
        dateFormat.setLenient( false );

        Date formatedDate;

        try
        {
            formatedDate = dateFormat.parse( date.trim(  ) );
        }
        catch ( ParseException e )
        {
            AppLogService.error( e );

            return null;
        }

        return dateFormat.format( formatedDate );
    }
}
