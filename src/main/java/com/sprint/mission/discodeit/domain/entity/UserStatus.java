package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.domain.enums.user.Status;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserStatus extends BaseEntity {

    private final UUID userId;
    private Status status = Status.OFFLINE;
    private Instant lastActiveAt;
    private boolean statusFixed;
    private boolean login;

    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

    public Status getStatus() {
        if (!login) return Status.OFFLINE;
        if (statusFixed) return status;
        if (lastActiveAt != null && lastActiveAt.isAfter(Instant.now().minus(ONLINE_THRESHOLD))) status = Status.ONLINE;
        else status = Status.IDLE;
        return status;
    }

    public void setStatus(Status status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.statusFixed = status != Status.ONLINE;
    }

    public void unfixStatus() {
        this.statusFixed = false;
    }

    public void login() {
        this.login = true;
        heartBeat();
        this.statusFixed = false;
        this.status = Status.ONLINE;
    }

    public void logout() {
        this.login = false;
        this.status = Status.OFFLINE;
    }

    public void heartBeat() {
        this.lastActiveAt = Instant.now();
    }
}
