package com.avanzarit.platform.saas.aws.kinesis.kinesis;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;

/**
 * Factory class used to create {@link PutRecordsRequestEntry} instances in an easy way without having to deal with
 * mapping objects to JSON.
 */
public class PutRecordsRequestEntryFactory {
    private ObjectMapper mapper;

    public PutRecordsRequestEntryFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Creates a new {@link PutRecordsRequestEntry} with the given partiton key and data.
     *
     * @param partitionKey A key used for partitioning data across shards in a Kinesis stream.
     * @param data         The data dat will be written to the Kinesis stream.
     * @return A new {@link PutRecordsRequestEntry} instance with the given partition key and the data object
     * transformed to a JSON representation using Jackson {@link ObjectMapper}.
     */
    public PutRecordsRequestEntry createPutRecordsRequestEntry(CmwContext cmwContext, String partitionKey,
                                                               Object data) {
        try {
            byte[] messageBytes = mapper.writeValueAsBytes(data);

            cmwContext.logInfo(
                    "Creating Kinesis record to put on Router stream (size: " + messageBytes.length + ", body: "
                            + mapper.writeValueAsString(data) + ")");

            return new PutRecordsRequestEntry()
                    .withPartitionKey(partitionKey)
                    .withData(ByteBuffer.wrap(messageBytes));
        } catch (JsonProcessingException e) {
            String message = "An error occurred while creating the PutRecordsRequestEntry";
            throw new PutRecordsRequestEntryCreationFailedException(message, e);
        }
    }
}
