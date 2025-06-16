package com.kpi.emailservice.controllers;

import com.kpi.emailservice.dtos.response.ErrorResponse;
import com.kpi.emailservice.exceptions.FailedToSendEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FailedToSendEmailException.class)
    public ResponseEntity<ErrorResponse> handleFailedToSendEmailException(FailedToSendEmailException e) {
        return new ResponseEntity<>(
                ErrorResponse.builder().errorMessage(e.getMessage()).build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
