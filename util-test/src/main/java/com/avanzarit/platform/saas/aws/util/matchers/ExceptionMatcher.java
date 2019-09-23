package com.avanzarit.platform.saas.aws.util.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Objects;

import static org.mockito.Matchers.argThat;

/**
 * Hamcrest {@link org.hamcrest.Matcher} that verifies whether the object being checked is of the expected exception
 * type and has the expected message.
 */
public class ExceptionMatcher extends BaseMatcher<Exception> {
    private Class<? extends Exception> type;
    private String message;

    public ExceptionMatcher(Class<? extends Exception> type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public boolean matches(Object o) {
        if (type.isInstance(o)) {
            Exception exception = (Exception) o;

            if (Objects.equals(exception.getMessage(), message)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(type.getTypeName() + ": " + message);
    }

    /**
     * Matches the given exception type and message.
     *
     * @param type    The expected exception type.
     * @param message The expected message that the exception has.
     * @return An object that wraps a Mockito matcher.
     */
    public static Exception exception(Class<? extends Exception> type, String message) {
        return argThat(new ExceptionMatcher(type, message));
    }
}
