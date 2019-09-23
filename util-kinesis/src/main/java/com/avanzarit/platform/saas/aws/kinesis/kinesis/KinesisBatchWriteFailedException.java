package com.avanzarit.platform.saas.aws.kinesis.kinesis;

/**
 * The KinesisBatchWriteFailedException is thrown when the {@link KinesisBatchWriter} fails to write its records to AWS
 * Kinesis.
 */
public class KinesisBatchWriteFailedException extends RuntimeException {
    public KinesisBatchWriteFailedException(String message) {
        super(message);
    }

    public KinesisBatchWriteFailedException(Throwable cause) {
        super(cause);
    }
}
