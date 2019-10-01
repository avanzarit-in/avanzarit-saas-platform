package com.avanzarit.platform.saas.aws.lambda.processors;

import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class DynamoDbRecordProcessor<T extends DynamoEntity> {
    private static final Logger LOGGER = LogManager.getLogger(DynamoDbRecordProcessor.class);

    private Map<String, EntityTrigger<? extends DynamoEntity>> triggers = new HashMap<>();

    /**
     * Adds an {@link EntityTrigger} for the given table. When events are being processed for the given table, the
     * trigger will be invoked.
     */
    public void addTrigger(String tableName, EntityTrigger<? extends DynamoEntity> trigger) {
        triggers.put(tableName, trigger);

        LOGGER.debug("Registered trigger for class " + trigger.getEntityClass() + ":" + trigger);
    }

    public Map<String, EntityTrigger<? extends DynamoEntity>> getTriggers() {
        return triggers;
    }
}
