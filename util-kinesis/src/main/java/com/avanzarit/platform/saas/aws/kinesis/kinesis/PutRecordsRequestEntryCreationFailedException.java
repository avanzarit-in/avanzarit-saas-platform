package com.avanzarit.platform.saas.aws.kinesis.kinesis;

/**
 * The PutRecordsRequestEntryCreationFailedException is thrown when the {@link PutRecordsRequestEntryFactory} fails to
 * create a {@link com.amazonaws.services.kinesis.model.PutRecordsRequestEntry}.
 */
public class PutRecordsRequestEntryCreationFailedException extends RuntimeException {
    public PutRecordsRequestEntryCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
