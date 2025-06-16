package com.kpi.authservice.exceptions;

public class TeacherNotFoundException extends IllegalStateException {
    private static final String message = "Can't find a teacher with the email %s in the database";
    public TeacherNotFoundException(String email) {
        super(String.format(message, email));
    }
    public TeacherNotFoundException(String message, String email) {
        super(String.format(message, email));
    }
}
