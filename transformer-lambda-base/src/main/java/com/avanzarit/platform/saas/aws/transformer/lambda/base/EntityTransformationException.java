package com.avanzarit.platform.saas.aws.transformer.lambda.base;

/**
 * An EntityTransformationException is thrown when an issue is found while converting from one entity representation
 * into another. In most cases this exception will be treated not as a critical system error, but as a validation
 * failure.
 */
public class EntityTransformationException extends RuntimeException {
    public EntityTransformationException(String message) {
        super(message);
    }

    public EntityTransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}
