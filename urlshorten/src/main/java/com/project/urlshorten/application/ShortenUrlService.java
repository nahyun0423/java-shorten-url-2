package com.project.urlshorten.application;

import com.project.urlshorten.domain.ShortKeyGenerator;
import com.project.urlshorten.domain.ShortenUrl;
import com.project.urlshorten.exception.ShortkeyNotFoundException;
import com.project.urlshorten.infrastructure.ShortenUrlRepository;
import com.project.urlshorten.presentation.ShortenUrlDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShortenUrlService {

    private final ShortenUrlRepository shortenUrlRepository;
    private final ShortKeyGenerator shortKeyGenerator;

    @Autowired
    public ShortenUrlService(ShortenUrlRepository shortenUrlRepository, ShortKeyGenerator shortKeyGenerator) {
        this.shortenUrlRepository = shortenUrlRepository;
        this.shortKeyGenerator = shortKeyGenerator;
    }

    @Transactional
    public ShortenUrlDto createShortKey(String originalUrl) {
        try {
            new URI(originalUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("적절한 url 형식이 아닙니다.");
        }

        String shortKey = shortKeyGenerator.generateShortKey(originalUrl);
        ShortenUrl shortenUrl = saveShortenUrl(new ShortenUrl(originalUrl, shortKey));

        return new ShortenUrlDto(shortenUrl);
    }

    public ShortenUrl saveShortenUrl(ShortenUrl shortenUrl) {
        return shortenUrlRepository.save(shortenUrl);
    }

    @Transactional(readOnly = true)
    public ShortenUrlDto findShortenUrl(String shortKey) {
        Optional<ShortenUrl> foundShortenUrl = shortenUrlRepository.findByShortKey(shortKey);

        if (foundShortenUrl.isEmpty()) {
            throw new ShortkeyNotFoundException(shortKey + "는 존재하지 않는 shortkey입니다.");
        }
        return new ShortenUrlDto(foundShortenUrl.get());
    }

    @Transactional(readOnly = true)
    public Page<ShortenUrlDto> findAllShortenUrl(Pageable pageable) {
        Page<ShortenUrl> shortenUrls = shortenUrlRepository.findAll(pageable);

        return shortenUrls.map(shortenUrl -> new ShortenUrlDto(shortenUrl));
    }

    @Transactional
    public ShortenUrl increaseRedirectCount(String shortKey) {
        Optional<ShortenUrl> foundShortenUrl = shortenUrlRepository.findByShortKey(shortKey);

        if (foundShortenUrl.isEmpty()) {
            throw new ShortkeyNotFoundException(shortKey);
        }

        ShortenUrl shortenUrl = foundShortenUrl.get();
        shortenUrl.increaseRedirectCount();
        return shortenUrl;
    }
}