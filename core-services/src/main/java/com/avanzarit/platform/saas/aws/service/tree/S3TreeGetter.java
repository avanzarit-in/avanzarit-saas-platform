package com.avanzarit.platform.saas.aws.service.tree;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

import java.io.IOException;

public class S3TreeGetter {

    private AmazonS3 s3Client;
    private ObjectMapper mapper;

    public S3TreeGetter(AmazonS3 s3Client, ObjectMapper mapper) {
        this.s3Client = s3Client;
        this.mapper = mapper;
    }

    public <T> T get(CmwContext cmwContext, UpdateInfo updateInfo, Class<T> objectType, String bucket, String key) {
        cmwContext.logInfo(updateInfo, "Getting object from bucket '" + bucket + "' with key '" + key + "'");

        T result = null;

        boolean exists = s3Client.doesObjectExist(bucket, key);

        if(exists) {
            S3Object s3Object = s3Client.getObject(bucket, key);

            try {
                result = mapper.readValue(s3Object.getObjectContent(), objectType);
            } catch (IOException e) {
                cmwContext.logError(updateInfo, e);
            }
        } else {
            cmwContext.logWarning(updateInfo, "Object in bucket '" + bucket + "' with key '" + key + "' does not exist.");
        }

        return result;
    }
}