package com.kpi.authservice.exceptions;

public class UserNotFoundException extends IllegalStateException {
    private static final String message = "Can't find a user with the email %s in the database";
    public UserNotFoundException(String email) {
        super(String.format(message, email));
    }
    public UserNotFoundException(String message, String email) {
        super(String.format(message, email));
    }
}
