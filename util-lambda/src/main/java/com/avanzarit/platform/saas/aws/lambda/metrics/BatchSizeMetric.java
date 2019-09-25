package com.avanzarit.platform.saas.aws.lambda.metrics;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.avanzarit.platform.saas.aws.util.CmwMetric;

import java.util.Date;

/**
 * The BatchSizeMetric is one of the metrics that the system uses to determine how many events an AWS Lambda function is
 * receiving in a single batch. It's a value object that can be passed to a
 * {@link com.avanzarit.platform.saas.aws.util.CmwContext#putMetrics(CmwMetric...)} call for logging to CloudWatch.
 * <p>
 * A BatchSizeMetric is a counter metric and has a single dimension: function_name (to identify the lambda function to
 * which the metric corresponds).
 */
public class BatchSizeMetric extends CmwMetric {

    private static final String METRIC_NAME = "batchsize";

    /**
     * Creates a new batch size metric with the given type (or function name) and value.
     */
    public BatchSizeMetric(String type, double value) {
        super(value, type);
    }

    @Override
    public MetricDatum createMetric(String prefix, String layer) {
        return new MetricDatum()
                .withDimensions(
                        new Dimension().withName("function_name").withValue(type))
                .withTimestamp(new Date())
                .withMetricName(METRIC_NAME)
                .withUnit(StandardUnit.Count)
                .withValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
