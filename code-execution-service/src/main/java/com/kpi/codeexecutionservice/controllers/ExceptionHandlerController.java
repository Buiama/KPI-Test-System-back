package com.kpi.codeexecutionservice.controllers;

import com.kpi.codeexecutionservice.dtos.responses.ErrorMessageResponse;
import com.kpi.codeexecutionservice.exceptions.DeadlinePassedException;
import com.kpi.codeexecutionservice.exceptions.ExecutionException;
import com.kpi.codeexecutionservice.exceptions.SubmissionLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<ErrorMessageResponse> executionException(ExecutionException exception) {
        return new ResponseEntity<>(
                ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(DeadlinePassedException.class)
    public ResponseEntity<ErrorMessageResponse> deadlinePassedException(DeadlinePassedException exception) {
        return new ResponseEntity<>(
                ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(SubmissionLimitExceededException.class)
    public ResponseEntity<ErrorMessageResponse> submissionLimitExceededException(SubmissionLimitExceededException exception) {
        return new ResponseEntity<>(
                ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.FORBIDDEN // Or HttpStatus.TOO_MANY_REQUESTS
        );
    }
}