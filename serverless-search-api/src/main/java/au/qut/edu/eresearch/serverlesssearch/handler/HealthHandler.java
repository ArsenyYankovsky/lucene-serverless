package au.qut.edu.eresearch.serverlesssearch.handler;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class HealthHandler {

    @GET
    @Path("/health")
    @PermitAll
    public Response check() {
        return Response.ok().build();
    }

}
