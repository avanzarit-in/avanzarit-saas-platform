package com.avanzarit.platform.saas.aws.dynamo;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.avanzarit.platform.saas.aws.util.region.RegionUtils;

import static com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.builder;

/**
 * DynamoConstants creates and provides an {@link AmazonDynamoDB} instances for use within the application.
 */
public class DynamoConstants {
    public static final AmazonDynamoDB DYNAMO_CLIENT;

    static {
        DefaultAWSCredentialsProviderChain credentialsProvider
                = new DefaultAWSCredentialsProviderChain();
        Region region = RegionUtils.getCurrentRegion();

        if (region == null) {
            throw new RegionNotSpecifiedException(
                    "Could not create Dynamo DB client. No Region specified."
            );
        }

        AmazonDynamoDB amazonDynamoDb = builder()
                .withCredentials(credentialsProvider)
                .withRegion(region.getName())
                .build();

        DYNAMO_CLIENT = amazonDynamoDb;
    }
}
