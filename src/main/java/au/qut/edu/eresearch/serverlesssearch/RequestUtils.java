package au.qut.edu.eresearch.serverlesssearch;

import au.qut.edu.eresearch.serverlesssearch.model.*;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import au.qut.edu.eresearch.serverlesssearch.model.*;
import org.jboss.logging.Logger;

import java.io.IOException;

public class RequestUtils {
    private static final Logger LOG = Logger.getLogger(RequestUtils.class);

    static ObjectWriter writer = new ObjectMapper().writerFor(ErrorResponse.class);
    static ObjectWriter queryResponseWriter = new ObjectMapper().writerFor(QueryResponse.class);
    static ObjectReader indexRequestReader = new ObjectMapper().readerFor(IndexRequest.class);
    static ObjectReader deleteIndexRequestReader = new ObjectMapper().readerFor(DeleteIndexRequest.class);
    static ObjectReader queryRequestReader = new ObjectMapper().readerFor(QueryRequest.class);

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

    public static APIGatewayProxyResponseEvent successResponse(QueryResponse queryResponse) throws JsonProcessingException {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            return response.withStatusCode(200).withBody(queryResponseWriter.writeValueAsString(queryResponse));
        } catch (JsonProcessingException e) {
            LOG.error(e);
            return response.withStatusCode(500).withBody("Internal error");
        }
    }
}
