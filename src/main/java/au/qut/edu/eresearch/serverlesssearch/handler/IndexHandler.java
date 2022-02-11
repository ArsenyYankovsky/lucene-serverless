package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.RequestUtils;
import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.service.IndexWriterService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("index")
public class IndexHandler implements RequestHandler<SQSEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOG = Logger.getLogger(IndexHandler.class);

    @Inject
    protected IndexWriterService indexWriterService;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(SQSEvent event, Context context) {
        List<SQSEvent.SQSMessage> records = event.getRecords();

        List<IndexRequest> requests = new ArrayList<>();

        for (SQSEvent.SQSMessage record : records) {
            requests.add(RequestUtils.parseIndexRequest(record.getBody()));
        }

        Map<String, IndexWriter> writerMap = new HashMap<>();

        for (IndexRequest request : requests) {
            IndexWriter writer;
            if (writerMap.containsKey(request.getIndexName())) {
                writer = writerMap.get(request.getIndexName());
            } else {
                writer = indexWriterService.getIndexWriter(request.getIndexName());
                writerMap.put(request.getIndexName(), writer);
            }

            List<Document> documents = new ArrayList<>();

            for (Map<String, Object> requestDocument : request.getDocuments()) {
                Document document = new Document();
                for (Map.Entry<String, Object> entry : requestDocument.entrySet()) {
                    document.add(new TextField(entry.getKey(), entry.getValue().toString(), Field.Store.YES));
                }
                documents.add(document);
            }

            try {
                writer.addDocuments(documents);
            } catch (IOException e) {
                LOG.error(e);
            }
        }

        for (IndexWriter writer : writerMap.values()) {
            try {
                writer.commit();
                writer.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }

        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }
}
