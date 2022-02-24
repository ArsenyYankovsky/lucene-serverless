package au.qut.edu.eresearch.serverlesssearch.handler;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
@TestProfile(ClaimHandlerTestProfile.class)
public class ClaimHandlerTest {

    @Test
    public void roles()  {
        given()
                .contentType("application/json")
                .accept("application/json")
                .auth().oauth2(Jwt
                        .claim("scope", "api/search api/index")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .when()
                .get("/claims/roles")
                .then()
                .log().body()
                .statusCode(200)
                .body("[0]", is("api/search"))
                .body("[1]", is("api/index"));
    }






}