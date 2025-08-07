package com.sprint.mission.discodeit.dto.userstatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserStatusUpdateRequest {
    private final UUID id;
    private final Instant lastOnlineAt;
}