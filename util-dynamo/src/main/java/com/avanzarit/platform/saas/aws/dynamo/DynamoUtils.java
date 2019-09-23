package com.avanzarit.platform.saas.aws.dynamo;

/**
 * Utiltiy class that can be used for handling DynamoDb keys.
 */
public class DynamoUtils {
    /**
     * Generates a DynamoDb key by concatenating the given parts using the pound ('#") character as
     * a separator.
     *
     * @return A concatenation of all parts. When invoked like:
     * <p>
     * {@code toKey("part1", "part2", "...", "partN")}
     * <p>
     * The output will be "part1#part2#...#partN".
     */
    public static String toKey(String... parts) {
        String result = "";

        for (String part : parts) {
            if (!result.equals("")) {
                result += "#" + part;
            } else {
                result = part;
            }
        }

        return result;
    }

    /**
     * Splits a DynamoDb key into its sub-components. The pound ('#') character is being used to perform the splitting.
     *
     * @return A String array with the separate components that have been derived from the key.
     * <p>
     * {@code fromKey("part1#part2#...#partN")}
     * <p>
     * The output will be ["part1", "part2", "...", "partN"].
     */
    public static String[] fromKey(String hashKey) {
        return hashKey.split("#");
    }
}
