package com.avanzarit.platform.saas.aws.lambda.eventhandler.impl;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.model.S3Object;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.S3EventHandler;
import com.avanzarit.platform.saas.aws.lambda.processors.S3ItemProcessor;
import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.util.CmwContext;

public class S3ObjectEventHandler implements S3EventHandler<S3Item<S3Object>> {
    private S3ItemProcessor<S3Item<S3Object>> processor;

    @Override
    public void handleEvent(CmwContext cmwContext, S3Event event) {

    }

    @Override
    public void addTrigger(String bucketName, EntityTrigger<S3Item<S3Object>> trigger) {
        processor.addTrigger(bucketName, trigger);
    }
}
