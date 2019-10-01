package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.validation.Validator;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a potential output channel for input that enters a {@link TransformationTrigger}.
 * Every output channel defines its own transformation and filtering logic.
 *
 * @param <I> The type of input entities that the output channel processes.
 * @param <O> The type of output entities that the output channel generates.
 */
public abstract class TransformationTriggerOutputChannel<I, O> {
    private String name;
    private Validator validator;
    private boolean supportsMultipleOutputEntities;
    private List<TransformerInputFilter<I>> genericPreTransformFilters;
    private List<TransformerInputFilter<I>> preTransformFilters;
    private Transformer<I, O> transformer;
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

    public TransformationTriggerOutputChannel(String name, Validator validator, Transformer<I, O> transformer) {
        this.name = name;
        this.validator = validator;
        this.transformer = transformer;
        this.genericPreTransformFilters = new ArrayList<>();
        this.preTransformFilters = new ArrayList<>();
        this.supportsMultipleOutputEntities = false;
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

    /**
     * Gives the name of the output channel (for logging purposes).
     */
    public String getName() {
        return name;
    }

    public Validator getValidator() {
        return validator;
    }

    public boolean getSupportsMultipleOutputEntities() {
        return supportsMultipleOutputEntities;
    }

    /**
     * Set the support for creating multiple output entities frmo one input entity to <code>true</code>.
     *
     * @param supportsMultipleOutputEntities Indicator of the multiple output entity support.
     */
    public void setSupportsMultipleOutputEntities(boolean supportsMultipleOutputEntities) {
        this.supportsMultipleOutputEntities = supportsMultipleOutputEntities;
    }

    /**
     * Indicates whether this output channel is applicable for the given input data. It uses a set of
     * {@link TransformerInputFilter} to determine which entities are applicable and which aren't.
     *
     * @param updateInfo The identification information of the current update event.
     * @param oldEntity  The old version of the input data.
     * @param newEntity  The new version of the input data.
     * @return true if this output channel is applicable, false otherwise.
     */
    public boolean isApplicable(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity) {
        return !filteredOutByPreFilters(cmwContext, updateInfo, oldEntity, newEntity);
    }

    /**
     * Transforms the input entity to a certain output format using a {@link Transformer}.
     *
     * @param cmwContext The context of the operation.
     * @param updateInfo The identification information of the current update event.
     * @param oldEntity  The old version of the entity that needs to be transformed.
     * @param newEntity  The new version of the entity that needs to be transformed.
     * @return The transformed output entity.
     */
    public O transform(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity) {
        notifyPreTransformationListeners(cmwContext, updateInfo, oldEntity, newEntity);

        cmwContext.logInfo(updateInfo, "Attempting transformation");

        return transformer.transform(cmwContext, updateInfo, newEntity);
    }

    /**
     * Transforms in input entity into (possibly) multiple output entities.
     *
     * @param cmwContext The context of the operation.
     * @param updateInfo The identification information of the current update event.
     * @param oldEntity  The old version of the entity that needs to be transformed.
     * @param newEntity  The new version of the entity that needs to be transformed.
     * @return A List of output entities.
     */
    public List<O> transformMultiple(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity) {
        notifyPreTransformationListeners(cmwContext, updateInfo, oldEntity, newEntity);

        cmwContext.logInfo(updateInfo, "Attempting transformation");

        return transformer.transformMultiple(cmwContext, updateInfo, newEntity);
    }

    /**
     * Saves an output entity to a repository if it passes all {@link TransformerOutputFilter} instances. Runs
     * {@link TransformerOutputSavedListener} instances when the entity was successfully saved.
     *
     * @param updateInfo   The identification information of the current update event.
     * @param inputEntity  The input entity that has been transformed.
     * @param outputEntity The transformed output entity.
     * @return true if the output was saved, false if the transformed entity was filtered out by a
     * {@link TransformerOutputFilter}.
     */
    public boolean saveOutput(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity, O outputEntity) {
        boolean result = false;

        if (!filteredOutByPostFilters(cmwContext, updateInfo, inputEntity, outputEntity)) {
            saveOutputEntity(cmwContext, updateInfo, outputEntity);

            result = true;

            notifyOutputSavedListeners(cmwContext, updateInfo, inputEntity, outputEntity);
        }

        return result;
    }

    /**
     * Runs a set of {@link TransformerValidationFailureListener} instances when transformation has failed.
     *
     * @param updateInfo        The identification information of the current update event.
     * @param transformedEntity The transformed entity that failed to validate.
     * @param message           A message indicating why validation failed.
     */
    public void onValidationFailure(CmwContext cmwContext, UpdateInfo updateInfo, O transformedEntity, String message) {
        for (TransformerValidationFailureListener<O> failureListener : validationFailureListeners) {
            failureListener.onValidationFailure(cmwContext, updateInfo, transformedEntity, message);
        }
    }

    /**
     * Invoked when transformation failed due to insufficient data and the retry policy indicates that no retries are
     * applicable anymore. Runs one or more {@link TransformerFailureListener} instances that are listening for
     * failures.
     *
     * @param updateInfo     The identification information of the current update event.
     * @param inputOldEntity The previous data of the entity that needs to be transformed.
     * @param inputNewEntity The new data representing the entity that needs to be transformed.
     * @param message        A message indicating what went wrong.
     */
    public void onTransformationFailure(CmwContext cmwContext, UpdateInfo updateInfo, I inputOldEntity,
                                        I inputNewEntity, String message) {
        for (TransformerFailureListener<I> genericFailureListener : genericFailureListeners) {
            genericFailureListener.onFailure(cmwContext, updateInfo, inputOldEntity, inputNewEntity, message);
        }

        for (TransformerFailureListener<I> transformerFailureListener : failureListeners) {
            transformerFailureListener.onFailure(cmwContext, updateInfo, inputOldEntity, inputNewEntity, message);
        }
    }

    /**
     * Handles the given exception using the configured {@link TransformerExceptionListener} instances.
     *
     * @param updateInfo  The identification of the current update event.
     * @param inputEntity The input entity that failed transformation.
     * @param e           The exception that occurred, causing transformation to fail.
     * @return true if the exception was handled, false otherwise.
     */
    public boolean handleException(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity, Exception e) {
        boolean result = false;
        for (TransformerExceptionListener<I> genericExceptionListener : genericExceptionListeners) {
            if (genericExceptionListener.handleException(cmwContext, updateInfo, inputEntity, e)) {
                result = true;
            }
        }

        for (TransformerExceptionListener<I> exceptionListener : exceptionListeners) {
            if (exceptionListener.handleException(cmwContext, updateInfo, inputEntity, e)) {
                result = true;
            }
        }

        return result;
    }

    private boolean filteredOutByPreFilters(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity) {
        for (TransformerInputFilter<I> preTransformFilter : preTransformFilters) {
            if (preTransformFilter.filteredOut(cmwContext, updateInfo, oldEntity, newEntity)) {
                cmwContext.logInfo(
                        updateInfo,
                        "Filtered out by pre transform filter "
                                + preTransformFilter.getClass().getSimpleName()
                );
                return true;
            }
        }

        for (TransformerInputFilter<I> genericPreTransformFilter : genericPreTransformFilters) {
            if (genericPreTransformFilter.filteredOut(cmwContext, updateInfo, oldEntity, newEntity)) {
                cmwContext.logInfo(
                        updateInfo,
                        "Filtered out by pre transform filter "
                                + genericPreTransformFilter.getClass().getSimpleName()
                );
                return true;
            }
        }

        return false;
    }

    private boolean filteredOutByPostFilters(CmwContext cmwContext, UpdateInfo updateInfo, I newEntity,
                                             O outputEntity) {
        for (TransformerOutputFilter<I, O> genericPostTransformFilter : genericPostTransformFilters) {
            if (genericPostTransformFilter.filteredOut(cmwContext, updateInfo, newEntity, outputEntity)) {
                cmwContext.logInfo(
                        updateInfo,
                        "Filtered out by post transform filter "
                                + genericPostTransformFilter.getClass().getSimpleName()
                );
                return true;
            }
        }

        for (TransformerOutputFilter<I, O> postTransformFilter : postTransformFilters) {
            if (postTransformFilter.filteredOut(cmwContext, updateInfo, newEntity, outputEntity)) {
                cmwContext.logInfo(
                        updateInfo,
                        "Filtered out by post transform filter "
                                + postTransformFilter.getClass().getSimpleName()
                );
                return true;
            }
        }

        return false;
    }

    public abstract void saveOutputEntity(CmwContext cmwContext, UpdateInfo updateInfo, O outputEntity);

    private void notifyPreTransformationListeners(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity,
                                                  I newEntity) {
        for (TransformerPreTransformationListener<I> preTransformationListener : preTransformationListeners) {
            preTransformationListener.onPreTransformation(cmwContext, updateInfo, oldEntity, newEntity);
        }
    }

    private void notifyOutputSavedListeners(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity,
                                            O outputEntity) {
        for (TransformerOutputSavedListener<I, O> listener : genericOutputSavedListeners) {
            listener.onOutputSaved(cmwContext, updateInfo, inputEntity, outputEntity);
        }

        for (TransformerOutputSavedListener<I, O> listener : outputSavedListeners) {
            listener.onOutputSaved(cmwContext, updateInfo, inputEntity, outputEntity);
        }
    }

    /**
     * Adds a non-type-specific {@link TransformerInputFilter} to this output channel.
     */
    public void addGenericPreTransformFilter(TransformerInputFilter<I> genericPreTransformFilter) {
        genericPreTransformFilters.add(genericPreTransformFilter);
    }

    /**
     * Adds a type-specific {@link TransformerInputFilter} to this output channel.
     */
    public void addPreTransformFilter(TransformerInputFilter<I> preTransformFilter) {
        preTransformFilters.add(preTransformFilter);
    }

    /**
     * Adds a non-type-specific {@link TransformerOutputFilter} to this output channel.
     */
    public void addGenericPostTransformFilter(TransformerOutputFilter<I, O> outputFilter) {
        genericPostTransformFilters.add(outputFilter);
    }

    /**
     * Adds a type-specific {@link TransformerOutputFilter} to this output channel.
     */
    public void addPostTransformFilter(TransformerOutputFilter<I, O> outputFilter) {
        postTransformFilters.add(outputFilter);
    }

    /**
     * Adds a non-type-specific {@link TransformerOutputSavedListener} to this output channel.
     */
    public void addGenericOutputSavedListener(TransformerOutputSavedListener<I, O> listener) {
        genericOutputSavedListeners.add(listener);
    }

    /**
     * Adds a type-specific {@link TransformerOutputSavedListener} to this output channel.
     */
    public void addOutputSavedListener(TransformerOutputSavedListener<I, O> outputSavedListener) {
        outputSavedListeners.add(outputSavedListener);
    }

    /**
     * Adds a {@link TransformerPreTransformationListener} to this output channel.
     */
    public void addPreTransformationListener(TransformerPreTransformationListener<I> preTransformationListener) {
        preTransformationListeners.add(preTransformationListener);
    }

    /**
     * Adds a {@link TransformerValidationFailureListener} to this output channel.
     */
    public void addValidationFailureListener(TransformerValidationFailureListener<O> failureListener) {
        validationFailureListeners.add(failureListener);
    }

    /**
     * Adds a non-type-specific {@link TransformerFailureListener} to this output channel.
     */
    public void addGenericFailureListener(TransformerFailureListener<I> genericFailureListener) {
        genericFailureListeners.add(genericFailureListener);
    }

    /**
     * Adds an input-type-specific {@link TransformerFailureListener} to this output channel.
     */
    public void addFailureListener(TransformerFailureListener<I> failureListener) {
        failureListeners.add(failureListener);
    }

    /**
     * Adds a non-type-specific {@link TransformerExceptionListener} to this output channel.
     */
    public void addGenericExceptionListener(TransformerExceptionListener<I> genericExceptionListener) {
        genericExceptionListeners.add(genericExceptionListener);
    }

    /**
     * Adds an input-type-specific {@link TransformerExceptionListener} to this output channel.
     */
    public void addExceptionListener(TransformerExceptionListener<I> exceptionListener) {
        exceptionListeners.add(exceptionListener);
    }
}
