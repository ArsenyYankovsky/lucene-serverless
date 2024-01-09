/*
 * Copyright Gunnar Morling
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package dev.arseny.graalvm.substitutions;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;

/**
 * An AttributeFactory creates instances of {@link AttributeImpl}s.
 */
@TargetClass(className = "org.apache.lucene.util.AttributeFactory$DefaultAttributeFactory")
public final class DefaultAttributeFactorySubstitution {
    @Substitute
    public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
        return AttributeCreator.create(attClass);
    }
}
