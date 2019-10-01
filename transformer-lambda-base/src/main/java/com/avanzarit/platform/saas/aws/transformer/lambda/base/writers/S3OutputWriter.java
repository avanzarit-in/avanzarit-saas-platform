package com.avanzarit.platform.saas.aws.transformer.lambda.base.writers;

import com.avanzarit.platform.saas.aws.s3.S3Manager;
import com.avanzarit.platform.saas.aws.util.CmwContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class S3OutputWriter<O> {
    private static final Logger LOGGER = LogManager.getLogger(S3OutputWriter.class);
    private CmwContext cmwContext;
    private S3Manager s3Manager;

    public S3OutputWriter(CmwContext cmwContext, S3Manager s3Manager) {
        this.cmwContext = cmwContext;
        this.s3Manager = s3Manager;
    }

    public void process(O outputEntity) {
      /*  S3Item<S3Object> s3Item=new S3FileItem()
        s3Manager.writeItem();
        */

    }

}
