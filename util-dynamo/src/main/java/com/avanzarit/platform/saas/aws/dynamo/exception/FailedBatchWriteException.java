package com.avanzarit.platform.saas.aws.dynamo.exception;

/**
 * FailedBatchWriteException is an exception class that is used when the
 * {@link com.avanzarit.platform.saas.aws.dynamo.DynamoBatchWriter} is unable to write all of the items it was intended to.
 */
public class FailedBatchWriteException extends RuntimeException {
    /**
     * Creates an exception using the given message.
     */
    public FailedBatchWriteException(String message) {
        super(message);
    }
}
