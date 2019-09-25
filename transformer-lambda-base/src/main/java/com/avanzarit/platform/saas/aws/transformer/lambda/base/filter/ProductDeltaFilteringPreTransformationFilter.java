package com.avanzarit.platform.saas.aws.transformer.lambda.base.filter;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.service.deltafiltering.DeltaDetectionService;
import com.avanzarit.platform.saas.aws.service.lateststate.States;
import com.avanzarit.platform.saas.aws.service.lateststate.UpdateLatestStateService;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Specialized version of the {@link DeltaFilteringPreTransformationFilter} that updates the latest product state to
 * DISCARDED if a product is being filtered out.
 */
public class ProductDeltaFilteringPreTransformationFilter extends DeltaFilteringPreTransformationFilter {

    private UpdateLatestStateService latestStateService;

    public ProductDeltaFilteringPreTransformationFilter(String layer, DeltaDetectionService deltaDetectionService,
                                                        UpdateLatestStateService latestStateService) {
        super(layer, deltaDetectionService);

        this.latestStateService = latestStateService;
    }

    @Override
    public boolean filteredOut(CmwContext cmwContext, UpdateInfo updateInfo, CoreEntity oldEntity,
                               CoreEntity newEntity) {
        boolean result = super.filteredOut(cmwContext, updateInfo, oldEntity, newEntity);

        if (result) {
            latestStateService.updateState(cmwContext, updateInfo, States.DISCARDED);
        }

        return result;
    }
}
