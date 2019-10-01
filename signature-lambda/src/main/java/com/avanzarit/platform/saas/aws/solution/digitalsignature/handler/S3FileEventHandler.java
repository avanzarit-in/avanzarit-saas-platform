package com.avanzarit.platform.saas.aws.solution.digitalsignature.handler;

import com.amazonaws.services.s3.model.S3Event;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.S3EventHandler;
import com.avanzarit.platform.saas.aws.util.CmwContext;

public class S3FileEventHandler implements S3EventHandler {
    @Override
    public void handleEvent(CmwContext cmwContext, S3Event event) {

    }

    @Override
    public void addTrigger(EntityTrigger<?> trigger) {

    }
}
