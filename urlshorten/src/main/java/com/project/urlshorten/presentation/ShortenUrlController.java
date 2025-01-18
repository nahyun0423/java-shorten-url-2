package com.project.urlshorten.presentation;

import com.project.urlshorten.application.ShortenUrlService;
import com.project.urlshorten.domain.ShortenUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ShortenUrlController {
    private ShortenUrlService shortenUrlService;

    @Autowired
    public ShortenUrlController(ShortenUrlService shortenUrlService) {
        this.shortenUrlService = shortenUrlService;
    }

    @PostMapping("/shorten-url")
    public ResponseEntity<ShortenUrlDto> createShortenUrl (@RequestBody Map<String, Object> map) {
        String originalUrl = (String)map.get("originalUrl");
        String shortKey = shortenUrlService.createShortKey(originalUrl);

        ShortenUrl shortenUrl = new ShortenUrl(originalUrl, shortKey);
        shortenUrlService.saveShortenUrl(shortenUrl);

        return ResponseEntity.ok(new ShortenUrlDto(shortenUrl));
    }
}
