package com.avanzarit.platform.saas.aws.lambda;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The LambdaLoggerOutputStream is an {@link OutputStream} implementation that redirects output to the AWS SDK
 * {@link LambdaLogger} implementation.
 */
public class LambdaLoggerOutputStream extends OutputStream {

    private LambdaLogger lambdaLogger;

    /**
     * Creates a new output stream for the given {@link LambdaLogger}.
     */
    public LambdaLoggerOutputStream(LambdaLogger lambdaLogger) {
        this.lambdaLogger = lambdaLogger;
    }

    private StringBuffer sb = new StringBuffer();

    @Override
    public void write(int b) throws IOException {
        if (b == 10) {
            writeLine();
        } else {
            sb.append((char) b);
        }
    }

    @Override
    public void flush() throws IOException {
        writeLine();
    }

    private void writeLine() {
        lambdaLogger.log(sb.toString());
        this.sb = new StringBuffer();
    }
}