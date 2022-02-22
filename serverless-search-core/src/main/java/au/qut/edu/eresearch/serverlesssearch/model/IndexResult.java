package au.qut.edu.eresearch.serverlesssearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class IndexResult {

    @JsonProperty("_index")
    private String index;

    @JsonProperty("_type")
    private String type = "_doc";

    @JsonProperty("_id")
    private String id;


}
