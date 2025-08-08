package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class UserResponse {
    private final UUID id;
    private final String username;
    private final String email;
    private final boolean online;

    public UserResponse(User u, boolean online) {
        this(u.getId(), u.getUsername(), u.getEmail(), online);
    }
}
