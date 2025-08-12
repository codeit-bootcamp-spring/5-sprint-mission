package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.time.Instant;
import java.util.UUID;


@Getter
@ToString
public class BinaryContent extends BaseEntity implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String fileName;
    private String contentType; // jpg, ... 확장자
    private Long size;
    private byte[] bytes;

    public BinaryContent(String fileName, String contentType, Long size, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;
    }
}
