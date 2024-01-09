package dev.arseny.graalvm.substitutions;

import org.apache.lucene.analysis.tokenattributes.BytesTermAttribute;
import org.apache.lucene.analysis.tokenattributes.BytesTermAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.SentenceAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.TermFrequencyAttribute;
import org.apache.lucene.analysis.tokenattributes.TermFrequencyAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttributeImpl;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.BoostAttributeImpl;
import org.apache.lucene.search.MaxNonCompetitiveBoostAttribute;
import org.apache.lucene.search.MaxNonCompetitiveBoostAttributeImpl;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;

/**
 * Utility to create an {@link Attribute} based on a class avoiding usage of {@link java.lang.invoke.MethodHandle}
 */
public final class AttributeCreator {

    static <A extends Attribute> AttributeImpl create(Class<A> attClass) {
        if (attClass == PackedTokenAttributeImpl.class) {
            return new PackedTokenAttributeImpl();
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
        } else if (attClass == PositionLengthAttribute.class) {
            return new PositionLengthAttributeImpl();
        } else if (attClass == BoostAttribute.class) {
            return new BoostAttributeImpl();
        } else if (attClass == KeywordAttribute.class) {
            return new KeywordAttributeImpl();
        } else if (attClass == PayloadAttribute.class) {
            return new PayloadAttributeImpl();
        } else if (attClass == BytesTermAttribute.class) {
            return new BytesTermAttributeImpl();
        } else if (attClass == FlagsAttribute.class) {
            return new FlagsAttributeImpl();
        } else if (attClass == MaxNonCompetitiveBoostAttribute.class) {
            return new MaxNonCompetitiveBoostAttributeImpl();
        } else if (attClass == SentenceAttributeImpl.class) {
            return new SentenceAttributeImpl();
        }

        throw new UnsupportedOperationException(
                String.format("Attribute class '%s' not supported in the image", attClass));
    }
}
