package com.avanzarit.platform.saas.aws.kinesis.kinesis;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.kinesis.model.PutRecordsResultEntry;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The KinesisBatchWriter offers functionality for writing items to Kinesis in batch without having to deal with
 * limitations inherent to AWS Kinesis batch requests.
 */
public class KinesisBatchWriter {
    private static final Logger LOGGER = LogManager.getLogger(KinesisBatchWriter.class);

    public static final int MAX_BATCH_RECORDS = 100;
    public static final long MAX_BATCH_SIZE = 1048576;     //1.0 MB
    private static final int MAX_WAIT_INTERVAL = 10000;
    private static final int MIN_WAIT_INTERVAL = 1000;

    private PutRecordsRequestEntryFactory putRecordsRequestEntryFactory;
    private AmazonKinesis kinesisClient;
    private String streamNameWithoutPrefix;
    private int maxRetries;

    private Map<String, PutRecordsRequestEntry> recordCache;
    private long recordCacheSize = 0;

    public KinesisBatchWriter(PutRecordsRequestEntryFactory putRecordsRequestEntryFactory,
                              AmazonKinesis kinesisClient, String streamNameWithoutPrefix) {
        this.putRecordsRequestEntryFactory = putRecordsRequestEntryFactory;
        this.kinesisClient = kinesisClient;
        this.streamNameWithoutPrefix = streamNameWithoutPrefix;
        this.maxRetries = 8;

        this.recordCache = new HashMap<>();
    }

    public KinesisBatchWriter(PutRecordsRequestEntryFactory putRecordsRequestEntryFactory,
                              AmazonKinesis kinesisClient, String streamNameWithoutPrefix, int maxRetries) {
        this.putRecordsRequestEntryFactory = putRecordsRequestEntryFactory;
        this.kinesisClient = kinesisClient;
        this.streamNameWithoutPrefix = streamNameWithoutPrefix;
        this.maxRetries = maxRetries;

        this.recordCache = new HashMap<>();
    }

    /**
     * Provides a set of records to the batch writer in order to enqueue it for being written in batch to Kinesis on the
     * next flush. A flush occurs either by manually calling {@link #flush(CmwContext)} or automatically when the amount
     * of queued up data reaches {@value MAX_BATCH_SIZE} bytes or the number of queued up records reaches
     * {@value MAX_BATCH_RECORDS}.
     *
     * @param records The records that will be enqueued in order to write it to Kinesis.
     */
    public void write(CmwContext cmwContext, PutRecordsRequestEntry[] records) {
        for (PutRecordsRequestEntry record : records) {
            write(cmwContext, record);
        }
    }

    /**
     * Provides a record to the batch writer in order to enqueue it for being written in batch to Kinesis on the next
     * flush. A flush occurs either by manually calling {@link #flush(CmwContext)} or automatically when the amount of
     * queued up data reaches {@value MAX_BATCH_SIZE} bytes or the number of queued up records reaches
     * {@value MAX_BATCH_RECORDS}.
     *
     * @param record The record that will be enqueued in order to write it to Kinesis.
     */
    public void write(CmwContext cmwContext, PutRecordsRequestEntry record) {
        if (record.getData() != null) {
            removeFromCache(record.getPartitionKey());

            long recordSize = record.getData().array().length;
            if (recordCacheSize + recordSize > MAX_BATCH_SIZE) {
                sendRecordsInBatch(cmwContext);
            }

            recordCache.put(record.getPartitionKey(), record);
            recordCacheSize += recordSize;

            if (recordCache.size() == MAX_BATCH_RECORDS) {
                sendRecordsInBatch(cmwContext);
            }
        }
    }

    /**
     * Writes any items still waiting to be written, in batch, to AWS Kinesis.
     */
    public void flush(CmwContext cmwContext) {
        sendRecordsInBatch(cmwContext);
    }

    /**
     * Removes any items that were waiting for the next batch to be written.
     */
    public void clear(CmwContext cmwContext) {
        cmwContext.logDebug("Clearing kinesis batch writer: " + recordCache.size() + " items were available");
        recordCache.clear();
        recordCacheSize = 0;
    }

