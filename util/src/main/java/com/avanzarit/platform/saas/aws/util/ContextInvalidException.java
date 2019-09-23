package com.avanzarit.platform.saas.aws.util;

/**
 * Exception that is thrown when an invalid context is encountered.
 *
 * @see ContextUtils
 */
public class ContextInvalidException extends RuntimeException {
    /**
     * Creates a new exception with the given message.
     */
    public ContextInvalidException(String message) {
        super(message);
    }
}
