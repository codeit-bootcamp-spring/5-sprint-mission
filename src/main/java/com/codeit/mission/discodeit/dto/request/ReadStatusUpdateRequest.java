package com.codeit.mission.discodeit.dto.request;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        Instant newLastReadAt
) {

}
