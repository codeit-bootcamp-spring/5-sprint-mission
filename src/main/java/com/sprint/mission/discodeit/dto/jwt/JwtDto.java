package com.sprint.mission.discodeit.dto.jwt;

import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtDto {

    private final UserResponse UserDto;
    private final String accessToken;
}