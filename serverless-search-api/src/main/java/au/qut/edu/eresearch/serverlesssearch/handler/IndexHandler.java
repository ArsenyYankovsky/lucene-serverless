package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.IndexResult;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;

@Path("/")
public class IndexHandler {

    @Inject
    SqsClient sqs;

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    @Inject
    protected IndexService indexService;


    private static final ObjectWriter INDEX_REQUEST_WRITER = new ObjectMapper().writerFor(IndexRequest.class);

    @PUT
    @Path("/{index}/_doc/{id}")
    @RolesAllowed("api/index")
    @Consumes(MediaType.APPLICATION_JSON)
    public IndexResult updateDocument(Map<String, Object> document, @PathParam("index") String index, @PathParam("id") String id) throws Exception {
        IndexRequest indexRequest = new IndexRequest(index, document, id);
        String message = INDEX_REQUEST_WRITER.writeValueAsString(indexRequest);
        sqs.sendMessage(m -> m.queueUrl(queueUrl).messageBody(message).messageGroupId(index)
                .messageDeduplicationId(String.format("%s:%d", index, indexRequest.hashCode())));
        return new IndexResult().setIndex(index).setId(id);
    }

    @POST
    @Path("/{index}/_doc/{id}")
    @RolesAllowed("api/index")
    @Consumes(MediaType.APPLICATION_JSON)
    public IndexResult updateDocumentPost(Map<String, Object> document, @PathParam("index") String index, @PathParam("id") String id) throws Exception {
       return updateDocument(document, index, id);
    }

    @POST
    @Path("/{index}/_doc")
    @RolesAllowed("api/index")
    @Consumes(MediaType.APPLICATION_JSON)
    public IndexResult addDocument(Map<String, Object> document, @PathParam("index") String index) throws Exception {
        String id = UUID.randomUUID().toString();
        return updateDocument(document, index, id);
    }

    @DELETE
    @Path("/{index}")
    @RolesAllowed("api/index")
    public Response addDocument(@PathParam("index") String index)  {
         indexService.deleteIndex(index);
         return Response.ok().build();
    }

}
