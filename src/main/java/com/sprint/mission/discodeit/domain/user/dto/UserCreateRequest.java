package com.sprint.mission.discodeit.domain.user.dto;

public record UserCreateRequest(
    String username,
    String email,
    String password
) {

}
