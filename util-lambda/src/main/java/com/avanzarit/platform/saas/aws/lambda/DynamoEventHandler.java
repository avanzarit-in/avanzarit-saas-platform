package com.avanzarit.platform.saas.aws.lambda;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for handling events on tables.
 */
public class DynamoEventHandler {
    private static final Logger LOGGER = LogManager.getLogger(DynamoEventHandler.class);

    private DynamoRecordProcessor dynamoRecordProcessor;

    public DynamoEventHandler() {
        this.dynamoRecordProcessor = new DynamoRecordProcessor();
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

            dynamoRecordProcessor.process(cmwContext, record);
        }
    }

    /**
     * Adds an entity trigger for a table to the {@link DynamoRecordProcessor}.
     */
    public void addTrigger(String tableName, EntityTrigger<?> trigger) {
        dynamoRecordProcessor.addTrigger(tableName, trigger);
    }
}
