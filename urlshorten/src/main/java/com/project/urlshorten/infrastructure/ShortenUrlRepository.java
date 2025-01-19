package com.project.urlshorten.infrastructure;

import com.project.urlshorten.domain.ShortenUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortenUrlRepository extends JpaRepository<ShortenUrl, Long> {
    ShortenUrl findByShortKey(String shortKey);
}
