package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String username;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID profileImageId;
    private boolean onlineStatus;
}
