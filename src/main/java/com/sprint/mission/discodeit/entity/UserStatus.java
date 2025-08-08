package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final long ONLINE_WINDOW_MINUTES = 5L;

    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;

    private Instant updatedAt;
    private Instant lastAccessedAt;

    /** 풀 생성자 (파일/JCF 저장 복원 시 사용) */
    public UserStatus(UUID id, UUID userId, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
        this.updatedAt = Objects.requireNonNullElse(this.createdAt, updatedAt);
        this.lastAccessedAt = this.updatedAt;
    }

    /** 편의 생성자: userId와 마지막 접속시각만으로 생성 */
    public UserStatus(UUID userId, Instant lastOnlineAt) {
        this(UUID.randomUUID(), userId, Instant.now(), Instant.now());
        this.lastAccessedAt = (lastOnlineAt == null ? this.updatedAt : lastOnlineAt);
    }

    /** 최근 접속시각 기준 온라인 여부(5분 이내) */
    public boolean isOnline() {
        return Duration.between(lastAccessedAt, Instant.now()).toMinutes() <= ONLINE_WINDOW_MINUTES;
    }

    /** 지금 시각으로 갱신 */
    public void update() {
        this.lastAccessedAt = Instant.now();
        this.updatedAt = this.lastAccessedAt;
    }

    /** 지정 시각으로 갱신(널이면 현재 시각) */
    public void update(Instant when) {
        this.lastAccessedAt = (when == null ? Instant.now() : when);
        this.updatedAt = Instant.now();
    }
}
