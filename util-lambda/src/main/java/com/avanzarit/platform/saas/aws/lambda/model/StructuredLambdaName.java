package com.avanzarit.platform.saas.aws.lambda.model;

/**
 * The StructuredLambdaName represents a AWS Lambda function name broken down into its sub-components.
 * <p>
 * <ul>
 * <li>project: The project the function is part of (e.g.: mw)</li>
 * <li>environment: The environment the function is part of (e.g.: develop)</li>
 * <li>layer: The layer the function is part of (e.g.: canonical)</li>
 * <li>component: The actual function name (e.g.: changedcache)</li>
 * </ul>
 */
public class StructuredLambdaName {
    private String project;
    private String environment;
    private String layer;
    private String component;

    /**
     * Creates a new lambda name using the given project, environment, layer and component.
     */
    public StructuredLambdaName(String project, String environment, String layer, String component) {
        this.project = project;
        this.environment = environment;
        this.layer = layer;
        this.component = component;
    }

    public String getProject() {
        return project;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getLayer() {
        return layer;
    }

    public String getComponent() {
        return component;
    }

    public String getProjectEnvironment() {
        return project + "_" + environment;
    }
}
