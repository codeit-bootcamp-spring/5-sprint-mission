package com.sprint.mission.discodeit.dto.auth;

public record LoginRequestDto(
        String email,
        String password
) {}
