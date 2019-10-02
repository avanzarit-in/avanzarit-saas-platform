package com.avanzarit.platform.saas.aws.lambda.eventhandler.impl;

import com.amazonaws.services.s3.model.S3Event;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.S3EventHandler;
import com.avanzarit.platform.saas.aws.lambda.processors.impl.S3ItemProcessor;
import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.util.CmwContext;

public class S3ItemEventHandler<T extends S3Item<T>> implements S3EventHandler {
    private S3ItemProcessor<T> processor;

    @Override
    public void handleEvent(CmwContext cmwContext, S3Event event) {

    }

    @Override
    public void addTrigger(EntityTrigger trigger) {
        processor.addTrigger(trigger);
    }
}
