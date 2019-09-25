package com.avanzarit.platform.saas.aws.core.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


@DynamoDBTable(tableName = "")
public abstract class CoreEntity implements DynamoEntity {
    public static final String COL_RETRY_UPDATE = "retry_update_id";
    public static final String COL_TSUNAMI_METADATA = "tsunami_metadata";

    private String retryUpdate;
    private TsunamiMetadata tsunamiMetadata;

    @DynamoDBIgnore
    @JsonIgnore
    public abstract Object getEntityBody();

    public final boolean hasEntityBody() {
        return getEntityBody() != null;
    }

    public abstract String getContext();

    public abstract String getObjectId();

    public abstract String getUpdateId();

    @JsonProperty(COL_RETRY_UPDATE)
    @DynamoDBAttribute(attributeName = COL_RETRY_UPDATE)
    public String getRetryUpdate() {
        return retryUpdate;
    }

    public void setRetryUpdate(String retryUpdate) {
        this.retryUpdate = retryUpdate;
    }

    @JsonProperty(COL_TSUNAMI_METADATA)
    @DynamoDBAttribute(attributeName = COL_TSUNAMI_METADATA)
    public TsunamiMetadata getTsunamiMetadata() {
        return tsunamiMetadata;
    }

    public void setTsunamiMetadata(TsunamiMetadata tsunamiMetadata) {
        this.tsunamiMetadata = tsunamiMetadata;
    }
}