package au.qut.edu.eresearch.serverlesssearch.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.tests.EventLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestProfile(QueueHandlerTest.IndexRequestHandlerProfile.class)
public class QueueHandlerTest {

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
                .body(is("\"Indexed 2 document(s) from 1 index request(s).\""));
    }

    public class IndexRequestHandlerProfile implements QuarkusTestProfile {
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.lambda.handler", "queue", "index.mount", "target/indexRequest/");
        }
    }

}