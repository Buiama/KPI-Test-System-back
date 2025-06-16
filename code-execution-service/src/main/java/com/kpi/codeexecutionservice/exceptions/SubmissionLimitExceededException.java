package com.kpi.codeexecutionservice.exceptions;

public class SubmissionLimitExceededException extends RuntimeException {
    public SubmissionLimitExceededException(String message) {
        super(message);
    }

    public SubmissionLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}