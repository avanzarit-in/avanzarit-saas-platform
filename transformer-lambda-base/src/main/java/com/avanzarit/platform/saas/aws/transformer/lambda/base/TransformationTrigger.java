package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import com.avanzarit.platform.saas.aws.util.jsonvalidation.JsonValidationErrorMetric;
import com.avanzarit.platform.saas.aws.util.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.validation.SchemaValidator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A TransformationTrigger can be added to a {@link com.avanzarit.platform.saas.aws.util.lambda.DynamoStreamHandlingLambda} or a
 * {@link com.avanzarit.platform.saas.aws.util.lambda.KinesisStreamHandlingLambda}. It takes care of all the logic associated with
 * changes to a specific DynamoDb table.
 * <p>
 * Next to handling create and update events it also provides a framework for error handling, retry and transformation
 * logic.
 *
 * @param <I> The input entity type that this trigger handles.
 */
public class TransformationTrigger<I extends CoreEntity> implements EntityTrigger<I> {

    private static final Logger LOGGER = LogManager.getLogger(TransformationTrigger.class);

    private Class<I> entityClass;
    private SchemaValidator validator;
    private ObjectMapper mapper;
    private TransformationTriggerRetryPolicy<I> retryPolicy;
    private List<TransformationTriggerFilter<CoreEntity>> genericFilters;
    private List<TransformationTriggerFilter<I>> filters;
    private List<TransformationTriggerOutputChannel<I, ? extends CoreEntity>> outputChannels;
    private List<TransformationTriggerValidationListener<I>> validationListeners;
    private List<TransformationTriggerRetryListener<I>> retryListeners;
    private List<TransformationTriggerErrorHandler<I>> errorHandlers;

    public TransformationTrigger(Class<I> entityClass, SchemaValidator validator, ObjectMapper mapper) {
        this.entityClass = entityClass;
        this.validator = validator;
        this.mapper = mapper;
        this.genericFilters = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.outputChannels = new ArrayList<>();
        this.validationListeners = new ArrayList<>();
        this.retryListeners = new ArrayList<>();
        this.errorHandlers = new ArrayList<>();
    }

    @Override
    public Class<I> getEntityClass() {
        return entityClass;
    }

    @Override
    public void onCreate(CmwContext context, I entity) {
        handleChange(context, null, entity);
    }

    @Override
    public void onUpdate(CmwContext context, I oldEntity, I newEntity) {
        handleChange(context, oldEntity, newEntity);
    }

    @Override
    public void onDelete(CmwContext context, I entity) {

    }

    @Override
    public boolean onError(CmwContext context, I oldObject, I newObject, Exception e) {
        context.logError(createUpdateInfo(newObject), e);
        return true;
    }

    private void handleChange(CmwContext cmwContext, I oldEntity, I newEntity) {
        UpdateInfo updateInfo = createUpdateInfo(newEntity);

        try {
            if (oldEntity != null) {
                LOGGER.debug(updateInfo);
                LOGGER.debug("Handling update from old entity: " + mapper.writeValueAsString(oldEntity)
                        + " to new entity: " + mapper.writeValueAsString(newEntity));
            } else {
                LOGGER.debug(updateInfo);
                LOGGER.debug("Handling creation of new entity: " + mapper.writeValueAsString(newEntity));
            }
        } catch (JsonProcessingException e) {
            cmwContext.logError(updateInfo, "Failed to log change event", e);
        }

        if (newEntity.hasEntityBody()) {
            try {
                if (!filteredOutByInputFilters(cmwContext, updateInfo, oldEntity, newEntity)) {
                    runOutputChannelPipelines(cmwContext, oldEntity, newEntity, updateInfo);
                }
            } catch (Exception e) {
                handleException(cmwContext, oldEntity, newEntity, updateInfo, e);
            }
        } else {
            cmwContext.logWarning(
                    updateInfo,
                    "Ignoring " + newEntity.getBareTableName() + " because it has no content"
            );
        }
    }

    private void handleException(CmwContext cmwContext, I oldEntity, I newEntity, UpdateInfo updateInfo, Exception e) {
        if (e instanceof EntityTransformationException) {
            cmwContext.logWarning(updateInfo, e.getMessage());
        } else {
            boolean isHandled = handleError(cmwContext, updateInfo, oldEntity, newEntity, e);

            if (!isHandled) {
                throw new TransformationTriggerFailedException(e);
            }
        }
    }

