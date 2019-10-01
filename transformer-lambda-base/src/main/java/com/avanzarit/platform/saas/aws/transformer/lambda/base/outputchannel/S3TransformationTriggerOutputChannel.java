package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannel;

import com.avanzarit.platform.saas.aws.core.validation.Validator;
import com.avanzarit.platform.saas.aws.s3.StructuredS3BucketNameParser;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTrigger;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.Transformer;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.writers.S3OutputWriter;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Represents a potential output channel for input that enters a {@link TransformationTrigger}. Every output channel
 * defines its own transformation and filtering logic.
 *
 * @param <I> The type of input entities that the output channel processes.
 * @param <O> The type of output entities that the output channel generates.
 */
public class S3TransformationTriggerOutputChannel<I, O> extends TransformationTriggerOutputChannel<I, O> {
    private S3OutputWriter<O> s3OutputWriter;
    private StructuredS3BucketNameParser s3BucketNameParser;
    private Validator validator;

    public S3TransformationTriggerOutputChannel(String name, Transformer<I, O> transformer,
                                                Validator validator,
                                                StructuredS3BucketNameParser s3BucketNameParser,
                                                S3OutputWriter<O> s3OutputWriter) {
        super(name, validator, transformer);
        this.s3OutputWriter = s3OutputWriter;
        this.validator = validator;
        this.s3BucketNameParser = s3BucketNameParser;
    }

    @Override
    public void saveOutputEntity(CmwContext cmwContext, UpdateInfo updateInfo, O outputEntity) {
        if (s3OutputWriter != null) {
            s3OutputWriter.process(outputEntity);
        }
    }
}
