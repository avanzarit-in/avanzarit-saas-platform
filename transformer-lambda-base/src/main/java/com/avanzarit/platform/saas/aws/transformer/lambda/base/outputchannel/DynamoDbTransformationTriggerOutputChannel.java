package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannel;

import com.avanzarit.platform.saas.aws.core.validation.Validator;
import com.avanzarit.platform.saas.aws.dynamo.DynamoDbRepository;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTrigger;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.Transformer;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

/**
 * Represents a potential output channel for input that enters a {@link TransformationTrigger}. Every output channel
 * defines its own transformation and filtering logic.
 *
 * @param <I> The type of input entities that the output channel processes.
 * @param <O> The type of output entities that the output channel generates.
 */
public class DynamoDbTransformationTriggerOutputChannel<I, O> extends TransformationTriggerOutputChannel<I, O> {
    private StructuredTableNameParser tableNameParser;
    private Validator validator;
    private DynamoDbRepository<O> outputRepository;

    public DynamoDbTransformationTriggerOutputChannel(String name, Transformer<I, O> transformer,
                                                      Validator validator,
                                                      StructuredTableNameParser tableNameParser,
                                                      DynamoDbRepository<O> outputRepository) {
        super(name, validator, transformer);
        this.tableNameParser = tableNameParser;
        this.outputRepository = outputRepository;
    }

    @Override
    public void saveOutputEntity(CmwContext cmwContext, UpdateInfo updateInfo, O outputEntity) {

        String tableName = tableNameParser.createTableName(
                cmwContext.getPrefix(), cmwContext.getLayer(), outputEntity.toString()
        );

        if (outputRepository.isBatchWriterAvailable()) {
            outputRepository.putInBatch(cmwContext, tableName, outputEntity);
        } else {
            outputRepository.put(tableName, outputEntity);
        }
        cmwContext.logInfo(updateInfo, "Stored " + outputEntity.toString() + " in " + tableName);

    }
}
