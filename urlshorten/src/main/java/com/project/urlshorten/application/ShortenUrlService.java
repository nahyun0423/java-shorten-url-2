package com.project.urlshorten.application;

import com.project.urlshorten.domain.ShortKeyGenerator;
import com.project.urlshorten.domain.ShortenUrl;
import com.project.urlshorten.exception.CustomException;
import com.project.urlshorten.infrastructure.ShortenUrlRepository;
import com.project.urlshorten.presentation.ShortenUrlDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.urlshorten.exception.ErrorCode.SHORTKEY_NOT_FOUND;

@Service
public class ShortenUrlService {

    private final ShortenUrlRepository shortenUrlRepository;
    private final ShortKeyGenerator shortKeyGenerator;

    @Autowired
    public ShortenUrlService(ShortenUrlRepository shortenUrlRepository, ShortKeyGenerator shortKeyGenerator) {
        this.shortenUrlRepository = shortenUrlRepository;
        this.shortKeyGenerator = shortKeyGenerator;
    }

    public String createShortKey(String originalUrl) {
        return shortKeyGenerator.generateShortKey(originalUrl);
    }

    public ShortenUrl saveShortenUrl(ShortenUrl shortenUrl) {
        return shortenUrlRepository.save(shortenUrl);
    }

    public ShortenUrl findShortenUrl(String shortKey) {
        if (shortenUrlRepository.findByShortKey(shortKey) == null) {
            throw new CustomException(SHORTKEY_NOT_FOUND);
        }
        return shortenUrlRepository.findByShortKey(shortKey);
    }

    public List<ShortenUrlDto> findAllShortenUrl() {
        return shortenUrlRepository.findAll().stream()
                .map(shortenUrl -> new ShortenUrlDto(shortenUrl))
                .collect(Collectors.toList());
    }

    public void increaseRedirectCount(ShortenUrl shortenUrl) {
        shortenUrl.increaseRedirectCount();
        shortenUrlRepository.save(shortenUrl);
    }
}
