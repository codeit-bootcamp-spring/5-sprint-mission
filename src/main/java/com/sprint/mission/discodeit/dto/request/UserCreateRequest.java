package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User 생성 정보")
public record UserCreateRequest(
    /*@NotBlank*/ String username,
    /*@NotBlank @Email*/ String email,
    /*@NotBlank*/ String password
) {

}
