package au.qut.edu.eresearch.serverlesssearch.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@RegisterForReflection
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Total {

    private long value;

    private String relation;

}
