package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputsavedlistener;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerOutputSavedListener;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import com.avanzarit.platform.saas.aws.util.throughput.ThroughputMetric;

/**
 * Writes a metric value to CloudWatch whenever an output entity was successfully saved.
 */
public class ThroughputMetricOutputSavedListener implements TransformerOutputSavedListener<CoreEntity, CoreEntity> {

    @Override
    public void onOutputSaved(CmwContext cmwContext, UpdateInfo updateInfo, CoreEntity inputEntity,
                              CoreEntity outputEntity) {
        cmwContext.putMetrics(new ThroughputMetric(outputEntity.getBareTableName(), 1.0));
    }
}
