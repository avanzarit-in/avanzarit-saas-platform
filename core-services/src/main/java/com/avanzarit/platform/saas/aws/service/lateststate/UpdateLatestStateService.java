package com.avanzarit.platform.saas.aws.service.lateststate;

import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.DateTimeUtils;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import com.avanzarit.platform.saas.aws.dynamo.DynamoBatchWriter;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;

import java.util.Date;

public class UpdateLatestStateService {
    
    private StructuredTableNameParser tableNameParser;
    private LatestStateRepository latestStateRepository;
    private DynamoBatchWriter latestStateBatchWriter;

    public UpdateLatestStateService(StructuredTableNameParser tableNameParser, LatestStateRepository latestStateRepository, DynamoBatchWriter latestStateBatchWriter) {
        this.tableNameParser = tableNameParser;
        this.latestStateRepository = latestStateRepository;
        this.latestStateBatchWriter = latestStateBatchWriter;
    }

    public void updateState(CmwContext cmwContext, UpdateInfo updateInfo, States state) {
        updateState(cmwContext, cmwContext.getLayer(), updateInfo, state);
    }

    public void updateState(CmwContext cmwContext, UpdateInfo updateInfo, States state, String message) {
        updateState(cmwContext, cmwContext.getLayer(), updateInfo, state, message);
    }

    public void updateState(CmwContext cmwContext, String layer, UpdateInfo updateInfo, States state) {
        updateState(cmwContext, layer, updateInfo, state, null);
    }

    private void updateState(CmwContext cmwContext, String layer, UpdateInfo updateInfo, States state, String message) {
        String objectId = updateInfo.getObjectId();
        String context = updateInfo.getContext();
        String updateId = updateInfo.getUpdateId();
        String timestamp = DateTimeUtils.getFormattedTimestamp(new Date());

        LatestStateEntity entity = new LatestStateEntity(objectId, context, updateId, layer, state, timestamp);
        entity.setMessage(message);

        String tableName = tableNameParser.createTableName(cmwContext.getPrefix(), LatestStateEntity.TABLE_NAME);
        latestStateRepository.putInBatch(cmwContext, tableName, entity);
    }
    
    public void flushLatestStates(CmwContext cmwContext) {
        latestStateBatchWriter.flush(cmwContext);
    }

}
