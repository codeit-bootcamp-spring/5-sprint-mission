package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserUpdateRequest", description = "수정할 User 정보")
public record UserUpdateRequest(
        @Schema(description = "새 사용자명")
        String newUsername,
        @Schema(description = "새 이메일")
        String newEmail,
        @Schema(description = "새 비밀번호")
        String newPassword
) {
}
