package au.qut.edu.eresearch.serverlesssearch.handler;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class IndexRequestHandlerProfile implements QuarkusTestProfile {
    public Map<String, String> getConfigOverrides() {
        return Map.of("quarkus.lambda.handler", "index", "index.mount", "target/indexRequest/");
    }
}
