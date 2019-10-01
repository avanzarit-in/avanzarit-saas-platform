package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * A listener that runs before any transformation happens. This allows for additional logging or reporting to happen.
 *
 * @param <I> The type of input entities that this listener supports.
 */
public interface TransformerPreTransformationListener<I> {

    /**
     * Notifies the listener that transformation is about to take place and provides it with all the necessary data.
     *
     * @param updateInfo The identification information of the current update event.
     * @param oldEntity  The old version of the input entity.
     * @param newEntity  The new version of the input entity.
     */
    void onPreTransformation(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity);
}
