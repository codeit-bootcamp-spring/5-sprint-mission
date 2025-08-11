package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    public final UUID id; // 고유 내부 식별자
    private final Instant createdAt; // 생성 시간
    public String userId;
    public UUID channelId;
    private Instant updatedAt;

    //final 생성자 초기화
    public ReadStatus(UUID id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }
}
