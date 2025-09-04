package com.sprint.mission.discodeit.dto.request;

public record LoginRequest(
    /*@NotBlank*/ String username,
    /*@NotBlank*/ String password
) {

}
