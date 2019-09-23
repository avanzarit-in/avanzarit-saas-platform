package com.avanzarit.platform.saas.aws.util.throughput;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.avanzarit.platform.saas.aws.util.CmwMetric;

import java.util.Date;

/**
 * The ThroughputMetric is one of the metrics that the system uses to determine how many and what
 * types of objects are passing through the system. It's a value object that can be passed to a
 * {@link com.avanzarit.platform.saas.aws.util.CmwContext#putMetrics(CmwMetric...)} call for logging to CloudWatch.
 * <p>
 * A ThroughputMetric is a counter metric and has three dimensions: prefix (to identify the
 * environment), layer and type.
 */
public class ThroughputMetric extends CmwMetric {

    private static final String METRIC_NAME = "throughput";

    /**
     * Creates a new throughput metric for the given type and with the given value.
     */
    public ThroughputMetric(String type, double value) {
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
                .withMetricName(METRIC_NAME)
                .withUnit(StandardUnit.Count)
                .withValue(value);
    }
}
