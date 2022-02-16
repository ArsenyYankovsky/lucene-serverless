package au.qut.edu.eresearch.serverlesssearch.handler;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/health")
public class HealthHandler {

    @GET
    public Response check() {
        return Response.ok().build();
    }

}
