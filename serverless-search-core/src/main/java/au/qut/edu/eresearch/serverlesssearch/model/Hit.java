package au.qut.edu.eresearch.serverlesssearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Hit {

    @JsonProperty("_index")
    private String index;

    @JsonProperty("_type")
    private String type = "_doc";

    @JsonProperty("_id")
    private String id;

    @JsonProperty("_score")
    private float score;

    @JsonProperty("_source")
    private Map<String, Object> source;

}
