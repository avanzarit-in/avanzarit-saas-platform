package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Filter that determines whether input entities should be transformed or not.
 *
 * @param <I> The type of input entities that this filter supports.
 */
public interface TransformationTriggerFilter<I> {
    /**
     * Determines whether the given input entity should be filtered or not.
     *
     * @param updateInfo The identification information of the current update event.
     * @param oldEntity  The old version of the input entity.
     * @param newEntity  The new version of the input entity.
     * @return true if the entity has been filtered out, false otherwise.
     */
    boolean filteredOut(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity);
}
