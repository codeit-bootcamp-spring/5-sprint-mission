package com.sprint.mission.discodeit.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(of = {"userId", "channelId"})
public class ReadStatus implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private final UUID channelId;

    private Instant createdAt;
    private Instant updatedAt;

    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = Objects.requireNonNull(userId, "userId");
        this.channelId = Objects.requireNonNull(channelId, "channelId");

        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = createdAt;
        this.lastReadAt = createdAt;
    }

    public ReadStatus(UUID userId, UUID channelId, Clock clock) { // Clock 이용한 테스트용
        this.id = UUID.randomUUID();
        this.userId = Objects.requireNonNull(userId, "userId");
        this.channelId = Objects.requireNonNull(channelId, "channelId");

        Instant now = Instant.now(clock).truncatedTo(ChronoUnit.SECONDS);
        this.createdAt = now;
        this.updatedAt = now;
        this.lastReadAt = now;
    }

    public boolean isUnread(Instant messageCreatedAt) {
        return Objects.requireNonNull(messageCreatedAt, "messageCreatedAt").isAfter(this.lastReadAt);
    }

    public boolean markReadNow() {
        return markRead(Instant.now());
    }

    public boolean markRead(Instant newLastReadAt) {
        Objects.requireNonNull(newLastReadAt, "newLastReadAt");
        if (this.lastReadAt == null || newLastReadAt.isAfter(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            this.updatedAt = Instant.now();
            return true;
        }
        return false;
    }

    public boolean markRead(Instant newLastReadAt, Clock clock) { // Test용 메서드
        Objects.requireNonNull(clock, "clock");
        if (this.lastReadAt == null || newLastReadAt.isAfter(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            this.updatedAt = Instant.now(clock);
            return true;
        }
        return false;
    }
}
