package com.project.urlshorten.presentation;

import com.project.urlshorten.application.ShortenUrlService;
import com.project.urlshorten.domain.ShortenUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ShortenUrlController {
    private ShortenUrlService shortenUrlService;

    @Autowired
    public ShortenUrlController(ShortenUrlService shortenUrlService) {
        this.shortenUrlService = shortenUrlService;
    }

    @PostMapping("/shorten-url")
    public ResponseEntity<ShortenUrlDto> createShortenUrl(@RequestBody Map<String, Object> map) {
        String originalUrl = (String) map.get("originalUrl");
        String shortKey = shortenUrlService.createShortKey(originalUrl);

        ShortenUrl shortenUrl = shortenUrlService.saveShortenUrl(new ShortenUrl(originalUrl, shortKey));

        return ResponseEntity.ok(new ShortenUrlDto(shortenUrl));
    }

    @GetMapping("/get/{shortKey}")
    public ResponseEntity<ShortenUrlDto> getShortenUrl(@PathVariable String shortKey) {
        ShortenUrl shortenUrl = shortenUrlService.findShortenUrl(shortKey);
        return ResponseEntity.ok(new ShortenUrlDto(shortenUrl));
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<ShortenUrlDto>> getAllShortenUrl() {
        List<ShortenUrlDto> urlList = shortenUrlService.findAllShortenUrl();
        return ResponseEntity.ok(urlList);
    }
}
