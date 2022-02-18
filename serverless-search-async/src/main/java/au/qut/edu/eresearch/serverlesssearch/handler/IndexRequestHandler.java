package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Named("index")
public class IndexRequestHandler implements RequestHandler<SQSEvent, String> {

    private static final Logger LOGGER = Logger.getLogger(IndexRequestHandler.class);

    @Inject
    protected IndexService indexService;

    private static final ObjectReader INDEX_REQUEST_READER = new ObjectMapper().readerFor(IndexRequest.class);

    private static Function<String, IndexRequest> TO_INDEX_REQUEST = message -> {
        try {
            return INDEX_REQUEST_READER.readValue(message);
        } catch (Exception e) {
            LOGGER.error("Error decoding message", e);
            throw new RuntimeException(e);
        }
    };

    @Override
    public String handleRequest(SQSEvent sqsEvent, Context context) {
        List<IndexRequest> indexRequests = sqsEvent.getRecords().stream().map(SQSEvent.SQSMessage::getBody).map(TO_INDEX_REQUEST).collect(Collectors.toList());
        LOGGER.infof("Received %d index request(s).", indexRequests.size());
        indexService.index(indexRequests);
        return String.format("Processed %d index request(s).", indexRequests.size());
    }
}
