package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * A listener that runs when validation of an output entity has failed.
 *
 * @param <O> The type of output entities that this listener supports.
 */
public interface TransformerValidationFailureListener<O extends CoreEntity> {
    /**
     * Notifies the listener that validation has failed and provides it with the necessary information.
     *
     * @param updateInfo      The identification information of the current update event.
     * @param validatedEntity The entity that failed validation.
     * @param message         A message indicating why the entity is not valid.
     */
    void onValidationFailure(CmwContext cmwContext, UpdateInfo updateInfo, O validatedEntity, String message);
}
