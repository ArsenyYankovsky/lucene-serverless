package au.qut.edu.eresearch.serverlesssearch.handler;

import io.quarkus.test.junit.QuarkusTestProfile;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class IndexHandlerTestProfile implements QuarkusTestProfile {

    private DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:latest");

    private LocalStackContainer container;

    private SqsClient client;

    public final static String QUEUE_NAME = "INDEX_QUEUE";

    public Map<String, String> getConfigOverrides() {
        DockerClientFactory.instance().client();
        String queueUrl;
        try {
            container = new LocalStackContainer(localstackImage).withServices(LocalStackContainer.Service.SQS);
            container.start();
            URI endpointOverride = container.getEndpointOverride(LocalStackContainer.EnabledService.named(LocalStackContainer.Service.SQS.getName()));

            StaticCredentialsProvider staticCredentials = StaticCredentialsProvider
                    .create(AwsBasicCredentials.create("accesskey", "secretKey"));

            client = SqsClient.builder()
                    .endpointOverride(endpointOverride)
                    .credentialsProvider(staticCredentials)
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .region(Region.US_EAST_1).build();

            queueUrl = client.createQueue(q -> q.queueName(QUEUE_NAME)).queueUrl();

            Map<String, String> properties = new HashMap<>();
            properties.put("quarkus.sqs.endpoint-override", endpointOverride.toString());
            properties.put("quarkus.sqs.aws.region", "us-east-1");
            properties.put("quarkus.sqs.aws.credentials.type", "static");
            properties.put("quarkus.sqs.aws.credentials.static-provider.access-key-id", "accessKey");
            properties.put("quarkus.sqs.aws.credentials.static-provider.secret-access-key", "secretKey");
            properties.put("queue.url", queueUrl);
            properties.put("index.mount", "target/indexRequest/");
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not start localstack server", e);
        }
    }

}
