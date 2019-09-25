package com.avanzarit.platform.saas.aws.transformer.lambda.base.filter;

import com.avanzarit.platform.saas.aws.core.model.CoreEntity;
import com.avanzarit.platform.saas.aws.service.deltafiltering.DeltaDetectionService;
import com.avanzarit.platform.saas.aws.service.lateststate.States;
import com.avanzarit.platform.saas.aws.service.lateststate.UpdateLatestStateService;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;
import com.avanzarit.platform.saas.aws.dynamo.DynamoDbRepository;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;

/**
 * Specialized version of the {@link DeltaFilteringTransformerOutputFilter} that updates the latest product state to
 * DISCARDED if a product is being filtered out.
 */
public class ProductDeltaFilteringTransformerOutputFilter<I extends CoreEntity, O extends CoreEntity>
        extends DeltaFilteringTransformerOutputFilter<I, O> {

    private UpdateLatestStateService latestStateService;

    public ProductDeltaFilteringTransformerOutputFilter(StructuredTableNameParser tableNameParser,
                                                        DynamoDbRepository<O> outputEntityRepository,
                                                        DeltaDetectionService deltaDetectionService,
                                                        UpdateLatestStateService latestStateService) {
        super(tableNameParser, outputEntityRepository, deltaDetectionService);

        this.latestStateService = latestStateService;
    }

    @Override
    public boolean filteredOut(CmwContext cmwContext, UpdateInfo updateInfo, I newInputEntity, O outputEntity) {
        boolean result = super.filteredOut(cmwContext, updateInfo, newInputEntity, outputEntity);

        if (result) {
            latestStateService.updateState(cmwContext, updateInfo, States.DISCARDED);
        }

        return result;
    }
}
