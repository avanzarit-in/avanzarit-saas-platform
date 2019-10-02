package com.avanzarit.platform.saas.aws.lambda.eventhandler.impl;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.KinesisEventHandler;
import com.avanzarit.platform.saas.aws.lambda.processors.impl.DynamoDbItemRecordProcessor;
import com.avanzarit.platform.saas.aws.lambda.processors.impl.DynamoDbStreamRecordProcessor;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for handling events on tables.
 */
public abstract class GenericKinesisStreamEventHandler<T extends DynamoEntity> implements KinesisEventHandler {
    private static final Logger LOGGER = LogManager.getLogger(GenericKinesisStreamEventHandler.class);

    private DynamoDbItemRecordProcessor recordProcessor;

    public GenericKinesisStreamEventHandler() {
        this.recordProcessor = new DynamoDbItemRecordProcessor();
    }

    /**
     * Handles the given event.
     *
     * @param event The Kinesis event that needs to be handled.
     */
    public void handleEvent(CmwContext cmwContext, KinesisEvent event) {
        for (KinesisEvent.KinesisEventRecord record : event.getRecords()) {
            LOGGER.debug(
                    "Event: " + record.getEventID()
                            + " " + record.getEventSource()
                            + " " + record.getEventName()
            );

            T itemToProcess = mapToDynamoEntity(record);

            if (itemToProcess != null) {
                LOGGER.debug(itemToProcess);
                recordProcessor.process(cmwContext, itemToProcess);
            }
        }
    }

    public abstract T mapToDynamoEntity(KinesisEvent.KinesisEventRecord record);

    /**
     * Adds an entity trigger for a table to the {@link DynamoDbStreamRecordProcessor}.
     */
    public void addTrigger(String tableName, EntityTrigger<? extends DynamoEntity> trigger) {
        recordProcessor.addTrigger(tableName, trigger);
    }
}
