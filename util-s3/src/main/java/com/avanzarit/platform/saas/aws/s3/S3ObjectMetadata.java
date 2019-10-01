package com.avanzarit.platform.saas.aws.s3;

import java.util.Map;

public abstract class S3ObjectMetadata<T> {
    private T item;
    private static Map<String, String> metadataMap;

    public S3ObjectMetadata(T item) {
        this.item = item;
    }

    public abstract Map<String, String> getS3ObjectMetadata();

    public T getItem() {
        return item;
    }
}
