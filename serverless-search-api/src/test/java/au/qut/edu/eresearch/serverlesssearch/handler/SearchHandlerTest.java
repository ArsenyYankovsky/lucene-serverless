package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.*;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestProfile(SearchHandlerTestProfile.class)
public class SearchHandlerTest {

    @Inject
    IndexService indexService;

    @Test
    public void search() throws Exception {

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


}