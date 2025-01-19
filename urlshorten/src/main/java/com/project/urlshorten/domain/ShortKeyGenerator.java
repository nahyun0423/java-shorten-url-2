package com.project.urlshorten.domain;

public interface ShortKeyGenerator {
    String generateShortKey(String originalUrl);
}
