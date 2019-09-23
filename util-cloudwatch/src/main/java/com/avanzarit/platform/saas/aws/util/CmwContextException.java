package com.avanzarit.platform.saas.aws.util;

/**
 * CmwContextException is an exception class that is used when something unexpected occurs while processing in the
 * {@link CmwContext}.
 */
public class CmwContextException extends RuntimeException {
    /**
     * Creates a new exception instance with the given message.
     */
    public CmwContextException(String message) {
        super(message);
    }
}
