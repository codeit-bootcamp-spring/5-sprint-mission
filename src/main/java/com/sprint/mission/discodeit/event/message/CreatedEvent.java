package com.sprint.mission.discodeit.event.message;

import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class CreatedEvent<T> {

    private final T data;
    private final Instant createdAt;

    protected CreatedEvent(final T data, final Instant createdAt) {
        this.data = data;
        this.createdAt = createdAt;
    }
}
