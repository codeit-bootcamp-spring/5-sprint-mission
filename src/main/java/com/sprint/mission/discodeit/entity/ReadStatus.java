package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 사용자가 특정 채널에서 마지막으로 메시지를 읽은 시각을 표현하는 도메인.
 * - unread 계산에 활용
 * - 단순 ID 참조로 결합도 최소화(userId, channelId)
 */
@Getter
public class ReadStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    // 고유 식별자
    private UUID id;
    // 생성/수정 시각 (BinaryContent 제외 모델은 갱신 가능하므로 updatedAt 유지)
    private Instant createdAt;
    private Instant updatedAt;


    // 사용자 식별자(단방향 ID 참조키)
    private UUID userId;
    // 채널 식별자(단방향 ID 참조키)
    private UUID channelId;
    // 마지막으로 읽은 시각
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant newLastReadAt) {
        boolean anyValueUpdated = false;

        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt=Instant.now();
        }
    }


}
