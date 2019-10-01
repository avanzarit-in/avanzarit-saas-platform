package com.avanzarit.platform.saas.aws.transformer.lambda.base.transformationtrigger;

import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTrigger;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A TransformationTrigger can be added to a Lambda Handler.
 * It takes care of all the logic associated with changes to a specific AWS entity (dynamoDB, S3, Kinesis) etc.
 * <p>
 * Next to handling create and update events it also provides a framework for error handling, retry and transformation
 * logic.
 *
 * @param <I> The input entity type that this trigger handles.
 */
public class KinesisStreamTransformationTrigger<I, O> extends TransformationTrigger<I, O> {
    private static final Logger LOGGER = LogManager.getLogger(KinesisStreamTransformationTrigger.class);

    public KinesisStreamTransformationTrigger(Class<I> entityClass) {
        super(entityClass);
    }

    @Override
    public UpdateInfo createUpdateInfo(Object entity) {
        return null;
    }
}