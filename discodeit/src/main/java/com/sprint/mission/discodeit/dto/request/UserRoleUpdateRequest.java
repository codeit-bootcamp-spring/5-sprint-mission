package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserRoleUpdateRequest(
        UUID userId,

        @NotBlank(message = "사용자 권한은 필수입니다")
        Role newRole
) {
}
