package com.project.urlshorten.application;

import com.project.urlshorten.domain.ShortKeyGenerator;
import com.project.urlshorten.domain.ShortenUrl;
import com.project.urlshorten.exception.ShortkeyNotFoundException;
import com.project.urlshorten.infrastructure.ShortenUrlRepository;
import com.project.urlshorten.presentation.ShortenUrlDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("동일한 URL로 두 번 단축 URL 생성하면, 서로 다른 단축 URL을 반환")
    void createShortKeyTest() throws InterruptedException {
        String originalUrl = "https://www.google.co.kr";
        String expectedShortKey1 = "abcd123";
        String expectedShortKey2 = "abcd456";

        ShortenUrl shortenUrl1 = new ShortenUrl(originalUrl, expectedShortKey1);
        ShortenUrl shortenUrl2 = new ShortenUrl(originalUrl, expectedShortKey2);

        when(shortKeyGenerator.generateShortKey(originalUrl))
                .thenReturn(expectedShortKey1, expectedShortKey2);

        when(shortenUrlRepository.save(any(ShortenUrl.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShortenUrlDto result1 = shortenUrlService.createShortKey(originalUrl);
        ShortenUrlDto result2 = shortenUrlService.createShortKey(originalUrl);

        assertNotEquals(result1.getShortKey(), result2.getShortKey());
    }

    @Test
    @DisplayName("단축 URL을 저장 하면, 동일한 데이터를 반환")
    void saveShortenUrlTest() {
        ShortenUrl shortenUrl = new ShortenUrl("https://www.google.co.kr", "abcd123");

        when(shortenUrlRepository.save(shortenUrl)).thenReturn(shortenUrl);
        ShortenUrl savedUrl = shortenUrlService.saveShortenUrl(shortenUrl);

        assertNotNull(savedUrl);
        assertEquals(shortenUrl.getOriginalUrl(), savedUrl.getOriginalUrl());
        assertEquals(shortenUrl.getShortKey(), savedUrl.getShortKey());
    }

    @Test
    @DisplayName("단축 키를 조회하면, 원본 URL 정보를 반환")
    void findShortenUrlSuccessTest() {
        ShortenUrl shortenUrl = new ShortenUrl("https://www.google.co.kr", "abcd123");

        when(shortenUrlRepository.findByShortKey(shortenUrl.getShortKey())).thenReturn(Optional.of(shortenUrl));
        ShortenUrlDto result = shortenUrlService.findShortenUrl(shortenUrl.getShortKey());

        assertNotNull(result);
        assertEquals(shortenUrl.getOriginalUrl(), result.getOriginalUrl());
        assertEquals(shortenUrl.getShortKey(), result.getShortKey());
    }

    @Test
    @DisplayName("존재하지 않는 단축 키를 조회하면, 예외가 발생")
    void findShortenUrlFailTest() {
        ShortenUrl shortenUrl = new ShortenUrl("https://www.google.co.kr", "abcd123");

        when(shortenUrlRepository.findByShortKey(shortenUrl.getShortKey())).thenReturn(Optional.empty());
        Exception exception = assertThrows(ShortkeyNotFoundException.class, () -> shortenUrlService.findShortenUrl(shortenUrl.getShortKey()));

        assertEquals(shortenUrl.getShortKey() + "는 존재하지 않는 shortkey입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("전체 단축 URL 목록을 조회하면, 모든 데이터를 반환")
    void findAllShortenUrlTest() {
        ShortenUrl shortenUrl1 = new ShortenUrl("https://www.google.co.kr", "abcd123");
        ShortenUrl shortenUrl2 = new ShortenUrl("https://www.naver.com", "abcd456");
        Page<ShortenUrl> shortenUrls = new PageImpl<>(List.of(shortenUrl1, shortenUrl2));

        when(shortenUrlRepository.findAll(any(Pageable.class))).thenReturn(shortenUrls);
        Page<ShortenUrlDto> result = shortenUrlService.findAllShortenUrl(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(shortenUrl1.getOriginalUrl(), result.getContent().get(0).getOriginalUrl());
        assertEquals(shortenUrl2.getOriginalUrl(), result.getContent().get(1).getOriginalUrl());
    }

    @Test
    @DisplayName("리다이렉트 카운트를 1 증가")
    void increaseRedirectCountTest() {
        ShortenUrl shortenUrl = new ShortenUrl("https://www.google.co.kr", "abcd123");

        when(shortenUrlRepository.findByShortKey("abcd123")).thenReturn(Optional.of(shortenUrl));

        ShortenUrl foundShortenUrl = shortenUrlService.increaseRedirectCount(shortenUrl.getShortKey());

        assertEquals(1, foundShortenUrl.getRedirectCount());
    }
}