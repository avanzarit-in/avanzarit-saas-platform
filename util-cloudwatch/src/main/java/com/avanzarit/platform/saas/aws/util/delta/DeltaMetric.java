package com.avanzarit.platform.saas.aws.util.delta;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.avanzarit.platform.saas.aws.util.CmwMetric;

import java.util.Date;

/**
 * The DeltaMetric is one of the metrics that the system uses to determine how many items passed
 * through the delta filtering functionality in the system and why they passed through. It's a
 * value object, containing both a double value (indicating how many items passed or did not pass
 * delta filtering) and a {@link DeltaValue} (indicating why an item did or did not pass delta
 * filtering), that can be passed to a
 * {@link com.avanzarit.platform.saas.aws.util.CmwContext#putMetrics(CmwMetric...)} call for logging to CloudWatch.
 * <p>
 * A DeltaMetric is a counter metric and has three dimensions: prefix (to identify the
 * environment), layer and type.
 */
public class DeltaMetric extends CmwMetric {
    private String layer;
    private DeltaValue deltaValue;

    /**
     * Creates a new delta metric for the given type, value and {@link DeltaValue}.
     */
    public DeltaMetric(String type, double value, DeltaValue deltaValue) {
        super(value, type);
        this.deltaValue = deltaValue;
    }

    /**
     * Creates a new delta metric for the given type, value, layer and {@link DeltaValue}.
     */
    public DeltaMetric(String type, double value, String layer, DeltaValue deltaValue) {
        super(value, type);
        this.deltaValue = deltaValue;
        this.layer = layer;
    }

    @Override
    public MetricDatum createMetric(String prefix, String layer) {
        if (this.layer == null) {
            this.layer = layer;
        }

        return new MetricDatum()
                .withDimensions(
                        new Dimension().withName("prefix").withValue(prefix),
                        new Dimension().withName("layer").withValue(this.layer),
                        new Dimension().withName("type").withValue(type))
                .withTimestamp(new Date())
                .withMetricName(deltaValue.toString())
                .withUnit(StandardUnit.Count)
                .withValue(value);
    }

    public DeltaValue getDeltaValue() {
        return deltaValue;
    }

    public String getLayer() {
        return layer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        DeltaMetric that = (DeltaMetric) o;

        return deltaValue == that.deltaValue;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (deltaValue != null ? deltaValue.hashCode() : 0);
        return result;
    }
}
