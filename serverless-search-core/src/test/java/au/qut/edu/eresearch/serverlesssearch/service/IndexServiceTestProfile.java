package au.qut.edu.eresearch.serverlesssearch.service;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.HashMap;
import java.util.Map;

public class IndexServiceTestProfile implements QuarkusTestProfile {

    public Map<String, String> getConfigOverrides() {
            Map<String, String> properties = new HashMap<>();
            properties.put("queue.url", "not.used");
            properties.put("index.mount", "target/test-indexes/");
            return properties;
    }

}
