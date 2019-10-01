package com.avanzarit.platform.saas.aws.transformer.lambda.base.exceptionlistener;

import com.avanzarit.platform.saas.aws.service.lateststate.States;
import com.avanzarit.platform.saas.aws.service.lateststate.UpdateLatestStateService;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.EntityTransformationException;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerExceptionListener;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Listens for exceptions and updates the latest product state accordingly. If an {@link EntityTransformationException}
 * was thrown, then the state is updated to WARNING. If any other exception is thrown, then the state is updated to
 * ERROR.
 */
public class LatestStateExceptionListener<I> implements TransformerExceptionListener<I> {

    private UpdateLatestStateService latestStateService;

    public LatestStateExceptionListener(UpdateLatestStateService latestStateService) {
        this.latestStateService = latestStateService;
    }

    @Override
    public boolean handleException(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity, Exception e) {
        if (e instanceof EntityTransformationException) {
            cmwContext.logWarning(
                    updateInfo,
                    "A transformation exception occurred while handling update (message: " + e.getMessage() + ")"
            );
            latestStateService.updateState(cmwContext, updateInfo, States.WARNING, e.getMessage());
        } else {
            cmwContext.logError(
                    updateInfo,
                    "An exception occurred while handling update (message: " + e.getMessage() + ")\n"
                            + ExceptionUtils.getStackTrace(e)
            );
            latestStateService.updateState(cmwContext, updateInfo, States.ERROR, e.getMessage());
        }

        return true;
    }
}
