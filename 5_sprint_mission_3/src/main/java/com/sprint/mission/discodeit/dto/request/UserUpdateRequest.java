package com.sprint.mission.discodeit.dto.request;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record UserUpdateRequest(
        UUID id,
        @Nullable String username,
        @Nullable String email,
        @Nullable String password,
        @Nullable BinaryContentCreateRequest profileImage
) {}
