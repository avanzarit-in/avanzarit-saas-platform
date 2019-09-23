package com.avanzarit.platform.saas.aws.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class that allows easy concatenation of Strings.
 */
public class StringConcatenationUtil {
    /**
     * Concatenates the given parts into a new String separated by a separator.
     *
     * @return The concatenated String which can differ based on how the method is being called.
     * <p>
     * concatenate('_', ["part1", "part2", "...", "partN"]) -> "part1_part2_..._partN"<br/>
     * concatenate('_', ["part1", "part2", null, "...", "partN"]) -> "part1_part2_..._partN"<br/>
     * concatenate('_', ["part1", "part2", "", "...", "partN"]) -> "part1_part2_..._partN"<br/>
     * concatenate('_', ["part1", "part2", " ", "...", "partN"]) -> "part1_part2_..._partN"
     * <p>
     * The above four methods will each have the exact same output, due to the blank String being
     * filtered.
     */
    public static String concatenate(char separator, String... parts) {
        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            if (StringUtils.isNotBlank(part)) {
                builder.append(part).append(separator);
            }
        }

        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - 1);
        } else {
            return null;
        }
    }
}
