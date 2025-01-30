package com.project.urlshorten.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.project.urlshorten.domain.ShortenUrl;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenUrlDto {

    @NotBlank(message = "URL은 필수 입력값입니다.")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "올바른 URL 형식이 아닙니다.")
    private String originalUrl;
    private String shortKey;
    private int redirectCount;

    public ShortenUrlDto(ShortenUrl shortenUrl) {
        this.originalUrl = shortenUrl.getOriginalUrl();
        this.shortKey = shortenUrl.getShortKey();
        this.redirectCount = shortenUrl.getRedirectCount();
    }

    public ShortenUrlDto() {
    }
}
