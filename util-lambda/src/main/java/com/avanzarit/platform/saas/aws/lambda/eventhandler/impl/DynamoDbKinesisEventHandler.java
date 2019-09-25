package com.avanzarit.platform.saas.aws.lambda.eventhandler.impl;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.avanzarit.platform.saas.aws.lambda.processors.DynamoRecordProcessor;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.KinesisEventRecordMapper;
import com.avanzarit.platform.saas.aws.lambda.LambdaRouterRecord;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.KinesisEventHandler;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for handling events on tables.
 */
public class DynamoDbKinesisEventHandler implements KinesisEventHandler {
    private static final Logger LOGGER = LogManager.getLogger(DynamoDbKinesisEventHandler.class);

    private KinesisEventRecordMapper recordMapper;
    private DynamoRecordProcessor dynamoRecordProcessor;

    public DynamoDbKinesisEventHandler() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.recordMapper = new KinesisEventRecordMapper(objectMapper);

        this.dynamoRecordProcessor = new DynamoRecordProcessor();
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

            LambdaRouterRecord dynamoStreamsRecord = recordMapper.map(record, LambdaRouterRecord.class);

            if (dynamoStreamsRecord != null) {
                dynamoRecordProcessor.process(cmwContext, dynamoStreamsRecord);
            }
        }
    }

    /**
     * Adds an entity trigger for a table to the {@link DynamoRecordProcessor}.
     */
    public void addTrigger(String tableName, EntityTrigger<?> trigger) {
        dynamoRecordProcessor.addTrigger(tableName, trigger);
    }
}
