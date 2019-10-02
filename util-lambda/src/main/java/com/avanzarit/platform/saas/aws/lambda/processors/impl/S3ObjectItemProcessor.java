package com.avanzarit.platform.saas.aws.lambda.processors.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.EntityTriggerCallFailedException;
import com.avanzarit.platform.saas.aws.lambda.processors.S3ItemProcessor;
import com.avanzarit.platform.saas.aws.lambda.triggercaller.EntityTriggerCaller;
import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.s3.StructuredS3BucketName;
import com.avanzarit.platform.saas.aws.s3.StructuredS3BucketNameParser;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class S3ObjectItemProcessor extends S3ItemProcessor<S3Item<S3Object>> {
    private static final Logger LOGGER = LogManager.getLogger(S3ObjectItemProcessor.class);

    @Override
    public void process(CmwContext cmwContext, S3Item<S3Object> entity) {
        try {
            String path = String.join("_",
                    entity.getBucketName(),
                    entity.getFolderPath()
            );
            LOGGER.debug("Bucket Name: " + path);

            StructuredS3BucketNameParser stnp = new StructuredS3BucketNameParser();
            StructuredS3BucketName stn = stnp.parse(path);

            LOGGER.debug("Parsed tablename: " + stn);

            EntityTrigger<S3Item<S3Object>> et = getTriggers().get(stn.getBucket());

            LOGGER.debug("Update on " + stn.getBucket() + " - using trigger " + et);

            if (et != null) {
                LOGGER.debug("Calling EntityTrigger " + et);

                EntityTriggerCaller<S3Item<S3Object>> etc = new EntityTriggerCaller<>(et);
                etc.call(cmwContext, entity);
            }
            LOGGER.debug("Finished processing");
        } catch (Exception e) {
            EntityTriggerCallFailedException exception = null;

            if (e instanceof EntityTriggerCallFailedException) {
                exception = (EntityTriggerCallFailedException) e;

                if (!exception.isLogged()) {
                    cmwContext.logError("Exception was not handled by entity trigger", e);
                }
            }

            if (exception != null && !exception.isCritical()) {
                cmwContext.logError("The Exception is not critical hence not rethrowing", e);
            } else {
                throw e;
            }
        }
    }
}
