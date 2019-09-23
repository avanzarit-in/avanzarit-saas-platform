package com.avanzarit.platform.saas.aws.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.builder;

/**
 * Utility class containing methods making it easier to interact with DynamoDb from integration
 * tests.
 */
public class TestUtils {

    private static final Logger LOGGER = LogManager.getLogger(TestUtils.class);

    private static AmazonDynamoDB dynamoClient;
    private static DynamoDBMapper mapper;

    /**
     * Initializes DynamoDb by creating a client for DynamoDb local and a DynamoDb mapper
     * associated with this client.
     */
    public static void initLocalDynamo() {
        dynamoClient = createNewAmazonDynamoDbClient();
        mapper = new DynamoDBMapper(dynamoClient);
    }

    public static AmazonDynamoDB getDynamoClient() {
        return dynamoClient;
    }

    /**
     * Creates a table with the given name based on the DynamoDb annotations defined in the given
     * DynamoDb entity class.
     *
     * @param dynamoEntityClass A class containing DynamoDb-specific annotations that will be used
     *                          to create the table.
     */
    public static void ensureTable(String tableName,
                                   Class<?> dynamoEntityClass) {
        if (!isTableAvailable(tableName)) {
            createTable(tableName, dynamoEntityClass);
            waitForTableToBecomeAvailable(tableName);
        }
    }

    /**
     * Creates a table with the given name, hash key and range key. Waits until the table is
     * actually created.
     *
     * @param hashKeyType The type of the hash key, where the type should correspond to a valid
     *                    DynamoDb attribute type.
     */
    public static void ensureTable(String tableName, String hashKeyName, String hashKeyType) {
        if (!isTableAvailable(tableName)) {
            createTable(tableName, hashKeyName, hashKeyType, null, null);
            waitForTableToBecomeAvailable(tableName);
        }
    }

    /**
     * Creates a table with the given name, hash key and range key. Waits until the table is
     * actually created.
     *
     * @param hashKeyType  The type of the hash key, where the type should correspond to a valid
     *                     DynamoDb attribute type.
     * @param rangeKeyType The type of the range key, where the type should correspond to a valid
     *                     DynamoDb attribute type.
     */
    public static void ensureTable(String tableName, String hashKeyName, String hashKeyType,
                                   String rangeKeyName, String rangeKeyType) {
        if (!isTableAvailable(tableName)) {
            createTable(tableName, hashKeyName, hashKeyType, rangeKeyName, rangeKeyType);
            waitForTableToBecomeAvailable(tableName);
        }
    }

