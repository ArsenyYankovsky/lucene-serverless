package dev.arseny;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.arseny.model.DeleteIndexRequest;
import dev.arseny.model.ErrorResponse;
import dev.arseny.model.IndexRequest;
import dev.arseny.model.QueryRequest;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestUtils {

    private static final Logger LOG = Logger.getLogger(RequestUtils.class);

    static ObjectWriter writer = new ObjectMapper().writerFor(ErrorResponse.class);
    static ObjectWriter topDocsWriter = new ObjectMapper().writerFor(ArrayList.class);
    static ObjectReader indexRequestReader = new ObjectMapper().readerFor(IndexRequest.class);
    static ObjectReader deleteIndexRequestReader = new ObjectMapper().readerFor(DeleteIndexRequest.class);
    static ObjectReader queryRequestReader = new ObjectMapper().readerFor(QueryRequest.class);

    static {
        topDocsWriter.withFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static APIGatewayProxyResponseEvent errorResponse(int errorCode, String message) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            return response.withStatusCode(errorCode).withBody(writer.writeValueAsString(new ErrorResponse(message, errorCode)));
        } catch (JsonProcessingException e) {
            LOG.error(e);
            return response.withStatusCode(500).withBody("Internal error");
        }
    }

    public static IndexRequest parseIndexRequest(String eventBody) {
        try {
            return indexRequestReader.readValue(eventBody);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse list of Index Requests in body", e);
        }
    }

    public static DeleteIndexRequest parseDeleteIndexRequest(APIGatewayProxyRequestEvent event) {
        try {
            return deleteIndexRequestReader.readValue(event.getBody());
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse a delete index request in body", e);
        }
    }

    public static QueryRequest parseQueryRequest(APIGatewayProxyRequestEvent event) {
        try {
            return queryRequestReader.readValue(event.getBody());
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse a query request in body", e);
        }
    }

    public static APIGatewayProxyResponseEvent successResponse(List<Map<String, String>> topDocs) throws JsonProcessingException {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            return response.withStatusCode(200).withBody(topDocsWriter.writeValueAsString(topDocs));
        } catch (JsonProcessingException e) {
            LOG.error(e);
            return response.withStatusCode(500).withBody("Internal error");
        }
    }
}
