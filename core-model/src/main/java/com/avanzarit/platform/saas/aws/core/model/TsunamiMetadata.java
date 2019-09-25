package com.avanzarit.platform.saas.aws.core.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@DynamoDBDocument
public  class TsunamiMetadata {
    public static final String COL_ID = "id";
    public static final String COL_SERIALIZERS = "serializers";

    private String id;
    private List<String> serializers;

    @JsonProperty(COL_ID)
    @DynamoDBAttribute(attributeName = COL_ID)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty(COL_SERIALIZERS)
    @DynamoDBAttribute(attributeName = COL_SERIALIZERS)
    public List<String> getSerializers() {
        return serializers;
    }

    public void setSerializers(List<String> serializers) {
        this.serializers = serializers;
    }
}
