package com.avanzarit.platform.saas.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class S3Manager {
    private static final Logger LOGGER = LogManager.getLogger(S3Manager.class);

    private AmazonS3 amazonS3;

    public S3Manager(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;

    }

    public S3Page<S3Item> readPage(String bucket, String prefix, String lastEvaluatedKey, Predicate<String> filter) throws Exception {
        List<S3Item> items = new ArrayList<>();

        S3Page<String> keys = listObjects(bucket, prefix, lastEvaluatedKey, filter);

        for (String key : keys.getItems()) {
            S3Object s3Object = amazonS3.getObject(bucket, key);
            items.add(new S3ItemBuilder<S3Object>().withS3Item(s3Object).build().orElseThrow(Exception::new));
        }

        return new S3Page<>(items, keys.getLastEvaluatedKey());
    }

    public void writeItem(String bucket, S3Item item) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.getUserMetadata().putAll(item.getMetadata());

        PutObjectRequest request = new PutObjectRequest(bucket, item.getItemKey(), item.getContent(), metadata);

        amazonS3.putObject(request);
    }

    public void copyBucket(String sourceBucket, String destinationBucket) {
        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        String lastEvaluatedKey = null;

        do {
            S3Page<String> keys = listObjects(sourceBucket, null, lastEvaluatedKey, key -> false);

            List<Copy> copyTasks = keys.getItems().stream()
                    .map(item -> transferManager.copy(sourceBucket, item, destinationBucket, item))
                    .collect(Collectors.toList());

            for (Copy copyTask : copyTasks) {
                try {
                    copyTask.waitForCompletion();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new S3Exception(e);
                }
            }

            lastEvaluatedKey = keys.getLastEvaluatedKey();
        } while (lastEvaluatedKey != null);

        transferManager.shutdownNow(false);
    }

    private S3Page<String> listObjects(String bucket, String prefix, String lastEvaluatedKey, Predicate<String> filter) {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(prefix)
                .withContinuationToken(lastEvaluatedKey)
                .withMaxKeys(100);

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        List<String> items = new ArrayList<>();

        for (S3ObjectSummary summary : result.getObjectSummaries()) {
            if (!filter.test(summary.getKey())) {
                items.add(summary.getKey());
            } else {
                LOGGER.info("Filtering S3 item with key '" + summary.getKey() + "'");
            }
        }

        return new S3Page<>(items, result.getNextContinuationToken());
    }
}
