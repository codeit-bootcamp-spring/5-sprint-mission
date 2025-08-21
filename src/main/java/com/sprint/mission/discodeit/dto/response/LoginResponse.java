package com.sprint.mission.discodeit.dto.response;

public record LoginResponse(
    String userName,
    String email,
    String phoneNumber,
    byte[] profileImage
) {

}

