package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequest(
    /*@NotBlank*/ String newUsername,
    /*@NotBlank @Email*/ String newEmail,
    /*@NotBlank*/ String newPassword
) {

}
