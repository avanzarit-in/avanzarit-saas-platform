package com.avanzarit.platform.saas.aws.solution.digitalsignature.model.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.avanzarit.platform.saas.aws.dynamo.DynamoDbRepository;
import com.avanzarit.platform.saas.aws.solution.digitalsignature.model.CreditNoteEntity;

public class CreditNoteRepository extends DynamoDbRepository<CreditNoteEntity> {
    public CreditNoteRepository(AmazonDynamoDB dynamo) {
        super(dynamo, CreditNoteEntity.class);
    }
}
