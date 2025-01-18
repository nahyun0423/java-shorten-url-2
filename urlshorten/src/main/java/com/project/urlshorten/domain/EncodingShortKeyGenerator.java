package com.project.urlshorten.domain;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class EncodingShortKeyGenerator implements ShortKeyGenerator {
    @Override
    public String generateShortKey(String originalUrl) {
        String timeString = String.valueOf(System.currentTimeMillis());

        String encodedUrl = Base64.getEncoder().withoutPadding().encodeToString(originalUrl.getBytes()).substring(0, 4);
        String encodedTime = Base64.getEncoder().withoutPadding().encodeToString(timeString.getBytes());

        return encodedUrl.concat(encodedTime.substring(encodedTime.length() - 3, encodedTime.length()));
    }
}
