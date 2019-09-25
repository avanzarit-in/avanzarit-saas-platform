package com.avanzarit.platform.saas.aws.core.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.avanzarit.platform.saas.aws.dynamo.DynamoUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

@DynamoDBTable(tableName = "")
public abstract class DataEntity extends CoreEntity {
    public static final String COL_OBJECTID_CONTEXT = "objectId_context";
    public static final String COL_OBJECTID = "object_id";
    public static final String COL_CONTEXT = "context";
    public static final String COL_UPDATEID = "update_id";

    private String objectIdContext;
    private String objectId;   // HC-3424
    private String context;    // nl_NL, en_UK, en_AA, ROOT
    private String updateId;   // 2015-08-03T15:12:15.123Z_HC-3424_nl_NL

    public DataEntity() {
    }

    public DataEntity(String objectIdContext) {
        this.objectIdContext = objectIdContext;

        String[] objectIdAndContext = DynamoUtils.fromKey(objectIdContext);
        this.objectId = objectIdAndContext[0];
        this.context = objectIdAndContext[1];
    }

    public DataEntity(String objectId, String context) {
        this.objectIdContext = DynamoUtils.toKey(objectId, context);
        this.objectId = objectId;
        this.context = context;
    }

    public DataEntity(String objectId, String context, String updateId) {
        this(objectId, context);

        this.updateId = updateId;
    }

    @JsonProperty(COL_OBJECTID_CONTEXT)
    @DynamoDBHashKey(attributeName = COL_OBJECTID_CONTEXT)
    public String getObjectIdContext() {
        return objectIdContext;
    }

    public void setObjectIdContext(String objectIdContext) {
        this.objectIdContext = objectIdContext;

        if (objectIdContext != null) {
            String[] objectIdAndContext = DynamoUtils.fromKey(objectIdContext);

            if (objectIdAndContext.length == 2) {
                this.objectId = objectIdAndContext[0];
                this.context = objectIdAndContext[1];
            }
        }
    }

    @JsonProperty(COL_OBJECTID)
    @DynamoDBAttribute(attributeName = COL_OBJECTID)
    @Override
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;

        if (context != null) {
            this.objectIdContext = DynamoUtils.toKey(objectId, context);
        }
    }

    @JsonProperty(COL_CONTEXT)
    @DynamoDBAttribute(attributeName = COL_CONTEXT)
    @Override
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;

        if (objectId != null) {
            this.objectIdContext = DynamoUtils.toKey(objectId, context);
        }
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

        DataEntity that = (DataEntity) o;

        return java.util.Objects.equals(objectIdContext, that.objectIdContext)
                && java.util.Objects.equals(objectId, that.objectId)
                && java.util.Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        int result = objectIdContext != null ? objectIdContext.hashCode() : 0;
        result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
        result = 31 * result + (context != null ? context.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataEntity{"
                + "objectIdContext='" + objectIdContext + '\''
                + ", objectId='" + objectId + '\''
                + ", context='" + context + '\''
                + ", updateId='" + updateId + '\''
                + '}';
    }
}
