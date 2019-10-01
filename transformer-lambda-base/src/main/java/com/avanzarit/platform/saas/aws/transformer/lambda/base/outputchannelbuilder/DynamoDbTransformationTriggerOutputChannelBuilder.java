package com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannelbuilder;

import com.avanzarit.platform.saas.aws.dynamo.DynamoDbRepository;
import com.avanzarit.platform.saas.aws.dynamo.StructuredTableNameParser;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerBuildingFailedException;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannel;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.TransformationTriggerOutputChannelBuilder;
import com.avanzarit.platform.saas.aws.transformer.lambda.base.outputchannel.DynamoDbTransformationTriggerOutputChannel;

/**
 * Builder class that can be used to build a {@link TransformationTriggerOutputChannel}. It is used to provide a small
 * DSL to make it easier to define output channels.
 *
 * @param <I> The type of input entity that the output channel will process.
 * @param <O> The type of output entity that the output channel will generate.
 */
public class DynamoDbTransformationTriggerOutputChannelBuilder<I, O> extends TransformationTriggerOutputChannelBuilder<I, O> {
    private StructuredTableNameParser tableNameParser;
    private DynamoDbRepository<O> outputRepository;

    public DynamoDbTransformationTriggerOutputChannelBuilder() {
        super();
    }

    /**
     * The {@link StructuredTableNameParser} instance that the output channel will use to generate and parse table
     * names.
     */
    public DynamoDbTransformationTriggerOutputChannelBuilder<I, O> withTableNameParser(
            StructuredTableNameParser tableNameParser) {
        this.tableNameParser = tableNameParser;
        return this;
    }

    /**
     * The {@link DynamoDbRepository} class to use for storing the output entity.
     */
    public DynamoDbTransformationTriggerOutputChannelBuilder<I, O> withOutputRepository(
            DynamoDbRepository<O> outputRepository) {
        this.outputRepository = outputRepository;
        return this;
    }

    /**
     * Builds the final output channel based on the configuration specified using the other methods in this class.
     *
     * @return The actual output channel object.
     */
    public TransformationTriggerOutputChannel<I, O> build() {
        super.build();
        if (tableNameParser == null && outputRepository != null) {
            throw new TransformationTriggerBuildingFailedException("No table name parser was specified");
        }

        if (outputRepository == null) {
            throw new TransformationTriggerBuildingFailedException(
                    "No output DynamoDB Repository was specified"
            );
        }

        return new DynamoDbTransformationTriggerOutputChannel<>(getName(), getTransformer(), getValidator(), tableNameParser, outputRepository);
    }
}
