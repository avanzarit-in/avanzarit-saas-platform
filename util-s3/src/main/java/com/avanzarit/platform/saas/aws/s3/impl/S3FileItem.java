package com.avanzarit.platform.saas.aws.s3.impl;

import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.s3.S3ReadObjectException;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class S3FileItem extends S3Item<File> {
    private byte[] content;

    public S3FileItem(File file, String itemKey, Map<String, String> s3ObjectMetadata, String bucketName, String folderPath) {
        super(file, itemKey, s3ObjectMetadata, bucketName, folderPath);

        try {
            this.content = IOUtils.toByteArray(new FileInputStream(file));
        } catch (IOException e) {
            throw new S3ReadObjectException(e);
        }
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(content);
    }
}
