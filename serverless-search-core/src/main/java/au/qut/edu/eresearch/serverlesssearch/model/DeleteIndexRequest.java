package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@RegisterForReflection
@Accessors(chain = true)
@Data
@AllArgsConstructor
public class DeleteIndexRequest {
    private String indexName;


}