package com.avanzarit.platform.saas.aws.solution.digitalsignature;

import com.amazonaws.regions.Region;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.S3EventHandler;
import com.avanzarit.platform.saas.aws.lambda.requesthandler.S3EventHandlingLambda;
import com.avanzarit.platform.saas.aws.solution.digitalsignature.model.Metadata;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTrigger;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannelbuilder.S3TransformationTriggerOutputChannelBuilder;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.transformationtriggerbuilder.S3TransformationTriggerBuilder;
import com.avanzarit.platform.saas.aws.util.CmwContext;

import java.io.File;

public class FileUploadLambda extends S3EventHandlingLambda {

    @Override
    public void registerTriggers(Region region, CmwContext cmwContext) {
        TransformationTriggerOutputChannel<File, Metadata> channelBuilder = new S3TransformationTriggerOutputChannelBuilder<File, Metadata>()
                .withName("parser")
                .build();

        TransformationTrigger<File, Metadata> triggerBuilder = new S3TransformationTriggerBuilder<File, Metadata>()
                .withEntityClass(File.class)
                .withOutputChannel(channelBuilder)
                .build();

        addTrigger(triggerBuilder);
    }

    @Override
    protected S3EventHandler getS3EventHandler() {
        return null;
    }

    @Override
    public void onInvoke(CmwContext cmwContext) {
    }

    @Override
    public void onFinish(CmwContext cmwContext) {
    }
}
