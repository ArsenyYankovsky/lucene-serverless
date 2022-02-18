package au.qut.edu.eresearch.serverlesssearch.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.tests.EventLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestProfile(IndexRequestHandlerProfile.class)
public class IndexRequestHandlerTest {

    @Test
    public void indexRequest() throws Exception {

        SQSEvent indexRequestEvent = EventLoader.loadSQSEvent("indexRequestEvent.json");
        given()
                .contentType("application/json")
                .accept("text/plain")
                .body(indexRequestEvent)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body(is("\"Processed 1 index request(s).\""));
    }

}