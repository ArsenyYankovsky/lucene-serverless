package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.QueryRequest;
import au.qut.edu.eresearch.serverlesssearch.model.QueryResponse;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("/query")
public class QueryHandler {
    private static final Logger LOGGER = Logger.getLogger(QueryHandler.class);

    @Inject
    protected IndexService indexService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public QueryResponse query(QueryRequest queryRequest) {
        QueryParser qp = new QueryParser("content", new StandardAnalyzer());
        QueryResponse queryResponse = new QueryResponse();
        try {
            Query query = qp.parse(queryRequest.getQuery());
            IndexSearcher searcher = indexService.getIndexSearcher(queryRequest.getIndexName());
            TopDocs topDocs = searcher.search(query, 10);
            for (ScoreDoc scoreDocs : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDocs.doc);
                Map<String, String> result = new HashMap<>();
                for (IndexableField field : document.getFields()) {
                    result.put(field.name(), field.stringValue());
                }
                queryResponse.getDocuments().add(result);
            }
            queryResponse.setTotalDocuments((topDocs.totalHits.relation == TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO ? "≥" : "") + topDocs.totalHits.value);
            return queryResponse;
        } catch (ParseException | IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }
}
