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
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

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
                    .setRelation(topDocs.totalHits.relation == TotalHits.Relation.EQUAL_TO ? "eq" : "gte");
            return searchResults;
        } catch (ParseException | IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    public List<IndexResult> index(List<IndexRequest> indexRequests) {
        Map<String, IndexWriter> writerMap = new HashMap<>();
        List<IndexResult> indexResults = new ArrayList<>();
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
            String id = Optional.ofNullable(indexRequest.getId()).orElse(UUID.randomUUID().toString());
            try {
                if (indexRequest.getId() != null) {
                    document.add(new StringField(ID_TERM, indexRequest.getId(), Field.Store.YES));
                    writer.updateDocument(new Term(ID_TERM, indexRequest.getId()), document);
                } else {
                    document.add(new StringField(ID_TERM, id, Field.Store.YES));
                    writer.addDocument(document);
                }
                indexResults.add(new IndexResult().setIndex(indexRequest.getIndexName()).setId(id));
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
        return indexResults;
    }


    public void deleteIndex(String indexName) {
        if (!Files.exists(getIndexPath(indexName))) {
            throw new IndexNotFoundException(indexName);
        }
        try {
            IndexWriter writer = new IndexWriter(
                    FSDirectory.open(getIndexPath(indexName)),
                    new IndexWriterConfig(new StandardAnalyzer())
                            .setOpenMode(IndexWriterConfig.OpenMode.CREATE));
            writer.deleteAll();
            writer.commit();
            writer.close();

            Files.walkFileTree(getIndexPath(indexName),
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult postVisitDirectory(
                                Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(
                                Path file, BasicFileAttributes attrs)
                                throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });

        } catch (IOException e) {
            throw mapIndexException(indexName, e);
        }
    }


    private Path getIndexPath(String indexName) {
        return Paths.get(indexMount + indexName);
    }


    private RuntimeException mapIndexException(String index, IOException e) {
        if (e instanceof org.apache.lucene.index.IndexNotFoundException) {
            return new IndexNotFoundException(index, e);
        }
        LOGGER.error(String.format("Unexpected error occurred for index %s", index), e);
        return new RuntimeException(String.format("Unexpected error occurred for index %s", index), e);
    }

    private IndexWriter getIndexWriter(String indexName) {
        try {
            return new IndexWriter(
                    FSDirectory.open(getIndexPath(indexName)),
                    new IndexWriterConfig(new StandardAnalyzer())
                            .setIndexDeletionPolicy(NoDeletionPolicy.INSTANCE)
            );
        } catch (IOException e) {
            throw mapIndexException(indexName, e);
        }
    }

    private IndexSearcher getIndexSearcher(String indexName) {
        try {
            DirectoryReader newDirectoryReader = DirectoryReader.open(FSDirectory.open(getIndexPath(indexName)));
            return new IndexSearcher(newDirectoryReader);
        } catch (IOException e) {
            throw mapIndexException(indexName, e);
        }
    }


}
