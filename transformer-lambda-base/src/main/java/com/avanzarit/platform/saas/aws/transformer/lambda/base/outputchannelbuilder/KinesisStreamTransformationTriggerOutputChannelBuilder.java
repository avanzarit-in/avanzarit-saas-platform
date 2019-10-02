package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannelbuilder;

import com.avanzarit.platform.saas.aws.core.validation.Validator;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerBuildingFailedException;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannelBuilder;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.Transformer;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerExceptionListener;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerFailureListener;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerInputFilter;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerOutputFilter;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerOutputSavedListener;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerPreTransformationListener;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerValidationFailureListener;
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

    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withValidator(Validator validator) {
        super.withValidator(validator);
        return this;
    }

    /**
     * Provides the name of the output channel. This name is used mostly in the system logging.
     *
     * @param name The name of the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withName(String name) {
        super.withName(name);
        return this;
    }

    /**
     * Sets the support for multiple output entities to <code>true</code>.<br/>
     * This flag will be transferred to the Transformation channels that the builder creates.
     *
     * @return The builder itself.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withMultpleOutputEntitiesSupport() {
        super.withMultpleOutputEntitiesSupport();
        return this;
    }

    /**
     * Adds a {@link TransformerInputFilter} that is not entity-type-specific (or generic) to the output channel. This
     * filter will run before any transformation is done by the output channel.
     *
     * @param filter The transformer filter to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withGenericPreTransformFilter(
            TransformerInputFilter<I> filter) {
        super.withGenericPreTransformFilter(filter);
        return this;
    }

    /**
     * Adds an entity-type-specific {@link TransformerInputFilter} to the output channel. This filter will run before
     * any transformation is done by the output channel.
     *
     * @param filter The transformer filter to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withPreTransformFilter(TransformerInputFilter<I> filter) {
        super.withPreTransformFilter(filter);
        return this;
    }

    /**
     * Adds a {@link Transformer} to the output channel.
     *
     * @param transformer The transformer that the output channel will use to convert from the input type to the output
     *                    type.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withTransformer(Transformer<I, O> transformer) {
        super.withTransformer(transformer);
        return this;
    }

    /**
     * Adds a {@link TransformerOutputFilter} that is not entity-type-specific (or generic) to the output channel. This
     * filter will run after the transformation is done by the output channel.
     *
     * @param filter The transformer filter to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withGenericPostTransformFilter(
            TransformerOutputFilter<I, O> filter) {
        super.withGenericPostTransformFilter(filter);
        return this;
    }

    /**
     * Adds an entity-type-specific {@link TransformerOutputFilter} to the output channel. This filter will run after
     * the transformation is done by the output channel.
     *
     * @param filter The transformer filter to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withPostTransformFilter(
            TransformerOutputFilter<I, O> filter) {
        super.withPostTransformFilter(filter);
        return this;
    }

    /**
     * Adds a {@link TransformerOutputSavedListener} that is not entity-type-specific (or generic) to the output
     * channel. This listener is invoked after the output has been saved.
     *
     * @param outputSavedListener The listener to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withGenericOutputSavedListener(
            TransformerOutputSavedListener<I, O> outputSavedListener) {
        super.withGenericOutputSavedListener(outputSavedListener);
        return this;
    }

    /**
     * Adds a {@link TransformerOutputSavedListener} that is entity-type-specific to the output channel. This listener
     * is invoked after the output has been saved.
     *
     * @param outputSavedListener The listener to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withOutputSavedListener(
            TransformerOutputSavedListener<I, O> outputSavedListener) {
        super.withOutputSavedListener(outputSavedListener);
        return this;
    }

    /**
     * Adds a {@link TransformerPreTransformationListener} to the output channel. This listener is invoked before the
     * transformation from input to output entity starts.
     *
     * @param preTransformationListener The listener to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withPreTransformationListener(
            TransformerPreTransformationListener<I> preTransformationListener) {
        super.withPreTransformationListener(preTransformationListener);
        return this;
    }

    /**
     * Adds a {@link TransformerValidationFailureListener} to the output channel. This listener is invoked when
     * validation of the output entity has failed.
     *
     * @param validationFailureListener The validation listener to add to the output channeL.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withValidationFailureListener(
            TransformerValidationFailureListener<O> validationFailureListener) {
        super.withValidationFailureListener(validationFailureListener);
        return this;
    }

    /**
     * Adds a {@link TransformerFailureListener} that is not entity-type-specific to the output channel. This listener
     * is invoked when an output channel fails to transform an item and has attempted retries (if applicable).
     *
     * @param genericFailureListener The failure listener to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withGenericFailureListener(
            TransformerFailureListener<I> genericFailureListener) {
        super.withGenericFailureListener(genericFailureListener);
        return this;
    }

    /**
     * Adds a {@link TransformerFailureListener} that is entity-type-specific to the output channel. This listener is
     * invoked when an output channel fails to transform an item and has attempted retries (if applicable).
     *
     * @param failureListener The failure listener to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withFailureListener(
            TransformerFailureListener<I> failureListener) {
        super.withFailureListener(failureListener);
        return this;
    }

    /**
     * Adds a {@link TransformerExceptionListener} that is not entity-type-specific to the output channel. This listener
     * is invoked when an output channel fails to transform an item due to an unexpected exception.
     *
     * @param genericExceptionListener The exception listener to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withGenericExceptionListener(
            TransformerExceptionListener<I> genericExceptionListener) {
        super.withGenericExceptionListener(genericExceptionListener);
        return this;
    }

    /**
     * Adds a {@link TransformerExceptionListener} that is entity-type-specific to the output channel. This listener is
     * invoked when an output channel fails to transform an item due to an unexpected exception.
     *
     * @param exceptionListener The exception listener to add to the output channel.
     */
    public KinesisStreamTransformationTriggerOutputChannelBuilder<I, O> withExceptionListener(
            TransformerExceptionListener<I> exceptionListener) {
        super.withExceptionListener(exceptionListener);
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
