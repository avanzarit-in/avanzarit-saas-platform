package com.avanzarit.platform.saas.aws.lambda.processors.impl;

import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableName;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.EntityTriggerCallFailedException;
import com.avanzarit.platform.saas.aws.lambda.processors.DynamoDbRecordProcessor;
import com.avanzarit.platform.saas.aws.lambda.triggercaller.EntityTriggerCaller;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The GenericRecordProcessor provides functionality for processing events that were put directly into the kinesis stream.
 * Based on the entity it will send the events to the appropriate {@link EntityTrigger} instances (if any are registered). It
 * uses the {@link EntityTriggerCaller} to provide the mapping between the object format and Java objects.
 */
public class DynamoDbItemRecordProcessor extends DynamoDbRecordProcessor<DynamoEntity> {
    private static final Logger LOGGER = LogManager.getLogger(DynamoDbItemRecordProcessor.class);

    public void process(CmwContext cmwContext, DynamoEntity entity) {
        try {
            String tableName = String.join("_", cmwContext.getPrefix(),
                    cmwContext.getLayer(),
                    entity.getBareTableName()
            );
            LOGGER.debug("Table: " + tableName);

            StructuredTableNameParser stnp = new StructuredTableNameParser();
            StructuredTableName stn = stnp.parse(tableName);

            LOGGER.debug("Parsed tablename: " + stn);

            EntityTrigger<DynamoEntity> et = getTriggers().get(stn.getTable());

            LOGGER.debug("Update on " + tableName + " - using trigger " + et);

            if (et != null) {
                LOGGER.debug("Calling EntityTrigger " + et);

                EntityTriggerCaller<DynamoEntity> etc = new EntityTriggerCaller<>(et);
                etc.call(cmwContext, entity);
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
