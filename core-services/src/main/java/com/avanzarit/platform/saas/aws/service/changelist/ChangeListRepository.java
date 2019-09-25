package com.avanzarit.platform.saas.aws.service.changelist;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.avanzarit.platform.saas.aws.dynamo.DynamoBatchWriter;
import com.avanzarit.platform.saas.aws.dynamo.DynamoDbRepository;

public class ChangeListRepository extends DynamoDbRepository<ChangeListEntity> {
    public ChangeListRepository(AmazonDynamoDB dynamo, DynamoBatchWriter batchWriter) {
        super(dynamo, ChangeListEntity.class, batchWriter);
    }
}
