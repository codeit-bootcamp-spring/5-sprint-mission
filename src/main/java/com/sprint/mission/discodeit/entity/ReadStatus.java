package com.sprint.mission.discodeit.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(of = {"userId", "channelId"})
public class ReadStatus {
    @Serial private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private final UUID channelId;

    private Instant createdAt;
    private Instant updatedAt;

    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Clock clock) {
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
        if (newLastReadAt.isAfter(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            this.updatedAt = Instant.now();
            return true;
        }
        return false;
    }

    public boolean markRead(Instant newLastReadAt, Clock clock) {
        Objects.requireNonNull(clock, "clock");
        if (newLastReadAt.isAfter(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            this.updatedAt = Instant.now(clock).truncatedTo(ChronoUnit.SECONDS);
            return true;
        }
        return false;
    }
}
