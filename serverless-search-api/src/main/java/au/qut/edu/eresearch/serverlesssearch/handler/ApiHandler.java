package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.DeleteIndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.QueryRequest;
import au.qut.edu.eresearch.serverlesssearch.model.QueryResponse;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/index")
public class ApiHandler {

    @Inject
    protected IndexService indexService;

    @Inject
    SqsClient sqs;

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    private static final ObjectWriter INDEX_REQUEST_WRITER = new ObjectMapper().writerFor(IndexRequest.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleRequest(IndexRequest indexRequest) throws Exception {
        String message = INDEX_REQUEST_WRITER.writeValueAsString(indexRequest);
        SendMessageResponse response = sqs.sendMessage(m -> m.queueUrl(queueUrl).messageBody(message));
        return Response.ok().entity(response.messageId()).build();
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
