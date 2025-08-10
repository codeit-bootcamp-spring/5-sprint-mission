package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 사용자가 특정 채널에서 마지막으로 메시지를 읽은 시각을 표현하는 도메인.
 * - unread 계산에 활용
 * - 단순 ID 참조로 결합도 최소화(userId, channelId)
 */
@Getter
public class ReadStatus {

    // 고유 식별자
    private UUID id;

    // 사용자 식별자(단방향 ID 참조)
    private UUID userId;

    // 채널 식별자(단방향 ID 참조)
    private UUID channelId;

    // 마지막으로 읽은 시각
    private Instant lastReadAt;

    // 생성/수정 시각 (BinaryContent 제외 모델은 갱신 가능하므로 updatedAt 유지)
    private Instant createdAt;
    private Instant updatedAt;

    // 기본 생성자(프레임워크/직렬화용)
    public ReadStatus() {}

    // 생성 팩토리: 새 ReadStatus 생성 시 현재 시각으로 세팅
    public static ReadStatus create(UUID userId, UUID channelId, Instant lastReadAt) {
        ReadStatus rs = new ReadStatus();
        rs.id = UUID.randomUUID();
        rs.userId = Objects.requireNonNull(userId, "userId");
        rs.channelId = Objects.requireNonNull(channelId, "channelId");
        rs.lastReadAt = Objects.requireNonNullElseGet(lastReadAt, Instant::now);
        rs.createdAt = Instant.now();
        rs.updatedAt = rs.createdAt;
        return rs;
    }

    // 마지막 읽은 시각 갱신
    public void touch(Instant readAt) {
        this.lastReadAt = Objects.requireNonNullElseGet(readAt, Instant::now);
        this.updatedAt = Instant.now();
    }


}
