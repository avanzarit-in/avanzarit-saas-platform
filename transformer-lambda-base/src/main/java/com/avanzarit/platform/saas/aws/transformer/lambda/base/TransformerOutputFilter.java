package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * A filter that is capable of filtering out transformed output.
 *
 * @param <I> The type of input entities that this filter supports.
 * @param <O> The type of output entities that this filter supports.
 */
public interface TransformerOutputFilter<I, O> {
    /**
     * Determines whether the given output entity should be filtered out.
     *
     * @param updateInfo     The identification information of the current update event.
     * @param newInputEntity The new version of the input entity.
     * @param outputEntity   The output entity that is potentially to be filtered out.
     * @return true if the output entity should be filtered out, false otherwise.
     */
    boolean filteredOut(CmwContext cmwContext, UpdateInfo updateInfo, I newInputEntity, O outputEntity);
}
