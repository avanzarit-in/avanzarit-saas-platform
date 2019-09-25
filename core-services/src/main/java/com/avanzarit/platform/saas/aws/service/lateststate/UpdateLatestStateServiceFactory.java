package com.avanzarit.platform.saas.aws.service.lateststate;

import com.avanzarit.platform.saas.aws.dynamo.DynamoBatchWriter;
import com.avanzarit.platform.saas.aws.dynamo.DynamoConstants;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;

public class UpdateLatestStateServiceFactory {
    public static UpdateLatestStateService createLatestStateService(StructuredTableNameParser tableNameParser, DynamoBatchWriter batchWriter) {
        LatestStateRepository latestStateRepository = new LatestStateRepository(DynamoConstants.DYNAMO_CLIENT, batchWriter);
        return new UpdateLatestStateService(tableNameParser, latestStateRepository, batchWriter);
    }
}
