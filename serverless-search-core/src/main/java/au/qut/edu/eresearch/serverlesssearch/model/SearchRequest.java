package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@RegisterForReflection

@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SearchRequest {

    private String indexName;
    private String query;


}
