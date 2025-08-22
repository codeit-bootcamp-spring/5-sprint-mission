package com.sprint.mission.discodeit.dto.request;

/** create용 요청 DTO */
public record UserCreateRequest (
    String username,
    String email,
    String password
){
}
