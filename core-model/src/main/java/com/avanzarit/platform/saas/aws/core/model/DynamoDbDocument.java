package com.avanzarit.platform.saas.aws.core.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@DynamoDBDocument
public class DynamoDbDocument {
}
