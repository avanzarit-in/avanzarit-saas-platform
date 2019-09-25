package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.validation.SchemaValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class offering a small DSL to build {@link TransformationTrigger} instances easily.
 *
 * @param <I> The type of input entity that the transformation trigger will process.
 */
public class TransformationTriggerBuilder<I extends CoreEntity> {

    private Class<I> entityClass;
    private SchemaValidator validator;
    private ObjectMapper mapper;
    private TransformationTriggerRetryPolicy<I> retryPolicy;
    private List<TransformationTriggerFilter<CoreEntity>> genericFilters;
    private List<TransformationTriggerFilter<I>> filters;
    private List<TransformationTriggerOutputChannel<I, ? extends CoreEntity>> outputChannels;
    private List<TransformationTriggerValidationListener<I>> validationListeners;
    private List<TransformationTriggerErrorHandler<I>> errorHandlers;

    public TransformationTriggerBuilder(ObjectMapper mapper) {
        this.mapper = mapper;
        this.genericFilters = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.outputChannels = new ArrayList<>();
        this.validationListeners = new ArrayList<>();
        this.errorHandlers = new ArrayList<>();
    }

    /**
     * Configures the type of entity that the transformation trigger will process.
     */
    public TransformationTriggerBuilder<I> withEntityClass(Class<I> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    /**
     * Configures the {@link SchemaValidator} that the transformation trigger should use.
     */
    public TransformationTriggerBuilder<I> withValidator(SchemaValidator validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Configures an, optional, retry policy that the transformation trigger can use.
     */
    public TransformationTriggerBuilder<I> withRetryPolicy(TransformationTriggerRetryPolicy<I> retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    /**
     * Configures an additional non-input-type-specific filter for the transformation trigger that allows filtering of
     * certain input data.
     */
    public TransformationTriggerBuilder<I> withGenericFilter(TransformationTriggerFilter<CoreEntity> filter) {
        this.genericFilters.add(filter);
        return this;
    }

    /**
     * Configures an additional input-type-specific filter for the transformation trigger that allows filtering of
     * certain input data.
     */
    public TransformationTriggerBuilder<I> withFilter(TransformationTriggerFilter<I> filter) {
        this.filters.add(filter);
        return this;
    }

    /**
     * Configures an additional non-input-type-specific filter for the transformation trigger that allows filtering of
     * certain input data.
     */
    public TransformationTriggerBuilder<I> withOutputChannel(
            TransformationTriggerOutputChannel<I, ? extends CoreEntity> outputChannel) {
        this.outputChannels.add(outputChannel);
        return this;
    }

    /**
     * Configures an additional validation listener for the transformation trigger, that allows to listen for validation
     * failure events.
     */
    public TransformationTriggerBuilder<I> withValidationListener(
            TransformationTriggerValidationListener<I> validationListener) {
        this.validationListeners.add(validationListener);
        return this;
    }

    /**
     * Configures an additional error listener for the transformation trigger, that allows to handle errors that occur
     * during transformation.
     */
    public TransformationTriggerBuilder<I> withErrorHandler(TransformationTriggerErrorHandler<I> errorHandler) {
        this.errorHandlers.add(errorHandler);
        return this;
    }

    /**
     * Builds the actual {@link TransformationTrigger}.
     */
    public TransformationTrigger<I> build() {
        if (entityClass == null) {
            throw new TransformationTriggerBuildingFailedException("Entity class is missing");
        }

        if (validator == null) {
            throw new TransformationTriggerBuildingFailedException("Validator is missing");
        }

        TransformationTrigger<I> transformationTrigger = new TransformationTrigger<>(entityClass, validator, mapper);
        transformationTrigger.setRetryPolicy(retryPolicy);

        for (TransformationTriggerFilter<CoreEntity> genericFilter : genericFilters) {
            transformationTrigger.addGenericFilter(genericFilter);
        }

        for (TransformationTriggerFilter<I> filter : filters) {
            transformationTrigger.addFilter(filter);
        }

        for (TransformationTriggerOutputChannel<I, ? extends CoreEntity> outputChannel : outputChannels) {
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
}
