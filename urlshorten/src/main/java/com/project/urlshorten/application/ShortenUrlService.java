package com.project.urlshorten.application;

import com.project.urlshorten.domain.ShortKeyGenerator;
import com.project.urlshorten.domain.ShortenUrl;
import com.project.urlshorten.infrastructure.ShortenUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void saveShortenUrl(ShortenUrl shortenUrl) {
        shortenUrlRepository.save(shortenUrl);
    }
}
