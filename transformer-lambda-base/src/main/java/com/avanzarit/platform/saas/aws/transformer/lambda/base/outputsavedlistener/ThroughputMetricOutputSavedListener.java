package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputsavedlistener;

import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerOutputSavedListener;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import com.avanzarit.platform.saas.aws.util.throughput.ThroughputMetric;

/**
 * Writes a metric value to CloudWatch whenever an output entity was successfully saved.
 */
public class ThroughputMetricOutputSavedListener<I, O> implements TransformerOutputSavedListener<I, O> {

    @Override
    public void onOutputSaved(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity,
                              O outputEntity) {
        cmwContext.putMetrics(new ThroughputMetric(outputEntity.toString(), 1.0));
    }
}
