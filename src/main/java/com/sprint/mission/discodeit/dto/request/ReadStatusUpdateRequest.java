package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotBlank(message = "Last read at is mandatory") Instant newLastReadAt) {

}
