package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record UserUpdateMultipartForm(

    @Schema(implementation = UserUpdateRequest.class)
    UserUpdateRequest userUpdateRequest,

    @Schema(type = "string", format = "binary", description = "수정할 User 프로필 이미지")
    MultipartFile profile
) {

}
