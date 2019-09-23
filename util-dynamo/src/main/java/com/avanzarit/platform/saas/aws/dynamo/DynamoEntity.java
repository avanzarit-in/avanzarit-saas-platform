package com.avanzarit.platform.saas.aws.dynamo;

/***
 * Marker interface for Dynamo-persistence classes that can be referred to in generics, etc...
 */
public interface DynamoEntity {

    /**
     * Gets the table name for this entity without any prefixes.
     *
     * @return A bare table name without any suffixes. (e.g.: "product" instead of
     * "mw_dev_cache_product").
     */
    String getBareTableName();

}
