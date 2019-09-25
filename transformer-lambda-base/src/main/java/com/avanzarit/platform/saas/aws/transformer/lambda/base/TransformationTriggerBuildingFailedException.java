package com.avanzarit.platform.saas.aws.transformer.lambda.base;

/**
 * An exception thrown when it is impossible to configure an {@link TransformationTrigger} due to missing configuration.
 */
public class TransformationTriggerBuildingFailedException extends RuntimeException {
    public TransformationTriggerBuildingFailedException(String message) {
        super(message);
    }
}
