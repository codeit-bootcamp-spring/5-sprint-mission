package com.sprint.mission.discodeit.dto.userstatus.reqeust;

import java.time.Instant;

public record UserStatusUpdateRequest(
        Instant newLastActiveAt
) {}
