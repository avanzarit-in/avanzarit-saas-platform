package com.avanzarit.platform.saas.aws.dynamo;

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
public class StructuredTableName {

    private String project;
    private String environment;
    private String layer;
    private String table;

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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "StructuredTableName: project=" + project
                + ", env=" + environment
                + ", layer=" + layer
                + ", table=" + table;
    }
}
