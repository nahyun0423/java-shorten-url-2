package com.project.urlshorten.exception;

public class ShortkeyNotFoundException extends RuntimeException {

    public ShortkeyNotFoundException(String shortKey) {
        super(shortKey + "는 존재하지 않는 shortkey입니다.");
    }
}