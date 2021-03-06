package com.avanzarit.platform.saas.aws.lambda.eventhandler;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.util.CmwContext;

/**
 * Responsible for handling events generated by S3.
 */
public interface S3EventHandler<T> {

    /**
     * Handles the given event.
     *
     * @param event The Kinesis event that needs to be handled.
     */
    void handleEvent(CmwContext cmwContext, S3Event event);

    /**
     * Adds an entity trigger.
     */
    void addTrigger(String bucketName, EntityTrigger<T> trigger);
}
