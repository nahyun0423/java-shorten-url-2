package com.project.urlshorten.presentation;

import com.project.urlshorten.application.ShortenUrlService;
import com.project.urlshorten.domain.ShortenUrl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Validated
@RestController
public class ShortenUrlController {
    private ShortenUrlService shortenUrlService;

    @Autowired
    public ShortenUrlController(ShortenUrlService shortenUrlService) {
        this.shortenUrlService = shortenUrlService;
    }

    @PostMapping("/shorten-url")
    public ResponseEntity<ShortenUrlDto> createShortenUrl(@Valid @RequestBody ShortenUrlDto requestDto) {
        ShortenUrlDto shortenUrlDto = shortenUrlService.createShortKey(requestDto.getOriginalUrl());
        return ResponseEntity.ok(shortenUrlDto);
    }

    @GetMapping("/shorten-url/{shortKey}")
    public ResponseEntity<ShortenUrlDto> getShortenUrl(@PathVariable String shortKey) {
        ShortenUrlDto shortenUrlDto = shortenUrlService.findShortenUrl(shortKey);
        return ResponseEntity.ok(shortenUrlDto);
    }

    @GetMapping("/shorten-url") // ?page=0&size=10 : 첫번째 페이지에 10개 항목만
    public ResponseEntity<Page<ShortenUrlDto>> getAllShortenUrl(
            @RequestParam(defaultValue = "0") @Min(0) @Max(100) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(10) int size) {
        Pageable validatedPageable = PageRequest.of(page, size);
        Page<ShortenUrlDto> urlList = shortenUrlService.findAllShortenUrl(validatedPageable);
        return ResponseEntity.ok(urlList);
    }

    @GetMapping("/{shortKey}")
    public void redirectShortenUrl(@PathVariable String shortKey, HttpServletResponse response) throws IOException {
        ShortenUrl shortenUrl = shortenUrlService.increaseRedirectCount(shortKey);
        response.sendRedirect(shortenUrl.getOriginalUrl());
    }
}