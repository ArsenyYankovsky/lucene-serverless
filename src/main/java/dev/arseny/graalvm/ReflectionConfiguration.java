package dev.arseny.graalvm;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.lucene.search.ScoreDoc;

@RegisterForReflection(targets = ScoreDoc.class)
public class ReflectionConfiguration {
}
