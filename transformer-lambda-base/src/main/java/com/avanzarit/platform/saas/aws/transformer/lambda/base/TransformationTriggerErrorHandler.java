package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Adds additional error handling logic to the {@link TransformationTrigger}.
 *
 * @param <I> The type of input entities that this error handler supports.
 */
public interface TransformationTriggerErrorHandler<I> {
    /**
     * Attempts to handle an exception.
     *
     * @param updateInfo The identification information of the current update event.
     * @param oldEntity  The old version of an input entity.
     * @param newEntity  The new version of an input entity.
     * @param e          The exception that has occurred.
     * @return true if the exception was successfully handled by this handler, false otherwise.
     */
    boolean handleError(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity, Exception e);
}
