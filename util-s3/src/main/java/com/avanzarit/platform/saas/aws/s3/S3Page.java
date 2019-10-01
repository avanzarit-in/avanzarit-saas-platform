package com.avanzarit.platform.saas.aws.s3;

import java.util.List;

public class S3Page<T> {
    private List<T> items;
    private String lastEvaluatedKey;

    public S3Page(List<T> items, String lastEvaluatedKey) {
        this.items = items;
        this.lastEvaluatedKey = lastEvaluatedKey;
    }

    public List<T> getItems() {
        return items;
    }

    public String getLastEvaluatedKey() {
        return lastEvaluatedKey;
    }

    public boolean isLastPage() {
        return lastEvaluatedKey == null;
    }
}
