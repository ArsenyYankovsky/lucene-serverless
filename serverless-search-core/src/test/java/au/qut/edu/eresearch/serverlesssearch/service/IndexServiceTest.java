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
    public void indexAndQueryNoId() {

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
                                .setId(results.getHits().getHits().get(0).getId())
                                .setScore(0.082873434f),
                        new Hit().setSource(
                                        Map.of("firstName", "James",
                                                "lastName", "Cagney"))
                                .setIndex(index)
                                .setId(results.getHits().getHits().get(1).getId())
                                .setScore(0.082873434f)
                ),
                results.getHits().getHits());

    }

    @Test
    public void indexAndQueryWithId() {

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
    public void indexAndQueryAll() {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = List.of(
                new IndexRequest()
                        .setIndexName(index)
                        .setId("dt")
                        .setDocument(
                                Map.of("firstName", "Donald",
                                        "lastName", "Trump")
                        ),
                new IndexRequest()
                        .setIndexName(index)
                        .setId("dd")
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
                                                .setId("dt")
                                                .setScore(0.082873434f),
                                        new Hit().setSource(
                                                        Map.of("firstName", "Donald",
                                                                "lastName", "Duck"))
                                                .setIndex(index)
                                                .setId("dd")
                                                .setScore(0.082873434f)
                                )),
                results.getHits());


    }

    @Test
    public void queryIndexNotFound() {

        // given
        String index = UUID.randomUUID().toString();



        // when
        Exception exception = Assertions.assertThrows(
                IndexNotFoundException.class,
                () -> indexService
                        .query(new SearchRequest()
                                .setIndexName(index).setQuery("frank")));


        // then
        Assertions.assertEquals(String.format("no such index [%s]", index), exception.getMessage());


    }

    @Test
    public void deleteIndexNotFound() {

        // given
        String index = UUID.randomUUID().toString();


        // when
        Exception exception = Assertions.assertThrows(
                IndexNotFoundException.class,
                () -> indexService
                        .deleteIndex(index));


        // then
        Assertions.assertEquals(String.format("no such index [%s]", index), exception.getMessage());

    }

    @Test
    public void createIndex() {

        // given
        String index = UUID.randomUUID().toString();


        // when
        Exception exception = Assertions.assertThrows(
                IndexNotFoundException.class,
                () -> indexService
                        .deleteIndex(index));


        // then
        Assertions.assertEquals(String.format("no such index [%s]", index), exception.getMessage());

    }

    @Test
    public void deleteIndex() {

        // given
        String index = UUID.randomUUID().toString();

        // when
        indexService.index(List.of(
                    new IndexRequest()
                            .setIndexName(index)
                            .setId("dt")
                            .setDocument(
                                    Map.of("firstName", "O'Doyle",
                                            "lastName", "Rules")
                            )));
        indexService.deleteIndex(index);

        // then
        Assertions.assertThrows(IndexNotFoundException.class, () -> indexService.deleteIndex(index));

    }


    @Test
    public void indexAndQueryNested() {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = List.of(
                new IndexRequest()
                        .setIndexName(index)
                        .setId("coolridge")
                        .setDocument(
                                Map.of("person", Map.of("firstName", "Calvin", "lastName", "Coolridge"))
                        ),
                new IndexRequest()
                        .setIndexName(index)
                        .setId("harrison")
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
                                                .setIndex(index).setId("coolridge")
                                                .setScore(0.31506687f)
                                )),
                results.getHits());

    }

}