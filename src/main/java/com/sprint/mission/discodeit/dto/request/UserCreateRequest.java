package com.sprint.mission.discodeit.dto.request;

public record UserCreateRequest(
    /*@NotBlank*/ String username,
    /*@NotBlank @Email*/ String email,
    /*@NotBlank*/ String password
) {

}
