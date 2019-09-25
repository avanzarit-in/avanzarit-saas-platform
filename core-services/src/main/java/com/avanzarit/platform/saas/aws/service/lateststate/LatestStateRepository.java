package com.avanzarit.platform.saas.aws.service.lateststate;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.avanzarit.platform.saas.aws.dynamo.DynamoBatchWriter;
import com.avanzarit.platform.saas.aws.dynamo.DynamoDbRepository;

public class LatestStateRepository extends DynamoDbRepository<LatestStateEntity> {

    public LatestStateRepository(AmazonDynamoDB dynamo, DynamoBatchWriter batchWriter) {
        super(dynamo, LatestStateEntity.class, batchWriter);
    }
}
