package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.*;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
@TestProfile(SearchHandlerTestProfile.class)
public class SearchHandlerTest {

    @Inject
    IndexService indexService;

    @Test
    public void search()  {

        // Given
        List<IndexRequest> indexRequests = List.of(
                new IndexRequest().setIndexName("searchable")
                        .setDocument(Map.of("firstName", "Don", "lastName", "Johnson"))
                        .setId("djohnson"),
                new IndexRequest().setIndexName("searchable")
                        .setDocument(Map.of("firstName", "Don", "lastName", "Draper"))
                        .setId("ddraper")
        );

        indexService.index(indexRequests);
        SearchResults expected = new SearchResults()
                .setHits(new Hits()
                        .setTotal(new Total().setValue(1).setRelation("eq"))
                        .setHits(
                                List.of(
                                        new Hit().setSource(
                                                        Map.of("person", Map.of("firstName", "Calvin", "lastName", "Coolridge")))
                                                .setIndex("searchable")
                                                .setScore(0.31506687f)
                                )));

        given()
                .auth().oauth2(Jwt
                        .claim("scope", "api/search ")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .param("q", "lastName:draper")
                .when()
                .get("/searchable/_search")
                .then()
                .log().body()
                .statusCode(200)
                .body("hits.hits[0]._source.lastName", equalTo("Draper"));
    }

    @Test
    @TestSecurity(user = "api", roles = "api/search")
    public void searchIndexNotFound() throws Exception {

        // Given
        given()
                .auth().oauth2(Jwt
                        .claim("scope", "api/search ")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .param("q", "lastName:should-not-be-found")
                .when()
                .get("/no-index/_search")
                .then()
                .log().body()
                .statusCode(404)
                .body( equalTo("no such index [no-index]"));
    }

    @Test
    @TestSecurity(user = "api", roles = "api/index")
    public void searchInvalidRole() throws Exception {

        // Given
        given()
                .auth().oauth2(Jwt
                        .claim("scope", "api/index ")
                        .issuer("https://server.example.com")
                        .audience("https://service.example.com")
                        .sign())
                .contentType("application/json")
                .accept("application/json")
                .param("q", "lastName:should-not-be-permitted")
                .when()
                .get("/not-authed/_search")
                .then()
                .log().body()
                .statusCode(403);
    }


}