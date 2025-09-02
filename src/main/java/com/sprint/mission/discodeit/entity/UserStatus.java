package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델
 * 사용자의 온라인 상태를 확인하기 위해 사용
 */
@Getter
public class UserStatus extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID userId;
    private Instant lastAccessAt;

    public UserStatus(UUID userId, Instant lastAccessAt) {
        this.userId = userId;
        this.lastAccessAt = lastAccessAt;
    }

    public boolean online() {
        Duration duration = Duration.between(lastAccessAt, Instant.now());
        return duration.toMinutes() <= 5;
    }

    public void update(Instant lastAccessAt) {
        boolean anyValueUpdated = false;
        if (lastAccessAt != null && !lastAccessAt.equals(this.lastAccessAt)) {
            this.lastAccessAt = lastAccessAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.setUpdatedAt(Instant.now());
        }
    }

    @Override
    public String toString() {
        return super.toString() + " UserStatus{" +
                "userId=" + userId +
                ", lastAccessAt=" + lastAccessAt +
                '}';
    }
}
