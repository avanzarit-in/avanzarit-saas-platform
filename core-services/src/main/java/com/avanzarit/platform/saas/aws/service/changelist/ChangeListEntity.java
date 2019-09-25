package com.avanzarit.platform.saas.aws.service.changelist;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;

@DynamoDBTable(tableName = ChangeListEntity.TABLE_NAME)
public class ChangeListEntity implements DynamoEntity {
    public static final String TABLE_NAME = "changelist";

    public static final String DAY_GSI_NAME = "day";
    public static final String CONTEXT_DAY_GSI_NAME = "context_day";

    public static final String COL_OBJECTID_CONTEXT_TYPE = "objectId_context_type";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_TYPE = "type";
    public static final String COL_DAY = "day";
    public static final String COL_CONTEXT_DAY = "context_day";
    public static final String COL_TIMESTAMP_OBJECTID_CONTEXT_TYPE = "timestamp_objectId_context_type";
    public static final String COL_UPDATEID = "update_id";
    public static final String COL_CONTEXT = "context";

    private String objectIdContextType;
    private Long timestamp;
    private String type;
    private String day;
    private String contextDay;
    private String timestampObjectIdContextType;
    private String updateId;
    private String context;

    public ChangeListEntity() {
    }

    public ChangeListEntity(String key) {
        setObjectIdContextType(key);
    }

    @JsonProperty(COL_OBJECTID_CONTEXT_TYPE)
    @DynamoDBHashKey(attributeName = COL_OBJECTID_CONTEXT_TYPE)
    public String getObjectIdContextType() {
        return objectIdContextType;
    }

    public void setObjectIdContextType(String objectIdContextType) {
        this.objectIdContextType = objectIdContextType;
    }

    @JsonProperty(COL_TIMESTAMP)
    @DynamoDBAttribute(attributeName = COL_TIMESTAMP)
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty(COL_TYPE)
    @DynamoDBAttribute(attributeName = COL_TYPE)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty(COL_DAY)
    @DynamoDBIndexHashKey(attributeName = COL_DAY, globalSecondaryIndexName = DAY_GSI_NAME)
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
    
    @JsonProperty(COL_CONTEXT_DAY)
    @DynamoDBIndexHashKey(attributeName = COL_CONTEXT_DAY, globalSecondaryIndexName = CONTEXT_DAY_GSI_NAME)
    public String getContextDay() {
        return contextDay;
    }
    
    public void setContextDay(String contextDay) {
        this.contextDay = contextDay;
    }

    @JsonProperty(COL_TIMESTAMP_OBJECTID_CONTEXT_TYPE)
    @DynamoDBIndexRangeKey(attributeName = COL_TIMESTAMP_OBJECTID_CONTEXT_TYPE, globalSecondaryIndexNames = { DAY_GSI_NAME, CONTEXT_DAY_GSI_NAME })
    public String getTimestampObjectIdContextType() {
        return timestampObjectIdContextType;
    }

    public void setTimestampObjectIdContextType(String timestampObjectIdContextType) {
        this.timestampObjectIdContextType = timestampObjectIdContextType;
    }

    @JsonProperty(COL_UPDATEID)
    @DynamoDBAttribute(attributeName = COL_UPDATEID)
    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }
    
    @JsonProperty(COL_CONTEXT)
    @DynamoDBAttribute(attributeName = COL_CONTEXT)
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }

    @JsonIgnore
    @DynamoDBIgnore
    public String getBareTableName() {
        return TABLE_NAME;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((objectIdContextType == null) ? 0 : objectIdContextType.hashCode());
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
        ChangeListEntity other = (ChangeListEntity) obj;
        if (objectIdContextType == null) {
            if (other.objectIdContextType != null)
                return false;
        } else if (!objectIdContextType.equals(other.objectIdContextType))
            return false;
        return true;
    }
    
}
