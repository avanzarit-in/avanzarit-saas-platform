package com.avanzarit.platform.saas.aws.dynamo;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.avanzarit.platform.saas.aws.dynamo.exception.RepositoryException;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DynamoDbRepository is an abstract class containing a lot of common functionality that can be reused across
 * repositories.
 *
 * @param <T> An entity class of type {@link DynamoEntity} that will be used for persisting to and reading from
 *            DynamoDb.
 */
public abstract class DynamoDbRepository<T> {

    private static final Logger LOGGER = LogManager.getLogger(DynamoDbRepository.class);

    protected AmazonDynamoDB dynamo;
    private Class<T> entityClass;
    private DynamoBatchWriter batchWriter;

    /**
     * Creates a new DynamoDbRepository for the given entity class.
     */
    public DynamoDbRepository(AmazonDynamoDB dynamo, Class<T> entityClass) {
        this.dynamo = dynamo;
        this.entityClass = entityClass;
    }

    /**
     * Creates a new DynamoDbRepository for the given entity class and enables writing items in
     * batch.
     */
    public DynamoDbRepository(AmazonDynamoDB dynamo, Class<T> entityClass,
                              DynamoBatchWriter batchWriter) {
        this.dynamo = dynamo;
        this.entityClass = entityClass;
        this.batchWriter = batchWriter;
    }

    /**
     * Gets the {@link DynamoDBMapper} for the given table.
     */
    public DynamoDBMapper getMapper(String tableName) {
        DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride.withTableNameReplacement(tableName))
                .build();

        return new DynamoDBMapper(dynamo, config);
    }

    public boolean isBatchWriterAvailable() {
        return batchWriter != null;
    }

    /**
     * Puts the given entity in the DynamoDb table using the {@link DynamoBatchWriter}.
     */
    public void putInBatch(CmwContext cmwContext, String tableName, T entity) {
        if (batchWriter == null) {
            throw new RuntimeException("Batch writer not available.");
        }

        batchWriter.writeItem(cmwContext, tableName, entity);
    }

    /**
     * Puts all given entities in the DynamoDb table using the {@link DynamoBatchWriter}.
     */
    public void putInBatch(CmwContext cmwContext, String tableName, Collection<T> entities) {
        if (batchWriter == null) {
            throw new RuntimeException("Batch writer not available.");
        }

        batchWriter.writeItems(cmwContext, tableName, entities);
    }

    /**
     * Puts the given entity into the DynamoDb table.
     */
    public void put(String tableName, T entity) {
        DynamoDBMapper mapper = getMapper(tableName);
        mapper.save(entity);
    }

    /**
     * Sends a put request to DynamoDb.
     */
    public void put(PutItemRequest putItemRequest) {
        try {
            LOGGER.debug(
                    "Put item: " + putItemRequest.getItem()
                            + " in table: " + putItemRequest.getTableName()
            );
            dynamo.putItem(putItemRequest);
        } catch (ConditionalCheckFailedException ccfe) {
            LOGGER.error("Put failed: Condition not met");
            throw new RepositoryException(ccfe);
        } catch (AmazonServiceException ase) {
            LOGGER.error(
                    "Put failed: Type: " + ase.getErrorType().name()
                            + " Message: " + ase.getErrorMessage()
            );
            throw new RepositoryException(ase);
        } catch (AmazonClientException | DynamoDBMappingException ex) {
            LOGGER.error("Put failed: " + ex.toString());
            throw new RepositoryException(ex);
        }
    }

    /**
     * Gets the given entity from the table. The entity provided must have values provided for both
     * the hash and range key attributes (if applicable)
     *
     * @return The actual fully populated entity retrieved from DynamoDb.
     */
    public T get(String tableName, T entity) {
        DynamoDBMapper mapper = getMapper(tableName);
        return mapper.load(entity);
    }

    /**
     * Deletes the given entity from the table.
     */
    public void delete(String tableName, T entity) {
        DynamoDBMapper mapper = getMapper(tableName);
        mapper.delete(entity);
    }

    /**
     * Loads the given set of entities from the table using DynamoDb batch get requests.
     *
     * @return A list containing the entities requested, if they exist in the DynamoDb table.
     */
    public List<T> batchGet(String tableName, Collection<T> entities) {
        LOGGER.debug("Batch getting entities from table: " + tableName);
        DynamoDBMapper mapper = getMapper(tableName);

        List<T> results = new ArrayList<>();
        if (entities.size() > 0) {
            List<Object> objects = mapper.batchLoad(new ArrayList<>(entities)).get(tableName);
            if (objects != null) {
                results.addAll((List<T>) (Object) objects);
            }
        }
        LOGGER.debug("Returning " + results.size() + " results");
        return results;
    }

    /**
     * Saves all entities in the given list to the table using DynamoDb batch writes.
     */
    public void batchSave(String tableName, List<T> entities) {
        DynamoDBMapper mapper = getMapper(tableName);

        mapper.batchSave(entities);
    }

    /**
     * Deletes all entities in the given list from the table using DynamoDb batch requests.
     */
    public void batchDelete(String tableName, List<T> entities) {
        DynamoDBMapper mapper = getMapper(tableName);

        mapper.batchDelete(entities);
    }

    /**
     * Queries the given table using a query expression.
     *
     * @return A list containing all the entities that matched the given query expression.
     */
    public List<T> query(String tableName, DynamoDBQueryExpression<T> queryExpression) {
        DynamoDBMapper mapper = getMapper(tableName);

        return mapper.query(entityClass, queryExpression);
    }

    /**
     * Eagerly scans the given table using a scanning expression.
     *
     * @return An eagerly loaded list of results. This means that all results will already have
     * been retrieved from DynamoDb and all results are kept in-memory.
     */
    public List<T> scan(String tableName, DynamoDBScanExpression scanExpression) {
        DynamoDBMapper mapper = getMapper(tableName);

        PaginatedScanList<T> result = mapper.scan(entityClass, scanExpression);
        result.loadAllResults();
        return result;
    }

    /**
     * Updates item in table.
     */
    public void updateItem(UpdateItemRequest updateItemRequest) {
        dynamo.updateItem(updateItemRequest);
    }
}
