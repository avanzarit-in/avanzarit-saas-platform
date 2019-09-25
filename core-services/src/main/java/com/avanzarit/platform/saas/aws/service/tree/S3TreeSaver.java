package com.avanzarit.platform.saas.aws.service.tree;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.avanzarit.platform.saas.aws.core.model.DataEntity;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import com.avanzarit.platform.saas.aws.util.UpdateInfo;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class S3TreeSaver {

    private ObjectMapper mapper;
    private AmazonS3 s3Client;
    private S3KeyGenerator s3KeyGenerator;

    public S3TreeSaver(ObjectMapper mapper, AmazonS3 s3Client, S3KeyGenerator s3KeyGenerator) {
        this.mapper = mapper;
        this.s3Client = s3Client;
        this.s3KeyGenerator = s3KeyGenerator;
    }

    public <T extends DataEntity> String save(CmwContext cmwContext, UpdateInfo updateInfo, T treeEntity, Object treeJson, String suffix) {
        String path = s3KeyGenerator.generatePath(cmwContext.getLayer(), treeEntity.getContext());

        String fullPath = path + treeEntity.getObjectId() + "_" + suffix + ".json";

        String bucket = s3KeyGenerator.generateBucketName(cmwContext.getPrefix());

        Map<String, String> userMetadata = new HashMap<>();
        userMetadata.put("context", treeEntity.getContext());
        userMetadata.put("object-id", treeEntity.getObjectId());
        userMetadata.put("update-id", treeEntity.getUpdateId());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentEncoding("UTF-8");
        metadata.setContentType("application/json");
        metadata.setUserMetadata(userMetadata);

        try {
            byte[] data = mapper.writeValueAsBytes(treeJson);
            metadata.setContentLength(data.length);
            s3Client.putObject(bucket, fullPath, new ByteArrayInputStream(data), metadata);
        } catch (JsonProcessingException e) {
            cmwContext.logError(updateInfo, e);
            throw new TreeSaverException(e);
        }

        return fullPath;
    }

}