    /**
     * create a table with a GSI.
     */
    public static void ensureTableWithGsi(String tableName, String hashKeyName, String hashKeyType,
                                          String rangeKeyName, String rangeKeyType, String gsiName,
                                          String gsiHashKeyName, String gsiPartitionKeyType) {
        if (!isTableAvailable(tableName)) {

            GlobalSecondaryIndex globalSecondaryIndex = new GlobalSecondaryIndex()
                    .withIndexName(gsiName)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long)8)
                        .withWriteCapacityUnits((long)8))
                    .withProjection(new Projection()
                            .withProjectionType(ProjectionType.KEYS_ONLY));

            List<KeySchemaElement> indexKeySchema = new ArrayList<>();
            indexKeySchema.add(new KeySchemaElement()
                    .withAttributeName(gsiHashKeyName)
                    .withKeyType(KeyType.HASH));

            globalSecondaryIndex.setKeySchema(indexKeySchema);

            GlobalSecondaryIndex[] indices = {globalSecondaryIndex};
            createTableWithGsi(tableName, hashKeyName,hashKeyType, null, null, indices);
            waitForTableToBecomeAvailable(tableName);
        }
    }

    /**
     * Deletes the given table and waits until it is actually deleted.
     */
    public static void ensureTableDelete(String tableName) {
        if (isTableAvailable(tableName)) {
            deleteTable(tableName);
            waitForTableToBeDeleted(tableName);
        }
    }

    /**
     * Creates a table with the given name and hash key.
     *
     * @param hashKeyType The type of the hash key, where the type should correspond to a valid
     *                    DynamoDb attribute type.
     */
    public static void createTable(String tableName, String hashKeyName, String hashKeyType) {

        createTableWithGsi(
                tableName, hashKeyName, hashKeyType, null, null, null,
                null, null
        );
    }

    private static void createTable(
            String tableName,
            String hashKeyName, String hashKeyType,
            String rangeKeyName, String rangeKeyType) {

        createTableWithGsi(
                tableName, hashKeyName, hashKeyType, rangeKeyName, rangeKeyType, null,
                null, null
        );
    }

    private static void createTable(String tableName, Class<?> entityClass) {
        ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput()
                .withReadCapacityUnits(5L)
                .withWriteCapacityUnits(5L);

        CreateTableRequest tableRequest = mapper.generateCreateTableRequest(entityClass)
                .withProvisionedThroughput(provisionedthroughput);

        tableRequest.withTableName(tableName);
        if (tableRequest.getLocalSecondaryIndexes() != null) {
            Projection projection = new Projection().withProjectionType(ProjectionType.ALL);
            for (LocalSecondaryIndex lsi : tableRequest.getLocalSecondaryIndexes()) {
                lsi.setProjection(projection);
            }
        }

        if (tableRequest.getGlobalSecondaryIndexes() != null) {
            Projection projection = new Projection().withProjectionType(ProjectionType.ALL);
            for (GlobalSecondaryIndex gsi : tableRequest.getGlobalSecondaryIndexes()) {
                gsi.setProjection(projection);
                gsi.setProvisionedThroughput(provisionedthroughput);
            }
        }

        try {
            dynamoClient.createTable(tableRequest);
        } catch (AmazonServiceException ase) {
            LOGGER.error(
                    "Is table available failed: Type: " + ase.getErrorType().name()
                            + " Message: " + ase.getErrorMessage()
            );
        } catch (AmazonClientException | DynamoDBMappingException ex) {
            LOGGER.error("Is table available failed: " + ex.toString());
        }
    }

    /**
     * Creates a table with the given name, hash key, range key and global secondary index.
     *
     * @param hashKeyType  The type of the hash key, where the type should correspond to a valid
     *                     DynamoDb attribute type.
     * @param rangeKeyType The type of the range key, where the type should correspond to a valid
     *                     DynamoDb attribute type.
     */
    private static void createTableWithGsi(
            String tableName,
            String hashKeyName, String hashKeyType,
            String rangeKeyName, String rangeKeyType,
            String gsiIndexName, String gsiHashKeyAttributeName,
            String gsiRangeKeyAttributeName) {

        GlobalSecondaryIndex gsi = null;

        if (gsiIndexName != null
                && gsiHashKeyAttributeName != null
                && gsiRangeKeyAttributeName != null) {
            gsi = new GlobalSecondaryIndex()
                    .withIndexName(gsiIndexName)
                    .withKeySchema(new KeySchemaElement(gsiHashKeyAttributeName, KeyType.HASH),
                            new KeySchemaElement(gsiRangeKeyAttributeName, KeyType.RANGE))
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                    .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
        }

        if (gsi == null) {
            createTableWithGsi(
                    tableName, hashKeyName, hashKeyType, rangeKeyName,
                    rangeKeyType, new GlobalSecondaryIndex[0]
            );
        } else {
            createTableWithGsi(
                    tableName, hashKeyName, hashKeyType, rangeKeyName,
                    rangeKeyType, new GlobalSecondaryIndex[]{gsi}
            );
        }
    }

    /**
     * Creates a table with the given name, hash key, range key and global secondary indexes.
     *
     * @param hashKeyType  The type of the hash key, where the type should correspond to a valid
     *                     DynamoDb attribute type.
     * @param rangeKeyType The type of the range key, where the type should correspond to a valid
     *                     DynamoDb attribute type.
     */
    public static void createTableWithGsi(
            String tableName,
            String hashKeyName, String hashKeyType,
            String rangeKeyName, String rangeKeyType,
            GlobalSecondaryIndex[] gsis) {

        Map<String, AttributeDefinition> attributeDefinitions = new HashMap<>();

        try {

            ArrayList<KeySchemaElement> keySchema = new ArrayList<>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName(hashKeyName)
                    .withKeyType(KeyType.HASH));

            attributeDefinitions.put(hashKeyName, new AttributeDefinition()
                    .withAttributeName(hashKeyName)
                    .withAttributeType(hashKeyType));

            if (rangeKeyName != null) {
                keySchema.add(new KeySchemaElement()
                        .withAttributeName(rangeKeyName)
                        .withKeyType(KeyType.RANGE));
                attributeDefinitions.put(rangeKeyName, new AttributeDefinition()
                        .withAttributeName(rangeKeyName)
                        .withAttributeType(rangeKeyType));
            }

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(1L)
                            .withWriteCapacityUnits(1L));

            if (gsis != null && gsis.length > 0) {
                request.withGlobalSecondaryIndexes(gsis);

                for (GlobalSecondaryIndex gsi : gsis) {
                    if (gsi != null && gsi.getKeySchema() != null) {
                        for (KeySchemaElement element : gsi.getKeySchema()) {
                            attributeDefinitions.put(
                                    element.getAttributeName(),
                                    new AttributeDefinition()
                                            .withAttributeName(element.getAttributeName())
                                            .withAttributeType("S")
                            );
                        }
                    }
                }
            }

            request.setAttributeDefinitions(attributeDefinitions.values());

            LOGGER.info("Issuing CreateTable request for " + tableName);
            Table table = new DynamoDB(dynamoClient).createTable(request);
            LOGGER.info("Waiting for " + tableName + " to be created...this may take a while...");
            table.waitForActive();
        } catch (Exception e) {
            LOGGER.error("CreateTable request failed for " + tableName, e);
        }
    }

    private static void waitForTableToBecomeAvailable(String tableName) {
        LOGGER.info("Waiting for " + tableName + " to become ACTIVE...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);
        while (System.currentTimeMillis() < endTime) {
            TableDescription tableDescription = getTableDescription(tableName);

            String tableStatus = tableDescription.getTableStatus();
            LOGGER.info("  - current state: " + tableStatus);
            if (tableStatus.equals(TableStatus.ACTIVE.toString())) {
                return;
            }
            try {
                Thread.sleep(1000 * 20);
            } catch (InterruptedException ignored) {
                // Ignored
            }
        }
        throw new RuntimeException("Table " + tableName + " never went active");
    }

    private static boolean isTableAvailable(String tableName) {
        boolean result = false;
        LOGGER.info("Checking if " + tableName + " is ACTIVE...");

        try {
            TableDescription tableDescription = getTableDescription(tableName);
            String tableStatus = tableDescription.getTableStatus();
            LOGGER.info("  - current state: " + tableStatus);
            if (tableStatus.equals(TableStatus.ACTIVE.toString())) {
                result = true;
            }
        } catch (ResourceNotFoundException e) {
            result = false;
        } catch (AmazonServiceException ase) {
            LOGGER.error(
                    "Is table available failed: Type: " + ase.getErrorType().name()
                            + " Message: " + ase.getErrorMessage()
            );
        } catch (AmazonClientException | DynamoDBMappingException ex) {
            LOGGER.error("Is table available failed: " + ex.toString());
        }
        return result;
    }

    private static TableDescription getTableDescription(String tableName) {
        DescribeTableRequest request = new DescribeTableRequest()
                .withTableName(tableName);
        return dynamoClient.describeTable(request).getTable();
    }

    private static void deleteTable(String tableName) {
        try {

            DeleteTableRequest request = new DeleteTableRequest()
                    .withTableName(tableName);

            dynamoClient.deleteTable(request);
        } catch (AmazonServiceException ase) {
            LOGGER.error(
                    "Delete table failed: Type: " + ase.getErrorType().name()
                            + " Message: " + ase.getErrorMessage()
            );
        } catch (AmazonClientException | DynamoDBMappingException ex) {
            LOGGER.error("Delete table failed: " + ex.toString());
        }
    }

    private static void waitForTableToBeDeleted(String tableName) {
        LOGGER.info("Waiting for " + tableName + " while status DELETING...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);

        boolean tableDeleted = false;

        while (System.currentTimeMillis() < endTime && !tableDeleted) {
            tableDeleted = isTableDeleted(tableName);

            if (!tableDeleted) {
                try {
                    Thread.sleep(1000 * 20);
                } catch (InterruptedException ignored) {
                    // This exception is never expected to occur
                }
            }
        }

        if (!tableDeleted) {
            throw new RuntimeException("Table " + tableName + " was never deleted");
        }
    }

    private static boolean isTableDeleted(String tableName) {
        try {
            TableDescription tableDescription = getTableDescription(tableName);
            String tableStatus = tableDescription.getTableStatus();
            LOGGER.debug("  - current state: " + tableStatus);
            if (tableStatus.equals(TableStatus.ACTIVE.toString())) {
                return true;
            }
        } catch (ResourceNotFoundException e) {
            LOGGER.debug("Table " + tableName + " is not found. It was deleted.");
            return true;
        } catch (AmazonServiceException ase) {
            String errorType = ase.getErrorType().name();
            String errorMessage = ase.getErrorMessage();

            LOGGER.error(
                    "Wait for delete failed: Type: " + errorType + " Message: " + errorMessage
            );
        } catch (AmazonClientException | DynamoDBMappingException ex) {
            LOGGER.error("Wait for delete failed: " + ex.toString());
        }

        return false;
    }

    /**
     * Puts the given item into the given DynamoDb table.
     */
    public static void createItem(String tableName, Item item) {
        Table table = new Table(dynamoClient, tableName);

        table.putItem(item);
    }

    private static String determineDynamoDbPort() {
        String result = System.getProperty("dynamodb.port");
        if (StringUtils.isBlank(result)) {
            result = "8000";
        }
        return result;
    }

    private static AmazonDynamoDB createNewAmazonDynamoDbClient() {
        BasicAWSCredentials credentials = new BasicAWSCredentials("local", "local");
        String endpoint = String.format("http://localhost:%s", determineDynamoDbPort());
        AmazonDynamoDB client = builder()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, Regions.EU_WEST_1.getName())
                )
                .build();
        return client;
    }
}
