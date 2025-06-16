package com.kpi.codeexecutionservice.exceptions;

public class DeadlinePassedException extends RuntimeException {
    public DeadlinePassedException(String message) {
        super(message);
    }

    public DeadlinePassedException(String message, Throwable cause) {
        super(message, cause);
    }
}
