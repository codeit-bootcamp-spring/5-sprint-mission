package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private final UUID id;
    private final String fileName;
    private final String fileType;
    private final byte[] data;
    private final Long fileSize;
    private final Instant createdAt;

    public BinaryContent(String fileName, String fileType, byte[] data, Long fileSize) {
        this(UUID.randomUUID(), fileName, fileType, data, fileSize, Instant.now());
    }

    public BinaryContent(UUID id, String fileName, String fileType, byte[] data, Long fileSize, Instant createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
    }
}
