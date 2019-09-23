package com.avanzarit.platform.saas.aws.dynamo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.avanzarit.platform.saas.aws.dynamo.exception.FailedBatchWriteException;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The DynamoBatchWriter can be used for easily writing entities to DynamoDb in batch. It transparently handles the
 * limitations inherent to DynamoDb batch requests.
 */
public class DynamoBatchWriter {

    private static final int MAX_DYNAMO_BATCH_SIZE = 25;

    private Map<String, Set<DynamoEntity>> batch = new HashMap<>();
    private int currentNumberOfItems = 0;
    private ObjectMapper mapper = new ObjectMapper();
    private DynamoDB dynamo;

    /**
     * Creates a new batch writer with the DynamoDb client and Jackson object mapper.
     */
    public DynamoBatchWriter(DynamoDB dynamo, ObjectMapper mapper) {
        this.dynamo = dynamo;
        this.mapper = mapper;
        this.mapper.setDateFormat(
                DateTimeUtils.getDateFormat(DateTimeUtils.TIMESTAMP_WITH_MILLISECONDS)
        );
        this.mapper.setSerializationInclusion(Include.NON_NULL);
    }

    /**
     * Adds the given entity to the batch, so that they are written to given DynamoDb table when
     * a flush of the batch happens. Flushes happen either through manual invocation or if the
     * amount of items in the batch exceeds a maximum batch size of {@value MAX_DYNAMO_BATCH_SIZE}.
     */
    public void writeItem(CmwContext cmwContext, String tableName, DynamoEntity entity) {
        write(cmwContext, tableName, entity);
    }

    /**
     * Adds the given entities to the batch, so that they are written to given DynamoDb table when
     * a flush of the batch happens. Flushes happen either through manual invocation or if the
     * amount of items in the batch exceeds a maximum batch size of {@value MAX_DYNAMO_BATCH_SIZE}.
     */
    public void writeItems(CmwContext cmwContext, String tableName,
                           Collection<? extends DynamoEntity> entities) {
        for (DynamoEntity entity : entities) {
            write(cmwContext, tableName, entity);
        }
    }

    /**
     * Writes the items currently stored in the batch to their corresponding DynamoDb tables.
     */
    public void flush(CmwContext cmwContext) {
        if (currentNumberOfItems > 0) {
            List<TableWriteItems> writeItems = new ArrayList<>();
            for (Entry<String, Set<DynamoEntity>> entry : batch.entrySet()) {
                TableWriteItems items = new TableWriteItems(entry.getKey())
                        .withItemsToPut(createItems(entry.getValue()));

                writeItems.add(items);
            }

            try {
                tryWriteItems(cmwContext, writeItems);
            } catch (AmazonServiceException e) {
                handleWriteItemError(cmwContext, writeItems, e);
            }
        }
    }

    private void tryWriteItems(CmwContext cmwContext, List<TableWriteItems> writeItems) {
        TableWriteItems[] tableWriteItems = writeItems.toArray(
                new TableWriteItems[writeItems.size()]
        );
        BatchWriteItemOutcome result = dynamo.batchWriteItem(tableWriteItems);

        if (!result.getUnprocessedItems().isEmpty()) {
            retryWriteItems(cmwContext, result, 0, "");
        }

        batch.clear();
        currentNumberOfItems = 0;
    }

    private void retryWriteItems(CmwContext cmwContext, BatchWriteItemOutcome result,
                                 int numberOfTimesRetried, String errorMessage) {
        Map<String, List<WriteRequest>> unprocessedItems = result.getUnprocessedItems();
        String exceptionString = errorMessage
                + "Batch write " + numberOfTimesRetried
                + " unprocessed items: " + unprocessedItems + "\n";

        if (numberOfTimesRetried < 2) {
            BatchWriteItemOutcome retryResult = dynamo.batchWriteItemUnprocessed(unprocessedItems);

            if (!retryResult.getUnprocessedItems().isEmpty()) {
                retryWriteItems(cmwContext, result, numberOfTimesRetried + 1, exceptionString);
            }
        } else {
            cmwContext.logError("Failed batch write - " + exceptionString);
            throw new FailedBatchWriteException("Failed to write items in batch");
        }
    }

    private void handleWriteItemError(CmwContext cmwContext, List<TableWriteItems> writeItems,
                                      AmazonServiceException e) {
        if (e.getErrorCode().equals("ValidationException")) {
            Map<String, List<Item>> batchItems = new HashMap<>();

            for (TableWriteItems itemsByTable : writeItems) {
                for (Item item : itemsByTable.getItemsToPut()) {
                    if (!batchItems.containsKey(itemsByTable.getTableName())) {
                        batchItems.put(itemsByTable.getTableName(), new ArrayList<>());
                    }

                    batchItems.get(itemsByTable.getTableName()).add(item);
                }
            }

            cmwContext.logError("Failed batch write of items: " + batchItems);
        }

        throw e;
    }

    /**
     * Removes all items from the current batch.
     */
    public void clear(CmwContext cmwContext) {
        cmwContext.logInfo(
                "Clearing Dynamo batch writer: " + currentNumberOfItems
                        + " items were available"
        );

        batch.clear();
        currentNumberOfItems = 0;
    }

    private void write(CmwContext cmwContext, String tableName, DynamoEntity item) {
        if (currentNumberOfItems == MAX_DYNAMO_BATCH_SIZE) {
            flush(cmwContext);
        }

        addItemToBatch(tableName, item);
    }

    private void addItemToBatch(String fullTableName, DynamoEntity item) {
        if (!batch.containsKey(fullTableName)) {
            batch.put(fullTableName, new HashSet<>());
        }

        boolean added = batch.get(fullTableName).add(item);

        //take the latest change.
        if (!added) {
            batch.get(fullTableName).remove(item);
            batch.get(fullTableName).add(item);
        }

        currentNumberOfItems++;
    }

    private List<Item> createItems(Collection<DynamoEntity> entities) {
        try {
            List<Item> result = new ArrayList<>();
            for (DynamoEntity entity : entities) {
                result.add(Item.fromJSON(mapper.writeValueAsString(entity)));
            }
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
