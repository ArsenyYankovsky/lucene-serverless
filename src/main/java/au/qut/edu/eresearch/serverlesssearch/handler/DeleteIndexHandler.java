package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.DeleteIndexRequest;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import org.apache.lucene.index.IndexWriter;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/delete")
public class DeleteIndexHandler {
    private static final Logger LOGGER = Logger.getLogger(IndexHandler.class);

    @Inject
    protected IndexService indexService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteIndex(DeleteIndexRequest deleteIndexRequest) {

        IndexWriter writer = indexService.getIndexWriter(deleteIndexRequest.getIndexName());

        try {
            writer.deleteAll();
            writer.commit();
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e);
            return Response.serverError().build();
        }

        return Response.ok().build();
    }
}
