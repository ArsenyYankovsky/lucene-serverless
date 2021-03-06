package dev.arseny.graalvm.substitutions;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.BoostAttributeImpl;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;

/**
 * An AttributeFactory creates instances of {@link AttributeImpl}s.
 */
@TargetClass(className = "org.apache.lucene.util.AttributeFactory$StaticImplementationAttributeFactory")
public final class StaticImplementationAttributeFactorySubstitution {

    public StaticImplementationAttributeFactorySubstitution() {
    }

    @Substitute
    public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
        if (attClass == BoostAttribute.class) {
            return new BoostAttributeImpl();
        } else if (attClass == CharTermAttribute.class) {
            return new CharTermAttributeImpl();
        } else if (attClass == OffsetAttribute.class) {
            return new OffsetAttributeImpl();
        } else if (attClass == PositionIncrementAttribute.class) {
            return new PositionIncrementAttributeImpl();
        } else if (attClass == TypeAttribute.class) {
            return new TypeAttributeImpl();
        } else if (attClass == TermFrequencyAttribute.class) {
            return new TermFrequencyAttributeImpl();
        } else if (attClass == PayloadAttribute.class) {
            return new PayloadAttributeImpl();
        } else if (attClass == PositionLengthAttribute.class) {
            return new PositionLengthAttributeImpl();
        } else if (attClass == KeywordAttribute.class) {
            return new KeywordAttributeImpl();
        }

        throw new UnsupportedOperationException("Unknown: " + attClass);
    }
}
