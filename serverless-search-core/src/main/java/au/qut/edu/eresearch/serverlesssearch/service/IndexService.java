package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.model.DeleteIndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.QueryRequest;
import au.qut.edu.eresearch.serverlesssearch.model.QueryResponse;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class IndexService {

    @ConfigProperty(name = "index.mount")
    String indexMount;

    private static final Logger LOGGER = Logger.getLogger(IndexService.class);

    public QueryResponse query(QueryRequest queryRequest) {
        QueryParser qp = new QueryParser("content", new StandardAnalyzer());
        QueryResponse queryResponse = new QueryResponse();
        try {
            Query query = qp.parse(queryRequest.getQuery());
            IndexSearcher searcher = getIndexSearcher(queryRequest.getIndexName());
            TopDocs topDocs = searcher.search(query, 10);
            for (ScoreDoc scoreDocs : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDocs.doc);
                Map<String, String> result = new HashMap<>();
                for (IndexableField field : document.getFields()) {
                    result.put(field.name(), field.stringValue());
                }
                queryResponse.getDocuments().add(result);
            }
            queryResponse.setTotalDocuments((topDocs.totalHits.relation == TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO ? "≥" : "") + topDocs.totalHits.value);
            return queryResponse;
        } catch (ParseException | IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }


    public int index(List<IndexRequest> indexRequests) {
        Map<String, IndexWriter> writerMap = new HashMap<>();
        var documentsIndexed = 0;
        for (IndexRequest indexRequest : indexRequests) {
            IndexWriter writer;
            if (writerMap.containsKey(indexRequest.getIndexName())) {
                writer = writerMap.get(indexRequest.getIndexName());
            } else {
                writer = getIndexWriter(indexRequest.getIndexName());
                writerMap.put(indexRequest.getIndexName(), writer);
            }
            List<Document> documents = new ArrayList<>();
            for (Map<String, Object> requestDocument : indexRequest.getDocuments()) {
                Document document = new Document();
                for (Map.Entry<String, Object> entry : requestDocument.entrySet()) {
                    document.add(new TextField(entry.getKey(), entry.getValue().toString(), Field.Store.YES));
                }
                documents.add(document);
            }
            try {
                writer.addDocuments(documents);
                documentsIndexed += documents.size();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        for (IndexWriter writer : writerMap.values()) {
            try {
                writer.commit();
                writer.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        return documentsIndexed;
    }

    public void deleteIndex(DeleteIndexRequest deleteIndexRequest) {
        IndexWriter writer = getIndexWriter(deleteIndexRequest.getIndexName());
        try {
            writer.deleteAll();
            writer.commit();
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }


    private IndexWriter getIndexWriter(String indexName) {
        try {
            IndexWriter indexWriter = new IndexWriter(
                    FSDirectory.open(Paths.get(indexMount + indexName)),
                    new IndexWriterConfig(new StandardAnalyzer())
                            .setIndexDeletionPolicy(NoDeletionPolicy.INSTANCE)
            );
            return indexWriter;
        } catch (IOException e) {
            LOGGER.error("Error while trying to create an index writer for index " + indexName, e);
            throw new RuntimeException(e);
        }
    }

    private IndexSearcher getIndexSearcher(String indexName) {
        try {
            DirectoryReader newDirectoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexMount + indexName)));
            return new IndexSearcher(newDirectoryReader);
        } catch (IOException e) {
            LOGGER.error("Error while trying to create an index searcher for index " + indexName, e);
            throw new RuntimeException(e);
        }
    }


}
