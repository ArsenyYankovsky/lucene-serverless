package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.Path;
import javax.ws.rs.GET;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/indexAsync")
public class IndexHandler {

    private static final Logger LOGGER = Logger.getLogger(IndexHandler.class);

    @Inject
    protected IndexService indexService;

    @Inject
    SqsClient sqs;

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    private static ObjectReader INDEX_REQUEST_READER = new ObjectMapper().readerFor(IndexRequest.class);

    private static Function<String, IndexRequest> TO_INDEX_REQUEST = message -> {
        try {
            return INDEX_REQUEST_READER.readValue(message);
        } catch (Exception e) {
            LOGGER.error("Error decoding message", e);
            throw new RuntimeException(e);
        }
    };

    @GET
    public List<IndexRequest> processAsyncIndexRequests() {
        List<Message> messages = sqs.receiveMessage(m -> m.maxNumberOfMessages(10).queueUrl(queueUrl)).messages();
        List<IndexRequest> indexRequests = messages.stream().map(Message::body).map(TO_INDEX_REQUEST).collect(Collectors.toList());
        Map<String, IndexWriter> writerMap = new HashMap<>();
        for (IndexRequest indexRequest : indexRequests) {
            IndexWriter writer;
            if (writerMap.containsKey(indexRequest.getIndexName())) {
                writer = writerMap.get(indexRequest.getIndexName());
            } else {
                writer = indexService.getIndexWriter(indexRequest.getIndexName());
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
        return indexRequests;
    }

}
