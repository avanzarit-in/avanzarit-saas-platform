package com.avanzarit.platform.saas.aws.lambda.processors.impl;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableName;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;
import com.avanzarit.platform.saas.aws.lambda.DynamoDbEntityTriggerCaller;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.EntityTriggerCallFailedException;
import com.avanzarit.platform.saas.aws.lambda.model.LambdaRouterRecord;
import com.avanzarit.platform.saas.aws.lambda.processors.DynamoDbRecordProcessor;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * The DynamoRecordProcessor provides functionality for processing events that were stored on a DynamoDb stream. Based
 * on the table it will send the events to the appropriate {@link EntityTrigger} instances (if any are registered). It
 * uses the {@link DynamoDbEntityTriggerCaller} to provide the mapping between the DynamoDb streams format and Java objects.
 */
public class DynamoDbStreamRecordProcessor extends DynamoDbRecordProcessor<DynamodbStreamRecord> {
    private static final Logger LOGGER = LogManager.getLogger(DynamoDbStreamRecordProcessor.class);

    /**
     * Processes a DynamoDb streams record and provides it to the entity triggers for processing.
     */
    public void process(CmwContext cmwContext, DynamodbStreamRecord record) {
        Map<String, AttributeValue> oldImage = record.getDynamodb().getOldImage();
        Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();

        process(cmwContext, record.getEventSourceARN(), oldImage, newImage);
    }

    /**
     * Processes a Lambda router record and provides it to the entity triggers for processing.
     */
    public void process(CmwContext cmwContext, LambdaRouterRecord record) {
        process(cmwContext, record.getEventSourceArn(), record.getOldImage(), record.getNewImage());
    }

    private void process(CmwContext cmwContext, String eventSourceArn, Map<String, AttributeValue> oldImage,
                         Map<String, AttributeValue> newImage) {
        try {
            String tableName = eventSourceArn.split(":")[5].split("/")[1];
            LOGGER.debug("Table: " + tableName);

            StructuredTableNameParser stnp = new StructuredTableNameParser();
            StructuredTableName stn = stnp.parse(tableName);

            LOGGER.debug("Parsed tablename: " + stn);

            EntityTrigger<? extends DynamoEntity> et = getTriggers().get(stn.getTable());

            LOGGER.debug("Update on " + tableName + " - using trigger " + et);

            if (et != null) {
                LOGGER.debug("Calling EntityTrigger " + et);

                DynamoDbEntityTriggerCaller<DynamoEntity> etc = new DynamoDbEntityTriggerCaller<DynamoEntity>((EntityTrigger<DynamoEntity>) et);
                etc.call(cmwContext, oldImage, newImage);
            }
            LOGGER.debug("Finished processing");
        } catch (Exception e) {
            EntityTriggerCallFailedException exception = null;

            if (e instanceof EntityTriggerCallFailedException) {
                exception = (EntityTriggerCallFailedException) e;

                if (!exception.isLogged()) {
                    cmwContext.logError("Exception was not handled by entity trigger", e);
                }
            }

            if (exception != null && !exception.isCritical()) {
                cmwContext.logError("The Exception is not critical hence not rethrowing", e);
            } else {
                throw e;
            }
        }
    }
}
