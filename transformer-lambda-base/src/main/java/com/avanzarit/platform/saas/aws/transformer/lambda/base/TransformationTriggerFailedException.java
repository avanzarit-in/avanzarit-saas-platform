package com.avanzarit.platform.saas.aws.transformer.lambda.base;

/**
 * Exception thrown when the {@link TransformationTrigger} is unable to handle the exception itself.
 */
public class TransformationTriggerFailedException extends RuntimeException {
    public TransformationTriggerFailedException(Throwable cause) {
        super(cause);
    }
}
