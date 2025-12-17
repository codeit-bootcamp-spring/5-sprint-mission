package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RoleUpdatedEvent {
    private final UUID userId;
    private final Role oldRole;
    private final Role newRole;
}