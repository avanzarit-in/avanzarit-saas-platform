package com.avanzarit.platform.saas.aws.kinesis.kinesis;

/**
 * The RegionNotSpecifiedException is thrown when no AWS region has been specified.
 */
public class RegionNotSpecifiedException extends RuntimeException {
    /**
     * Creates a new exception with the given message.
     */
    public RegionNotSpecifiedException(String message) {
        super(message);
    }
}