    private boolean filteredOutByInputFilters(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity) {
        for (TransformationTriggerFilter<CoreEntity> filter : genericFilters) {
            if (filter.filteredOut(cmwContext, updateInfo, oldEntity, newEntity)) {
                cmwContext.logWarning(
                        updateInfo,
                        "Filtered out by trigger filter " + filter.getClass().getSimpleName()
                );
                return true;
            }
        }

        for (TransformationTriggerFilter<I> filter : filters) {
            if (filter.filteredOut(cmwContext, updateInfo, oldEntity, newEntity)) {
                cmwContext.logWarning(
                        updateInfo,
                        "Filtered out by trigger filter " + filter.getClass().getSimpleName()
                );
                return true;
            }
        }

        return false;
    }

    private void runOutputChannelPipelines(CmwContext cmwContext, I oldEntity, I newEntity, UpdateInfo updateInfo) {
        List<TransformationPipeline<I, ? extends CoreEntity>> pipelines = createTransformationPipelines();
        List<TransformationPipeline<I, ? extends CoreEntity>> applicablePipelines = filterPipelinesThatAreNotApplicable(
                pipelines, cmwContext, updateInfo, oldEntity, newEntity
        );

        if (!applicablePipelines.isEmpty()) {
            List<TransformationPipeline<I, ? extends CoreEntity>> successfulPipelines = applyTransformations(
                    applicablePipelines, cmwContext, oldEntity, newEntity, updateInfo
            );

            if (isTransformationSuccessful(applicablePipelines, successfulPipelines)) {
                validateAndSaveOutput(successfulPipelines, cmwContext, oldEntity, newEntity, updateInfo);
            } else {
                cmwContext.logWarning(updateInfo, "No output channel was successful in handling this update");
            }
        } else {
            cmwContext.logWarning(updateInfo, "No output channel is handling this update");
        }
    }

    private List<TransformationPipeline<I, ? extends CoreEntity>> filterPipelinesThatAreNotApplicable(
            List<TransformationPipeline<I, ? extends CoreEntity>> pipelines, CmwContext cmwContext,
            UpdateInfo updateInfo, I oldEntity, I newEntity) {
        List<TransformationPipeline<I, ? extends CoreEntity>> applicablePipelines = new ArrayList<>();

        for (TransformationPipeline<I, ? extends CoreEntity> pipeline : pipelines) {
            if (pipeline.isApplicable(cmwContext, updateInfo, oldEntity, newEntity)) {
                applicablePipelines.add(pipeline);
            }
        }

        return applicablePipelines;
    }

    private void validateAndSaveOutput(List<TransformationPipeline<I, ? extends CoreEntity>> successfulPipelines,
                                       CmwContext cmwContext, I oldEntity, I newEntity, UpdateInfo updateInfo) {
        if (isValid(successfulPipelines)) {
            saveOutput(successfulPipelines, cmwContext, oldEntity, newEntity, updateInfo);
        } else {
            handleValidationFailure(successfulPipelines, cmwContext, newEntity, updateInfo);
        }
    }

    private List<TransformationPipeline<I, ? extends CoreEntity>> applyTransformations(
            List<TransformationPipeline<I, ? extends CoreEntity>> pipelines, CmwContext cmwContext, I oldEntity,
            I newEntity, UpdateInfo updateInfo) {
        List<TransformationPipeline<I, ? extends CoreEntity>> successfulPipelines = new ArrayList<>();

        for (TransformationPipeline<I, ? extends CoreEntity> pipeline : pipelines) {
            try {
                pipeline.runTransformation(cmwContext, updateInfo, oldEntity, newEntity);
                cmwContext.logInfo(updateInfo, "Transformed by output channel " + pipeline.getName());

                if (pipeline.validateTransformation(cmwContext, updateInfo)) {
                    cmwContext.logInfo(
                            updateInfo,
                            "Transformation is valid according to output channel "
                                    + pipeline.getName()
                    );
                } else {
                    JsonValidationErrorMetric errorMetric = new JsonValidationErrorMetric(
                            cmwContext.getLayer() + pipeline.getTransformedEntity().getBareTableName(), 1.0
                    );
                    cmwContext.putMetrics(errorMetric);

                    cmwContext.logWarning(
                            updateInfo,
                            "JSON validation failed for " + pipeline.getTransformedEntity().getBareTableName()
                                    + " with message:\n" + pipeline.getValidationError()
                    );
                }

                successfulPipelines.add(pipeline);
            } catch (InsufficientDataException e) {
                handleRetry(pipelines, cmwContext, updateInfo, oldEntity, newEntity, e);
                break;
            } catch (Exception e) {
                cmwContext.logInfo(
                        updateInfo,
                        "An exception occurred while transforming, passing exception to exception handlers (message: "
                                + ExceptionUtils.getStackTrace(e) + ")"
                );

                boolean handled = pipeline.handleException(cmwContext, updateInfo, newEntity, e);

                if (!handled) {
                    throw e;
                }
            }
        }

        return successfulPipelines;
    }

