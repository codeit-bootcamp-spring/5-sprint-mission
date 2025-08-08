package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class BinaryContent implements java.io.Serializable {

    private final UUID id;
    private final Instant createdAt;

    private final String fileName;
    private final String contentType;
    private final Long size;
    private final byte[] bytes;
    private UUID ownerId;

    public BinaryContent(String fileName, String contentType, Long size, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;
    }
}
