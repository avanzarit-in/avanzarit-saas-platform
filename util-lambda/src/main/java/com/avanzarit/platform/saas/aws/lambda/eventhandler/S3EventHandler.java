package com.avanzarit.platform.saas.aws.lambda.eventhandler;

import com.amazonaws.services.s3.model.S3Event;
import com.avanzarit.platform.saas.aws.lambda.processors.DynamoRecordProcessor;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.util.CmwContext;

/**
 * Responsible for handling events on tables.
 */
public interface S3EventHandler {

    /**
     * Handles the given event.
     *
     * @param event The Kinesis event that needs to be handled.
     */
    void handleEvent(CmwContext cmwContext, S3Event event);

    /**
     * Adds an entity trigger for a table to the {@link DynamoRecordProcessor}.
     */
    void addTrigger(String tableName, EntityTrigger<?> trigger);
}
