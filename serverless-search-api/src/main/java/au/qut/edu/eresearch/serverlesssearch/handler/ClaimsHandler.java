package au.qut.edu.eresearch.serverlesssearch.handler;

import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Set;

@Path("/claims")
public class ClaimsHandler {

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/scope")
    @PermitAll
    public String scope() {
        return jwt.getClaim("scope");
    }

    @GET
    @Path("/groups")
    @PermitAll
    public Set<String> groups() {
        return jwt.getGroups();
    }

    @GET
    @Path("/subject")
    @PermitAll
    public String subject() {
        return jwt.getSubject();
    }
}
