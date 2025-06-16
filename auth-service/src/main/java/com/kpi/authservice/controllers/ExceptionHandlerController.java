package com.kpi.authservice.controllers;

import com.kpi.authservice.dtos.responses.ErrorMessageResponse;
import com.kpi.authservice.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<?> emailAlreadyTakenException(EmailAlreadyTakenException exception) {
        return new ResponseEntity<>(ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FailedToSendEmailException.class)
    public ResponseEntity<?> failedToSendEmailException(FailedToSendEmailException exception) {
        return new ResponseEntity<>(ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<?> GroupNotFound(GroupNotFoundException exception) {
        return new ResponseEntity<>(ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<?> InvalidEmailException(InvalidEmailException exception) {
        return new ResponseEntity<>(ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<?> StudentNotFoundException(StudentNotFoundException exception) {
        return new ResponseEntity<>(ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> TokenExpired(TokenExpiredException exception) {
        return new ResponseEntity<>(ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WaitingForConfirmationException.class)
    public ResponseEntity<?> WaitingForConfirmation(WaitingForConfirmationException exception) {
        return new ResponseEntity<>(ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.TEMPORARY_REDIRECT);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<?> WrongPasswordException(WrongPasswordException exception) {
        return new ResponseEntity<>(ErrorMessageResponse.builder().errorMessage(exception.getMessage()).build(),
                HttpStatus.UNAUTHORIZED);
    }
}
