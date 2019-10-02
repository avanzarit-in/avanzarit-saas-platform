package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class offering a small DSL to build {@link TransformationTrigger} instances easily.
 *
 * @param <I> The type of input entity that the transformation trigger will process.
 */
public abstract class TransformationTriggerBuilder<I, O> {

    private Class<I> entityClass;
    private TransformationTriggerRetryPolicy<CoreEntity> retryPolicy;
    private List<TransformationTriggerFilter<I>> genericFilters;
    private List<TransformationTriggerFilter<I>> filters;
    private List<TransformationTriggerOutputChannel<I, ? extends O>> outputChannels;
    private List<TransformationTriggerValidationListener<I>> validationListeners;
    private List<TransformationTriggerErrorHandler<I>> errorHandlers;

    public TransformationTriggerBuilder() {
        this.genericFilters = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.outputChannels = new ArrayList<>();
        this.validationListeners = new ArrayList<>();
        this.errorHandlers = new ArrayList<>();
    }

    public Class<I> getEntityClass() {
        return entityClass;
    }

    /**
     * Configures the type of entity that the transformation trigger will process.
     */
    public TransformationTriggerBuilder<I, O> withEntityClass(Class<I> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    /**
     * Configures an, optional, retry policy that the transformation trigger can use.
     */
    public TransformationTriggerBuilder<I, O> withRetryPolicy(TransformationTriggerRetryPolicy<CoreEntity> retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    /**
     * Configures an additional non-input-type-specific filter for the transformation trigger that allows filtering of
     * certain input data.
     */
    public TransformationTriggerBuilder<I, O> withGenericFilter(TransformationTriggerFilter<I> filter) {
        this.genericFilters.add(filter);
        return this;
    }

    /**
     * Configures an additional input-type-specific filter for the transformation trigger that allows filtering of
     * certain input data.
     */
    public TransformationTriggerBuilder<I, O> withFilter(TransformationTriggerFilter<I> filter) {
        this.filters.add(filter);
        return this;
    }

    /**
     * Configures an additional non-input-type-specific filter for the transformation trigger that allows filtering of
     * certain input data.
     */
    public TransformationTriggerBuilder<I, O> withOutputChannel(
            TransformationTriggerOutputChannel<I, ? extends O> outputChannel) {
        this.outputChannels.add(outputChannel);
        return this;
    }

    /**
     * Configures an additional validation listener for the transformation trigger, that allows to listen for validation
     * failure events.
     */
    public TransformationTriggerBuilder<I, O> withValidationListener(
            TransformationTriggerValidationListener<I> validationListener) {
        this.validationListeners.add(validationListener);
        return this;
    }

    /**
     * Configures an additional error listener for the transformation trigger, that allows to handle errors that occur
     * during transformation.
     */
    public TransformationTriggerBuilder<I, O> withErrorHandler(TransformationTriggerErrorHandler<I> errorHandler) {
        this.errorHandlers.add(errorHandler);
        return this;
    }

    /**
     * Builds the actual {@link TransformationTrigger}.
     */
    public TransformationTrigger<I, O> build() {
        if (entityClass == null) {
            throw new TransformationTriggerBuildingFailedException("Entity class is missing");
        }

        TransformationTrigger<I, O> transformationTrigger = createTransformationTrigger();

        transformationTrigger.setRetryPolicy(retryPolicy);

        for (TransformationTriggerFilter<I> genericFilter : genericFilters) {
            transformationTrigger.addGenericFilter(genericFilter);
        }

        for (TransformationTriggerFilter<I> filter : filters) {
            transformationTrigger.addFilter(filter);
        }

        for (TransformationTriggerOutputChannel<I, ? extends O> outputChannel : outputChannels) {
            transformationTrigger.addOutputChannel(outputChannel);
        }

        for (TransformationTriggerValidationListener<I> validationListener : validationListeners) {
            transformationTrigger.addValidationListener(validationListener);
        }

        for (TransformationTriggerErrorHandler<I> errorListener : errorHandlers) {
            transformationTrigger.addErrorListener(errorListener);
        }

        return transformationTrigger;
    }

    protected abstract TransformationTrigger<I, O> createTransformationTrigger();
}
