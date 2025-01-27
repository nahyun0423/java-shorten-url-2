package com.project.urlshorten.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncodingShortKeyGeneratorTest {

    private EncodingShortKeyGenerator generator = new EncodingShortKeyGenerator();

    @Test
    @DisplayName("동일한 URL로 두 번 단축 URL 생성하면, 서로 다른 단축 URL을 반환")
    void generateShortKeyTest() throws InterruptedException {
        String originalUrl = "https://www.google.co.kr";

        String shortenUrl1 = generator.generateShortKey(originalUrl);
        Thread.sleep(1000);
        String shortenUrl2 = generator.generateShortKey(originalUrl);

        assertNotEquals(shortenUrl1, shortenUrl2);
    }
}