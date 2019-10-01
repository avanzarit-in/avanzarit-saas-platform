package com.avanzarit.platform.saas.aws.transformer.lambda.base.transformationtriggerbuilder;

import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTrigger;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerBuilder;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.transformationtrigger.S3TransformationTrigger;

/**
 * Utility class offering a small DSL to build {@link TransformationTrigger} instances easily.
 *
 * @param <I> The type of input entity that the transformation trigger will process.
 */
public class S3TransformationTriggerBuilder<I, O> extends TransformationTriggerBuilder<I, O> {
    public S3TransformationTriggerBuilder() {
        super();
    }

    @Override
    protected TransformationTrigger<I, O> createTransformationTrigger() {
        return new S3TransformationTrigger<>(getEntityClass());
    }
}
