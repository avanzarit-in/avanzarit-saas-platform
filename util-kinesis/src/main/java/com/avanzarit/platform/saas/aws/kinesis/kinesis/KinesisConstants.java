package com.avanzarit.platform.saas.aws.kinesis.kinesis;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.avanzarit.platform.saas.aws.util.region.RegionUtils;

public class KinesisConstants {
    public static final AmazonKinesis KINESIS_CLIENT;

    static {
        DefaultAWSCredentialsProviderChain credentialsProvider
                = new DefaultAWSCredentialsProviderChain();
        Region region = RegionUtils.getCurrentRegion();

        if (region == null) {
            throw new RegionNotSpecifiedException(
                    "Could not create Kinesis client. No Region specified."
            );
        }

        AmazonKinesis kinesisClient = AmazonKinesisClient.builder()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(region.getName())
                .build();

        KINESIS_CLIENT = kinesisClient;
    }
}

