package com.avanzarit.platform.saas.aws.transformer.lambda.base.exceptionlistener;

import com.avanzarit.platform.saas.aws.transformer.lambda.base.EntityTransformationException;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerExceptionListener;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Listens for {@link EntityTransformationException} and if one is thrown, logs a warning and marks the exception as
 * handled.
 */
public class EntityTransformationExceptionListener<I> implements TransformerExceptionListener<I> {

    @Override
    public boolean handleException(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity, Exception e) {
        boolean result = false;

        if (e instanceof EntityTransformationException) {
            cmwContext.logWarning(updateInfo, e.getMessage());
            result = true;
        }

        return result;
    }
}
