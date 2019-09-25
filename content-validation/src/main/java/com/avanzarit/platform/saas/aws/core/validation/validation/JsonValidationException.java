package com.avanzarit.platform.saas.aws.core.validation.validation;

public class JsonValidationException extends RuntimeException {
    public JsonValidationException() {
    }

    public JsonValidationException(String message) {
        super(message);
    }

    public JsonValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
