package com.sprint.mission.discodeit.dto.request;

public record UserUpdateRequest(
    /*@NotBlank*/ String newUsername,
    /*@NotBlank @Email*/ String newEmail,
    /*@NotBlank*/ String newPassword
) {

}
