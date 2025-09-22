package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "username은 필수입니다.") String username,
    @NotBlank(message = "password는 필수입니다.") String password
) {}
