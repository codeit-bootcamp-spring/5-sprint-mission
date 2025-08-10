package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private UUID id;
    private String email;
    private String username;
    private String password;
    private UUID profileImageId;
}
