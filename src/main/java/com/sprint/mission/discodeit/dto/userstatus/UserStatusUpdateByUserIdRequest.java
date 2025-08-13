package com.sprint.mission.discodeit.dto.userstatus;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserStatusUpdateByUserIdRequest {
    private UUID userId;
    private Instant lastOnline;
}
