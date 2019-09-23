package com.avanzarit.platform.saas.aws.util;

import com.amazonaws.services.cloudwatch.model.MetricDatum;

/**
 * CmwMetric is a base class that can be used for describing a metric that needs to be sent to CloudWatch. It has both a
 * value (describing the weight of the metric) and a type (describing the type of entity for which the metric needs to
 * be logged).
 * <p>
 * The {@link CmwContext} class depends heavily on CmwMetric for its metric logging capabilities.
 * <p>
 * Subclasses should implement the {@link #createMetric(String, String)} factory method with an implementation returning
 * the CloudWatch equivalent of the metric.
 */
public abstract class CmwMetric {
    protected double value;
    protected String type;

    /**
     * Creates a new CmwMetric with the given value and type.
     */
    public CmwMetric(double value, String type) {
        this.value = value;
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Creates a AWS SDK compatible representation of this metric.
     * <p>
     * Subclasses should implement this method and return a valid AWS CloudWatch {@link MetricDatum}. This method is
     * used by {@link CmwContext} for logging the metric to CloudWatch.
     */
    public abstract MetricDatum createMetric(String prefix, String layer);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CmwMetric cmwMetric = (CmwMetric) o;

        if (Double.compare(cmwMetric.value, value) != 0) {
            return false;
        }
        return !(type != null ? !type.equals(cmwMetric.type) : cmwMetric.type != null);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CmwMetric{"
                + "value=" + value
                + ", type='" + type + '\''
                + '}';
    }
}
