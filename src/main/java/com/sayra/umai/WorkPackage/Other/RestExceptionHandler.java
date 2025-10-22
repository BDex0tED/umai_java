package com.sayra.umai.WorkPackage.Other;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
        return new ResponseEntity<>(
                "JWT expired, please login again",
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(UserPrincipalNotFoundException.class)
    public ResponseEntity<String> handleUserPrincipalNotFoundException(UserPrincipalNotFoundException ex) {
        return new ResponseEntity<>(
                "User not found",
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }


}