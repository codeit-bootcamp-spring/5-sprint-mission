package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(@NotBlank(message = "User ID is mandatory") UUID userId,
                                      @NotBlank(message = "Channel ID is mandatory") UUID channelId,
                                      @NotBlank(message = "Last read at is mandatory") Instant lastReadAt) {

}
