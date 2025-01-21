package com.project.urlshorten.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_URL(HttpStatus.BAD_REQUEST, "URL 형식이 아닙니다."),
    SHORTKEY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 SHORTKEY입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}