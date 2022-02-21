package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.base.JacksonJsonValue;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class IndexService {

    public static final String ID_TERM = "_id";

    @ConfigProperty(name = "index.mount")
    String indexMount;

    private static final Logger LOGGER = Logger.getLogger(IndexService.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public SearchResults query(SearchRequest queryRequest) {
        QueryParser qp = new QueryParser(AllField.FIELD_NAME, new StandardAnalyzer());
        SearchResults searchResults = new SearchResults();
        try {
            Query query = qp.parse(queryRequest.getQuery());
            IndexSearcher searcher = getIndexSearcher(queryRequest.getIndexName());
            long start = System.currentTimeMillis();
            TopDocs topDocs = searcher.search(query, 10);
            long end = System.currentTimeMillis();
            for (ScoreDoc scoreDocs : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDocs.doc);
                String sourceField = document.get(SourceField.FIELD_NAME);
                Map<String, Object> source = JsonUnflattener.unflattenAsMap(sourceField);
                searchResults.getHits().getHits().add(new Hit()
                        .setSource(source)
                        .setScore(scoreDocs.score)
                        .setIndex(queryRequest.getIndexName())
                        .setId(document.get(ID_TERM))
                );
            }
            searchResults
                    .setTook(end - start)
                    .getHits().getTotal()
                    .setValue(topDocs.totalHits.value)
                    .setRelation(topDocs.totalHits.relation == topDocs.totalHits.relation.EQUAL_TO ? "eq" : "gte");
            return searchResults;
        } catch (ParseException | IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    public void index(List<IndexRequest> indexRequests) {
        Map<String, IndexWriter> writerMap = new HashMap<>();
        for (IndexRequest indexRequest : indexRequests) {
            IndexWriter writer;
            if (writerMap.containsKey(indexRequest.getIndexName())) {
                writer = writerMap.get(indexRequest.getIndexName());
            } else {
                writer = getIndexWriter(indexRequest.getIndexName());
                writerMap.put(indexRequest.getIndexName(), writer);
            }
            Document document = new Document();
            JsonNode jsonNode = objectMapper.convertValue(indexRequest.getDocument(), JsonNode.class);
            JacksonJsonValue jacksonJsonValue = new JacksonJsonValue(jsonNode);
            String source = JsonFlattener.flatten(jacksonJsonValue);
            Map<String, Object> flattened = JsonFlattener.flattenAsMap(jacksonJsonValue);
            document.add(new SourceField(source));
            for (Map.Entry<String, Object> entry : flattened.entrySet()) {
                document.add(new TextField(entry.getKey(), entry.getValue().toString(), Field.Store.NO));
                document.add(new AllField(entry.getValue().toString()));
            }
            try {
                if (indexRequest.getId() != null) {
                    document.add(new StringField(ID_TERM, indexRequest.getId(), Field.Store.YES));
                    writer.updateDocument(new Term(ID_TERM, indexRequest.getId()), document);
                } else {
                    document.add(new StringField(ID_TERM, UUID.randomUUID().toString(), Field.Store.YES));
                    writer.addDocument(document);
                }
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
