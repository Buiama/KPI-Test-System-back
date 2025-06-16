package com.kpi.authservice.exceptions;

public class WrongPasswordException extends IllegalStateException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
