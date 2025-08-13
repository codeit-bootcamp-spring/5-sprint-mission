package com.codeit.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class BinaryContent implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;

    private String fileName;
    private String contentType;
    private Long size;
    private byte[] bytes;

    private final UUID profileId;
    private final UUID attachmentId;

    public BinaryContent(String fileName, String contentType, Long size, byte[] bytes, UUID profileId, UUID attachmentId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;

        this.profileId = profileId;
        this.attachmentId = attachmentId;
    }
}
