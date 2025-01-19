package com.project.urlshorten.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncodingShortKeyGeneratorTest {

    private EncodingShortKeyGenerator generator = new EncodingShortKeyGenerator();

    @Test
    @DisplayName("같은 url 입력시 다른 단축 url 출력 테스트")
    void generateShortKeyTest() throws InterruptedException {
        String originalUrl = "https://www.google.co.kr";

        String shortenUrl1 = generator.generateShortKey(originalUrl);
        Thread.sleep(1000);
        String shortenUrl2 = generator.generateShortKey(originalUrl);

        assertNotEquals(shortenUrl1, shortenUrl2);
    }
}