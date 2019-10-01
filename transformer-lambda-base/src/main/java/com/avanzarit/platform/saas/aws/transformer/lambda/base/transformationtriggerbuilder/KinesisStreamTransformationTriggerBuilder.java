package com.avanzarit.platform.saas.aws.transformer.lambda.base.transformationtriggerbuilder;

import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTrigger;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerBuilder;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.transformationtrigger.KinesisStreamTransformationTrigger;

/**
 * Utility class offering a small DSL to build {@link TransformationTrigger} instances easily.
 *
 * @param <I> The type of input entity that the transformation trigger will process.
 */
public class KinesisStreamTransformationTriggerBuilder<I, O> extends TransformationTriggerBuilder<I, O> {
    public KinesisStreamTransformationTriggerBuilder() {
        super();
    }

    @Override
    protected TransformationTrigger<I, O> createTransformationTrigger() {
        return new KinesisStreamTransformationTrigger<>(getEntityClass());
    }
}
