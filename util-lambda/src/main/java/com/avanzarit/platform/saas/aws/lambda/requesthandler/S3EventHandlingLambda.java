package com.avanzarit.platform.saas.aws.lambda.requesthandler;

import com.amazonaws.regions.Region;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.model.S3Event;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.LambdaCmwContextFactory;
import com.avanzarit.platform.saas.aws.lambda.LambdaLoggingConfigurator;
import com.avanzarit.platform.saas.aws.lambda.LambdaRegion;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.S3EventHandler;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * KinesisStreamHandlingLambda is a base class that allows subclasses to just handle the Kinesis events that are coming
 * in without having to bother with all the boilerplate of the actual Lambda function invocation. The class provides
 * several lifecycle hooks that allow subclasses to implement custom logic without having to interact with the Lambda
 * handling code itself.
 */
public abstract class S3EventHandlingLambda<T extends S3EventHandler> implements RequestHandler<S3Event, String> {

    private static final Logger LOGGER = LogManager.getLogger(S3EventHandlingLambda.class);

    private S3EventHandler s3EventHandler;
    private CmwContext cmwContext;

    @Override
    public String handleRequest(S3Event event, Context context) {
        LambdaLoggingConfigurator.configureLogging(context);

        if (s3EventHandler == null) {
            AmazonCloudWatch amazonCloudWatch = AmazonCloudWatchClient.builder()
                    .withRegion(LambdaRegion.getCurrentRegion())
                    .build();

            cmwContext = new LambdaCmwContextFactory().createContext(context, amazonCloudWatch);
            s3EventHandler = getS3EventHandler();

            registerTriggers(Region.getRegion(LambdaRegion.getCurrentRegion()), cmwContext);
        }

        cmwContext.setAwsRequestId(context.getAwsRequestId());

        try {
            LOGGER.info("Lambda running in context: " + cmwContext);

            LOGGER.info("Starting event handling");

            onInvoke(cmwContext);

            /**

             if (event != null) {
             cmwContext.putMetrics(new BatchSizeMetric(context.getFunctionName(), event.getRecords().size()));

             kinesisEventHandler.handleEvent(cmwContext, event);
             }
             */
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

    protected abstract T getS3EventHandler();

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
     * Adds an entity trigger.
     */
    public <T> void addTrigger(EntityTrigger<T> trigger) {
        s3EventHandler.addTrigger(trigger);
    }
}