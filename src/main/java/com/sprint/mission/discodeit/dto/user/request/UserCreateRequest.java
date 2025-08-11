package com.sprint.mission.discodeit.dto.user.request;

public record UserCreateRequest (
        String username,
        String email,
        String password
) {}
