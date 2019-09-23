package com.avanzarit.platform.saas.aws.lambda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class RecordProcessor {
    private static final Logger LOGGER = LogManager.getLogger(RecordProcessor.class);

    private Map<String, EntityTrigger<?>> triggers = new HashMap<>();

    /**
     * Adds an {@link EntityTrigger} for the given table. When events are being processed for the given table, the
     * trigger will be invoked.
     */
    public void addTrigger(String tableName, EntityTrigger<?> trigger) {
        triggers.put(tableName, trigger);

        LOGGER.debug("Registered trigger for class " + trigger.getEntityClass() + ":" + trigger);
    }

    public Map<String, EntityTrigger<?>> getTriggers() {
        return triggers;
    }
}
