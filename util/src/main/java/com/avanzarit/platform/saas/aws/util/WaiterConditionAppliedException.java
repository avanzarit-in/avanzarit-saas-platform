package com.avanzarit.platform.saas.aws.util;

/**
 * An exception that occurs when a {@link Waiter} condition unexpectantly applies.
 */
public class WaiterConditionAppliedException extends RuntimeException {
    /**
     * Creates an exception with the given message.
     */
    public WaiterConditionAppliedException(String message) {
        super(message);
    }
}
