package dev.arseny.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dev.arseny.RequestUtils;
import dev.arseny.model.DeleteIndexRequest;
import dev.arseny.service.IndexWriterService;
import org.apache.lucene.index.IndexWriter;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

@Named("deleteIndex")
public class DeleteIndex implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOG = Logger.getLogger(IndexHandler.class);

    @Inject
    protected IndexWriterService indexWriterService;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        DeleteIndexRequest deleteIndexRequest = RequestUtils.parseDeleteIndexRequest(event);

        IndexWriter writer = indexWriterService.getIndexWriter(deleteIndexRequest.getIndexName());

        try {
            writer.deleteAll();
            writer.commit();
            writer.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }
}
