package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class that can be used to build a {@link TransformationTriggerOutputChannel}. It is used to provide a small
 * DSL to make it easier to define output channels.
 *
 * @param <I> The type of input entity that the output channel will process.
 * @param <O> The type of output entity that the output channel will generate.
 */
public abstract class TransformationTriggerOutputChannelBuilder<I, O> {
    private String name;
    private Validator validator;
    private Transformer<I, O> transformer;
    private boolean supportsMultipleOutputEntities;
    private List<TransformerInputFilter<I>> genericPreTransformFilters;
    private List<TransformerInputFilter<I>> preTransformFilters;
    private List<TransformerOutputFilter<I, O>> genericPostTransformFilters;
    private List<TransformerOutputFilter<I, O>> postTransformFilters;
    private List<TransformerOutputSavedListener<I, O>> genericOutputSavedListeners;
    private List<TransformerOutputSavedListener<I, O>> outputSavedListeners;
    private List<TransformerPreTransformationListener<I>> preTransformationListeners;
    private List<TransformerValidationFailureListener<O>> validationFailureListeners;
    private List<TransformerFailureListener<I>> genericFailureListeners;
    private List<TransformerFailureListener<I>> failureListeners;
    private List<TransformerExceptionListener<I>> genericExceptionListeners;
    private List<TransformerExceptionListener<I>> exceptionListeners;

