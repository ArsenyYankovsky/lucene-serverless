package au.qut.edu.eresearch.serverlesssearch.handler;

import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Set;

@Path("/claims")
public class ClaimsHandler {

    @Inject
    JsonWebToken jwt;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/token")

    public JsonWebToken token() {
        return jwt;
    }

    @GET
    @Path("/scope")

    public String scope() {
        return jwt.getClaim("scope");
    }

    @GET
    @Path("/roles")
    public Set<String> roles() {
        return securityIdentity.getRoles();
    }


    @GET
    @Path("/groups")
    public Set<String> groups() {
        return jwt.getGroups();
    }

    @GET
    @Path("/subject")
    public String subject() {
        return jwt.getSubject();
    }
}
