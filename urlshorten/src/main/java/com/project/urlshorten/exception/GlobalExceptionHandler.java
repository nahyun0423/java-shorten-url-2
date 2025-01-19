package com.project.urlshorten.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    private ResponseEntity<String> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.getErrorCode().getMessage(), ex.getErrorCode().getHttpStatus());
    }
}