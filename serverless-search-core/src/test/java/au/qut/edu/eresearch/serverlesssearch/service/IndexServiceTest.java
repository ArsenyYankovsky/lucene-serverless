package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.model.SearchRequest;
import au.qut.edu.eresearch.serverlesssearch.model.SearchResults;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.apache.lucene.search.TotalHits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@QuarkusTest
@TestProfile(IndexServiceTestProfile.class)
public class IndexServiceTest {

    @Inject
    IndexService indexService;

    @Test
    public void indexAndQueryNoId() throws Exception {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = List.of(
                new IndexRequest()
                        .setIndexName(index)
                        .setDocument(
                                Map.of("firstName", "James",
                                        "lastName", "Cagney")
                        ),
                new IndexRequest()
                        .setIndexName(index)
                        .setDocument(
                                Map.of("firstName", "James",
                                        "lastName", "Cagney")
                        )

        );
        indexService.index(indexRequests);

        // when
        SearchResults results = indexService
                .query(new SearchRequest().setIndexName(index).setQuery("lastName:Cagney"));

        // then
        Assertions.assertEquals(
                new SearchResults()
                        .setDocuments(List.of(Map.of("firstName", "James",
                                        "lastName", "Cagney"),
                                Map.of("firstName", "James",
                                        "lastName", "Cagney"))
                        ).setTotalHits(new TotalHits(2, TotalHits.Relation.EQUAL_TO)),
                results);


    }

    @Test
    public void indexAndQueryWithId() throws Exception {

        // given
        String index = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();

        indexService.index(List.of(
                new IndexRequest()
                        .setIndexName(index)
                        .setDocument(
                                Map.of("firstName", "James",
                                        "lastName", "Cagney")
                        )
                        .setId(id),
                new IndexRequest()
                        .setIndexName(index)
                        .setDocument(
                                Map.of("firstName", "James",
                                        "lastName", "Dean")
                        )
                        .setId(id)
        ));

        // when
        SearchResults results = indexService
                .query(new SearchRequest().setIndexName(index).setQuery("firstName:James"));

        // then
        Assertions.assertEquals(
                new SearchResults()
                        .setDocuments(List.of(Map.of("_id", id, "firstName", "James",
                                        "lastName", "Dean"
                                ))
                        ).setTotalHits(new TotalHits(1, TotalHits.Relation.EQUAL_TO)),
                results);


    }

    @Test
    public void indexAndQueryAll() throws Exception {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = List.of(
                new IndexRequest()
                        .setIndexName(index)
                        .setDocument(
                                Map.of("firstName", "Donald",
                                        "lastName", "Trump")
                        ),
                new IndexRequest()
                        .setIndexName(index)
                        .setDocument(
                                Map.of("firstName", "Donald",
                                        "lastName", "Duck")
                        )

        );
        indexService.index(indexRequests);

        // when
        SearchResults results = indexService
                .query(new SearchRequest().setIndexName(index).setQuery("donald"));

        // then
        Assertions.assertEquals(
                new SearchResults()
                        .setDocuments(List.of(
                                Map.of("firstName", "Donald",
                                        "lastName", "Trump"),
                                Map.of("firstName", "Donald",
                                        "lastName", "Duck")
                        )).setTotalHits(new TotalHits(2, TotalHits.Relation.EQUAL_TO)),
                results);


    }

}