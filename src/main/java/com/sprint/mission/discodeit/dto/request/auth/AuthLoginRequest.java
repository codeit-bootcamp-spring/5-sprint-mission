package com.sprint.mission.discodeit.dto.request.auth;

import jakarta.validation.constraints.Email;

public record AuthLoginRequest(

        @Email
        String email,
        String password
) {
}
