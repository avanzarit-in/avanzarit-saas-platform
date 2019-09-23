package com.avanzarit.platform.saas.aws.lambda;

/**
 * The EntityTriggerCallFailedException is thrown when calling an {@link EntityTrigger} fails from the
 * {@link EntityTriggerCaller}. The exception tracks whether the error was already handled by the {@link EntityTrigger}
 * itself or not.
 * <p>
 * One of the side-effects of not handling such exceptions properly in the lambda and rethrowing it from the lambda
 * (thus making the lambda function fail) could trigger AWS Lambda to attempt to process the erring batch of records
 * again until the time the data expires, which can be up to seven days for Amazon Kinesis Data Streams.
 * <p>
 * The exception is treated as blocking, and AWS Lambda will not read any new records from the stream until
 * the failed batch of records either expires or processed successfully.
 * <p>
 * Good Read to understand Retry Behavior of lambdas : https://docs.aws.amazon.com/lambda/latest/dg/retries-on-errors.html
 * <p>
 * Therefore, its important to assert in the program flow when a exception of this type is thrown should we treat it as
 * a lambda function failure or ingest the exception and mark the lambda function as success.
 * <p>
 * The attribute "isCritical" should be set to true to actually consider
 * the exception as failure and rethrow it from lambda to mark the lambda failure.
 */
public class EntityTriggerCallFailedException extends RuntimeException {
    private boolean logged;
    private boolean isCritical;

    /**
     * Creates a new exception for the given cause.
     *
     * @param logged     Whether the exception was already handled by the {@link EntityTrigger}.
     * @param isCritical Whether the exception should be treated as critical and thus can be
     *                   rethrown from lambda thus marking the lambda as failure.
     */
    public EntityTriggerCallFailedException(Throwable cause, boolean logged, boolean isCritical) {
        super(cause);

        this.logged = logged;
        this.isCritical = isCritical;
    }

    public boolean isLogged() {
        return logged;
    }

    public boolean isCritical() {
        return isCritical;
    }
}
