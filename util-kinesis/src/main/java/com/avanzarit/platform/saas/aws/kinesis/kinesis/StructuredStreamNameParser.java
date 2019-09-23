package com.avanzarit.platform.saas.aws.kinesis.kinesis;

/**
 * StructuredStreamNameParser is a class that can be used for generating Kinesis names for use within the system.
 */
public class StructuredStreamNameParser {
    private static final String SEPARATOR = "_";
    private static final String SUFFIX = SEPARATOR + "stream";

    /**
     * Generates a Kinesis stream name using the given parts.
     *
     * @return A concatenation of all the parts using an underscore ('_') as the separator and suffixed with '_stream'.
     * When invoked like:
     * <p>
     * {@code createStreamName("part1", "part2", "part3", "...", "partN")}
     * <p>
     * The output will be "part1_part2_part3_..._partN_stream".
     */
    public String createStreamName(String... parts) {
        StringBuilder result = new StringBuilder("");

        if (parts != null) {
            for (String part : parts) {
                if (!result.toString().equals("")) {
                    result.append(SEPARATOR);
                }
                result.append(part);
            }
        }

        if (result.length() > 0) {
            result.append(SUFFIX);
        }

        return result.toString();
    }
}
