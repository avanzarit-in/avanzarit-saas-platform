package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannel;

import com.avanzarit.platform.saas.aws.core.validation.Validator;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTrigger;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.Transformer;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.writers.KinesisStreamOutputWriter;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Represents a potential output channel for input that enters a {@link TransformationTrigger}. Every output channel
 * defines its own transformation and filtering logic.
 *
 * @param <I> The type of input entities that the output channel processes.
 * @param <O> The type of output entities that the output channel generates.
 */
public class KinesisStreamTransformationTriggerOutputChannel<I, O> extends TransformationTriggerOutputChannel<I, O> {
    private KinesisStreamOutputWriter<O> kinesisStreamOutputWriter;
    private Validator validator;

    public KinesisStreamTransformationTriggerOutputChannel(String name, Transformer<I, O> transformer,
                                                           Validator validator,
                                                           KinesisStreamOutputWriter<O> kinesisStreamOutputWriter) {
        super(name, validator, transformer);
        this.kinesisStreamOutputWriter = kinesisStreamOutputWriter;
    }

    @Override
    public void saveOutputEntity(CmwContext cmwContext, UpdateInfo updateInfo, O outputEntity) {
        if (kinesisStreamOutputWriter != null) {
            kinesisStreamOutputWriter.process(outputEntity);
            cmwContext.logInfo(updateInfo, "Stored " + outputEntity.toString()
                    + " in " + kinesisStreamOutputWriter.getStreamName());
        }
    }
}
