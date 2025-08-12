package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 사용자의 마지막 접속 시각을 표현하는 도메인.
 * - 온라인 여부 판단에 사용
 */

@Getter
public class UserStatus {

    // 고유 식별자
    private UUID id;

    // 사용자 식별자(단방향 ID 참조)
    private UUID userId;

    // 마지막 접속 시각
    private Instant lastSeenAt;

    // 생성/수정 시각             <--------- 이것도 필요한지 봐야함
    private Instant createdAt;
    private Instant updatedAt;

    // 온라인 판정 임계값(요구사항: 5분)
    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

    public UserStatus() {}

    public static UserStatus create(UUID userId, Instant lastSeenAt) {
        UserStatus us = new UserStatus();
        us.id = UUID.randomUUID();
        us.userId = Objects.requireNonNull(userId, "userId");
        us.lastSeenAt = Objects.requireNonNullElseGet(lastSeenAt, Instant::now);
        us.createdAt = Instant.now();
        us.updatedAt = us.createdAt;
        return us;
    }

    // 마지막 접속 시각 갱신
    public void seenNow() {
        this.lastSeenAt = Instant.now();
        this.updatedAt = this.lastSeenAt;
    }

    // 요구사항 메서드: 현재 시각 기준 온라인 여부
    public boolean isOnline() {
        return isOnlineAt(Instant.now());
    }

    // 테스트 용이성을 위한 주입형 판단 메서드
    public boolean isOnlineAt(Instant now) {
        Objects.requireNonNull(now, "now");
        return !now.isBefore(lastSeenAt) && Duration.between(lastSeenAt, now).compareTo(ONLINE_THRESHOLD) <= 0;
    }

}
