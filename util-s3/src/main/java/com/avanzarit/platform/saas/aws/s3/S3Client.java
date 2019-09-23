package com.avanzarit.platform.saas.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * The S3Client provides some wrapper methods around the functionality provided by the {@link AmazonS3Client}.
 */
public class S3Client {

    private AmazonS3 client;

    /**
     * Creates a new S3 client.
     */
    public S3Client() {
        client = S3Constants.S3_CLIENT;
    }

    /**
     * Uploads a file to the specified bucket using the provided key.
     */
    public void upload(String bucketName, String key, File file) {
        client.putObject(bucketName, key, file);
    }

    /**
     * Uploads a Java object to the specified bucket using the provided key. The object is first mapped to JSON using
     * the Jackson {@link ObjectMapper} after which the resulting JSON is uploaded to the S3 bucket with the given key.
     */
    public void upload(String bucketName, String key, Object objectToPut) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentEncoding("UTF-8");
        omd.setContentType("application/json");

        upload(bucketName, key, objectToPut, omd);
    }

    /**
     * Uploads data stored in a byte array to the specified bucket using the provided key and metadata.
     */
    public void upload(String bucketName, String fileName, byte[] data, ObjectMetadata omd) {
        client.putObject(bucketName, fileName, new ByteArrayInputStream(data), omd);
    }

    /**
     * Uploads a Java object to the specified bucket using the provided key. The object is first mapped to JSON using
     * the Jackson {@link ObjectMapper} after which the resulting JSON is uploaded to the S3 bucket with the given key
     * and metadata.
     */
    public void upload(String bucketName, String fileName, Object objectToPut, ObjectMetadata omd) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String objectAsString = mapper.writeValueAsString(objectToPut);

            InputStream inputStream = new ByteArrayInputStream(objectAsString.getBytes(StandardCharsets.UTF_8));
            client.putObject(bucketName, fileName, inputStream, omd);
        } catch (JsonProcessingException e) {
            throw new S3ClientException("Could not process object of class " + objectToPut.getClass().getName(), e);
        }
    }
}
