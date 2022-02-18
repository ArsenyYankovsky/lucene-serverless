package au.qut.edu.eresearch.serverlesssearch.handler;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class HealthHandler {

    @GET
    @Path("/health")
    public Response check() {
        return Response.ok().build();
    }

}