    private void saveOutput(List<TransformationPipeline<I, ? extends CoreEntity>> pipelines, CmwContext cmwContext,
                            I oldEntity, I newEntity, UpdateInfo updateInfo) {
        for (TransformationPipeline<I, ? extends CoreEntity> pipeline : pipelines) {
            try {
                if (pipeline.isValid()) {
                    boolean saveSuccessful = pipeline.save(cmwContext, updateInfo, newEntity);

                    if (saveSuccessful) {
                        cmwContext.logInfo(
                                updateInfo,
                                "Updated " + pipeline.getTransformedEntity().getBareTableName() + " in database"
                        );
                    }
                }
            } catch (InsufficientDataException e) {
                handleRetry(pipelines, cmwContext, updateInfo, oldEntity, newEntity, e);
                break;
            } catch (Exception e) {
                cmwContext.logInfo(
                        updateInfo,
                        "An exception occurred while transforming, passing exception to exception handlers (message: "
                                + ExceptionUtils.getStackTrace(e) + ")"
                );

                boolean handled = pipeline.handleException(cmwContext, updateInfo, newEntity, e);

                if (!handled) {
                    throw e;
                }
            }
        }
    }

    private void handleRetry(List<TransformationPipeline<I, ? extends CoreEntity>> pipelines, CmwContext cmwContext,
                             UpdateInfo updateInfo, I oldEntity, I newEntity, InsufficientDataException e) {
        if (retryPolicy != null) {
            cmwContext.logWarning(updateInfo, "Retrying object (cause: " + e.getMessage() + ")");

            boolean retry = retryPolicy.retry(cmwContext, e.getMissingEntities(), e.getCoreEntity());

            if (retry) {
                handleRetryFailed(
                        pipelines, cmwContext, updateInfo, oldEntity, newEntity,
                        "Error while trying to add item into cache_retry table for entity " + e.getMissingEntities()
                                + e.getMessage() + ")"
                );
            }
        } else {
            handleRetryFailed(
                    pipelines, cmwContext, updateInfo, oldEntity, newEntity,
                    "Insufficient data and no retry policy defined, not retrying transformation (cause: "
                            + e.getMessage() + ")"
            );
        }
    }

    private void handleRetryFailed(List<TransformationPipeline<I, ? extends CoreEntity>> pipelines,
                                   CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity,
                                   String message) {
        for (TransformationPipeline<I, ? extends CoreEntity> pipeline : pipelines) {
            if (pipeline.isApplicable(cmwContext, updateInfo, oldEntity, newEntity)) {
                pipeline.onTransformerFailure(cmwContext, updateInfo, oldEntity, newEntity, message);
            }
        }

        cmwContext.logWarning(updateInfo, message);

        for (TransformationTriggerRetryListener<I> retryListener : retryListeners) {
            retryListener.onRetryFailed(cmwContext, updateInfo, oldEntity, newEntity);
        }
    }

    private void handleValidationFailure(List<TransformationPipeline<I, ? extends CoreEntity>> pipelines,
                                         CmwContext cmwContext, I newEntity, UpdateInfo updateInfo) {
        cmwContext.logWarning(updateInfo, "Not all transformed output was valid");

        List<String> validationErrors = gatherPipelineValidationErrors(pipelines);

        for (TransformationTriggerValidationListener<I> validationListener : validationListeners) {
            validationListener.onValidationFailure(cmwContext, updateInfo, newEntity, validationErrors);
        }
    }

    private List<TransformationPipeline<I, ? extends CoreEntity>> createTransformationPipelines() {
        List<TransformationPipeline<I, ? extends CoreEntity>> result = new ArrayList<>();

        for (TransformationTriggerOutputChannel<I, ? extends CoreEntity> outputChannel : outputChannels) {
            result.add(createTransformationPipeline(outputChannel));
        }

        return result;
    }

    private <O extends CoreEntity> TransformationPipeline<I, O> createTransformationPipeline(
            TransformationTriggerOutputChannel<I, O> outputChannel) {
        return new TransformationPipeline<>(outputChannel, validator);
    }

