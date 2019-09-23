package com.avanzarit.platform.saas.aws.util.skippedpropagation;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.avanzarit.platform.saas.aws.util.CmwMetric;

import java.util.Date;

/**
 * The SkippedPropagationMetric is one of the metrics that the system uses to determine how many
 * propagation events were skipped by the system, due to another propagation event for the same
 * object already being in the pipeline. It's a value object that can be passed to a
 * {@link com.avanzarit.platform.saas.aws.util.CmwContext#putMetrics(CmwMetric...)} call for logging to CloudWatch.
 * <p>
 * A SkippedPropagationMetric is a counter metric and has only a single dimension: prefix (used to
 * determine the environment in which the event occurred.
 */
public class SkippedPropagationMetric extends CmwMetric {

    private static final String METRIC_NAME = "skippedpropagation";

    /**
     * Creates a new skipped propagation metric instance. The value is always 1.
     */
    public SkippedPropagationMetric() {
        super(1, null);
    }

    @Override
    public MetricDatum createMetric(String prefix, String layer) {
        return new MetricDatum()
                .withDimensions(
                        new Dimension().withName("prefix").withValue(prefix))
                .withTimestamp(new Date())
                .withMetricName(METRIC_NAME)
                .withUnit(StandardUnit.Count)
                .withValue(value);
    }
}
