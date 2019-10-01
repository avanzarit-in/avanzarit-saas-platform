package com.avanzarit.platform.saas.aws.service.changelist;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.core.model.DataEntity;
import com.avanzarit.platform.saas.aws.dynamo.DynamoUtils;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.DateTimeUtils;

import java.util.Date;
import java.util.List;

public class ChangeListService {
    private StructuredTableNameParser tableNameParser;
    private ChangeListRepository changeListRepository;

    public ChangeListService(StructuredTableNameParser tableNameParser, ChangeListRepository changeListRepository) {
        this.tableNameParser = tableNameParser;
        this.changeListRepository = changeListRepository;
    }

    public void updateChangeList(CmwContext cmwContext, List<? extends DataEntity> entities) {
        for (DataEntity entity : entities) {
            updateChangeList(cmwContext, entity);
        }
    }

    public void updateChangeList(CmwContext cmwContext, CoreEntity entity) {
        updateChangeList(cmwContext, entity.getObjectId(), entity.getContext(), entity.getUpdateId(), entity.getBareTableName());
    }

    public void updateChangeList(CmwContext cmwContext, CoreEntity entity, String type) {
        updateChangeList(cmwContext, entity.getObjectId(), entity.getContext(), entity.getUpdateId(), type);
    }

    public void updateChangeList(CmwContext cmwContext, String objectId, String context, String updateId, String type) {
        Date timestamp = new Date();
        String day = DateTimeUtils.getFormattedDay(timestamp);

        ChangeListEntity changeListEntity = new ChangeListEntity();
        changeListEntity.setObjectIdContextType(DynamoUtils.toKey(objectId, context, type));
        changeListEntity.setTimestamp(timestamp.getTime());
        changeListEntity.setDay(day);
        changeListEntity.setContextDay(DynamoUtils.toKey(context, day));
        changeListEntity.setType(type);
        changeListEntity.setTimestampObjectIdContextType(
                DynamoUtils.toKey(DateTimeUtils.getFormattedTimestamp(timestamp), objectId, context, type)
        );
        changeListEntity.setUpdateId(updateId);
        changeListEntity.setContext(context);

        String tableName = tableNameParser.createTableName(cmwContext.getPrefix(), cmwContext.getLayer(), ChangeListEntity.TABLE_NAME);
        changeListRepository.putInBatch(cmwContext, tableName, changeListEntity);
    }
}
