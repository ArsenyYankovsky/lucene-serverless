package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.model.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
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
                List.of(
                        new Hit().setSource(
                                        Map.of("firstName", "James",
                                                "lastName", "Cagney"))
                                .setIndex(index)
                                .setScore(0.082873434f),
                        new Hit().setSource(
                                        Map.of("firstName", "James",
                                                "lastName", "Cagney"))
                                .setIndex(index)
                                .setScore(0.082873434f)
                ),
                results.getHits().getHits());

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
                new Hits()
                        .setTotal(new Total().setValue(1).setRelation("eq"))
                        .setHits(List.of(
                                new Hit().setSource(
                                                Map.of("firstName", "James",
                                                        "lastName", "Dean"))
                                        .setIndex(index)
                                        .setScore(0.13076457f)
                                        .setId(id)
                        )),
                results.getHits());


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
                new Hits()
                        .setTotal(new Total().setValue(2).setRelation("eq"))
                        .setHits(
                                List.of(
                                        new Hit().setSource(
                                                        Map.of("firstName", "Donald",
                                                                "lastName", "Trump"))
                                                .setIndex(index)
                                                .setScore(0.082873434f),
                                        new Hit().setSource(
                                                        Map.of("firstName", "Donald",
                                                                "lastName", "Duck"))
                                                .setIndex(index)
                                                .setScore(0.082873434f)
                                )),
                results.getHits());


    }


    @Test
    public void indexAndQueryNested() throws Exception {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = List.of(
                new IndexRequest()
                        .setIndexName(index)
                        .setDocument(
                                Map.of("person", Map.of("firstName", "Calvin", "lastName", "Coolridge"))
                        ),
                new IndexRequest()
                        .setIndexName(index)
                        .setDocument(
                                Map.of("person", Map.of("firstName", "William", "lastName", "Harrison"))
                        )
        );
        indexService.index(indexRequests);

        // when
        SearchResults results = indexService
                .query(new SearchRequest().setIndexName(index).setQuery("person.firstName:Calvin"));

        // then
        Assertions.assertEquals(
                new Hits()
                        .setTotal(new Total().setValue(1).setRelation("eq"))
                        .setHits(
                List.of(
                        new Hit().setSource(
                                        Map.of("person", Map.of("firstName", "Calvin", "lastName", "Coolridge")))
                                .setIndex(index)
                                .setScore(0.31506687f)
                )),
                results.getHits());

    }

}