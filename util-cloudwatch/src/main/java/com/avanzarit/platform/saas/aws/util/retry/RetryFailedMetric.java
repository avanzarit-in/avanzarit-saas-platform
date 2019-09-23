package com.avanzarit.platform.saas.aws.util.retry;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.avanzarit.platform.saas.aws.util.CmwMetric;

import java.util.Date;

/**
 * The RetryMetric is one of the metrics that the system uses to determine how many retries have
 * already failed when processing items. This can be an indication that something is wrong with the
 * data in the system or the system itself. It's a value object that can be passed to a
 * {@link com.avanzarit.platform.saas.aws.util.CmwContext#putMetrics(CmwMetric...)} call for logging to CloudWatch.
 * <p>
 * A RetryMetric is a counter metric and has three dimensions: prefix (to identify the
 * environment), layer and type.
 */
public class RetryFailedMetric extends CmwMetric {
    private static final String RETRY_FAILED = "retryfailed";

    /**
     * Creates a new retry failed metric for the given type and with the given value.
     */
    public RetryFailedMetric(String type, double value) {
        super(value, type);
    }

    @Override
    public MetricDatum createMetric(String prefix, String layer) {
        return new MetricDatum()
                .withDimensions(
                        new Dimension().withName("prefix").withValue(prefix),
                        new Dimension().withName("layer").withValue(layer),
                        new Dimension().withName("type").withValue(type))
                .withTimestamp(new Date())
                .withMetricName(RETRY_FAILED)
                .withUnit(StandardUnit.Count)
                .withValue(value);
    }
}
