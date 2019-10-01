package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

import java.util.List;

/**
 * Default implementation of the <code>transformMultiple</code> method,
 * which will throw an UnsupportedOperationException.
 *
 * @param <I> The input entity
 * @param <O> The output entity
 */
public abstract class AbstractTransformerBase<I, O> implements Transformer<I, O> {
    @Override
    public List<O> transformMultiple(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity) {
        throw new UnsupportedOperationException("transformMultiple() method not supported in this transformer");
    }
}
