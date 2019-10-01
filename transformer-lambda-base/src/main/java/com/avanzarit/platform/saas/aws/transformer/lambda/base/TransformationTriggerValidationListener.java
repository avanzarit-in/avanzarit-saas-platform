package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

import java.util.List;

/**
 * Listens for validation failures in the {@link TransformationTrigger}.
 *
 * @param <I> The type of input entities that this listener supports.
 */
public interface TransformationTriggerValidationListener<I> {
    /**
     * Notifies the listener that validation has failed and provides it with the relevant information.
     *
     * @param updateInfo The identification information of the current update event.
     * @param newEntity  The new version of the input entity.
     * @param messages   A list of messages indicating why validation failed.
     */
    void onValidationFailure(CmwContext cmwContext, UpdateInfo updateInfo, I newEntity, List<String> messages);
}
