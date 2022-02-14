package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


@QuarkusTest
@TestProfile(LocalStackSqsProfile.class)
public class IndexHandlerTest {
    

    @Test
    public void indexRequest() throws Exception {
        IndexRequest indexRequest = new IndexRequest();

        given()
                .contentType("application/json")
                .accept("application/json")
                .body(indexRequest)
                .when()
                .post("/index")
                .then()
                .statusCode(200)
                .body(UUIDMatcher.matches());
    }
}