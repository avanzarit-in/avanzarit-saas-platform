package com.avanzarit.platform.saas.aws.lambda.requesthandler.impl;

import com.avanzarit.platform.saas.aws.lambda.eventhandler.impl.DynamoDbKinesisStreamEventHandler;
import com.avanzarit.platform.saas.aws.lambda.requesthandler.KinesisStreamEventHandlingLambda;

/**
 * KinesisStreamHandlingLambda is a base class that allows subclasses to just handle the Kinesis events that are coming
 * in without having to bother with all the boilerplate of the actual Lambda function invocation. The class provides
 * several lifecycle hooks that allow subclasses to implement custom logic without having to interact with the Lambda
 * handling code itself.
 */
public abstract class DynamoDbStreamEventHandlingLambda extends KinesisStreamEventHandlingLambda<DynamoDbKinesisStreamEventHandler> {
    protected DynamoDbKinesisStreamEventHandler getKinesisEventHandler() {
        return new DynamoDbKinesisStreamEventHandler();
    }
}