package com.sprint.mission.discodeit.dto.auth.request;

public record LoginRequest(
        String username,
        String password
) {}