    /**
     * Creates a PutRecordsRequestEntry (see
     * {@link PutRecordsRequestEntryFactory#createPutRecordsRequestEntry(CmwContext, String, Object)}).
     */
    public PutRecordsRequestEntry createPutRecordsRequestEntry(CmwContext cmwContext, String partitionKey,
                                                               Object message) {
        return putRecordsRequestEntryFactory.createPutRecordsRequestEntry(cmwContext, partitionKey, message);
    }

    private void removeFromCache(String partitionKey) {
        PutRecordsRequestEntry old = recordCache.remove(partitionKey);

        if (old != null) {
            recordCacheSize -= old.getData().array().length;
        }
    }

    private void sendRecordsInBatch(CmwContext cmwContext) {
        sendRecordsInBatch(cmwContext, 0);
    }

    private void sendRecordsInBatch(CmwContext cmwContext, int retries) {
        cmwContext.logInfo("putting l2 in sendRecordsInBatch ");
        LOGGER.debug("records size: " + recordCacheSize);
        LOGGER.debug("record cache: " + recordCache);

        if (!recordCache.isEmpty()) {
            String streamName = cmwContext.getPrefix() + "_" + streamNameWithoutPrefix;
            List<PutRecordsRequestEntry> records = new ArrayList<>(recordCache.values());
            cmwContext.logDebug("records " + records.stream());
            cmwContext.logDebug("records " + records.stream().toString());
            PutRecordsRequest putRecordsRequest = new PutRecordsRequest()
                    .withStreamName(streamName)
                    .withRecords(records);

            PutRecordsResult result = kinesisClient.putRecords(putRecordsRequest);

            cmwContext.logDebug("put success " + result.getRecords());
            cmwContext.logDebug("put success " + result.getRecords().stream());

            if (result.getFailedRecordCount() > 0) {
                cmwContext.logWarning("Failed to put all records in batch");
                retryFailedRecords(cmwContext, records, result, retries);
            }

            recordCache.clear();
            recordCacheSize = 0;
        }
    }

    private void retryFailedRecords(CmwContext cmwContext, List<PutRecordsRequestEntry> records,
                                    PutRecordsResult result, int retries) {
        Map<String, PutRecordsRequestEntry> failedRecords = new HashMap<>();
        List<PutRecordsResultEntry> resultRecords = result.getRecords();

        String errorCode = null;
        String errorMessage = null;

        for (int i = 0; i < resultRecords.size(); i++) {
            if (StringUtils.isNotBlank(resultRecords.get(i).getErrorCode())) {
                errorCode = resultRecords.get(i).getErrorCode();
                errorMessage = resultRecords.get(i).getErrorMessage();

                PutRecordsRequestEntry record = records.get(i);
                failedRecords.put(record.getPartitionKey(), record);
            }
        }

        if (retries == maxRetries) {
            handleFailure(cmwContext, errorCode, errorMessage);
        }

        cmwContext.logWarning("Not all records failed to write, retrying " + recordCache.size() + " failed records...");
        recordCache = failedRecords;

        //Wait a little bit longer before each retry:
        //http://docs.aws.amazon.com/general/latest/gr/api-retries.html
        long waitTime = getWaitTime(retries);

        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KinesisBatchWriteFailedException(e);
        }

        sendRecordsInBatch(cmwContext, retries + 1);
    }

    private long getWaitTime(int retryCount) {
        long waitTime = getWaitTimeExp(retryCount);

        waitTime = Math.min(waitTime, MAX_WAIT_INTERVAL);
        waitTime = Math.max(waitTime, MIN_WAIT_INTERVAL);

        return waitTime;
    }

    private long getWaitTimeExp(int retryCount) {
        long waitTime = ((long) Math.pow(2, retryCount) * 100L);
        return waitTime;
    }

    private void handleFailure(CmwContext cmwContext, String errorCode, String errorMessage) {
        String message = "An error occurred while sending records to Kinesis - Code: '" + errorCode + "', Message: '"
                + errorMessage + "'";

        cmwContext.logError(message + "\n" + recordCache);

        throw new KinesisBatchWriteFailedException(message);
    }

    public String getStreamNameWithoutPrefix() {
        return streamNameWithoutPrefix;
    }
}
