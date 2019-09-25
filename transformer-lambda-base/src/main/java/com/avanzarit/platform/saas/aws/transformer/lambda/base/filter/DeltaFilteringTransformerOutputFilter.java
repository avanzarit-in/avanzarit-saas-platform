package com.avanzarit.platform.saas.aws.transformer.lambda.base.filter;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.service.deltafiltering.DeltaDetectionService;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformerOutputFilter;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import com.avanzarit.platform.saas.aws.util.delta.DeltaMetric;
import com.avanzarit.platform.saas.aws.dynamo.DynamoDbRepository;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;

import static com.avanzarit.platform.saas.aws.util.delta.DeltaValue.NEW;
import static com.avanzarit.platform.saas.aws.util.delta.DeltaValue.SAME;
import static com.avanzarit.platform.saas.aws.util.delta.DeltaValue.UPDATED;

/**
 * Filters the transformed output based on whether the content has actually changed or not.
 */
public class DeltaFilteringTransformerOutputFilter<I extends CoreEntity, O extends CoreEntity>
        implements TransformerOutputFilter<I, O> {

    private StructuredTableNameParser tableNameParser;
    private DynamoDbRepository<O> outputEntityRepository;
    private DeltaDetectionService deltaDetectionService;

    public DeltaFilteringTransformerOutputFilter(StructuredTableNameParser tableNameParser,
                                                 DynamoDbRepository<O> outputEntityRepository,
                                                 DeltaDetectionService deltaDetectionService) {
        this.tableNameParser = tableNameParser;
        this.outputEntityRepository = outputEntityRepository;
        this.deltaDetectionService = deltaDetectionService;
    }

    @Override
    public boolean filteredOut(CmwContext cmwContext, UpdateInfo updateInfo, I newInputEntity, O outputEntity) {
        boolean result = false;

        String tableName = tableNameParser.createTableName(
                cmwContext.getPrefix(), cmwContext.getLayer(), outputEntity.getBareTableName()
        );

        O oldEntity = outputEntityRepository.get(tableName, outputEntity);

        if (oldEntity != null) {
            result = !deltaDetectionService.detectsDelta(
                    cmwContext, oldEntity, outputEntity
            );
        } else {
            cmwContext.putMetrics(new DeltaMetric(outputEntity.getBareTableName(), 1.0, NEW));
        }

        if (result) {
            cmwContext.logDiscarded(updateInfo, outputEntity.getBareTableName() + " was not changed");
            cmwContext.putMetrics(new DeltaMetric(outputEntity.getBareTableName(), 1.0, SAME));
        } else {
            cmwContext.putMetrics(new DeltaMetric(outputEntity.getBareTableName(), 1.0, UPDATED));
        }

        return result;
    }
}
