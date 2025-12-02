package com.sprint.mission.discodeit.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record UserCreateMultipartForm(
    @Schema(implementation = UserCreateRequest.class)
    UserCreateRequest userCreateRequest,
    @Schema(type = "string", format = "binary", description = "User 프로필 이미지")
    MultipartFile profile
) {
}