    private boolean isTransformationSuccessful(List<TransformationPipeline<I, ? extends CoreEntity>> pipelines,
                                               List<TransformationPipeline<I, ? extends CoreEntity>> successfulPipelines
    ) {
        return pipelines.size() == successfulPipelines.size();
    }

    private boolean isValid(List<TransformationPipeline<I, ? extends CoreEntity>> pipelines) {
        return pipelines.size() == countValidPipelines(pipelines);
    }

    private int countValidPipelines(List<TransformationPipeline<I, ? extends CoreEntity>> pipelines) {
        int count = 0;

        for (TransformationPipeline<I, ? extends CoreEntity> pipeline : pipelines) {
            if (pipeline.isValid()) {
                count++;
            }
        }

        return count;
    }

    private List<String> gatherPipelineValidationErrors(
            List<TransformationPipeline<I, ? extends CoreEntity>> pipelines) {
        List<String> validationErrors = new ArrayList<>();

        for (TransformationPipeline<I, ? extends CoreEntity> pipeline : pipelines) {
            if (!pipeline.isValid()) {
                validationErrors.add(
                        "JSON validation failed for " + pipeline.getTransformedEntity().getBareTableName()
                                + " with message:\n" + pipeline.getValidationError()
                );
            }
        }

        return validationErrors;
    }

    private UpdateInfo createUpdateInfo(I entity) {
        return new UpdateInfo(entity.getContext(), entity.getObjectId(), entity.getUpdateId());
    }

    private boolean handleError(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity, Exception e) {
        boolean isHandled = false;

        for (TransformationTriggerErrorHandler<I> errorListener : errorHandlers) {
            if (errorListener.handleError(cmwContext, updateInfo, oldEntity, newEntity, e)) {
                isHandled = true;
            }
        }

        return isHandled;
    }

    /**
     * Adds a non-type-specific filter to this TransformationTrigger. This type of filter will be used every time a
     * change happens on the DynamoDb table to filter out entities that this trigger is not interested in.
     *
     * @param genericFilter The filter that needs to be added.
     */
    public void addGenericFilter(TransformationTriggerFilter<CoreEntity> genericFilter) {
        genericFilters.add(genericFilter);
    }

    /**
     * Adds a type-specific filter to this TransformationTrigger. This type of filter will be used every time a change
     * happens on the DynamoDb table to filter out entities that this trigger is not interested in.
     *
     * @param filter The filter that needs to be added.
     */
    public void addFilter(TransformationTriggerFilter<I> filter) {
        filters.add(filter);
    }

    /**
     * Adds an output channel to this TransformationTrigger. An output channel represents a specific type of output that
     * can be the result of the TransformationTrigger logic. It contains transformation logic and filtering specific to
     * the type of output that it generates.
     *
     * @param outputChannel The output channel that needs to be added.
     */
    public void addOutputChannel(TransformationTriggerOutputChannel<I, ? extends CoreEntity> outputChannel) {
        outputChannels.add(outputChannel);
    }

    /**
     * Adds a validation listener to the TransformationTrigger. A validation listener is used when specific validation
     * events occur in the trigger's logic.
     *
     * @param validationListener The validation listener that needs to be added.
     */
    public void addValidationListener(TransformationTriggerValidationListener<I> validationListener) {
        validationListeners.add(validationListener);
    }

    /**
     * Adds a retry listener to the TransformationTrigger. A retry listener is used when retry-specific events occur in
     * the trigger's logic.
     *
     * @param retryListener The retry listener that needs to be added.
     */
    public void addRetryListener(TransformationTriggerRetryListener<I> retryListener) {
        retryListeners.add(retryListener);
    }

    /**
     * Adds an error listener to the TransformationTrigger. An error listener is invoked when an unexpected exception
     * occurs in the trigger's logic that can't be handled.
     *
     * @param errorListener The error listener that needs to be added.
     */
    public void addErrorListener(TransformationTriggerErrorHandler<I> errorListener) {
        errorHandlers.add(errorListener);
    }

    /**
     * Enables a {@link TransformationTriggerRetryPolicy} for this TransformationTrigger. The retry policy is being
     * used to determine whether retries need to happen and to handle them, if so.
     *
     * @param retryPolicy The retry policy that needs to be added.
     */
    public void setRetryPolicy(TransformationTriggerRetryPolicy<I> retryPolicy) {
        this.retryPolicy = retryPolicy;
    }
}