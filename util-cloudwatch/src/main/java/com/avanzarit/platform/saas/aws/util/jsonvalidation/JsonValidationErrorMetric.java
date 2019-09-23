package com.avanzarit.platform.saas.aws.util.jsonvalidation;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.avanzarit.platform.saas.aws.util.CmwMetric;

import java.util.Date;

/**
 * The JsonValidationErrorMetric is one of the metrics that the system uses to determine how many
 * times validation of JSON failed in the system. This can be an indication of a data issue. It's
 * a value object that can be passed to a
 * {@link com.avanzarit.platform.saas.aws.util.CmwContext#putMetrics(CmwMetric...)} call for logging to CloudWatch.
 * <p>
 * A JsonValidationErrorMetric is a counter metric and has three dimensions: prefix (to identify
 * the environment), layer and type.
 */
public class JsonValidationErrorMetric extends CmwMetric {

    private static final String METRIC_NAME = "jsonvalidationerror";

    /**
     * Creates a new json validation error metric for the given type and with the given value.
     */
    public JsonValidationErrorMetric(String type, double value) {
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
