package com.avanzarit.platform.saas.aws.s3;

import java.io.InputStream;
import java.util.Map;

public abstract class S3Item<T> {
    private T item;
    private Map<String, String> s3ObjectMetadata;
    private String itemKey;

    public S3Item(T item) {
        this.item = item;
    }

    public S3Item(T item, Map<String, String> s3ObjectMetadata) {
        this.item = item;
        this.s3ObjectMetadata = s3ObjectMetadata;
    }

    public S3Item(T item, String itemKey, Map<String, String> s3ObjectMetadata) {
        this.item = item;
        this.itemKey = itemKey;
        this.s3ObjectMetadata = s3ObjectMetadata;
    }

    public String getItemKey() {
        return itemKey;
    }

    public Map<String, String> getMetadata() {
        return s3ObjectMetadata;
    }

    public abstract InputStream getContent();

    public T getItem() {
        return item;
    }
}