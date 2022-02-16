package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.QueryRequest;
import au.qut.edu.eresearch.serverlesssearch.model.QueryResponse;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(ApiTestProfile.class)
public class ApiHandlerTest {

    @Inject
    IndexService indexService;


    @Test
    public void queryRequest() throws Exception {

        // Given
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.setIndexName("query");
        indexRequest.setDocuments(List.of(
                Map.of("firstName", "Don", "lastName", "Johnson"),
                Map.of("firstName", "Don", "lastName", "Draper")));
        indexService.index(List.of(indexRequest));

        QueryResponse expected = new QueryResponse();
        expected.setTotalDocuments("1");
        expected.setDocuments(List.of(Map.of("firstName", "Don", "lastName", "Draper")));

        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setIndexName("query");
        queryRequest.setQuery("lastName:Draper");
        given()
                .contentType("application/json")
                .accept("application/json")
                .body(queryRequest)
                .when()
                .post("/index/query")
                .then()
                .statusCode(200);
//                .body(is("{\"totalDocuments\":\"1\",\"documents\":[{\"lastName\":\"Draper\",\"firstName\":\"Don\"}]}\n"));
    }



}