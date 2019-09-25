package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputsavedlistener;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.service.changelist.ChangeListService;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerOutputSavedListener;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Writes an entry to the changelist whenever an output entity is successfully saved.
 */
public class ChangeListOutputSavedListener implements TransformerOutputSavedListener<CoreEntity, CoreEntity> {

    private ChangeListService changeListService;

    public ChangeListOutputSavedListener(ChangeListService changeListService) {
        this.changeListService = changeListService;
    }

    @Override
    public void onOutputSaved(CmwContext cmwContext, UpdateInfo updateInfo, CoreEntity inputEntity,
                              CoreEntity outputEntity) {
        cmwContext.logInfo(updateInfo, "Updating change list");

        changeListService.updateChangeList(cmwContext, outputEntity);

        cmwContext.logInfo(updateInfo, "Updated change list");
    }
}
