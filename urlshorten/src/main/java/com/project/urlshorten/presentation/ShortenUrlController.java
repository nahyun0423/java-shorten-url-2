package com.project.urlshorten.presentation;

import com.project.urlshorten.application.ShortenUrlService;
import com.project.urlshorten.domain.ShortenUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Controller
public class ShortenUrlController {
    private ShortenUrlService shortenUrlService;

    @Autowired
    public ShortenUrlController(ShortenUrlService shortenUrlService) {
        this.shortenUrlService = shortenUrlService;
    }

    @PostMapping("/shorten-url")
    public ResponseEntity<ShortenUrlDto> createShortenUrl(@RequestBody Map<String, Object> map) {
        String originalUrl = (String) map.get("originalUrl");
        ShortenUrlDto shortenUrlDto = shortenUrlService.createShortKey(originalUrl);
        return ResponseEntity.ok(shortenUrlDto);
    }

    @GetMapping("/get/{shortKey}")
    public ResponseEntity<ShortenUrlDto> getShortenUrl(@PathVariable String shortKey) {
        ShortenUrlDto shortenUrlDto = shortenUrlService.findShortenUrl(shortKey);
        return ResponseEntity.ok(shortenUrlDto);
    }

    @GetMapping("/get/all") // ?page=0&size=10 : 첫번째 페이지에 10개 항목만
    public ResponseEntity<Page<ShortenUrlDto>> getAllShortenUrl(Pageable pageable) {
        Page<ShortenUrlDto> urlList = shortenUrlService.findAllShortenUrl(pageable);
        return ResponseEntity.ok(urlList);
    }

    @Transactional
    @GetMapping("/{shortKey}")
    public String redirectShortenUrl(@PathVariable String shortKey) {
        ShortenUrl shortenUrl = shortenUrlService.increaseRedirectCount(shortKey);
        return "redirect:" + shortenUrl.getOriginalUrl();
    }
}
