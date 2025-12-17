package com.sprint.mission.discodeit.event.message;

import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class UpdatedEvent<T> {

    private final T from;
    private final T to;
    private final Instant updatedAt;

    protected UpdatedEvent(final T from, final T to, final Instant updatedAt) {
        this.from = from;
        this.to = to;
        this.updatedAt = updatedAt;
    }
}
