package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.DeleteIndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.QueryRequest;
import au.qut.edu.eresearch.serverlesssearch.model.QueryResponse;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("/index")
public class IndexHandler {

    private static final Logger LOGGER = Logger.getLogger(IndexHandler.class);

    @Inject
    protected IndexService indexService;

    @Inject
    SqsClient sqs;

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    private static final ObjectReader INDEX_REQUEST_READER = new ObjectMapper().readerFor(IndexRequest.class);

    private static final ObjectWriter INDEX_REQUEST_WRITER = new ObjectMapper().writerFor(IndexRequest.class);

    private static Function<String, IndexRequest> TO_INDEX_REQUEST = message -> {
        try {
            return INDEX_REQUEST_READER.readValue(message);
        } catch (Exception e) {
            LOGGER.error("Error decoding message", e);
            throw new RuntimeException(e);
        }
    };

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleRequest(IndexRequest indexRequest) throws Exception {
        String message = INDEX_REQUEST_WRITER.writeValueAsString(indexRequest);
        SendMessageResponse response = sqs.sendMessage(m -> m.queueUrl(queueUrl).messageBody(message));
        return Response.ok().entity(response.messageId()).build();
    }

    @GET
    @Path("/process")
    public List<IndexRequest> processIndexRequests() {
        List<Message> messages = sqs.receiveMessage(m -> m.maxNumberOfMessages(10).queueUrl(queueUrl)).messages();
        List<IndexRequest> indexRequests = messages.stream().map(Message::body).map(TO_INDEX_REQUEST).collect(Collectors.toList());
        indexService.index(indexRequests);
        return indexRequests;
    }

    @POST
    @Path("/query")
    @Consumes(MediaType.APPLICATION_JSON)
    public QueryResponse query(QueryRequest queryRequest) {
        return indexService.query(queryRequest);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteIndex(DeleteIndexRequest deleteIndexRequest) {
        indexService.deleteIndex(deleteIndexRequest);
        return Response.ok().build();
    }

}
