package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserCreateRequest", description = "User 생성 정보")
public record UserCreateRequest(
        @Schema(description = "사용자명")
        String username,
        @Schema(description = "이메일")
        String email,
        @Schema(description = "비밀번호")
        String password
) {
}
