package com.kpi.authservice.exceptions;

public class EmailAlreadyTakenException extends IllegalStateException {
    private static final String message = "This Email is already taken. Only one account per student is available!";
    public EmailAlreadyTakenException() {
        super(message);
    }
    public EmailAlreadyTakenException(String message) {
        super(message);
    }
}
