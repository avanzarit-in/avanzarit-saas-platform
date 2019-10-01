package com.avanzarit.platform.saas.aws.lambda.processors.impl;

import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class S3ItemProcessor<T extends S3Item<T>> {
    private static final Logger LOGGER = LogManager.getLogger(S3ItemProcessor.class);

    private List<EntityTrigger<T>> triggers = new ArrayList<>();

    /**
     * Adds an {@link EntityTrigger} for the given table. When events are being processed for the given table, the
     * trigger will be invoked.
     */
    public void addTrigger(EntityTrigger<T> trigger) {
        triggers.add(trigger);

        LOGGER.debug("Registered trigger for class " + trigger.getEntityClass() + ":" + trigger);
    }

    public List<EntityTrigger<T>> getTriggers() {
        return triggers;
    }

    public void process(CmwContext cmwContext, T entity) {

    }
}
