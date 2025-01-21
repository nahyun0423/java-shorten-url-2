package com.project.urlshorten.application;

import com.project.urlshorten.domain.ShortKeyGenerator;
import com.project.urlshorten.domain.ShortenUrl;
import com.project.urlshorten.exception.CustomException;
import com.project.urlshorten.infrastructure.ShortenUrlRepository;
import com.project.urlshorten.presentation.ShortenUrlDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortenUrlServiceTest {

    @Mock
    private ShortenUrlRepository shortenUrlRepository;

    @Mock
    private ShortKeyGenerator shortKeyGenerator;

    @InjectMocks
    private ShortenUrlService shortenUrlService;

    @Test
    @DisplayName("같은 url 입력시 다른 단축 url 출력 테스트")
    void createShortKeyTest() throws InterruptedException {
        String originalUrl = "https://www.google.co.kr";
        String expectedShortKey1 = "abcd123";
        String expectedShortKey2 = "abcd456";

        when(shortKeyGenerator.generateShortKey(originalUrl))
                .thenReturn(expectedShortKey1)
                .thenReturn(expectedShortKey2);

        String shortenUrl1 = shortenUrlService.createShortKey(originalUrl);
        Thread.sleep(1000);
        String shortenUrl2 = shortenUrlService.createShortKey(originalUrl);

        assertNotEquals(shortenUrl1, shortenUrl2);
    }

    @Test
    void saveShortenUrlTest() {
        ShortenUrl shortenUrl = new ShortenUrl("https://www.google.co.kr", "abcd123");

        when(shortenUrlRepository.save(shortenUrl)).thenReturn(shortenUrl);
        ShortenUrl savedUrl = shortenUrlService.saveShortenUrl(shortenUrl);

        assertNotNull(savedUrl);
        assertEquals(shortenUrl.getOriginalUrl(), savedUrl.getOriginalUrl());
        assertEquals(shortenUrl.getShortKey(), savedUrl.getShortKey());
    }

    @Test
    void findShortenUrlSuccessTest() {
        ShortenUrl shortenUrl = new ShortenUrl("https://www.google.co.kr", "abcd123");

        when(shortenUrlRepository.findByShortKey(shortenUrl.getShortKey())).thenReturn(Optional.of(shortenUrl));
        ShortenUrl result = shortenUrlService.findShortenUrl(shortenUrl.getShortKey());

        assertNotNull(result);
        assertEquals(shortenUrl.getOriginalUrl(), result.getOriginalUrl());
        assertEquals(shortenUrl.getShortKey(), result.getShortKey());
    }

    @Test
    void findShortenUrlFailTest() {
        ShortenUrl shortenUrl = new ShortenUrl("https://www.google.co.kr", "abcd123");

        when(shortenUrlRepository.findByShortKey(shortenUrl.getShortKey())).thenReturn(Optional.empty());
        Exception exception = assertThrows(CustomException.class, () -> shortenUrlService.findShortenUrl(shortenUrl.getShortKey()));

        assertEquals("존재하지 않는 SHORTKEY입니다.", exception.getMessage());
    }

    @Test
    void findAllShortenUrlTest() {
        ShortenUrl shortenUrl1 = new ShortenUrl("https://www.google.co.kr", "abcd123");
        ShortenUrl shortenUrl2 = new ShortenUrl("https://www.naver.com", "abcd456");
        List<ShortenUrl> shortenUrls = List.of(shortenUrl1, shortenUrl2);

        when(shortenUrlRepository.findAll()).thenReturn(shortenUrls);
        List<ShortenUrlDto> result = shortenUrlService.findAllShortenUrl();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(shortenUrl1.getOriginalUrl(), result.get(0).getOriginalUrl());
        assertEquals(shortenUrl2.getOriginalUrl(), result.get(1).getOriginalUrl());
    }

    @Test
    void increaseRedirectCountTest() {
        ShortenUrl shortenUrl = new ShortenUrl("https://www.google.co.kr", "abcd123");

        when(shortenUrlRepository.save(shortenUrl)).thenReturn(shortenUrl);
        shortenUrlService.increaseRedirectCount(shortenUrl);

        assertEquals(1, shortenUrl.getRedirectCount());
    }
}