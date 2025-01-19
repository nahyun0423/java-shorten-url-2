package com.project.urlshorten.presentation;

import com.project.urlshorten.application.ShortenUrlService;
import com.project.urlshorten.domain.ShortenUrl;
import com.project.urlshorten.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static com.project.urlshorten.exception.ErrorCode.INVALID_URL;

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

        try {
            new URI(originalUrl);
        } catch (URISyntaxException e) {
            throw new CustomException(INVALID_URL);
        }

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

    @GetMapping("/{shortKey}")
    public String redirectShortenUrl(@PathVariable String shortKey) {
        ShortenUrl shortenUrl = shortenUrlService.findShortenUrl(shortKey);
        shortenUrlService.increaseRedirectCount(shortenUrl);
        return "redirect:" + shortenUrl.getOriginalUrl();
    }
}
