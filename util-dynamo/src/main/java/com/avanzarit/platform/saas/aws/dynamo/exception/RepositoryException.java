package com.avanzarit.platform.saas.aws.dynamo.exception;

/**
 * RepositoryException is an excepton class that is used when something unexpected occurs when executing logic in the
 * {@link com.avanzarit.platform.saas.aws.dynamo.DynamoDbRepository} class or one of its subclasses.
 */
public class RepositoryException extends RuntimeException {

    /**
     * Creates a new exception using the given message.
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * Creates a new exception wrapping another original exception.
     */
    public RepositoryException(Throwable cause) {
        super(cause);
    }
}
