package dev.arseny.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import dev.arseny.RequestUtils;
import dev.arseny.model.QueryRequest;
import dev.arseny.service.IndexSearcherService;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("query")
public class QueryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    protected IndexSearcherService indexSearcherService;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        QueryRequest queryRequest = RequestUtils.parseQueryRequest(event);

        QueryParser qp = new QueryParser("content", new StandardAnalyzer());
        try {
            Query query = qp.parse(queryRequest.getQuery());

            IndexSearcher searcher = indexSearcherService.getIndexSearcher(queryRequest.getIndexName());

            TopDocs topDocs = searcher.search(query, 10);

            List<Map<String, String>> results = new ArrayList<>();

            for (ScoreDoc scoreDocs : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDocs.doc);

                Map<String, String> result = new HashMap<>();

                for (IndexableField field : document.getFields()) {
                    result.put(field.name(), field.stringValue());
                }

                results.add(result);
            }

            return RequestUtils.successResponse(results);
        } catch (ParseException | IOException e) {
            e.printStackTrace();

            return RequestUtils.errorResponse(500, "Error");
        }
    }
}
