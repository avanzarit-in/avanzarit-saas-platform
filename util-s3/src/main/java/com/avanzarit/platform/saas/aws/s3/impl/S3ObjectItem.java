package com.avanzarit.platform.saas.aws.s3.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.s3.S3ReadObjectException;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class S3ObjectItem extends S3Item<S3Object> {
    private byte[] content;

    public S3ObjectItem(S3Object s3Object, String itemKey, Map<String, String> s3ObjectMetadata) {
        super(s3Object, itemKey, s3ObjectMetadata);

        try (InputStream content = s3Object.getObjectContent()) {
            this.content = IOUtils.toByteArray(content);
        } catch (IOException e) {
            throw new S3ReadObjectException(e);
        }
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(content);
    }
}
