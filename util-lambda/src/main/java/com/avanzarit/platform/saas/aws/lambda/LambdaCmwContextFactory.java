package com.avanzarit.platform.saas.aws.lambda;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.lambda.runtime.Context;
import com.avanzarit.platform.saas.aws.lambda.model.StructuredLambdaName;
import com.avanzarit.platform.saas.aws.util.CmwContext;

/**
 * LambdaCmwContextFactory is a factory class that can be used to create {@link CmwContext} instances when running
 * inside a Lambda function.
 */
public class LambdaCmwContextFactory {
    private static final String SPLIT_CHARACTER = "_";

    /**
     * Creates a CmwContext instance based on the AWS Lambda SDK {@link Context} class and including a CloudWatch
     * client.
     */
    public CmwContext createContext(Context context, AmazonCloudWatch amazonCloudWatch) {
        StructuredLambdaName lambdaName = parseFunctionName(context.getFunctionName());

        return new CmwContext(
                amazonCloudWatch,
                lambdaName.getProjectEnvironment(),
                lambdaName.getLayer(),
                lambdaName.getComponent(),
                context.getAwsRequestId()
        );
    }

    /**
     * Creates a CmwContext instance based on the AWS Lambda SDK {@link Context} class.
     */
    public CmwContext createContext(Context context) {
        StructuredLambdaName lambdaName = parseFunctionName(context.getFunctionName());

        return new CmwContext(
                lambdaName.getProjectEnvironment(),
                lambdaName.getLayer(),
                lambdaName.getComponent(),
                context.getAwsRequestId()
        );
    }

    private StructuredLambdaName parseFunctionName(String functionName) {
        StructuredLambdaName result = null;
        if (functionName != null) {
            String[] split = functionName.split(SPLIT_CHARACTER);
            if (split.length == 4) {
                result = new StructuredLambdaName(split[0], split[1], split[2], split[3]);
            }
            //serializers
            if (split.length == 5) {
                result = new StructuredLambdaName(split[0], split[1], split[2] + SPLIT_CHARACTER + split[3], split[4]);
            }
        }
        return result;
    }
}
