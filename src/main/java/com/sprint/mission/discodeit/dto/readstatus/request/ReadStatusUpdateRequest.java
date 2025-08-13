package com.sprint.mission.discodeit.dto.readstatus.request;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        Instant newLastReadAt
) {}
