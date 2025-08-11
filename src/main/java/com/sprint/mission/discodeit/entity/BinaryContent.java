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

    private BinaryContentType binaryContentType; // 프로필 || 첨부파일
    private String name; // 파일명
    private String contentType; // 확장자
    private byte[] bytes; // 데이터

    public BinaryContent(String name, String contentType, BinaryContentType binaryContentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.binaryContentType = binaryContentType;
        this.name = name;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}
