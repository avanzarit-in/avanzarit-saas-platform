package com.avanzarit.platform.saas.aws.s3;

/**
 * The StructuredTableName represents a table name broken down into its sub-components.
 * <p>
 * <ul>
 * <li>project: The project the table is part of (e.g.: mw)</li>
 * <li>environment: The environment the table is part of (e.g.: develop)</li>
 * <li>layer: The layer the table is part of (e.g.: canonical)</li>
 * <li>table: The actual table name (e.g.: product)</li>
 * </ul>
 */
public class StructuredS3BucketName {

    private String project;
    private String environment;
    private String layer;
    private String bucket;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Override
    public String toString() {
        return "StructuredTableName: project=" + project
                + ", env=" + environment
                + ", layer=" + layer
                + ", bucket=" + bucket;
    }
}
