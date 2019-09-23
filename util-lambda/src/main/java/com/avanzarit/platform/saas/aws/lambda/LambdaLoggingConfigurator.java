package com.avanzarit.platform.saas.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Utility class that redirects all logging that is being done to {@link System#out} to the
 * {@link com.amazonaws.services.lambda.runtime.LambdaLogger} instance associated with the current Lambda function
 * invocation.
 */
public class LambdaLoggingConfigurator {
    /**
     * Configures the logging to be redirected to the {@link com.amazonaws.services.lambda.runtime.LambdaLogger}
     * associated with the current Lambda function invocation.
     */
    public static void configureLogging(Context context) {
        try {
            LambdaLoggerOutputStream lambdaLoggerOutputStream = new LambdaLoggerOutputStream(context.getLogger());
            PrintStream printStream = new PrintStream(lambdaLoggerOutputStream, false, "UTF-8");

            System.setOut(printStream);
        } catch (UnsupportedEncodingException e) {
            context.getLogger().log("Failed to route System.out to LambdaLogger");
        }
    }
}
