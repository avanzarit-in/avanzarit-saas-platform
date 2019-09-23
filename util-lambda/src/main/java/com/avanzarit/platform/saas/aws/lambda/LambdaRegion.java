package com.avanzarit.platform.saas.aws.lambda;

import com.amazonaws.regions.Regions;

/**
 * LambdaRegion is an utility class that can be used to get the current AWS region when running inside an AWS Lambda
 * function.
 */
public class LambdaRegion {
    /**
     * Gets the current region for a Lambda function.
     */
    public static Regions getCurrentRegion() {
        return Regions.fromName(System.getenv("AWS_REGION"));
    }
}
