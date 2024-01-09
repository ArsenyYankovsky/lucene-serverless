package dev.arseny.graalvm.substitutions;

import com.oracle.svm.core.annotate.*;

/**
 * Used to disable the workaround for bug <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4724038">JDK-4724038</a>
 *
 * @author w.glanzer, 23.08.2023
 */
@TargetClass(className = "org.apache.lucene.store.MappedByteBufferIndexInputProvider")
public final class MappedByteBufferIndexInputProviderReplacement {
    @Substitute
    private static boolean checkUnmapHackSysprop() {
        return false; // disable permanently
    }
}
