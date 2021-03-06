package dev.arseny.graalvm.substitutions;


import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.lucene.util.AttributeImpl;

import java.lang.invoke.MethodHandle;

/**
 * An AttributeFactory creates instances of {@link AttributeImpl}s.
 */
@TargetClass(className = "org.apache.lucene.util.AttributeFactory")
public final class AttributeFactorySubstitution {

    public AttributeFactorySubstitution() {
    }

    @Substitute
    static final MethodHandle findAttributeImplCtor(Class<? extends AttributeImpl> clazz) {
        return null;
    }
}
