package com.avanzarit.platform.saas.aws.transformer.lambda.base.failurelistener;

import com.avanzarit.platform.saas.aws.service.lateststate.States;
import com.avanzarit.platform.saas.aws.service.lateststate.UpdateLatestStateService;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerFailureListener;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Listens for failures in the transformation logic and if a failure occurs, updates the latest state for the entity to
 * WARNING.
 */
public class LatestStateFailureListener<I> implements TransformerFailureListener<I> {

    private UpdateLatestStateService latestStateService;

    public LatestStateFailureListener(UpdateLatestStateService latestStateService) {
        this.latestStateService = latestStateService;
    }

    @Override
    public void onFailure(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity,
                          String message) {
        latestStateService.updateState(cmwContext, updateInfo, States.WARNING, message);
    }
}
