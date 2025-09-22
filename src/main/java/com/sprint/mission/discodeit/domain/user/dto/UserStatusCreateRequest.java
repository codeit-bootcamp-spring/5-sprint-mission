package com.sprint.mission.discodeit.domain.user.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(

    @NotNull UUID userId,
    Instant lastActiveAt
) {}
