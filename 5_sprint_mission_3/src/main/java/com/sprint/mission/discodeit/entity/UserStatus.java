package com.sprint.mission.discodeit.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private Instant lastAccessAt;
    @Setter
    private boolean online;

    public UserStatus(UUID userId, Instant lastAccessAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.userId = userId;
        this.lastAccessAt = lastAccessAt;
        this.online = true;
    }

    public void userAccessUpdate(Instant newLastAccessAt) {
        if (newLastAccessAt !=null && !newLastAccessAt.equals(this.lastAccessAt)) {
            this.lastAccessAt = newLastAccessAt;
            this.updatedAt = Instant.now();
        }
        this.online = true;
    }

    public boolean isOnline() {
        return lastAccessAt != null &&
                Instant.now().minusSeconds(300).isBefore(lastAccessAt);
    }
}
