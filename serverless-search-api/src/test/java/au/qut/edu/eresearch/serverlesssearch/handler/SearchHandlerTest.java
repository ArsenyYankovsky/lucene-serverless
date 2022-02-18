package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.SearchResults;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.apache.lucene.search.TotalHits;
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
                .setTotalHits(new TotalHits(1, TotalHits.Relation.EQUAL_TO))
                .setDocuments(List.of(Map.of("firstName", "Don", "lastName", "Draper")));

        given()
                .contentType("application/json")
                .accept("application/json")
                .param("q", "lastName:draper")
                .when()
                .get("/searchable/_search")
                .then()
                .statusCode(200)
                .body("documents[0].lastName", equalTo("Draper"));
    }


}