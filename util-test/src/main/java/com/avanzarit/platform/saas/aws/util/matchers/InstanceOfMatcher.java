package com.avanzarit.platform.saas.aws.util.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import static org.mockito.Matchers.argThat;

/**
 * Hamcrest {@link org.hamcrest.Matcher} that verifies whether the object being checked is an instance of the given
 * class or one of the sub-types of the given class.
 *
 * @param <T> The class of which an instance is expected.
 */
public class InstanceOfMatcher<T> extends BaseMatcher<T> {
    private Class<T> cls;

    private InstanceOfMatcher(Class<T> cls) {
        this.cls = cls;
    }

    @Override
    public boolean matches(Object o) {
        return o != null && cls.isAssignableFrom(o.getClass());

    }

    @Override
    public void describeTo(Description description) {
        description.appendText("An instance of " + cls.getName());
    }

    /**
     * Matches an instance of cls or any of cls' sub-types.
     *
     * @param cls The class that is being expected.
     * @return An object that wraps a Mockito matcher.
     */
    public static <T> T instanceOf(Class<T> cls) {
        return argThat(new InstanceOfMatcher<>(cls));
    }
}
