package com.sprint.mission.discodeit.dto.user;

import java.util.Optional;

/** create용 요청 DTO */
public record UserCreateRequest (
    String username,
    String email,
    String password
){
}
