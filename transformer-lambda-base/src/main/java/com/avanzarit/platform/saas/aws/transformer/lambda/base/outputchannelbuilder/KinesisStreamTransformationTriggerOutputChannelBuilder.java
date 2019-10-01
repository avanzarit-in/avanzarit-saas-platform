package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannelbuilder;

import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerBuildingFailedException;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannelBuilder;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannel.KinesisStreamTransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.writers.KinesisStreamOutputWriter;

/**
 * Builder class that can be used to build a {@link TransformationTriggerOutputChannel}. It is used to provide a small
 * DSL to make it easier to define output channels.
 *
 * @param <I> The type of input entity that the output channel will process.
 * @param <O> The type of output entity that the output channel will generate.
 */
public class KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> extends TransformationTriggerOutputChannelBuilder<I, O> {

    private KinesisStreamOutputWriter<O> kinesisStreamOutputWriter;

    public KinesisStreamTransformationTriggerOutputChannelBuilder() {
        super();
    }

    /**
     * The {@link KinesisStreamOutputWriter} class to use for storing the output entity.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withOutputKinesisStreamWriter(
            KinesisStreamOutputWriter<O> kinesisStreamOutputWriter) {
        this.kinesisStreamOutputWriter = kinesisStreamOutputWriter;
        return this;
    }

    /**
     * Builds the final output channel based on the configuration specified using the other methods in this class.
     *
     * @return The actual output channel object.
     */
    public TransformationTriggerOutputChannel<I, O> build() {
        super.build();
        if (kinesisStreamOutputWriter == null) {
            throw new TransformationTriggerBuildingFailedException(
                    "No output Kinesis Stream Writer was specified"
            );
        }

        return new KinesisStreamTransformationTriggerOutputChannel<>(getName(), getTransformer(), getValidator(), kinesisStreamOutputWriter);
    }
}
