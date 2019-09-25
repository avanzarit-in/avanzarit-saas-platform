package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Listener that is invoked when retrying transformation has failed.
 *
 * @param <I> The type of input entity that the transformation used.
 */
public interface TransformationTriggerRetryListener<I extends CoreEntity> {
    /**
     * Invoked when retry has failed.
     *
     * @param updateInfo The identification information of the update that failed.
     * @param oldEntity  The old version of the input entity.
     * @param newEntity  The new version of the input entity.
     */
    void onRetryFailed(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity);
}
