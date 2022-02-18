package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/")
public class IndexHandler {

    @Inject
    SqsClient sqs;

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    private static final ObjectWriter INDEX_REQUEST_WRITER = new ObjectMapper().writerFor(IndexRequest.class);

    @PUT
    @Path("/{index}/_doc/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDocument(Map<String, Object> document, @PathParam("index") String index, @PathParam("id") String id) throws Exception {
        IndexRequest indexRequest = new IndexRequest(index, document, id);
        String message = INDEX_REQUEST_WRITER.writeValueAsString(indexRequest);
        SendMessageResponse response = sqs.sendMessage(m -> m.queueUrl(queueUrl).messageBody(message));
        return Response.ok().entity(response.messageId()).build();
    }

    @POST
    @Path("/{index}/_doc/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDocumentPost(Map<String, Object> document, @PathParam("index") String index, @PathParam("id") String id) throws Exception {
       return updateDocument(document, index, id);
    }

    @POST
    @Path("/{index}/_doc")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addDocument(Map<String, Object> document, @PathParam("index") String index) throws Exception {
        return updateDocument(document, index, null);
    }

}
