package com.avanzarit.platform.saas.aws.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class containing methods that can help when dealing with contexts. Contexts are simple
 * two-part Strings consisting of a language and a country identifier separated by an underscore
 * ('_') character.
 */
public class ContextUtils {
    /**
     * Gets the language from a context String.
     *
     * @return A String value representing the language component.
     * <p>
     * getLanguageFromContext("yy_YY") -> "yy"<br/>
     * getLanguageFromContext("language_country") -> "language"
     * @throws ContextInvalidException If the context is not valid according to the rules outlined
     *                                 for the {@link #isValid(String)} method.
     */
    public static String getLanguageFromContext(String context) {
        if (isValid(context)) {
            String[] splits = context.split("_");
            return splits[0];
        } else {
            throw new ContextInvalidException("Invalid context: " + context);
        }
    }

    /**
     * Gets the country from a context String.
     *
     * @return A String value representing the country component.
     * <p>
     * getCountryFromContext("yy_YY") -> "YY"<br/>
     * getCountryFromContext("language_country") -> "country"
     * @throws ContextInvalidException If the context is not valid according to the rules outlined
     *                                 for the {@link #isValid(String)} method.
     */
    public static String getCountryFromContext(String context) {
        if (isValid(context)) {
            String[] splits = context.split("_");
            return splits[1];
        } else {
            throw new ContextInvalidException("Invalid context: " + context);
        }
    }

    /**
     * Verifies that the given context is valid.
     *
     * @return true if the context is not blank and consists of two parts split by an underscore
     * ('_') character, otherwise false.
     * <p>
     * isValid("yy_YY") -> true<br/>
     * isValid("language_country") -> true<br/>
     * isValid(null) -> false<br/>
     * isValid("") -> false<br/>
     * isValid(" ") -> false<br/>
     * isValid("context") -> false
     */
    public static boolean isValid(String context) {
        if (StringUtils.isNotBlank(context)) {
            String[] splits = context.split("_");
            return splits.length == 2;
        } else {
            return false;
        }
    }

    /**
     * Gets a context String in the format language_country (e.g.: yy_YY) for the given language
     * and country.
     *
     * @throws ContextInvalidException If either language or country are blank.
     */
    public static String getContext(String language, String country) {
        if (StringUtils.isNoneBlank(language, country)) {
            return language + "_" + country;
        } else {
            throw new ContextInvalidException(
                    "Invalid context input: language = " + language
                            + ", country = " + country
            );
        }
    }
}
