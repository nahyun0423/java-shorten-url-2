package com.project.urlshorten.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

@Getter
@Setter
@Entity
public class ShortenUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;
    private String shortKey;
    private int redirectCount;

    public ShortenUrl(String originalUrl, String shortKey) {
        this.originalUrl = originalUrl;
        this.shortKey = shortKey;
        this.redirectCount = 0;
    }

    public ShortenUrl() {
    }

    public void increaseRedirectCount() {
        this.redirectCount++;
    }
}
