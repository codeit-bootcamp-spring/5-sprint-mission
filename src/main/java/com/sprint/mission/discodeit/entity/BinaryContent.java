package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


@Getter
@ToString
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private final String fileName;
    private final String contentType;
    private final Long size;
    private final byte[] bytes;
    private final UUID userId;
    private UUID messageId;

    public BinaryContent(UUID id, UUID userId, byte[] bytes, String fileName, String contentType, Long size, UUID messageId) {
        this.id = id;
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;
        this.userId = userId;
        this.messageId = messageId;
    }

    public void setMessageId(UUID MessageId) {
        this.messageId = MessageId;
    }
}
