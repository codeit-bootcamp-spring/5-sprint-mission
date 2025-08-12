package com.sprint.mission.discodeit.entity;

import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Getter
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;

    private final String fileName; // 파일 이름
    private final long size; // 파일 사이즈
    private final String contentType; // 파일 타입
    private final UUID ownerId;
    private byte[] bytes; // 실제 파일 데이터

    //기본 생성자
    public BinaryContent(UUID id, Instant createdAt, UUID ownerId, String fileName, String contentType, long size) {
        this.id = id;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
    }
}
