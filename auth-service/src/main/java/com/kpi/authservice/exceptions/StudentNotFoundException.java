package com.kpi.authservice.exceptions;

public class StudentNotFoundException extends IllegalStateException {
    private static final String message = "Can't find a student with the email %s in the database";
    public StudentNotFoundException(String email) {
        super(String.format(message, email));
    }
    public StudentNotFoundException(String message, String email) {
        super(String.format(message, email));
    }
}
