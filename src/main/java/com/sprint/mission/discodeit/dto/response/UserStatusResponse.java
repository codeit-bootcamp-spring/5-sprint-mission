package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserStatusResponse {
    private UUID id;
    private UUID userId;
    private Instant lastOnlineTime;
    private boolean isOnline;
}
