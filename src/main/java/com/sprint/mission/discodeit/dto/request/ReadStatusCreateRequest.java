package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotNull
    UUID userId,
    @NotNull
    UUID channelId,
    @PastOrPresent
    Instant lastReadAt
) {

}
