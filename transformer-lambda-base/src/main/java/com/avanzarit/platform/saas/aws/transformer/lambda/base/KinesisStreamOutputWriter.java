package com.avanzarit.platform.saas.aws.transformer.lambda.base;

import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.kinesis.KinesisBatchWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class KinesisStreamOutputWriter<O> {
    private static final Logger LOGGER = LogManager.getLogger(KinesisStreamOutputWriter.class);
    private CmwContext cmwContext;
    private KinesisBatchWriter writer;

    public KinesisStreamOutputWriter(CmwContext cmwContext, KinesisBatchWriter writer) {
        this.cmwContext = cmwContext;
        this.writer = writer;
    }

    public void process(O outputEntity) {
        String partitionKey = getPartitionKey(outputEntity);
        LOGGER.debug("Handled record with keys: {}", partitionKey);

        PutRecordsRequestEntry requestEntry = writer.createPutRecordsRequestEntry(cmwContext, partitionKey, outputEntity);
        writer.write(cmwContext, requestEntry);
        writer.flush(cmwContext);
        LOGGER.debug("done");
    }

    public abstract String getPartitionKey(O outputEntity);

    public String getStreamName() {
        return writer.getStreamNameWithoutPrefix();
    }
}
