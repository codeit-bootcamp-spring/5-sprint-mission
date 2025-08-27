package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 정보")
public record UserLoginRequest(
    /*@NotBlank*/ String username,
    /*@NotBlank*/ String password
) {

}
