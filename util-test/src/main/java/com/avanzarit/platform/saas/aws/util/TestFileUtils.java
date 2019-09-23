package com.avanzarit.platform.saas.aws.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class that offers functionality to easily retrieve a file's contents from a file stored in somewhere in the
 * classpath.
 */
public class TestFileUtils {
    /**
     * Gets file contents for a file stored somewhere in the classpath.
     */
    public static String getFile(String fileName) throws IOException {
        StringBuilder result = new StringBuilder("");

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

        if (is != null) {
            try (
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                String line = br.readLine();
                while (line != null) {
                    result.append(line);
                    line = br.readLine();
                }
            }
        } else {
            throw new RuntimeException("file " + fileName + " not found!");
        }

        return result.toString();
    }

    /**
     * Gets file contents as stream for a file stored somewhere in the classpath.
     */
    public static InputStream getFileAsStream(String fileName) throws IOException {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }
}
