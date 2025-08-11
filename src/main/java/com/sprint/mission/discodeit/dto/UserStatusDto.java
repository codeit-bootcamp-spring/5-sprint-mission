package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto() {

    public record CreateUserStatus(UUID userId) {}
    public record UpdateUserStatus(UUID id, Instant newAccessTime) {}
}
