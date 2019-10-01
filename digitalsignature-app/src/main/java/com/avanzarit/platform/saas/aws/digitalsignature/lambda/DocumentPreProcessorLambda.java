package com.avanzarit.platform.saas.aws.digitalsignature.lambda;

import com.amazonaws.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.S3EventHandler;
import com.avanzarit.platform.saas.aws.lambda.requesthandler.S3EventHandlingLambda;
import com.avanzarit.platform.saas.aws.util.CmwContext;

public class DocumentPreProcessorLambda extends S3EventHandlingLambda {
    @Override
    public void registerTriggers(Region region, CmwContext cmwContext) {

    }

    @Override
    protected S3EventHandler getS3EventHandler() {
        return null;
    }

    @Override
    public Object handleRequest(Object o, Context context) {
        return null;
    }
}
