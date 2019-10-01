package com.avanzarit.platform.saas.aws.lambda.eventhandler;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.processors.impl.DynamoDbStreamRecordProcessor;
import com.avanzarit.platform.saas.aws.util.CmwContext;

/**
 * Responsible for handling events on tables.
 */
public interface KinesisEventHandler {

    /**
     * Handles the given event.
     *
     * @param event The Kinesis event that needs to be handled.
     */
    void handleEvent(CmwContext cmwContext, KinesisEvent event);

    /**
     * Adds an entity trigger for a table to the {@link DynamoDbStreamRecordProcessor}.
     */
    void addTrigger(String tableName, EntityTrigger<?> trigger);
}
