package com.sprint.mission.discodeit.dto.response;

public record LoginDto(
        String userName,
        String email,
        String phoneNumber,
        byte[] profileImage
) {
}
