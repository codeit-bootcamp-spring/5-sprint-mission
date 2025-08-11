package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class BinaryContent {
    private final UUID id; // 고유 내부 식별자
    private final Instant createdAt; // 생성 시간
    private final String fileName; // 파일 이름
    private final String contentType; // 파일 타입
    private final long size; // 파일 사이즈
    private String userId; // 유저 아이디

    //기본 생성자
    public BinaryContent(UUID id, Instant createdAt, String fileName, String contentType, long size) {
        this.id = id;
        this.createdAt = createdAt;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
    }
}
