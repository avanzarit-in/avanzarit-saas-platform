package com.avanzarit.platform.saas.aws.util;

/**
 * An exception that occurs when the {@link Waiter} times out waiting for a condition to apply.
 */
public class WaiterTimeOutException extends RuntimeException {

    /**
     * Creates a new exception with the given message.
     */
    public WaiterTimeOutException(String message) {
        super(message);
    }
}
