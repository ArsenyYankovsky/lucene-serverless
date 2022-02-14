package au.qut.edu.eresearch.serverlesssearch.handler;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/health")
public class HealthHandler {

    private static final Logger LOGGER = Logger.getLogger(HealthHandler.class);

    @GET
    public Response check() {
        return Response.ok().build();
    }

}
