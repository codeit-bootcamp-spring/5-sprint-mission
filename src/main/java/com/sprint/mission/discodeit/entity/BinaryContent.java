package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String fileName;
    private Long fileSize;
    private byte[] content;
    private String contentType;
    private Instant createdAt;

    public BinaryContent(byte[] content, String contentType, String fileName, Long fileSize) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.contentType = contentType;
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public static BinaryContent of(byte[] content, String contentType, String fileName, Long fileSize) {
        return new BinaryContent(content, contentType, fileName, fileSize);
    }

    public static BinaryContent of(MultipartFile file) {
        try {
            byte[] content = file.getBytes();
            String contentType = file.getContentType();
            String fileName = file.getName();
            Long fileSize = file.getSize();

            return new BinaryContent(content, contentType, fileName, fileSize);
        } catch (IOException e) {
            // TODO 필요하면 추가 처리
            throw new RuntimeException("BinaryContent of error", e);
        }
    }
}