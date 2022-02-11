package au.qut.edu.eresearch.serverlesssearch.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import javax.inject.Inject;
import javax.inject.Named;

@Named("enqueue-index")
public class EnqueueIndexHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    protected String queueName = System.getenv("QUEUE_URL");

    @Inject
    protected SqsClient sqsClient;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        this.sqsClient.sendMessage(SendMessageRequest.builder()
                .messageBody(event.getBody())
                .queueUrl(queueName).build());

        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }
}
