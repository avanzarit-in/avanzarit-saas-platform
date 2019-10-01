package com.avanzarit.platform.saas.aws.lambda.requesthandler;

import com.amazonaws.regions.Region;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.LambdaCmwContextFactory;
import com.avanzarit.platform.saas.aws.lambda.LambdaLoggingConfigurator;
import com.avanzarit.platform.saas.aws.lambda.LambdaRegion;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.KinesisEventHandler;
import com.avanzarit.platform.saas.aws.lambda.metrics.BatchSizeMetric;
import com.avanzarit.platform.saas.aws.lambda.processors.impl.DynamoDbStreamRecordProcessor;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * KinesisStreamHandlingLambda is a base class that allows subclasses to just handle the Kinesis events that are coming
 * in without having to bother with all the boilerplate of the actual Lambda function invocation. The class provides
 * several lifecycle hooks that allow subclasses to implement custom logic without having to interact with the Lambda
 * handling code itself.
 */
public abstract class KinesisStreamEventHandlingLambda<T extends KinesisEventHandler> implements RequestHandler<KinesisEvent, String> {

    private static final Logger LOGGER = LogManager.getLogger(KinesisStreamEventHandlingLambda.class);

    private T kinesisEventHandler;
    private CmwContext cmwContext;

    @Override
    public String handleRequest(KinesisEvent event, Context context) {
        LambdaLoggingConfigurator.configureLogging(context);

        if (kinesisEventHandler == null) {
            AmazonCloudWatch amazonCloudWatch = AmazonCloudWatchClient.builder()
                    .withRegion(LambdaRegion.getCurrentRegion())
                    .build();

            cmwContext = new LambdaCmwContextFactory().createContext(context, amazonCloudWatch);
            kinesisEventHandler = getKinesisEventHandler();

            registerTriggers(Region.getRegion(LambdaRegion.getCurrentRegion()), cmwContext);
        }

        cmwContext.setAwsRequestId(context.getAwsRequestId());

        try {
            LOGGER.info("Lambda running in context: " + cmwContext);

            LOGGER.info("Starting event handling");

            onInvoke(cmwContext);

            if (event != null) {
                cmwContext.putMetrics(new BatchSizeMetric(context.getFunctionName(), event.getRecords().size()));

                kinesisEventHandler.handleEvent(cmwContext, event);
            }

            onFinish(cmwContext);
            cmwContext.flushMetrics();
        } catch (Exception e) {
            cmwContext.logError(e);
            throw new RuntimeException(e);
        }

        return "ok";
    }

    /**
     * Subclasses can register their entity triggers as part of this method.
     */
    public abstract void registerTriggers(Region region, CmwContext cmwContext);

    protected abstract T getKinesisEventHandler();

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
     * Adds an entity trigger for a table to the {@link DynamoDbStreamRecordProcessor}.
     */
    public void addTrigger(String tableName, EntityTrigger<?> trigger) {
        kinesisEventHandler.addTrigger(tableName, trigger);
    }
}