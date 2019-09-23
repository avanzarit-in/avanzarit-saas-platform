package com.avanzarit.platform.saas.aws.lambda;

import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;

/**
 * An EntityTrigger is used by the {@link DynamoRecordProcessor} in order to handle events on entities within the
 * system.
 */
public interface EntityTrigger<T extends DynamoEntity> {

    /**
     * Gets the entity class this trigger is associated with.
     */
    Class<T> getEntityClass();

    /**
     * Called when an entity is created within the system.
     *
     * @param entity The Java object representation of the entity that was created.
     */
    void onCreate(CmwContext context, T entity);

    /**
     * Called when an entity is updated within the system.
     *
     * @param oldEntity The Java object representation of the old version of the entity that was updated.
     * @param newEntity The Java object representation of the new version of the entity that was updated.
     */
    void onUpdate(CmwContext context, T oldEntity, T newEntity);

    /**
     * Called when an entity is deleted from the system.
     *
     * @param entity The Java object representation of the entity that was deleted.
     */
    void onDelete(CmwContext context, T entity);

    /**
     * Called when an unexpected error occurred within the trigger to allow the trigger to handle the error itself.
     *
     * @param oldObject The Java object representation of the old version of the entity that was being processed at the
     *                  time the error occurred (if applicable).
     * @param newObject The Java object representation of the new version of the entity that was being processed at the
     *                  time the error occurred (if applicable).
     * @param e         The exception that occurred.
     * @return true if the exception was handled by the trigger, false otherwise.
     */
    boolean onError(CmwContext context, T oldObject, T newObject, Exception e);
}
