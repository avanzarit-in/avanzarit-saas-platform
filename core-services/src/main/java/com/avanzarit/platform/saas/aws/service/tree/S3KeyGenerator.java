package com.avanzarit.platform.saas.aws.service.tree;

public class S3KeyGenerator {

    public String generatePath(String layer, String context) {
        String result = layer;

        result += "/trees/";

        result += context + "/";

        return result;
    }

    public String generateBucketName(String prefix) {
        return "philips-" + prefix.replace('_', '-');
    }

}
