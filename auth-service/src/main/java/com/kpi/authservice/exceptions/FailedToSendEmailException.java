package com.kpi.authservice.exceptions;

public class FailedToSendEmailException extends IllegalArgumentException {
    private static final String message = "Failed to send email";
    public FailedToSendEmailException() {
        super(message);
    }
    public FailedToSendEmailException(String message) {
        super(message);
    }
}
