package com.avanzarit.platform.saas.aws.lambda;

/**
 * The RecordMappingFailedException is thrown when the {@link KinesisEventRecordMapper} fails to map a Kinesis event to
 * its Java object representation.
 */
public class RecordMappingFailedException extends RuntimeException {
    /**
     * Creates a new exception with the given message and cause.
     */
    public RecordMappingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
