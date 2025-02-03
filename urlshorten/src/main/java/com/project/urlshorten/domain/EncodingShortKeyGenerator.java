package com.project.urlshorten.domain;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@Component
public class EncodingShortKeyGenerator implements ShortKeyGenerator {
    @Override
    public String generateShortKey(String originalUrl) {
        UUID uuid = UUID.randomUUID();
        String encodedUUID = Base64.getUrlEncoder().withoutPadding().encodeToString(toBytes(uuid));
        return encodedUUID.substring(0, 7);
    }

    private byte[] toBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }
}
