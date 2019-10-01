package com.avanzarit.platform.saas.aws.transformer.lambda.base.transformationtriggerbuilder;

import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTrigger;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerBuilder;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.transformationtrigger.DynamoDbTransformationTrigger;

/**
 * Utility class offering a small DSL to build {@link TransformationTrigger} instances easily.
 *
 * @param <I> The type of input entity that the transformation trigger will process.
 */
public class DynamoDbTransformationTriggerBuilder<I, O> extends TransformationTriggerBuilder<I, O> {
    public DynamoDbTransformationTriggerBuilder() {
        super();
    }

    @Override
    protected TransformationTrigger<I, O> createTransformationTrigger() {
        return new DynamoDbTransformationTrigger<>(getEntityClass());
    }
}
