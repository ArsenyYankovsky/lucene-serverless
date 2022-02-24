package au.qut.edu.eresearch.serverlesssearch.handler;

import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class ClaimsHandler {

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/claims")
    @PermitAll

    public JsonWebToken claims() {
        return jwt;
    }
}
