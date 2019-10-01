package com.avanzarit.platform.saas.aws.lambda;

import com.amazonaws.services.dynamodbv2.datamodeling.ConversionSchema;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.ItemConverter;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * The EntityTriggerCaller takes care of the conversion of the DynamoDb streams format to a Java object before providing
 * the old and the new version of an entity to an entity trigger.
 */
public class DynamoDbEntityTriggerCaller<T extends DynamoEntity> {

    private static final Logger LOGGER = LogManager.getLogger(DynamoDbEntityTriggerCaller.class);

    private EntityTrigger<T> trigger;

    /**
     * Creates a new EntityTriggerCaller wrapping an {@link EntityTrigger}.
     */
    public DynamoDbEntityTriggerCaller(EntityTrigger<T> trigger) {
        this.trigger = trigger;
    }

    public void call(CmwContext context, T entity) {
        trigger.onCreate(context, entity);
    }

    /**
     * Calls the wrapped {@link EntityTrigger} instance and provides it with a Java object representing the old and the
     * new value of the entity.
     *
     * @param oldItem The old entity as it was retrieved from a DynamoDb stream.
     * @param newItem The new entity as it was retrieved from a DynamoDb stream.
     */
    public void call(CmwContext context, Map<String, AttributeValue> oldItem, Map<String, AttributeValue> newItem) {
        T oldObject = tryMapOldItem(context, oldItem);
        T newObject = tryMapNewItem(context, newItem);

        LOGGER.debug("  old obj : " + oldObject);
        LOGGER.debug("  new obj : " + newObject);

        try {
            if (oldObject == null && newObject != null) {
                trigger.onCreate(context, newObject);
            } else if (oldObject != null && newObject != null) {
                trigger.onUpdate(context, oldObject, newObject);
            } else if (oldObject != null && newObject == null) {
                trigger.onDelete(context, oldObject);
            } else {
                LOGGER.warn("Could not perform any changes, possibly because the old object could not be parsed");
            }
        } catch (Exception e) {
            boolean isLogged = trigger.onError(context, oldObject, newObject, e);
            throw new EntityTriggerCallFailedException(e, isLogged, true);
        }
    }

    private T tryMapOldItem(CmwContext cmwContext, Map<String, AttributeValue> item) {
        try {
            return map(item);
        } catch (DynamoDBMappingException e) {
            UpdateInfo updateInfo = createUpdateInfoFromItem(item);
            cmwContext.logError(
                    updateInfo,
                    "Could not parse old object due to mapping exception, so discarding in favor of new version",
                    e
            );
            return null;
        }
    }

    private T tryMapNewItem(CmwContext cmwContext, Map<String, AttributeValue> item) {
        try {
            return map(item);
        } catch (DynamoDBMappingException e) {
            UpdateInfo updateInfo = createUpdateInfoFromItem(item);
            cmwContext.logError(
                    updateInfo,
                    "Could not process new object due to mapping exception, discarding change",
                    e
            );
            throw new EntityTriggerCallFailedException(e, true, false);
        }
    }

    private T map(Map<String, AttributeValue> item) {
        T result = null;

        if (item != null) {
            ItemConverter itemConverter = DynamoDBMapperConfig.DEFAULT.getConversionSchema().getConverter(
                    new ConversionSchema.Dependencies()
            );
            result = itemConverter.unconvert(trigger.getEntityClass(), item);
        }

        return result;
    }

    private UpdateInfo createUpdateInfoFromItem(Map<String, AttributeValue> item) {
        String context = item.get("context") == null ? null : item.get("context").getS();
        String objectId = item.get("object_id") == null ? null : item.get("object_id").getS();
        String updateId = item.get("update_id") == null ? null : item.get("update_id").getS();

        return new UpdateInfo(context, objectId, updateId);
    }
}
