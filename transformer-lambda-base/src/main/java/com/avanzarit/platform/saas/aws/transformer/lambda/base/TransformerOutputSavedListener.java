package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Listens for when the output is saved by the {@link TransformationTrigger}.
 *
 * @param <I> The type of input entities that this listener supports.
 * @param <O> The type of output entities that this listener supports.
 */
public interface TransformerOutputSavedListener<I, O> {
    /**
     * Notifies the listener that the output was saved and provides it with the relevant data.
     *
     * @param updateInfo   The identification information of the current update event.
     * @param inputEntity  The input entity that has been transformed and saved.
     * @param outputEntity The transformed output that was saved.
     */
    void onOutputSaved(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity, O outputEntity);
}
