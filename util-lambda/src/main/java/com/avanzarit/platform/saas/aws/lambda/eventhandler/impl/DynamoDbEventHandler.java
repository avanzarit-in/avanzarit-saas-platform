package com.avanzarit.platform.saas.aws.lambda.eventhandler.impl;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.processors.impl.DynamoDbStreamRecordProcessor;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for handling events on tables.
 */
public class DynamoDbEventHandler<T extends DynamoEntity> {
    private static final Logger LOGGER = LogManager.getLogger(DynamoDbEventHandler.class);

    private DynamoDbStreamRecordProcessor<? extends DynamoEntity> dynamoStreamRecordProcessor;

    public DynamoDbEventHandler() {
        this.dynamoStreamRecordProcessor = new DynamoDbStreamRecordProcessor<>();
    }

    /**
     * Handles the given event.
     *
     * @param dynamoDbEvent The DynamoDB event that needs to be handled.
     */
    public void handleEvent(CmwContext cmwContext, DynamodbEvent dynamoDbEvent) {
        for (DynamodbEvent.DynamodbStreamRecord record : dynamoDbEvent.getRecords()) {
            LOGGER.debug(
                    "Event: " + record.getEventID()
                            + " " + record.getEventSource()
                            + " " + record.getEventName() + ":"
            );

            dynamoStreamRecordProcessor.process(cmwContext, record);
        }
    }

    /**
     * Adds an entity trigger for a table to the {@link DynamoDbStreamRecordProcessor}.
     */
    public void addTrigger(String tableName, EntityTrigger<? extends DynamoEntity> trigger) {
        dynamoStreamRecordProcessor.addTrigger(tableName, trigger);
    }
}
