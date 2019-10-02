package com.avanzarit.platform.saas.aws.lambda.processors.impl;

import com.avanzarit.platform.saas.aws.lambda.processors.S3ItemProcessor;
import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.util.CmwContext;

import java.io.File;

public class S3FileItemProcessor extends S3ItemProcessor<S3Item<File>> {

    @Override
    public void process(CmwContext cmwContext, S3Item<File> entity) {

    }
}
