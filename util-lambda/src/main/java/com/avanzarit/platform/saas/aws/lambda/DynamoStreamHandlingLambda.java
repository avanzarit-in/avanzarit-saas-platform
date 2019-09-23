package com.avanzarit.platform.saas.aws.lambda;

import com.amazonaws.regions.Region;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.avanzarit.platform.saas.aws.lambda.metrics.BatchSizeMetric;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DynamoStreamHandlingLambda is a base class that allows subclasses to just handle the DynamoDb stream events that are
 * coming in without having to bother with all the boilerplate of the actual Lambda function invocation. The class
 * provides several lifecycle hooks that allow subclasses to implement custom logic without having to interact with the
 * Lambda handling code itself.
 */
public abstract class DynamoStreamHandlingLambda implements RequestHandler<DynamodbEvent, String> {
    private static final Logger LOGGER = LogManager.getLogger(DynamoStreamHandlingLambda.class);

    private DynamoEventHandler dynamoEventHandler;
    private CmwContext cmwContext;

    @Override
    public String handleRequest(DynamodbEvent dynamoDbEvent, Context context) {
        LambdaLoggingConfigurator.configureLogging(context);

        if (dynamoEventHandler == null) {
            AmazonCloudWatch amazonCloudWatch = AmazonCloudWatchClient.builder()
                    .withRegion(LambdaRegion.getCurrentRegion())
                    .build();

            cmwContext = new LambdaCmwContextFactory().createContext(context, amazonCloudWatch);
            dynamoEventHandler = new DynamoEventHandler();

            registerTriggers(Region.getRegion(LambdaRegion.getCurrentRegion()), cmwContext);
        }

        cmwContext.setAwsRequestId(context.getAwsRequestId());

        try {
            LOGGER.info("Lambda running in context: " + cmwContext);

            LOGGER.info("Starting event handling");

            onInvoke(cmwContext);

            LOGGER.info("Received " + dynamoDbEvent.getRecords().size() + " events");
            cmwContext.putMetrics(new BatchSizeMetric(context.getFunctionName(), dynamoDbEvent.getRecords().size()));

            dynamoEventHandler.handleEvent(cmwContext, dynamoDbEvent);

            onFinish(cmwContext);
            cmwContext.flushMetrics();
        } catch (Exception e) {
            cmwContext.logError(e);
            throw new RuntimeException(e);
        }

        return "Successfully processed " + dynamoDbEvent.getRecords().size() + " records.";
    }

    /**
     * Subclasses can register their entity triggers as part of this method.
     */
    public abstract void registerTriggers(Region region, CmwContext cmwContext);

    /**
     * The onInvoke method is called when the Lambda function is invoked. It is an extension point that can be used in
     * subclasses to hook into the 'invocation' lifecycle event.
     */
    public void onInvoke(CmwContext cmwContext) {
    }

    /**
     * The onFinish method is called when the Lambda function is done processing all events. It is an extension point
     * that can be used in subclasses to hook into the 'finish' lifecycle event.
     */
    public void onFinish(CmwContext cmwContext) {
    }

    /**
     * Adds an entity trigger for a table to the {@link DynamoRecordProcessor}.
     */
    public void addTrigger(String tableName, EntityTrigger<?> trigger) {
        dynamoEventHandler.addTrigger(tableName, trigger);
    }
}