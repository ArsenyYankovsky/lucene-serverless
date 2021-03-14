package dev.arseny.graalvm;

import com.amazonaws.services.s3.internal.AWSS3V4Signer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.lucene.search.ScoreDoc;

@RegisterForReflection(targets = {
        ScoreDoc.class,
        AWSS3V4Signer.class,
        com.amazonaws.partitions.model.Partitions.class,
        com.amazonaws.partitions.model.Partition.class,
        com.amazonaws.partitions.model.Endpoint.class,
        com.amazonaws.partitions.model.Region.class,
        com.amazonaws.partitions.model.Service.class,
        com.amazonaws.partitions.model.CredentialScope.class,
        java.util.HashSet.class
})
public class ReflectionConfiguration {
}
