package com.sprint.mission.discodeit.dto.user;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {

    @Builder
    public record response(
            UUID userId,
            boolean online,
            Instant lastAccessedAt
    ) {}
}
