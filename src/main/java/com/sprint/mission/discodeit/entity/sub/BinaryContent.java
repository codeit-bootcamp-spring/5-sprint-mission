package com.sprint.mission.discodeit.entity.sub;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
final class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;

    private final String fileName;
    private final String contentType;

    private final Long size;

    private final byte[] bytes;

    private final Instant createdAt;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;
    }
}