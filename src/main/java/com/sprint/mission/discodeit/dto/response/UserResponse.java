package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString // <= 이것만 붙여도 콘솔에 예쁘게 나옵니다
public class UserResponse {
    private final UUID id;
    private final String username;
    private final String email;
    private final boolean online;

    public UserResponse(User u, boolean online) {
        this(u.getId(), u.getUsername(), u.getEmail(), online);
    }
}
