package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputsavedlistener;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.service.lateststate.States;
import com.avanzarit.platform.saas.aws.service.lateststate.UpdateLatestStateService;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerOutputSavedListener;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Updates the latest product state to CONFIRMATION when a product entity was successfully saved.
 */
public class LatestStateOutputSavedListener implements TransformerOutputSavedListener<CoreEntity, CoreEntity> {

    private UpdateLatestStateService latestStateService;

    public LatestStateOutputSavedListener(UpdateLatestStateService latestStateService) {
        this.latestStateService = latestStateService;
    }

    @Override
    public void onOutputSaved(CmwContext cmwContext, UpdateInfo updateInfo, CoreEntity inputEntity,
                              CoreEntity outputEntity) {
        latestStateService.updateState(cmwContext, updateInfo, States.CONFIRMATION);
    }
}
