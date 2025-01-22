package com.project.urlshorten.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    private ResponseEntity<String> handleillegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>("적절한 url 형식이 아닙니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ShortkeyNotFoundException.class})
    private ResponseEntity<String> handleShortkeyNotFoundException(ShortkeyNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}