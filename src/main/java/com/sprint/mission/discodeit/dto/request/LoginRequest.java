package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginRequest", description = "로그인 정보")
public record LoginRequest(
        @Schema(description = "사용자명")
        String username,
        @Schema(description = "비밀번호")
        String password
) {
}
