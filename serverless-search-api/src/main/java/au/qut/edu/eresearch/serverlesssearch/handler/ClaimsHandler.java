package au.qut.edu.eresearch.serverlesssearch.handler;

import io.quarkus.oidc.IdToken;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Set;

@Path("/claims")
@Authenticated
public class ClaimsHandler {

    @Inject
    @IdToken
    JsonWebToken idToken;

    @GET
    @Path("/id/scope")
    @PermitAll
    public String idScope() {
        return idToken.getClaim("scope");
    }

    @GET
    @Path("/id/groups")
    @PermitAll
    public Set<String> idGroups() {
        return idToken.getGroups();
    }

    @GET
    @Path("/id/subject")
    @PermitAll
    public String idSubject() {
        return idToken.getSubject();
    }


}
