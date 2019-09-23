package com.avanzarit.platform.saas.aws.s3;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.avanzarit.platform.saas.aws.util.region.RegionUtils;

import static com.amazonaws.services.s3.AmazonS3Client.builder;

/**
 * Creates and provides an {@link AmazonS3Client} instance for use throughout the system.
 */
public class S3Constants {

    public static final AmazonS3 S3_CLIENT;

    static {
        S3_CLIENT = builder()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(RegionUtils.getCurrentRegion().getName())
                .build();
    }
}
