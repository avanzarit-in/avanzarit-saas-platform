package com.avanzarit.platform.saas.aws.lambda.eventhandler.impl;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.S3EventHandler;
import com.avanzarit.platform.saas.aws.lambda.processors.S3ItemProcessor;
import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.util.CmwContext;

import java.io.File;

public class S3FileEventHandler implements S3EventHandler<S3Item<File>> {
    private S3ItemProcessor<S3Item<File>> processor;

    @Override
    public void handleEvent(CmwContext cmwContext, S3Event event) {

    }

    @Override
    public void addTrigger(EntityTrigger<S3Item<File>> trigger) {
        processor.addTrigger(trigger);
    }
}
