package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Path("/index")
public class EnqueueIndexHandler {

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    @Inject
    protected SqsClient sqsClient;

    private static final ObjectWriter INDEX_REQUEST_WRITER = new ObjectMapper().writerFor(IndexRequest.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleRequest(IndexRequest indexRequest) throws Exception {

        String message = INDEX_REQUEST_WRITER.writeValueAsString(indexRequest);
        SendMessageResponse response = sqsClient.sendMessage(m -> m.queueUrl(queueUrl).messageBody(message));
        return Response.ok().entity(response.messageId()).build();


    }
}
