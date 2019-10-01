package com.avanzarit.platform.saas.aws.s3;

import com.amazonaws.services.s3.model.S3Object;
import com.avanzarit.platform.saas.aws.s3.impl.S3FileItem;
import com.avanzarit.platform.saas.aws.s3.impl.S3ObjectItem;

import java.io.File;
import java.util.Optional;

public class S3ItemBuilder<T> {
    private T item;
    private S3KeyGenerator<T> s3KeyGenerator;
    private S3ObjectMetadata<T> s3ObjectMetadata;

    public S3ItemBuilder<T> withS3KeyGenerator(S3KeyGenerator<T> s3KeyGenerator) {
        this.s3KeyGenerator = s3KeyGenerator;
        return this;
    }

    public S3ItemBuilder<T> withS3Item(T item) {
        this.item = item;
        return this;
    }

    public S3ItemBuilder<T> withS3Metadata(S3ObjectMetadata<T> s3ObjectMetadata) {
        this.s3ObjectMetadata = s3ObjectMetadata;
        return this;
    }

    public Optional<S3Item> build() throws Exception {
        if (item != null) {
            if (item instanceof File) {
                return Optional.of(new S3FileItem((File) item,
                        Optional.ofNullable(s3KeyGenerator.generateKey()).orElseThrow(Exception::new),
                        Optional.ofNullable(s3ObjectMetadata.getS3ObjectMetadata()).orElseThrow(Exception::new)));
            } else if (item instanceof S3Object) {
                return Optional.of(new S3ObjectItem((S3Object) item,
                        s3KeyGenerator == null ? ((S3Object) item).getKey() : s3KeyGenerator.generateKey(),
                        s3ObjectMetadata == null ? ((S3Object) item).getObjectMetadata().getUserMetadata() : s3ObjectMetadata.getS3ObjectMetadata()
                ));
            }
        }
        return Optional.empty();
    }
}
