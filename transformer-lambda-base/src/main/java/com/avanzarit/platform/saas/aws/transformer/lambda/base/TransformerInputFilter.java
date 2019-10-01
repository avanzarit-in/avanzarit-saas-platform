package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * A filter that applies to the input provided to an output channel. It verifies whether the input applies to the
 * conditions required by the output channel and if this is not the case, filters out the input.
 *
 * @param <I> The type of the input entity.
 */
public interface TransformerInputFilter<I> {

    /**
     * Checks whether the input entity needs to be filtered out or not.
     *
     * @param oldEntity The old version of the input entity.
     * @param newEntity The new version of the input entity.
     * @return true if the entity is being filtered out, false otherwise.
     */
    boolean filteredOut(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity);
}
