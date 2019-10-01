package com.avanzarit.platform.saas.aws.core.validation;

public class ValidationReport {
    private boolean valid;
    private String message;

    public ValidationReport(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
