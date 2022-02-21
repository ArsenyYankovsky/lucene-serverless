package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Hits {

    private Total total = new Total();

    private List<Hit> hits = new ArrayList<>();

}
