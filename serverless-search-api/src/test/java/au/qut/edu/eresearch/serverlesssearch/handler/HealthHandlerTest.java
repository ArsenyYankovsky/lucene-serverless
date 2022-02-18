package au.qut.edu.eresearch.serverlesssearch.handler;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(HealthHandlerTestProfile.class)
public class HealthHandlerTest {

    @Test
    public void health() throws Exception {
        given()
                .when()
                .get("/health")
                .then()
                .statusCode(200);
    }

}