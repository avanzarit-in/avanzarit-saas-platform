package com.avanzarit.platform.saas.aws.lambda.processors;

import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class S3ItemProcessor<T> {
    private static final Logger LOGGER = LogManager.getLogger(S3ItemProcessor.class);

    private Map<String, EntityTrigger<T>> triggers = new HashMap<>();

    /**
     * Adds an {@link EntityTrigger} for the given table. When events are being processed for the given table, the
     * trigger will be invoked.
     */
    public void addTrigger(String bucketName, EntityTrigger<T> trigger) {
        triggers.put(bucketName, trigger);

        LOGGER.debug("Registered trigger for class " + trigger.getEntityClass() + ":" + trigger);
    }

    public Map<String, EntityTrigger<T>> getTriggers() {
        return triggers;
    }

    public abstract void process(CmwContext cmwContext, T entity);
}
