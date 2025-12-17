package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.entity.Role;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class RoleUpdatedEvent extends UpdatedEvent<Role> {

    private final UUID userId;

    public RoleUpdatedEvent(UUID userId, Role from, Role to, Instant updatedAt) {
        super(from, to, updatedAt);
        this.userId = userId;
    }
}
