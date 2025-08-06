package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;

    // 파일 관련 정보
    private final String fileName;
    private final String contentType;
    private final byte[] content;
    private final long fileSize;

    public BinaryContent(String fileName, String contentType, byte[] content, long fileSize) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
        this.fileSize = fileSize;
    }
}
