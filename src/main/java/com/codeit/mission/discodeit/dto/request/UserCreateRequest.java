package com.codeit.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "사용자 생성 요청 정보")
public record UserCreateRequest(
    @Schema(description = "username")
    @NotBlank(message = "username은 필수입니다.")
    String username,

    @Schema(description = "email")
    @NotBlank(message = "email은 필수입니다.")
    String email,

    @Schema(description = "password")
    @NotBlank(message = "password는 필수입니다.")
    String password
) {

}