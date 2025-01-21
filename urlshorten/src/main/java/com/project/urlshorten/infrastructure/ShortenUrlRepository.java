package com.project.urlshorten.infrastructure;

import com.project.urlshorten.domain.ShortenUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortenUrlRepository extends JpaRepository<ShortenUrl, Long> {
    Optional<ShortenUrl> findByShortKey(String shortKey);
}
