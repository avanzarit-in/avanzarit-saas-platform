package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import com.avanzarit.platform.saas.aws.validation.SchemaValidator;
import com.avanzarit.platform.saas.aws.validation.ValidationReport;

import java.util.ArrayList;
import java.util.List;

/**
 * A TransformationPipeline is a wrapper object for an output channel that contains some state information specific for
 * a transformation that is currently in progress. It retains possible validation errors and the transformation result
 * from the output channel.
 *
 * @param <I> The type of input entity that this pipeline processes.
 * @param <O> The type of output entity that this pipeline generates.
 */
public class TransformationPipeline<I extends CoreEntity, O extends CoreEntity> {
    private TransformationTriggerOutputChannel<I, O> outputChannel;
    private SchemaValidator validator;
    //private O transformedEntity;
    private List<O> transformedEntities;
    private String validationError;

    public TransformationPipeline(TransformationTriggerOutputChannel<I, O> outputChannel, SchemaValidator validator) {
        this.outputChannel = outputChannel;
        this.validator = validator;
        this.transformedEntities = new ArrayList<>();
    }

    /**
     * Gives the name of the output channel (for logging purposes).
     */
    public String getName() {
        return outputChannel.getName();
    }

    /**
     * Indicates whether the pipeline is applicable to the given input data (see
     * {@link TransformationTriggerOutputChannel#isApplicable(CmwContext, UpdateInfo, CoreEntity, CoreEntity)}).
     *
     * @param updateInfo The identification information of the current update event.
     * @param oldEntity  The old version of the input entity.
     * @param newEntity  The new version of the input entity.
     * @return true if this pipeline is applicable, false otherwise.
     */
    public boolean isApplicable(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity) {
        return outputChannel.isApplicable(cmwContext, updateInfo, oldEntity, newEntity);
    }

    /**
     * Transforms the input entity and stores the result as part of this pipeline. The input entity is transformed
     * based on the logic defined in the {@link TransformationTriggerOutputChannel}.
     *
     * @param updateInfo The identification information of the current update event.
     * @param oldEntity  The old version of the input entity.
     * @param newEntity  The new version of the input entity.
     */
    public void runTransformation(CmwContext cmwContext, UpdateInfo updateInfo, I oldEntity, I newEntity) {
        if (outputChannel.getSupportsMultipleOutputEntities()) {
            transformedEntities.addAll(outputChannel.transformMultiple(cmwContext, updateInfo, oldEntity, newEntity));
        } else {
            O transformedEntity = outputChannel.transform(cmwContext, updateInfo, oldEntity, newEntity);
            if (transformedEntity != null) {
                transformedEntities.add(outputChannel.transform(cmwContext, updateInfo, oldEntity, newEntity));
            }
        }
    }

    /**
     * Validates whether the transformed entity is correct according to its JSON schema.
     * <p>
     * The validation will only happen of the transformedEntity is itself not null.
     * There can be cases where the transformer returns a null object as
     * transformation was not necessary for the object. In such case the validation should return true.
     * <p>
     * For example BVFamilyGroup converter may return null.
     *
     * @param updateInfo The identification information of the current update event.
     * @return true if all transformed entities are valid, false otherwise.
     */
    public boolean validateTransformation(CmwContext cmwContext, UpdateInfo updateInfo) {
        for (O transformedEntity : transformedEntities) {
            if (transformedEntity != null) {
                ValidationReport validationReport = validator.validate(cmwContext, updateInfo, transformedEntity);

                if (!validationReport.isValid()) {
                    validationError = validationReport.getMessage();
                    outputChannel.onValidationFailure(
                            cmwContext, updateInfo, transformedEntity,
                            "JSON validation failed for " + transformedEntity.getBareTableName()
                                    + " with message:\n" + validationReport.getMessage()
                    );
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Indicates whether the transformed entity validated or not. (Does not run the actual validation)
     *
     * @return true if validation succeeded, false otherwise.
     */
    public boolean isValid() {
        return validationError == null;
    }

    /**
     * Gives the transformed entity, but does not perform the transformation logic.
     */
    public O getTransformedEntity() {
        return transformedEntities.isEmpty() ? null : transformedEntities.get(0);
    }

    public List<O> getTransformedEntities() {
        return transformedEntities;
    }

    /**
     * Saves the transformed entities using the logic defined in the {@link TransformationTriggerOutputChannel}.
     *
     * @param updateInfo     The identification information of the current update event.
     * @param newInputEntity The new version of the input entity.
     * @return true if all output entities were saved, false otherwise.
     */
    public boolean save(CmwContext cmwContext, UpdateInfo updateInfo, I newInputEntity) {
        boolean anyFailures = false;

        for (O transformedEntity : transformedEntities) {
            anyFailures = anyFailures
                    || !outputChannel.saveOutput(cmwContext, updateInfo, newInputEntity, transformedEntity);
        }

        return !transformedEntities.isEmpty() && !anyFailures;
    }

    /**
     * Gives the validation errors, if any occurred.
     */
    public String getValidationError() {
        return validationError;
    }

    /**
     * Notifies the output channel that transformation failed.
     *
     * @param updateInfo     The identification information of the current update event.
     * @param inputOldEntity The old version of the input entity.
     * @param inputNewEntity The new version of the input entity.
     * @param message        A message indicating why transformation failed.
     */
    public void onTransformerFailure(CmwContext cmwContext, UpdateInfo updateInfo, I inputOldEntity, I inputNewEntity,
                                     String message) {
        outputChannel.onTransformationFailure(cmwContext, updateInfo, inputOldEntity, inputNewEntity, message);
    }

    /**
     * Notifies the output channel that an exception occurred and gives the opportunity to the output channel to handle
     * the exception itself.
     *
     * @param updateInfo  The identification information of the current update event.
     * @param inputEntity The input entity that was being transformed.
     * @param e           The exception that occurred during transformation.
     * @return true if the exception was handled by the output channel, false otherwise.
     */
    public boolean handleException(CmwContext cmwContext, UpdateInfo updateInfo, I inputEntity, Exception e) {
        return outputChannel.handleException(cmwContext, updateInfo, inputEntity, e);
    }
}
