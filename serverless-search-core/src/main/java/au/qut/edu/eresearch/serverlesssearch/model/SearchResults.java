package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.lucene.search.TotalHits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SearchResults {

    private TotalHits totalHits;
    private List<Map<String, Object>> documents = new ArrayList<>();


}
