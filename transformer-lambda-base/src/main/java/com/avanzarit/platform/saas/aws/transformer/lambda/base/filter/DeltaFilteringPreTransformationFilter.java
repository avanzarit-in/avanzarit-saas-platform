package com.avanzarit.platform.saas.aws.transformer.lambda.base.filter;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.service.deltafiltering.DeltaDetectionService;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerInputFilter;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import com.avanzarit.platform.saas.aws.util.delta.DeltaMetric;

import static com.avanzarit.platform.saas.aws.util.delta.DeltaValue.NEW;
import static com.avanzarit.platform.saas.aws.util.delta.DeltaValue.SAME;
import static com.avanzarit.platform.saas.aws.util.delta.DeltaValue.UPDATED;

/**
 * Checks whether the new entity has actually changed when compared with the previous version. If there are no changes,
 * then the update is filtered out (see {@link DeltaDetectionService}.
 */
public class DeltaFilteringPreTransformationFilter implements TransformerInputFilter<CoreEntity> {

    private String layer;
    private DeltaDetectionService deltaDetectionService;

    public DeltaFilteringPreTransformationFilter(String layer, DeltaDetectionService deltaDetectionService) {
        this.layer = layer;
        this.deltaDetectionService = deltaDetectionService;
    }

    @Override
    public boolean filteredOut(CmwContext cmwContext, UpdateInfo updateInfo, CoreEntity oldEntity,
                               CoreEntity newEntity) {
        if (oldEntity == null) {
            cmwContext.putMetrics(new DeltaMetric(newEntity.getBareTableName(), 1.0, layer, NEW));
            return false;
        }

        boolean shouldFilterOut = !deltaDetectionService.detectsDelta(
                cmwContext, oldEntity, newEntity
        );

        if (shouldFilterOut) {
            cmwContext.logDiscarded(updateInfo, newEntity.getBareTableName() + " was not changed");
            cmwContext.putMetrics(new DeltaMetric(newEntity.getBareTableName(), 1.0, layer, SAME));
        } else {
            cmwContext.putMetrics(new DeltaMetric(newEntity.getBareTableName(), 1.0, layer, UPDATED));
        }

        return shouldFilterOut;
    }
}
