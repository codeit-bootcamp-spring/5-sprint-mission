package com.codeit.mission.discodeit.dto.binarycontent;

import com.codeit.mission.discodeit.entity.BinaryContent;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class BinaryContentResponse {
    private final UUID id;
    private final Instant createdAt;

    private final String fileName;
    private final String contentType;
    private final Long size;
    private final byte[] bytes;

    public BinaryContentResponse(BinaryContent binaryContent) {
        this.id = binaryContent.getId();
        this.createdAt = binaryContent.getCreatedAt();
        this.fileName = binaryContent.getFileName();
        this.contentType = binaryContent.getContentType();
        this.size = binaryContent.getSize();
        this.bytes = binaryContent.getBytes();
    }
}