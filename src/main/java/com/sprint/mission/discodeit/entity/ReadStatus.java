package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

// 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델
// 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용

@Getter
@ToString
public class ReadStatus {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt; // 마지막으로 읽은 시간

    private UUID userId;
    private UUID channelId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id  = UUID.randomUUID();
        this.createdAt = Instant.now();;
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void update(){
        this.updatedAt = Instant.now();;
    }
}
