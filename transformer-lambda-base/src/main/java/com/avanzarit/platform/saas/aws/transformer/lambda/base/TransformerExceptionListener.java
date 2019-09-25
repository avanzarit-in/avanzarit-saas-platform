package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * The TransformerExceptionListener is invoked when an unexpected exception occurs during transformation or while saving
 * the transformed output.
 *
 * @param <I> The type of input entity that was being transformed.
 */
public interface TransformerExceptionListener<I extends CoreEntity> {
    /**
     * Invoked when transformation or saving the output failed due to an unexpected exception.
     *
     * @param updateInfo  The identification information of the update.
     * @param inputEntity The input entity that was being transformed or saved.
     * @param e           The exception that occurred.
     * @return true if the exception was handled by the listener, false otherwise.
     */
    boolean handleException(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity, Exception e);
}
