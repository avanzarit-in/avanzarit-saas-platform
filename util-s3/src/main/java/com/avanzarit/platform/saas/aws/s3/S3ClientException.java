package com.avanzarit.platform.saas.aws.s3;

/**
 * An S3ClientException is thrown when something goes wrong while executing logic in the {@link S3Client}.
 */
public class S3ClientException extends RuntimeException {
    /**
     * Creates a new exception with the given message and cause.
     */
    public S3ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
