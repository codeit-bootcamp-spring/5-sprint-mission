package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    private String filename;
    private ContentType contentType;
    private Long size;
    private byte[] bytes;


    public BinaryContent(String fileName, Long size, ContentType contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.filename = fileName;
        this.contentType = contentType;
        this.size = size;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public enum ContentType { // Entity에 따로 뺄지 BinaryContent 내부에 포함할 지 고민됩니다
        TEXT,
        IMAGE,
        VIDEO
    }
}
