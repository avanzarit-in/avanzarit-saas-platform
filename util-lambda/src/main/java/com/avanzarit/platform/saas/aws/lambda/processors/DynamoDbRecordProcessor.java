package com.avanzarit.platform.saas.aws.lambda.processors;

import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class DynamoDbRecordProcessor<T> {
    private static final Logger LOGGER = LogManager.getLogger(DynamoDbRecordProcessor.class);

    private Map<String, EntityTrigger<DynamoEntity>> triggers = new HashMap<>();

    /**
     * Adds an {@link EntityTrigger} for the given table. When events are being processed for the given table, the
     * trigger will be invoked.
     */
    public void addTrigger(String tableName, EntityTrigger<DynamoEntity> trigger) {
        triggers.put(tableName, trigger);

        LOGGER.debug("Registered trigger for class " + trigger.getEntityClass() + ":" + trigger);
    }

    public Map<String, EntityTrigger<DynamoEntity>> getTriggers() {
        return triggers;
    }

    public abstract void process(CmwContext cmwContext, T entity);
}
