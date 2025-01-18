package com.project.urlshorten.presentation;

import com.project.urlshorten.domain.ShortenUrl;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenUrlDto {
    private Long id;
    private String originalUrl;
    private String shortKey;
    private int redirectCount;

    public ShortenUrlDto(ShortenUrl shortenUrl) {
        this.originalUrl = shortenUrl.getOriginalUrl();
        this.shortKey = shortenUrl.getShortKey();
        this.redirectCount = shortenUrl.getRedirectCount();    }
}
