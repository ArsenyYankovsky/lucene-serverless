package dev.arseny.graalvm.substitutions;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeFactory.StaticImplementationAttributeFactory;
import org.apache.lucene.util.AttributeImpl;

/**
 * An AttributeFactory creates instances of {@link AttributeImpl}s.
 */
@TargetClass(StaticImplementationAttributeFactory.class)
public final class StaticImplementationAttributeFactorySubstitution {
    @Substitute
    public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
        return AttributeCreator.create(attClass);
    }
}
