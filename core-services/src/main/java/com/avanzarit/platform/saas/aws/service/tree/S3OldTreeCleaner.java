package com.avanzarit.platform.saas.aws.service.tree;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

import java.util.List;

public class S3OldTreeCleaner {

    private AmazonS3 s3Client;
    private S3KeyGenerator keyGenerator;

    public S3OldTreeCleaner(AmazonS3 s3Client, S3KeyGenerator keyGenerator) {
        this.s3Client = s3Client;
        this.keyGenerator = keyGenerator;
    }

    public void clean(CmwContext cmwContext, UpdateInfo updateInfo, String lastKey) {
        String bucket = keyGenerator.generateBucketName(cmwContext.getPrefix());
        String prefix = keyGenerator.generatePath(cmwContext.getLayer(), updateInfo.getContext());

        prefix = prefix + updateInfo.getObjectId();

        ObjectListing treeListing = s3Client.listObjects(bucket, prefix);

        List<S3ObjectSummary> trees = treeListing.getObjectSummaries();

        int removeFromIndex = 0;
        int removeUntilIndex;

        int numberOfVersionsToKeep;

        if(listingContainsLastKey(trees, lastKey)) {
            numberOfVersionsToKeep = 3;
        } else {
            numberOfVersionsToKeep = 2;
        }

        removeUntilIndex = trees.size() - numberOfVersionsToKeep;

        for (int i = removeFromIndex; i < removeUntilIndex; i++) {
            s3Client.deleteObject(bucket, trees.get(i).getKey());
        }
    }

    public boolean listingContainsLastKey(List<S3ObjectSummary> trees, String lastKey) {
        return trees.get(trees.size() - 1).getKey().equals(lastKey);
    }

}
