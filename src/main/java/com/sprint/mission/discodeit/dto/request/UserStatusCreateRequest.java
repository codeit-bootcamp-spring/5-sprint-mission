package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(@NotBlank(message = "User ID is mandatory") UUID userId,
                                      @NotBlank(message = "Last active at is mandatory") Instant lastActiveAt) {

}
