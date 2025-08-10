package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserCreateRequest {
    private String username;
    private String email;
    private String password;
    private UUID profileImageId; // BinaryContent의 id (nullable)
}