package com.avanzarit.platform.saas.aws.lambda.eventhandler.impl;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.avanzarit.platform.saas.aws.dynamo.DynamoEntity;
import com.avanzarit.platform.saas.aws.lambda.EntityTrigger;
import com.avanzarit.platform.saas.aws.lambda.RecordMappingFailedException;
import com.avanzarit.platform.saas.aws.lambda.eventhandler.KinesisEventHandler;
import com.avanzarit.platform.saas.aws.lambda.model.LambdaRouterRecord;
import com.avanzarit.platform.saas.aws.lambda.processors.impl.DynamoDbStreamRecordProcessor;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/**
 * Responsible for handling events on tables.
 */
public class DynamoDbKinesisStreamEventHandler implements KinesisEventHandler {
    private static final Logger LOGGER = LogManager.getLogger(DynamoDbKinesisStreamEventHandler.class);

    private KinesisEventRecordMapper recordMapper;
    private DynamoDbStreamRecordProcessor dynamoStreamRecordProcessor;

    public DynamoDbKinesisStreamEventHandler() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.recordMapper = new KinesisEventRecordMapper(objectMapper);

        this.dynamoStreamRecordProcessor = new DynamoDbStreamRecordProcessor();
    }

    /**
     * Handles the given event.
     *
     * @param event The Kinesis event that needs to be handled.
     */
    public void handleEvent(CmwContext cmwContext, KinesisEvent event) {
        for (KinesisEvent.KinesisEventRecord record : event.getRecords()) {
            LOGGER.debug(
                    "Event: " + record.getEventID()
                            + " " + record.getEventSource()
                            + " " + record.getEventName()
            );

            LambdaRouterRecord dynamoStreamsRecord = recordMapper.map(record, LambdaRouterRecord.class);

            if (dynamoStreamsRecord != null) {
                dynamoStreamRecordProcessor.process(cmwContext, dynamoStreamsRecord);
            }
        }
    }

    /**
     * Adds an entity trigger for a table to the {@link DynamoDbStreamRecordProcessor}.
     */
    public void addTrigger(String tableName, EntityTrigger<? extends DynamoEntity> trigger) {
        dynamoStreamRecordProcessor.addTrigger(tableName, trigger);
    }

    class KinesisEventRecordMapper {
        private ObjectMapper mapper;

        /**
         * Creates a new KinesisEventRecordMapper.
         */
        KinesisEventRecordMapper(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        /**
         * Maps a {@link com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord} to a Java object.
         *
         * @param record The record that needs to mapped to the object.
         * @param cls    The target class of the mapping. This class will need to be annotated with Jackson annotations.
         * @return An instance of 'cls' containing the event record data.
         */
        public <T> T map(KinesisEvent.KinesisEventRecord record, Class<T> cls) {
            if (record != null) {
                KinesisEvent.Record kinesis = record.getKinesis();

                if (kinesis != null) {
                    ByteBuffer data = kinesis.getData();

                    if (data != null) {
                        return map(data.array(), cls);
                    }
                }
            }

            return null;
        }

        private <T> T map(byte[] data, Class<T> cls) {
            try {
                return mapper.readValue(data, cls);
            } catch (IOException e) {
                throw new RecordMappingFailedException("Mapping the Kinesis record failed", e);
            }
        }

        /**
         * Base64 decodes, decompresses and maps a Kinesis stream payload to a Java object.
         *
         * @param data The payload that needs to be processed.
         * @param cls  The class that the payload needs to be mapped to.
         * @return An instance of the class defined by the 'cls' parameter containing the payload information.
         */
        public <T> T decompressAndMap(String data, Class<T> cls) {
            try {
                byte[] kinesisData = Base64.getDecoder().decode(data);
                byte[] uncompressedPayload = uncompress(kinesisData);

                return map(uncompressedPayload, cls);
            } catch (IOException e) {
                throw new RecordMappingFailedException("Mapping the Kinesis record failed", e);
            }
        }

        private byte[] uncompress(byte[] compressedData) throws IOException {
            byte[] buffer = new byte[1024];

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(compressedData))) {
                    int len;
                    while ((len = gzis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
                out.flush();
                return out.toByteArray();
            }
        }
    }
}
