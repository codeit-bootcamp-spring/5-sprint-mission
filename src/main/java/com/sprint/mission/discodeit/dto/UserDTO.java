package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserDto(UUID id, String username, String email, String password){

    public record CreateUser
            (String username, String email, String password) { }

    public record UpdateUser
            (UUID id, String username, String email, String password) { }

    public record Login
            (String username, String password) { }
}
