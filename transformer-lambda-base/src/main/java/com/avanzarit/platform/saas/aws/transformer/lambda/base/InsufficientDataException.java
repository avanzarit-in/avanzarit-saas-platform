package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;

import java.util.List;

/**
 * Exception thrown when a {@link Transformer} instance does not have enough information to perform the actual
 * transformation. This will trigger the retry mechanism.
 */
public class InsufficientDataException extends RuntimeException {
    private CoreEntity coreEntity;
    private List<? extends CoreEntity> missingEntities;

    public InsufficientDataException(String message) {
        super(message);
    }

    /**
     * The constructor receives the following.
     *
     * @param message         The error message
     * @param missingEntities The entiries which were not found while processing the coreEntity (for example the
     *                        missing references for a given product)
     * @param coreEntity      The entity being processed and transformed
     */
    public InsufficientDataException(String message, List<? extends CoreEntity> missingEntities,
                                     CoreEntity coreEntity) {
        super(message);
        this.coreEntity = coreEntity;
        this.missingEntities = missingEntities;
    }

    public CoreEntity getCoreEntity() {
        return coreEntity;
    }

    public void setCoreEntity(CoreEntity coreEntity) {
        this.coreEntity = coreEntity;
    }

    public List<? extends CoreEntity> getMissingEntities() {
        return missingEntities;
    }

    public void setMissingEntities(List<CoreEntity> missingEntities) {
        this.missingEntities = missingEntities;
    }
}
