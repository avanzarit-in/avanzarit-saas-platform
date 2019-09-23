package com.avanzarit.platform.saas.aws.dynamo;

import java.util.ArrayList;
import java.util.List;

/**
 * DynamoBatchEntityGetter provides the capability of getting entities from a DynamoDb table using batch requests. It
 * transparently handles the limit of retrieving at most {@value BATCH_SIZE} entities in one go.
 */
public class DynamoBatchEntityGetter {

    private static final Integer BATCH_SIZE = 100;

    private DynamoDbRepository repository;

    /**
     * Creates a new batch entity getter for the given repository.
     */
    public DynamoBatchEntityGetter(DynamoDbRepository repository) {
        this.repository = repository;
    }

    /**
     * Get the given list of entities from the DynamoDb table.
     *
     * @param entities A list of {@link DynamoEntity} objects that contain DynamoDb mapper annotations which can be used
     *                 to provide the hash and range keys to DynamoDb for retrieval.
     * @return A list of {@link DynamoEntity} with all fields populated using data from the actual table.
     */
    public <T extends DynamoEntity> List<T> getEntities(String tableName, List<T> entities) {
        List<T> result = new ArrayList<>();

        if (entities == null
                || entities.size() < 1) {
            return result;
        }

        boolean keepLooping = true;

        int start = 0;
        int end = BATCH_SIZE;

        while (keepLooping) {
            if (end > entities.size()) {
                end = entities.size();
            }

            List<T> subList = entities.subList(start, end);

            result.addAll(repository.batchGet(tableName, subList));

            if (end == entities.size()) {
                keepLooping = false;
            } else {
                start += BATCH_SIZE;
                end += BATCH_SIZE;
            }
        }

        return result;
    }
}
