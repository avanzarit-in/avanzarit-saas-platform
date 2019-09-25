package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;

import java.util.List;

/**
 * Defines how a {@link TransformationTrigger} should handle retries when an {@link InsufficientDataException} is
 * thrown during the transformation logic of an output channel.
 *
 * @param <I> The type of input entities that this retry policy supports.
 */
public interface TransformationTriggerRetryPolicy<I extends CoreEntity> {
    /**
     * Retries transformation of the given input entity, if applicable.
     *
     * @param missingEntityList The input entity that were missing while transforming the coreEntity.
     * @param coreEntity     The input entity that was being transformed.
     */
    boolean retry(CmwContext cmwContext, List<? extends CoreEntity> missingEntityList, CoreEntity coreEntity);
}
