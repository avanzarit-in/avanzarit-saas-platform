package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

import java.util.List;

/**
 * A Transformer represents transformation logic responsible for converting from a certain input type to an output type.
 *
 * @param <I> The type of the input entity.
 * @param <O> The type of the output entity.
 */
public interface Transformer<I extends CoreEntity, O extends CoreEntity> {
    /**
     * Transforms the given input entity to the required output representation.
     *
     * @param cmwContext The context of the operation
     * @param updateInfo  The identification information of the update.
     * @param inputEntity The input entity that needs to be transformed.
     * @return The transformed entity.
     */
    O transform(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity);

    /**
     * Transforms in input entity into (possibly) multiple output entities.
     *
     * @param cmwContext  The context of the operation.
     * @param updateInfo  The identification information of the current update event.
     * @param inputEntity The new version of the entity that needs to be transformed.
     * @return A List of output entities.
     */
    List<O> transformMultiple(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity);
}
