package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UserRoleUpdateRequest(
        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userId,

        @NotBlank(message = "권한은 필수입니다.")
        Role role
) {
}