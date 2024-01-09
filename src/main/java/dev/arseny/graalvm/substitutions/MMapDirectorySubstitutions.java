package dev.arseny.graalvm.substitutions;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(org.apache.lucene.store.MMapDirectory.class)
final class MMapDirectorySubstitutions {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FromAlias, isFinal = true)
    private static boolean UNMAP_SUPPORTED = false;
}
