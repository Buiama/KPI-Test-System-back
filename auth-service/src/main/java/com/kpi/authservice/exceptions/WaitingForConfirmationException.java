package com.kpi.authservice.exceptions;

public class WaitingForConfirmationException extends IllegalStateException {
    private static final String message = "Confirmation pending, try again in 15 minutes";
    public WaitingForConfirmationException() {
        super(message);
    }
    public WaitingForConfirmationException(String message) {
        super(message);
    }
}
