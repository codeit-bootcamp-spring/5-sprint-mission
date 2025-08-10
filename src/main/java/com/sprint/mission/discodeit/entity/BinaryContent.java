package com.sprint.mission.discodeit.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
@Getter
@ToString
@NoArgsConstructor
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private String fileName;
    private String contentType; // 확장자
    private Long size;
    private byte[] bytes;
    private UUID userId;
    private UUID messageId;

    public BinaryContent(String fileName, String contentType, Long size, byte[] bytes, UUID userId, UUID messageId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;
        this.userId = userId;
        this.messageId = messageId;
    }
}
