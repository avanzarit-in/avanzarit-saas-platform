package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Listens for failures in the {@link TransformationTrigger}.
 *
 * @param <I> The type of input entities that this listener supports.
 */
public interface TransformerFailureListener<I> {
    /**
     * Notifies the listener that transformation failed.
     *
     * @param updateInfo The identification information of the current update event.
     * @param oldEntity  The old version of the input entity.
     * @param newEntity  The new version of the input entity.
     * @param message    A message indicating why transformation failed.
     */
    void onFailure(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity, String message);
}
