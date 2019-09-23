package com.avanzarit.platform.saas.aws.util;

/**
 * Utility class containing methods for formatting exception messages.
 */
public class ExceptionUtil {
    private static final String SEPARATOR = " -> ";

    /**
     * Extracts a formatted error message from the {@link Throwable} object using the
     * {@value SEPARATOR} separator.
     *
     * @param e The throwable from which the formatted error message should be constructed
     * @return The formatted error message
     */
    public static String errorMessage(Throwable e) {
        if (e == null) {
            return null;
        }

        return e.getMessage() + (e.getCause() != null ? SEPARATOR + errorMessage(e.getCause()) : "");
    }
}