    public TransformationTriggerOutputChannelBuilder() {
        this.genericPreTransformFilters = new ArrayList<>();
        this.preTransformFilters = new ArrayList<>();
        this.genericPostTransformFilters = new ArrayList<>();
        this.postTransformFilters = new ArrayList<>();
        this.genericOutputSavedListeners = new ArrayList<>();
        this.outputSavedListeners = new ArrayList<>();
        this.preTransformationListeners = new ArrayList<>();
        this.validationFailureListeners = new ArrayList<>();
        this.genericFailureListeners = new ArrayList<>();
        this.failureListeners = new ArrayList<>();
        this.genericExceptionListeners = new ArrayList<>();
        this.exceptionListeners = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Validator getValidator() {
        return validator;
    }

    public Transformer<I, O> getTransformer() {
        return transformer;
    }

    /**
     * Provides the name of the output channel. This name is used mostly in the system logging.
     *
     * @param name The name of the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withName(String name) {
        this.name = name;
        return this;
    }

    public TransformationTriggerOutputChannelBuilder<I, O> withValidator(Validator validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Sets the support for multiple output entities to <code>true</code>.<br/>
     * This flag will be transferred to the Transformation channels that the builder creates.
     *
     * @return The builder itself.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withMultpleOutputEntitiesSupport() {
        this.supportsMultipleOutputEntities = true;
        return this;
    }

    /**
     * Adds a {@link TransformerInputFilter} that is not entity-type-specific (or generic) to the output channel. This
     * filter will run before any transformation is done by the output channel.
     *
     * @param filter The transformer filter to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withGenericPreTransformFilter(
            TransformerInputFilter<I> filter) {
        this.genericPreTransformFilters.add(filter);
        return this;
    }

    /**
     * Adds an entity-type-specific {@link TransformerInputFilter} to the output channel. This filter will run before
     * any transformation is done by the output channel.
     *
     * @param filter The transformer filter to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withPreTransformFilter(TransformerInputFilter<I> filter) {
        this.preTransformFilters.add(filter);
        return this;
    }

    /**
     * Adds a {@link Transformer} to the output channel.
     *
     * @param transformer The transformer that the output channel will use to convert from the input type to the output
     *                    type.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withTransformer(Transformer<I, O> transformer) {
        this.transformer = transformer;
        return this;
    }

    /**
     * Adds a {@link TransformerOutputFilter} that is not entity-type-specific (or generic) to the output channel. This
     * filter will run after the transformation is done by the output channel.
     *
     * @param filter The transformer filter to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withGenericPostTransformFilter(
            TransformerOutputFilter<I, O> filter) {
        this.genericPostTransformFilters.add(filter);
        return this;
    }

    /**
     * Adds an entity-type-specific {@link TransformerOutputFilter} to the output channel. This filter will run after
     * the transformation is done by the output channel.
     *
     * @param filter The transformer filter to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withPostTransformFilter(
            TransformerOutputFilter<I, O> filter) {
        this.postTransformFilters.add(filter);
        return this;
    }

    /**
     * Adds a {@link TransformerOutputSavedListener} that is not entity-type-specific (or generic) to the output
     * channel. This listener is invoked after the output has been saved.
     *
     * @param outputSavedListener The listener to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withGenericOutputSavedListener(
            TransformerOutputSavedListener<I, O> outputSavedListener) {
        this.genericOutputSavedListeners.add(outputSavedListener);
        return this;
    }

    /**
     * Adds a {@link TransformerOutputSavedListener} that is entity-type-specific to the output channel. This listener
     * is invoked after the output has been saved.
     *
     * @param outputSavedListener The listener to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withOutputSavedListener(
            TransformerOutputSavedListener<I, O> outputSavedListener) {
        this.outputSavedListeners.add(outputSavedListener);
        return this;
    }

    /**
     * Adds a {@link TransformerPreTransformationListener} to the output channel. This listener is invoked before the
     * transformation from input to output entity starts.
     *
     * @param preTransformationListener The listener to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withPreTransformationListener(
            TransformerPreTransformationListener<I> preTransformationListener) {
        this.preTransformationListeners.add(preTransformationListener);
        return this;
    }

    /**
     * Adds a {@link TransformerValidationFailureListener} to the output channel. This listener is invoked when
     * validation of the output entity has failed.
     *
     * @param validationFailureListener The validation listener to add to the output channeL.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withValidationFailureListener(
            TransformerValidationFailureListener<O> validationFailureListener) {
        this.validationFailureListeners.add(validationFailureListener);
        return this;
    }

    /**
     * Adds a {@link TransformerFailureListener} that is not entity-type-specific to the output channel. This listener
     * is invoked when an output channel fails to transform an item and has attempted retries (if applicable).
     *
     * @param genericFailureListener The failure listener to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withGenericFailureListener(
            TransformerFailureListener<I> genericFailureListener) {
        this.genericFailureListeners.add(genericFailureListener);
        return this;
    }

    /**
     * Adds a {@link TransformerFailureListener} that is entity-type-specific to the output channel. This listener is
     * invoked when an output channel fails to transform an item and has attempted retries (if applicable).
     *
     * @param failureListener The failure listener to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withFailureListener(
            TransformerFailureListener<I> failureListener) {
        this.failureListeners.add(failureListener);
        return this;
    }

    /**
     * Adds a {@link TransformerExceptionListener} that is not entity-type-specific to the output channel. This listener
     * is invoked when an output channel fails to transform an item due to an unexpected exception.
     *
     * @param genericExceptionListener The exception listener to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withGenericExceptionListener(
            TransformerExceptionListener<I> genericExceptionListener) {
        this.genericExceptionListeners.add(genericExceptionListener);
        return this;
    }

    /**
     * Adds a {@link TransformerExceptionListener} that is entity-type-specific to the output channel. This listener is
     * invoked when an output channel fails to transform an item due to an unexpected exception.
     *
     * @param exceptionListener The exception listener to add to the output channel.
     */
    public TransformationTriggerOutputChannelBuilder<I, O> withExceptionListener(
            TransformerExceptionListener<I> exceptionListener) {
        this.exceptionListeners.add(exceptionListener);
        return this;
    }

    /**
     * Builds the final output channel based on the configuration specified using the other methods in this class.
     *
     * @return The actual output channel object.
     */
    public TransformationTriggerOutputChannel<I, O> build() {
        if (name == null) {
            throw new TransformationTriggerBuildingFailedException("No name was specified");
        }

        if (transformer == null) {
            throw new TransformationTriggerBuildingFailedException("No transformer was specified");
        }

        TransformationTriggerOutputChannel<I, O> outputChannel = null;

        if (supportsMultipleOutputEntities) {
            outputChannel.setSupportsMultipleOutputEntities(supportsMultipleOutputEntities);
        }

        for (TransformerInputFilter<I> genericPreTransformFilter : genericPreTransformFilters) {
            outputChannel.addGenericPreTransformFilter(genericPreTransformFilter);
        }

        for (TransformerInputFilter<I> preTransformFilter : preTransformFilters) {
            outputChannel.addPreTransformFilter(preTransformFilter);
        }

        for (TransformerOutputFilter<I, O> genericPostTransformFilter : genericPostTransformFilters) {
            outputChannel.addGenericPostTransformFilter(genericPostTransformFilter);
        }

        for (TransformerOutputFilter<I, O> postTransformFilter : postTransformFilters) {
            outputChannel.addPostTransformFilter(postTransformFilter);
        }

        for (TransformerOutputSavedListener<I, O> listener : genericOutputSavedListeners) {
            outputChannel.addGenericOutputSavedListener(listener);
        }

        for (TransformerOutputSavedListener<I, O> outputSavedListener : outputSavedListeners) {
            outputChannel.addOutputSavedListener(outputSavedListener);
        }

        for (TransformerPreTransformationListener<I> preTransformationListener : preTransformationListeners) {
            outputChannel.addPreTransformationListener(preTransformationListener);
        }

        for (TransformerValidationFailureListener<O> validationFailureListener : validationFailureListeners) {
            outputChannel.addValidationFailureListener(validationFailureListener);
        }

        for (TransformerFailureListener<I> genericFailureListener : genericFailureListeners) {
            outputChannel.addGenericFailureListener(genericFailureListener);
        }

        for (TransformerFailureListener<I> failureListener : failureListeners) {
            outputChannel.addFailureListener(failureListener);
        }

        for (TransformerExceptionListener<I> genericExceptionListener : genericExceptionListeners) {
            outputChannel.addGenericExceptionListener(genericExceptionListener);
        }

        for (TransformerExceptionListener<I> exceptionListener : exceptionListeners) {
            outputChannel.addExceptionListener(exceptionListener);
        }

        return outputChannel;
    }
}
