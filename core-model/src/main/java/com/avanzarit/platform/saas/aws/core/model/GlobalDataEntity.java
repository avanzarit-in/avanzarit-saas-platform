package com.avanzarit.platform.saas.aws.core.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@DynamoDBTable(tableName = "")
public abstract class GlobalDataEntity extends CoreEntity {
    public static final String COL_OBJECTID = "object_id";
    public static final String COL_UPDATEID = "update_id";

    private String objectId;
    private String updateId;

    public GlobalDataEntity() {
    }

    @DynamoDBIgnore
    @JsonIgnore
    public String getContext() {
        throw new UnsupportedOperationException("Method getContext() for a Global Entity is not supported");
    }

    public GlobalDataEntity(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public Object getEntityBody() {
        return null;
    }

    @JsonProperty(COL_OBJECTID)
    @DynamoDBHashKey(attributeName = COL_OBJECTID)
    @Override
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @JsonProperty(COL_UPDATEID)
    @DynamoDBAttribute(attributeName = COL_UPDATEID)
    @Override
    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GlobalDataEntity that = (GlobalDataEntity) o;

        return objectId != null ? objectId.equals(that.objectId) : that.objectId == null;
    }

    @Override
    public int hashCode() {
        return objectId != null ? objectId.hashCode() : 0;
    }
}