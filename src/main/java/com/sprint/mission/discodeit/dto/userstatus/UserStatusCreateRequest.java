package com.sprint.mission.discodeit.dto.userstatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserStatusCreateRequest {
    private final UUID userId;
    private final Instant lastOnlineAt;
}
