package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private final UUID id;
    private final byte[] content;
    private final String contentType;
    private final Instant createdAt;

    public BinaryContent(byte[] content, String contentType) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.contentType = contentType;
        this.createdAt = Instant.now();
    }

    public static BinaryContent of(byte[] content, String contentType) {
        return new BinaryContent(content, contentType);
    }

    public static BinaryContent of(MultipartFile file) {
        try {
            byte[] content = file.getBytes();
            String contentType = file.getContentType();
            return new BinaryContent(content, contentType);
        } catch (IOException e) {
            // TODO 필요하면 추가 처리
            throw new RuntimeException("BinaryContent of error", e);
        }
    }
}