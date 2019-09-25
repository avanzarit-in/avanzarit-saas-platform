package com.avanzarit.platform.saas.aws.service.lateststate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.dynamo.DynamoUtils;


@DynamoDBTable(tableName = LatestStateEntity.TABLE_NAME)
public class LatestStateEntity implements DynamoEntity {
    
    public static final String TABLE_NAME = "latest_product_state";
    
    private static final String OBJECT_ID = "object_id";
    private static final String CONTEXT_LAYER = "context_layer";
    private static final String CONTEXT = "context";
    private static final String UPDATE_ID = "update_id";
    private static final String LAYER = "layer";
    private static final String STATE = "state";
    private static final String TIMESTAMP = "timestamp";
    private static final String MESSAGE = "message";
    
    private String objectId;
    private String contextLayer;
    private String context;
    private String updateId;
    private String layer;
    private States state;
    private String timestamp;
    private String message;
    
    public LatestStateEntity() {
    }
    
    public LatestStateEntity(String objectId, String context, String updateId, String layer, States state,
            String timestamp) {
        this.objectId = objectId;
        this.contextLayer = DynamoUtils.toKey(context, layer);
        this.context = context;
        this.updateId = updateId;
        this.layer = layer;
        this.state = state;
        this.timestamp = timestamp;
    }

    @DynamoDBHashKey(attributeName = OBJECT_ID)
    @JsonProperty(OBJECT_ID)
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    @DynamoDBRangeKey(attributeName = CONTEXT_LAYER)
    @JsonProperty(CONTEXT_LAYER)
    public String getContextLayer() {
        return contextLayer;
    }
    
    public void setContextLayer(String contextLayer) {
        this.contextLayer = contextLayer;
    }

    @DynamoDBAttribute(attributeName = CONTEXT)
    @JsonProperty(CONTEXT)
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @DynamoDBAttribute(attributeName = UPDATE_ID)
    @JsonProperty(UPDATE_ID)
    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    @DynamoDBAttribute(attributeName = LAYER)
    @JsonProperty(LAYER)
    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    @DynamoDBAttribute(attributeName = STATE)
    @JsonProperty(STATE)
    @DynamoDBTypeConvertedEnum
    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    @DynamoDBAttribute(attributeName = TIMESTAMP)
    @JsonProperty(TIMESTAMP)
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute(attributeName = MESSAGE)
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    @DynamoDBIgnore
    @JsonIgnore
    public String getBareTableName() {
        return TABLE_NAME;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contextLayer == null) ? 0 : contextLayer.hashCode());
        result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LatestStateEntity other = (LatestStateEntity) obj;
        if (contextLayer == null) {
            if (other.contextLayer != null)
                return false;
        } else if (!contextLayer.equals(other.contextLayer))
            return false;
        if (objectId == null) {
            if (other.objectId != null)
                return false;
        } else if (!objectId.equals(other.objectId))
            return false;
        return true;
    }
    
}
