package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannelbuilder;

import com.avanzarit.platform.saas.aws.s3.StructuredS3BucketNameParser;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerBuildingFailedException;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannelBuilder;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannel.S3TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.writers.KinesisStreamOutputWriter;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.writers.S3OutputWriter;

/**
 * Builder class that can be used to build a {@link TransformationTriggerOutputChannel}. It is used to provide a small
 * DSL to make it easier to define output channels.
 *
 * @param <I> The type of input entity that the output channel will process.
 * @param <O> The type of output entity that the output channel will generate.
 */
public class S3TransformationTriggerOutputChannelBuilder<I, O> extends TransformationTriggerOutputChannelBuilder<I, O> {
    private S3OutputWriter<O> s3OutputWriter;
    private StructuredS3BucketNameParser s3BucketNameParser;

    public S3TransformationTriggerOutputChannelBuilder() {
        super();
    }

    public S3TransformationTriggerOutputChannelBuilder<I, O> withS3BucketParser(
            StructuredS3BucketNameParser s3BucketParser) {
        this.s3BucketNameParser = s3BucketParser;
        return this;
    }

    /**
     * The {@link KinesisStreamOutputWriter} class to use for storing the output entity.
     */
    public S3TransformationTriggerOutputChannelBuilder<I, O> withOutputS3Writer(
            S3OutputWriter<O> s3Writer) {
        this.s3OutputWriter = s3Writer;
        return this;
    }

    /**
     * Builds the final output channel based on the configuration specified using the other methods in this class.
     *
     * @return The actual output channel object.
     */
    public TransformationTriggerOutputChannel<I, O> build() {
        super.build();
        if (s3BucketNameParser == null && s3OutputWriter != null) {
            throw new TransformationTriggerBuildingFailedException("No s3 bucket name parser was specified");
        }

        if (s3OutputWriter == null) {
            throw new TransformationTriggerBuildingFailedException(
                    "No output S3 writer was specified"
            );
        }

        return new S3TransformationTriggerOutputChannel<>(getName(), getTransformer(), getValidator(), s3BucketNameParser, s3OutputWriter);
    }
}
