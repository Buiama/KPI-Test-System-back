package com.kpi.authservice.exceptions;

public class InvalidEmailException extends IllegalStateException {
    private static final String message = "Email is invalid, can only be used with the domain @kpi.ua or @lll.kpi.ua";
    public InvalidEmailException() {
        super(message);
    }
    public InvalidEmailException(String message) {
        super(message);
    }
}
